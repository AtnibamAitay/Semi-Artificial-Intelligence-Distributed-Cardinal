package space.atnibam.minio.service;

import com.baomidou.mybatisplus.extension.service.IService;
import space.atnibam.minio.model.dto.UploadFileParamsDTO;
import space.atnibam.minio.model.entity.File;

/**
 * @ClassName: FileService
 * @Description: 提供对媒资信息表（File）的数据库操作服务接口
 * @Author: AtnibamAitay
 * @CreateTime: 2023-10-16 14:32:52
 **/
public interface FileService extends IService<File> {

    /**
     * 文件上传方法
     *
     * @param uploadFileParamsDTO 包含文件信息的数据传输对象
     * @param bytes               要上传的文件的字节数组
     * @param folder              目标存储的子目录名称
     * @return 返回文件上传后的结果信息
     */
    String uploadFile(UploadFileParamsDTO uploadFileParamsDTO, byte[] bytes, String folder);
}