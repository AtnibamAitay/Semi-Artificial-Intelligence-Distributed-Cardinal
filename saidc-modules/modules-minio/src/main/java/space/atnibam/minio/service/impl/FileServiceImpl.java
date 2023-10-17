package space.atnibam.minio.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import space.atnibam.common.core.exception.MinioException;
import space.atnibam.common.core.utils.text.StringUtils;
import space.atnibam.minio.mapper.FileMapper;
import space.atnibam.minio.model.dto.UploadFileParamsDTO;
import space.atnibam.minio.model.entity.File;
import space.atnibam.minio.service.FileService;
import space.atnibam.minio.utils.FileServiceUtil;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;

import static space.atnibam.common.core.enums.ResultCode.MINIO_SAVE_FILE_INFO_ERROR;
import static space.atnibam.common.core.enums.ResultCode.MINIO_UPLOAD_ERROR;
import static space.atnibam.minio.constant.FileServiceConstants.EXTENSION_SEPARATOR;
import static space.atnibam.minio.constant.FileServiceConstants.SLASH_SEPARATOR;

/**
 * @ClassName: FileServiceImpl
 * @Description: 针对表【files(媒资信息)】的数据库操作Service实现
 * @Author: AtnibamAitay
 * @CreateTime: 2023-10-16 11:36
 **/
@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, File> implements FileService {

    @Resource
    private FileMapper fileMapper;

    @Resource
    private MinioClient minioClient;

    /**
     * 上传文件
     *
     * @param uploadFileParamsDTO 文件信息
     * @param bytes               文件字节数组
     * @param folder              桶下边的子目录
     * @return 返回上传文件结果信息
     */
    @Override
    public String uploadFile(UploadFileParamsDTO uploadFileParamsDTO, byte[] bytes, String folder) {
        String objectName;

        // 计算文件md5值
        String md5 = DigestUtils.md5DigestAsHex(bytes);

        if (StringUtils.isEmpty(folder)) {
            // 如果目录不存在，则自动生成一个目录
            folder = FileServiceUtil.getFileFolder(true, true, true);
        } else if (!folder.endsWith(SLASH_SEPARATOR)) {
            // 如果目录末尾没有 / ，替他加一个
            folder = folder + SLASH_SEPARATOR;
        }

        // objectName = md5值 + 文件后缀名
        String fileName = uploadFileParamsDTO.getFileName();
        objectName = md5 + fileName.substring(fileName.lastIndexOf(EXTENSION_SEPARATOR));
        objectName = folder + objectName;

        try {
            // 将文件上传到 MinIO 并将相关信息添加到数据库
            uploadFileToMinio(bytes, uploadFileParamsDTO.getBucket(), uploadFileParamsDTO.getContentType(), objectName);

            // 将文件信息添加到数据库并返回url
            return insertFileInfoToDB(uploadFileParamsDTO, objectName, md5, uploadFileParamsDTO.getBucket());
        } catch (Exception e) {
            // 在上传过程中出现异常时，抛出自定义异常，信息为："上传过程中出错"
            throw new MinioException(MINIO_UPLOAD_ERROR);
        }
    }

    /**
     * 将文件信息添加到数据库
     *
     * @param uploadFileParamsDTO 上传文件的信息
     * @param objectName          对象名称
     * @param md5                 文件的md5码
     * @param bucket              桶
     * @return 返回保存在数据库的文件信息
     */
    private String insertFileInfoToDB(UploadFileParamsDTO uploadFileParamsDTO, String objectName, String md5, String bucket) {
        // 根据 MD5 查询文件是否已存在于数据库中
        File file = fileMapper.selectById(md5);
        String url = SLASH_SEPARATOR + bucket + SLASH_SEPARATOR + objectName;
        if (file == null) {
            // 初始化一个新的 File 对象，并将上传文件的信息拷贝到其中
            file = new File();
            BeanUtils.copyProperties(uploadFileParamsDTO, file);

            // 设置 File 的各项属性
            file.setId(md5);
            file.setBucket(bucket);
            file.setCreateDate(LocalDateTime.now());
            file.setFilePath(objectName);
            file.setUrl(url);
            file.setUserId(uploadFileParamsDTO.getUserId());

            // 将文件信息保存到数据库，若插入失败则抛出自定义异常
            if (fileMapper.insert(file) <= 0) {
                //抛出异常，信息为："保存文件信息失败"
                throw new MinioException(MINIO_SAVE_FILE_INFO_ERROR);
            }
        }

        return url;
    }

    /**
     * 将文件上传到 MinIO 服务器
     *
     * @param bytes      文件字节数组
     * @param bucket     桶的名称
     * @param objectName 对象名称
     */
    private void uploadFileToMinio(byte[] bytes, String bucket, String contentType, String objectName) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);

        try {
            // 使用 MinIO 客户端将文件上传到 MinIO 服务器
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .stream(byteArrayInputStream, byteArrayInputStream.available(), -1)
                    .contentType(contentType)
                    .build()
            );
        } catch (Exception e) {
            //抛出异常，信息为："上传过程中出错"
            throw new MinioException(MINIO_UPLOAD_ERROR);
        }
    }


}