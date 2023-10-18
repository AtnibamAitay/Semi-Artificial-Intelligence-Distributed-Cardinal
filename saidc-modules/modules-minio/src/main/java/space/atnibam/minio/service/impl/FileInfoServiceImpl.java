package space.atnibam.minio.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.UploadObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import space.atnibam.common.core.exception.MinioException;
import space.atnibam.common.core.utils.text.StringUtils;
import space.atnibam.minio.mapper.FileInfoMapper;
import space.atnibam.minio.model.dto.UploadFileParamsDTO;
import space.atnibam.minio.model.entity.FileInfo;
import space.atnibam.minio.service.FileInfoService;
import space.atnibam.minio.utils.FileServiceUtil;

import javax.annotation.Resource;
import java.io.*;
import java.time.LocalDateTime;

import static space.atnibam.common.core.enums.ResultCode.*;
import static space.atnibam.minio.constant.FileServiceConstants.*;
import static space.atnibam.minio.utils.FileServiceUtil.getContentType;

/**
 * @ClassName: FileInfoServiceImpl
 * @Description: 针对表【files(媒资信息)】的数据库操作Service实现
 * @Author: AtnibamAitay
 * @CreateTime: 2023-10-16 11:36
 **/
@Slf4j
@Service
public class FileInfoServiceImpl extends ServiceImpl<FileInfoMapper, FileInfo> implements FileInfoService {

    @Resource
    private FileInfoMapper fileInfoMapper;

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
            uploadFileToMinio(bytes, uploadFileParamsDTO.getBucket(), objectName);
            FileInfo fileInfo = insertFileInfoToDB(uploadFileParamsDTO, objectName, md5);
            // 将文件信息添加到数据库并返回url
            return fileInfo.getUrl();
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
     * @return 返回保存在数据库的文件信息
     */
    private FileInfo insertFileInfoToDB(UploadFileParamsDTO uploadFileParamsDTO, String objectName, String md5) {
        // 根据 MD5 查询文件是否已存在于数据库中
        FileInfo fileInfo = fileInfoMapper.selectById(md5);
        String url = SLASH_SEPARATOR + uploadFileParamsDTO.getBucket() + SLASH_SEPARATOR + objectName;
        if (fileInfo == null) {
            // 初始化一个新的 FileInfo 对象，并将上传文件的信息拷贝到其中
            fileInfo = new FileInfo();
            BeanUtils.copyProperties(uploadFileParamsDTO, fileInfo);

            // 设置 FileInfo 的各项属性
            fileInfo.setId(md5);
            fileInfo.setBucket(uploadFileParamsDTO.getBucket());
            fileInfo.setCreateDate(LocalDateTime.now());
            fileInfo.setFilePath(objectName);
            fileInfo.setUrl(url);
            fileInfo.setUserId(uploadFileParamsDTO.getUserId());

            // 将文件信息保存到数据库，若插入失败则抛出自定义异常
            if (fileInfoMapper.insert(fileInfo) <= 0) {
                //抛出异常，信息为："保存文件信息失败"
                throw new MinioException(MINIO_SAVE_FILE_INFO_ERROR);
            }
        }

        return fileInfo;
    }

