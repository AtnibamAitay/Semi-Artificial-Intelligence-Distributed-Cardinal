package space.atnibam.minio.constant;

/**
 * @ClassName: VideoTaskConstants
 * @Description: 视频任务常量类
 * @Author: AtnibamAitay
 * @CreateTime: 2023/10/23 0023 11:47
 **/
public class VideoTaskConstants {

    /**
     * 任务状态 - 已处理
     */
    public static final String TASK_STATUS_PROCESSED = "2";

    /**
     * 任务状态 - 处理失败
     */
    public static final String TASK_STATUS_FAILED = "3";

    /**
     * 转换结果 - 成功
     */
    public static final String CONVERT_RESULT_SUCCESS = "success";

    /**
     * 临时文件前缀 - 原始
     */
    public static final String TEMP_FILE_PREFIX_ORIGINAL = "original";

    /**
     * 临时文件前缀 - MP4
     */
    public static final String TEMP_FILE_PREFIX_MP4 = "mp4";

    /**
     * 临时文件后缀 - MP4
     */
    public static final String TEMP_FILE_SUFFIX_MP4 = ".mp4";

    /**
     * 等待超时时间（单位：分钟）
     */
    public static final long AWAIT_TIMEOUT_MINUTES = 30;
}
