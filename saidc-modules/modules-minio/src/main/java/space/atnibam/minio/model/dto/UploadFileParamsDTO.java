package space.atnibam.minio.model.dto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @ClassName: UploadFileParamsDTO
 * @Description: 包含文件信息的数据传输对象
 * @Author: AtnibamAitay
 * @CreateTime: 2023/10/16 0016 16:21
 **/
@Data
@ToString
@Builder
public class UploadFileParamsDTO implements Serializable {

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件content-type
     */
    private String contentType;

    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * 上传人
     */
    private Integer userId;

    /**
     * 存储桶
     */
    private String bucket;
}