    /**
     * 将文件上传到 MinIO 服务器
     *
     * @param bytes      文件字节数组
     * @param bucket     桶的名称
     * @param objectName 对象名称
     */
    private void uploadFileToMinio(byte[] bytes, String bucket, String objectName) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);

        try {
            // 使用 MinIO 客户端将文件上传到 MinIO 服务器
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .stream(byteArrayInputStream, byteArrayInputStream.available(), -1)
                    .contentType(getContentType(objectName))
                    .build()
            );
        } catch (Exception e) {
            //抛出异常，信息为："上传过程中出错"
            throw new MinioException(MINIO_UPLOAD_ERROR);
        }
    }

    /**
     * 将文件上传到 MinIO 服务器
     *
     * @param filePath   需要上传的本地文件路径
     * @param bucket     MinIO 的存储桶名称
     * @param objectName 上传后在MinIO中的对象名，即文件路径
     */
    private void uploadFileToMinio(String filePath, String bucket, String objectName) {
        // 获取文件的 MIME 类型
        String contentType = getContentType(objectName);

        try {
            // 使用 MinIO 客户端将文件上传到 MinIO 服务器
            minioClient.uploadObject(UploadObjectArgs
                    .builder()
                    .bucket(bucket)
                    .object(objectName)
                    .filename(filePath)
                    .contentType(contentType)
                    .build());
        } catch (Exception e) {
            // 抛出自定义异常，信息为："上传过程中出错"
            throw new MinioException(MINIO_UPLOAD_ERROR);
        }
    }

    /**
     * 检查文件是否存在
     *
     * @param md5 文件的MD5值
     * @return Boolean 返回文件是否存在的结果，如果文件存在返回true，否则返回false
     */
    @Override
    public Boolean checkFile(String md5) {
        // 利用MD5值从数据库中查询对应的媒体文件
        FileInfo fileInfo = fileInfoMapper.selectById(md5);

        // 如果在数据库中没有找到对应的媒体文件，则直接返回false，表示文件不存在
        if (fileInfo == null) {
            return false;
        }

        // 若数据库中存在，根据数据库中的文件信息，继续判断bucket中是否存在该文件
        try {
            InputStream inputStream = minioClient.getObject(GetObjectArgs
                    .builder()
                    .bucket(fileInfo.getBucket())
                    .object(fileInfo.getFilePath())
                    .build());
            // 如果bucket中没有找到对应的文件，也返回false，表示文件不存在
            if (inputStream == null) {
                return false;
            }
        } catch (Exception e) {
            // 获取文件异常
            throw new MinioException(MINIO_GET_FILE_ERROR);
        }

        // 如果以上都没有问题，说明文件存在，返回true
        return true;
    }

    /**
     * 检查文件块是否存在
     *
     * @param md5        文件的MD5值
     * @param chunkIndex 文件块的索引
     * @param bucket     桶的名称
     * @return Boolean   返回文件块是否存在的结果，如果文件块存在返回true，否则返回false
     */
    @Override
    public Boolean checkChunk(String md5, int chunkIndex, String bucket) {
        // 根据MD5值和文件块的索引获取文件块的路径
        String chunkFileFolderPath = getChunkFileFolderPath(md5);
        String chunkFilePath = chunkFileFolderPath + chunkIndex;

        try {
            // 判断文件块是否存在
            InputStream inputStream = minioClient.getObject(GetObjectArgs
                    .builder()
                    .bucket(bucket)
                    .object(chunkFilePath)
                    .build());

            // 如果文件块不存在，返回false
            if (inputStream == null) {
                return false;
            }
        } catch (Exception e) {
            // 获取文件块异常
            throw new MinioException(MINIO_GET_FILE_BLOCK_ERROR);
        }

        // 如果以上都没有问题，说明文件块存在，返回true
        return true;
    }

    /**
     * 获取文件块文件夹路径
     *
     * @param md5 文件的MD5值
     * @return String 返回文件块的文件夹路径
     */
    private String getChunkFileFolderPath(String md5) {
        return md5.charAt(0) + SLASH_SEPARATOR
                + md5.charAt(1)
                + SLASH_SEPARATOR + md5
                + SLASH_SEPARATOR
                + CHUNK_SEPARATOR
                + SLASH_SEPARATOR;
    }

    /**
     * 上传文件块
     *
     * @param md5    文件的MD5值
     * @param chunk  文件块的索引
     * @param bytes  文件块的字节数据
     * @param bucket 存储桶名称
     * @return 返回上传结果，如果上传成功返回true，否则直接抛出异常
     */
    @Override
    public Boolean uploadChunk(String md5, int chunk, byte[] bytes, String bucket) {
        // 获取文件块的路径
        String chunkFilePath = getChunkFileFolderPath(md5) + chunk;

        try {
            // 上传文件块到MinIO
            uploadFileToMinio(bytes, bucket, chunkFilePath);
            return true;
        } catch (Exception e) {
            // 上传失败，记录错误信息并抛出异常
            throw new MinioException(MINIO_UPLOAD_ERROR);
        }
    }

    /**
     * 合并分块文件，并上传至MinIO，最后将文件信息写入数据库.
     *
     * @param fileMd5             文件的MD5值
     * @param chunkTotal          分块的总数目
     * @param uploadFileParamsDTO 上传文件的参数对象
     */
    @Override
    public void mergeChunks(String fileMd5, int chunkTotal, UploadFileParamsDTO uploadFileParamsDTO) {
        // 下载分块文件
        File[] chunkFiles = checkChunkStatus(fileMd5, chunkTotal, uploadFileParamsDTO.getBucket());

        // 获取源文件名
        String fileName = uploadFileParamsDTO.getFileName();

        // 获取源文件扩展名
        String extension = fileName.substring(fileName.lastIndexOf(EXTENSION_SEPARATOR));

        // 创建出临时文件，准备合并
        File mergeFile;
        try {
            mergeFile = File.createTempFile(fileName, extension);
        } catch (IOException e) {
            throw new MinioException(MINIO_CREATE_MERGE_TEMP_FILE_ERROR);
        }

        // 缓冲区
        byte[] buffer = new byte[1024];

        try {
            // 写入流，向临时文件写入
            try (RandomAccessFile raf_write = new RandomAccessFile(mergeFile, "rw")) {
                // 遍历分块文件数组
                for (File chunkFile : chunkFiles) {
                    // 读取流，读分块文件
                    try (RandomAccessFile raf_read = new RandomAccessFile(chunkFile, "r")) {
                        int len;
                        while ((len = raf_read.read(buffer)) != -1) {
                            raf_write.write(buffer, 0, len);
                        }
                    }
                }
            } catch (Exception e) {
                throw new MinioException(MINIO_MERGE_FILE_ERROR);
            }

            uploadFileParamsDTO.setFileSize(mergeFile.length());

            // 对文件进行校验，通过MD5值比较
            try (FileInputStream mergeInputStream = new FileInputStream(mergeFile)) {
                String mergeMd5 = org.apache.commons.codec.digest.DigestUtils.md5Hex(mergeInputStream);
                if (!fileMd5.equals(mergeMd5)) {
                    throw new MinioException(MINIO_MERGE_FILE_CHECK_ERROR);
                }

                log.debug("合并文件校验通过：{}", mergeFile.getAbsolutePath());
            } catch (Exception e) {
                throw new MinioException(MINIO_MERGE_FILE_CHECK_EXCEPTION);
            }

            String mergeFilePath = getFilePathByMd5(fileMd5, extension);

            // 将本地合并好的文件，上传到minio中，这里重载了一个方法
            uploadFileToMinio(mergeFile.getAbsolutePath(), uploadFileParamsDTO.getBucket(), mergeFilePath);

            log.debug("合并文件上传至MinIO完成{}", mergeFile.getAbsolutePath());

            // 将文件信息写入数据库
            FileInfo fileInfo = insertFileInfoToDB(uploadFileParamsDTO, mergeFilePath, fileMd5);
            if (fileInfo == null) {
                throw new MinioException(MINIO_MEDIA_FILE_INSERT_ERROR);
            }

            log.debug("媒资文件入库完成");

        } finally {
            for (File chunkFile : chunkFiles) {
                try {
                    chunkFile.delete();
                } catch (Exception e) {
                    throw new MinioException(MINIO_DELETE_TEMP_FILE_ERROR);
                }
            }

            try {
                mergeFile.delete();
            } catch (Exception e) {
                throw new MinioException(MINIO_DELETE_TEMP_MERGE_FILE_ERROR);
            }
        }
    }

    /**
     * 检查分块文件的状态，下载分块文件，并组成结果返回.
     *
     * @param fileMd5    文件的MD5值
     * @param chunkTotal 分块的总数目
     * @return FileInfo[] 下载的分块文件数组
     */
    private File[] checkChunkStatus(String fileMd5, int chunkTotal, String bucket) {
        // 作为结果返回
        File[] files = new File[chunkTotal];

        // 获取分块文件目录
        String chunkFileFolder = getChunkFileFolderPath(fileMd5);

        for (int i = 0; i < chunkTotal; i++) {
            // 获取分块文件路径
            String chunkFilePath = chunkFileFolder + i;
            File chunkFile;

            try {
                // 创建临时的分块文件
                chunkFile = File.createTempFile("chunk" + i, null);
            } catch (Exception e) {
                throw new MinioException(MINIO_CREATE_TEMP_FILE_ERROR);
            }

            // 下载分块文件
            chunkFile = downloadFileFromMinio(chunkFile, bucket, chunkFilePath);

            // 组成结果
            files[i] = chunkFile;
        }

        return files;
    }

    /**
     * 从MinIO服务器下载文件.
     *
     * @param file       需要写入的文件
     * @param bucket     MinIO的存储桶名称
     * @param objectName MinIO中的对象名，即文件路径
     * @return FileInfo 下载后的文件
     */
    private File downloadFileFromMinio(File file, String bucket, String objectName) {
        try (
                // 创建文件输出流
                FileOutputStream fileOutputStream = new FileOutputStream(file);

                // 获取MinIO中的对象输入流
                InputStream inputStream = minioClient.getObject(GetObjectArgs
                        .builder()
                        .bucket(bucket)
                        .object(objectName)
                        .build())
        ) {
            // 将输入流的内容复制到输出流，完成文件下载
            IOUtils.copy(inputStream, fileOutputStream);

            return file;
        } catch (Exception e) {
            throw new MinioException(MINIO_QUERY_FILE_BLOCK_ERROR);
        }
    }

    /**
     * 根据MD5和文件扩展名，生成文件路径，例 /2/f/2f6451sdg/2f6451sdg.mp4
     *
     * @param fileMd5   文件MD5
     * @param extension 文件扩展名
     */
    private String getFilePathByMd5(String fileMd5, String extension) {
        return fileMd5.charAt(0) + "/" + fileMd5.charAt(1) + "/" + fileMd5 + "/" + fileMd5 + extension;
    }

}