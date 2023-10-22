package space.atnibam.minio.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 
 * @TableName file_process_history
 */
@TableName(value ="file_process_history")
@Data
public class FileProcessHistory implements Serializable {
    /**
     * 
     */
    @TableId
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
     * 存储源
     */
    private String bucket;

    /**
     * 状态 - 1代表未处理，2代表处理成功，3代表处理失败
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
     * 文件路径
     */
    private String filePath;

    /**
     * 失败原因
     */
    private String errorMsg;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}