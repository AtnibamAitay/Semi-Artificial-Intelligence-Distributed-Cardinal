package space.atnibam.minio.utils;

import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: Mp4VideoUtil
 * @Description: 视频处理工具类，继承自VideoUtil，主要用于将其他视频格式转化为mp4格式
 * @Author: atnibamaitay
 * @CreateTime: 2023-10-22 22:43
 **/
public class Mp4VideoUtil {

    @Value("${ffmpeg.path}")
    private static String ffmpegPath;

    /**
     * 使用ffmpeg进行视频编码，生成mp4文件
     *
     * @param videoPath     待转换视频的路径
     * @param mp4folderPath 转换后的mp4文件的存放路径
     * @return 返回字符串。如果转换成功，返回"success"，失败则返回控制台日志
     */
    public static String generateMp4(String videoPath, String mp4folderPath) {
        // 清除已生成的mp4
        clearMp4(mp4folderPath);

        List<String> commend = new ArrayList<>();
        commend.add(ffmpegPath);
        commend.add("-i");
        commend.add(videoPath);
        commend.add("-c:v");
        commend.add("libx264");
        //覆盖输出文件
        commend.add("-y");
        commend.add("-s");
        commend.add("1280x720");
        commend.add("-pix_fmt");
        commend.add("yuv420p");
        commend.add("-b:a");
        commend.add("63k");
        commend.add("-b:v");
        commend.add("753k");
        commend.add("-r");
        commend.add("18");
        commend.add(mp4folderPath);

        String outstring = null;
        try {
            ProcessBuilder builder = new ProcessBuilder();
            builder.command(commend);
            // 将标准输入流和错误输入流合并，通过标准输入流程读取信息
            builder.redirectErrorStream(true);
            Process p = builder.start();
            outstring = VideoUtil.waitFor(p);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        Boolean checkVideoTime = VideoUtil.checkVideoTime(videoPath, mp4folderPath);
        if (!checkVideoTime) {
            return outstring;
        } else {
            return "success";
        }
    }

    /**
     * 清除已生成的mp4文件
     *
     * @param mp4Path mp4文件的路径
     */
    private static void clearMp4(String mp4Path) {
        // 删除原来已经生成的m3u8及ts文件
        File mp4File = new File(mp4Path);
        if (mp4File.exists() && mp4File.isFile()) {
            mp4File.delete();
        }
    }
}
