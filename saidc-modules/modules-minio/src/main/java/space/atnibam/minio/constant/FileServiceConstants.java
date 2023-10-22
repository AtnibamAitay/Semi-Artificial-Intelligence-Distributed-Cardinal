package space.atnibam.minio.constant;

/**
 * @ClassName: FileServiceConstants
 * @Description: 文件服务常量类
 * @Author: AtnibamAitay
 * @CreateTime: 2023/10/17 23:55
 **/
public class FileServiceConstants {
    /**
     * 斜杠分隔符
     */
    public static final String SLASH_SEPARATOR = "/";
    /**
     * 日期格式
     */
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    /**
     * 日期分隔符
     */
    public static final String DATE_SEPARATOR = "-";
    /**
     * 文件后缀名分隔符
     */
    public static final String EXTENSION_SEPARATOR = ".";
    /**
     * "chunk"分隔符
     */
    public static final String CHUNK_SEPARATOR = "chunk";

    /**
     * 任务状态：待处理
     */
    public static final String STATUS_SUCCESS = "2";

    /**
     * 任务状态：处理失败
     */
    public static final String STATUS_FAILURE = "3";
}
