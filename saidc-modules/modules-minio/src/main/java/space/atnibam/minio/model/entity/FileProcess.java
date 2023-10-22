package space.atnibam.minio.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 
 * @TableName file_process
 */
@TableName(value ="file_process")
@Data
public class FileProcess implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 文件标识
     */
    private String fileId;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 存储桶
     */
    private String bucket;

    /**
     * 存储路径
     */
    private String filePath;

    /**
     * 状态,1:未处理，2：处理成功  3处理失败
     */
    private String status;

    /**
     * 上传时间
     */
    private LocalDateTime createDate;

    /**
     * 完成时间
     */
    private LocalDateTime finishDate;

    /**
     * 文件访问地址
     */
    private String url;

    /**
     * 失败原因
     */
    private String errorMsg;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}