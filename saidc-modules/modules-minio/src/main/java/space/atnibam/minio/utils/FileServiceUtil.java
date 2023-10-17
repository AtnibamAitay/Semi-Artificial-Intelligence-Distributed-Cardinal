package space.atnibam.minio.utils;

import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import org.springframework.http.MediaType;

import java.text.SimpleDateFormat;
import java.util.Date;

import static space.atnibam.minio.constant.FileServiceConstants.*;

/**
 * @ClassName: FilesUtil
 * @Description: 文件工具类
 * @Author: AtnibamAitay
 * @CreateTime: 2023-10-17 22:35
 **/
public class FileServiceUtil {

    /**
     * 根据文件名获取其内容类型
     *
     * @param fileName 文件名
     * @return 文件的Content-Type
     */
    public static String getContentType(String fileName) {
        // 默认content-type为未知二进制流
        String contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;

        // 判断文件名是否包含"."
        if (fileName.contains(EXTENSION_SEPARATOR)) {
            // 从文件名中划分出扩展名
            String extension = fileName.substring(fileName.lastIndexOf(EXTENSION_SEPARATOR));

            /*
             * 根据扩展名得到Content-Type
             * 如果是未知扩展名（例如 .abc等)，则返回null
             */
            ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(extension);

            // 如果能够找到对应的Content-Type，则更新默认的Content-Type
            if (extensionMatch != null) {
                contentType = extensionMatch.getMimeType();
            }
        }

        return contentType;
    }

    /**
     * 生成文件夹路径
     *
     * @param year  是否包含年份
     * @param month 是否包含月份
     * @param day   是否包含日期
     * @return 返回文件夹路径字符串
     */
    public static String getFileFolder(boolean year, boolean month, boolean day) {
        StringBuilder stringBuffer = new StringBuilder();
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

        // 获取当前日期并格式化为"yyyy-MM-dd"形式
        String dateString = dateFormat.format(new Date());
        String[] split = dateString.split(DATE_SEPARATOR);

        // 如果year为true，则在路径中添加年份
        if (year) {
            stringBuffer.append(split[0]).append(SLASH_SEPARATOR);
        }

        // 如果month为true，则在路径中添加月份
        if (month) {
            stringBuffer.append(split[1]).append(SLASH_SEPARATOR);
        }

        // 如果day为true，则在路径中添加日
        if (day) {
            stringBuffer.append(split[2]).append(SLASH_SEPARATOR);
        }

        return stringBuffer.toString();
    }

}
