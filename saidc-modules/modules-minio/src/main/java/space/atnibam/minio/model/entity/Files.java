package space.atnibam.minio.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;

/**
 * @ClassName: Files
 * @Description: 文件实体类
 * @Author: AtnibamAitay
 * @CreateTime: 2023/10/16 16:04
 **/
@TableName(value ="files")
@Data
public class Files implements Serializable {
    /**
     * 文件ID（MD5值）
     */
    @TableId
    private String id;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 存储目录
     */
    private String bucket;

    /**
     * 存储路径
     */
    private String filePath;

    /**
     * 文件访问地址
     */
    private String url;

    /**
     * 上传人
     */
    private String userName;

    /**
     * 上传时间
     */
    private LocalDateTime createDate;

    /**
     * 修改时间
     */
    private LocalDateTime changeDate;

    /**
     * 状态,1:正常，0:禁用
     */
    private String status;

    /**
     * 文件大小
     */
    private Long fileSize;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}