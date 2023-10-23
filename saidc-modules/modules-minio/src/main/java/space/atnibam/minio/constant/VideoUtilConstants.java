package space.atnibam.minio.constant;

/**
 * @ClassName: VideoUtilConstants
 * @Description: 视频工具类常量类
 * @Author: AtnibamAitay
 * @CreateTime: 2023/10/23 0023 11:39
 **/
public class VideoUtilConstants {

    /**
     * 用于ffmpeg命令的-i参数，表示后面接待处理的输入文件
     */
    public static final String FFMPEG_INPUT_PARAM = "-i";

    /**
     * 在ffmpeg的输出信息中找到"Duration: "字段的位置的标记
     */
    public static final String DURATION_MARKER = "Duration: ";

    /**
     * 在ffmpeg的输出信息中找到", start:"字段的位置的标记
     */
    public static final String START_MARKER = ", start:";

    /**
     * 默认返回结果为"error"
     */
    public static final String DEFAULT_RESULT = "error";

    /**
     * 默认退出值为-1
     */
    public static final int DEFAULT_EXIT_VALUE = -1;

    /**
     * 最大重试次数为600，每次休眠1秒，最长执行时间10分钟
     */
    public static final int MAX_RETRY = 600;

    /**
     * 字符串最后一个字符的位置
     */
    public static final String LAST_CHAR_POSITION = ".";
}
