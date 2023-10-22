package space.atnibam.minio.service;

import com.baomidou.mybatisplus.extension.service.IService;
import space.atnibam.minio.model.dto.UploadFileParamsDTO;
import space.atnibam.minio.model.entity.FileInfo;

import java.io.File;

/**
 * @ClassName: FileInfoService
 * @Description: 提供对媒资信息表（FileInfo）的数据库操作服务接口
 * @Author: AtnibamAitay
 * @CreateTime: 2023-10-16 14:32:52
 **/
public interface FileInfoService extends IService<FileInfo> {

    /**
     * 文件上传方法
     *
     * @param uploadFileParamsDTO 包含文件信息的数据传输对象
     * @param bytes               要上传的文件的字节数组
     * @param folder              目标存储的子目录名称
     * @return 返回文件上传后的结果信息
     */
    String uploadFile(UploadFileParamsDTO uploadFileParamsDTO, byte[] bytes, String folder);

    /**
     * 将文件上传到 MinIO 服务器
     *
     * @param filePath   需要上传的本地文件路径
     * @param bucket     MinIO 的存储桶名称
     * @param objectName 上传后在MinIO中的对象名，即文件路径
     */
    void uploadFileToMinio(String filePath, String bucket, String objectName);

    /**
     * 检查文件是否存在
     *
     * @param md5 文件的MD5值
     * @return Boolean 返回文件是否存在的结果，如果文件存在返回true，否则返回false
     */
    Boolean checkFile(String md5);

    /**
     * 检查分块是否存在
     *
     * @param md5        文件的MD5
     * @param chunkIndex 分块序号
     * @param bucket     存储桶名称
     * @return Boolean   返回文件块是否存在的结果，如果文件块存在返回true，否则返回false
     */
    Boolean checkChunk(String md5, int chunkIndex, String bucket);

    /**
     * 上传文件块
     *
     * @param md5    文件的MD5值
     * @param chunk  文件块的索引
     * @param bytes  文件块的字节数据
     * @param bucket 存储桶名称
     * @return R 返回上传结果，如果上传成功返回true，否则返回false和错误信息
     */
    Boolean uploadChunk(String md5, int chunk, byte[] bytes, String bucket);

    /**
     * 合并分块文件，并上传至MinIO，最后将文件信息写入数据库.
     *
     * @param md5                 文件的MD5值
     * @param chunkTotal          分块的总数目
     * @param uploadFileParamsDTO 上传文件的参数对象
     */
    void mergeChunks(String md5, int chunkTotal, UploadFileParamsDTO uploadFileParamsDTO);

    /**
     * 从MinIO服务器下载文件.
     *
     * @param file       需要写入的文件
     * @param bucket     MinIO的存储桶名称
     * @param objectName MinIO中的对象名，即文件路径
     * @return FileInfo 下载后的文件
     */
    File downloadFileFromMinio(File file, String bucket, String objectName);

    /**
     * 根据MD5和文件扩展名，生成文件路径，例 /2/f/2f6451sdg/2f6451sdg.mp4
     *
     * @param md5       文件MD5
     * @param extension 文件扩展名
     */
    String getFilePathByMd5(String md5, String extension);
}