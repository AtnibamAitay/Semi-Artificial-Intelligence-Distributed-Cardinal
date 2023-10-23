package space.atnibam.minio.utils;

import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static space.atnibam.minio.constant.VideoUtilConstants.*;

/**
 * @ClassName: VideoUtil
 * @Description: 视频文件处理工具类，提供获取视频时长和校验两个视频的时长是否相等的方法。
 * @Author: AtnibamAitay
 * @CreateTime: 2023-10-22 22:43
 **/
public class VideoUtil {



    @Value("${ffmpeg.path}")
    private static String ffmpegPath;

    /**
     * 检查两个视频时间是否一致。
     *
     * @param source 源视频文件路径
     * @param target 目标视频文件路径
     * @return 是否一致。当两个视频的播放时间完全一致时返回true，否则返回false
     */
    public static Boolean checkVideoTime(String source, String target) {
        String sourceTime = getVideoTime(source);
        // 取出时分秒
        sourceTime = sourceTime.substring(0, sourceTime.lastIndexOf(LAST_CHAR_POSITION));
        String targetTime = getVideoTime(target);
        // 取出时分秒
        targetTime = targetTime.substring(0, targetTime.lastIndexOf(LAST_CHAR_POSITION));

        // 判断获取到的时间是否为空
        if (sourceTime == null || targetTime == null) {
            return false;
        }

        // 判断两个时间是否相等
        return sourceTime.equals(targetTime);
    }

    /**
     * 根据视频路径获取视频时长的方法。
     *
     * @param videoPath 视频文件的路径
     * @return 返回视频的时长，如果过程中有任何异常，返回null
     */
    public static String getVideoTime(String videoPath) {
        // 初始化命令行参数列表
        List<String> commend = new ArrayList<>();
        // 添加ffmpeg的路径到命令行参数
        commend.add(ffmpegPath);
        // 添加-i参数，表示后面接待处理的输入文件
        commend.add(FFMPEG_INPUT_PARAM);
        // 添加待处理的视频文件路径
        commend.add(videoPath);

        try {
            // 创建一个新的进程生成器
            ProcessBuilder builder = new ProcessBuilder();
            // 设置此进程生成器的操作系统程序和参数
            builder.command(commend);
            // 将标准输入流和错误输入流合并，通过这样做可以通过标准输入流读取信息
            builder.redirectErrorStream(true);
            // 启动一个新进程，并使用此进程生成器的属性
            Process p = builder.start();
            // 等待由此Process对象表示的进程完成，返回该进程的输出信息
            String outString = waitFor(p);
            System.out.println(outString);

            // 在ffmpeg的输出信息中找到"Duration: "字段的位置
            int start = outString.trim().indexOf(DURATION_MARKER);
            if (start >= 0) {
                // 在ffmpeg的输出信息中找到", start:"字段的位置
                int end = outString.trim().indexOf(START_MARKER);
                if (end >= 0) {
                    // 截取出时长信息
                    String time = outString.substring(start + 10, end);
                    // 如果获取的时长不为空且不为""，则返回该时长
                    if (time != null && !time.equals("")) {
                        return time.trim();
                    }
                }
            }

        } catch (Exception ex) {
            // 输出异常信息到错误输出流
            ex.printStackTrace();

        }
        // 如果在过程中有任何异常发生，将返回null
        return null;
    }

    /**
     * 等待进程执行完成，并获取执行结果
     *
     * @param p 执行的进程
     * @return 执行结果
     */
    public static String waitFor(Process p) {
        // 初始化输入流为null
        InputStream in = null;
        // 默认退出值为-1
        int exitValue = DEFAULT_EXIT_VALUE;
        // 初始化输出字符串缓冲区
        StringBuilder outputString = new StringBuilder();

        try {
            // 获取进程的输入流
            in = p.getInputStream();
            // 设置完成标志位为false
            boolean finished = false;
            // 设定最大重试次数为600，每次休眠1秒，最长执行时间10分钟
            // 当前已重试次数为0
            int retry = 0;

            // 进程未完成时持续循环
            while (!finished) {
                // 若当前已重试次数超过最大重试次数，则返回"error"
                if (retry > MAX_RETRY) {
                    return DEFAULT_RESULT;
                }
                try {
                    // 当输入流可读取的数据量大于0时，持续从输入流中读取数据
                    while (in.available() > 0) {
                        Character c = new Character((char) in.read());
                        // 将读取到的字符添加至输出字符串缓冲区中
                        outputString.append(c);
                        // 打印读取到的字符
                        System.out.print(c);
                    }
                    // 尝试获取进程的退出值，若进程未结束，此调用会抛出IllegalThreadStateException异常
                    exitValue = p.exitValue();
                    // 若没有抛出异常，说明进程已结束，设置完成标志位为true
                    finished = true;

                } catch (IllegalThreadStateException e) {
                    // 进程未结束时，暂停1秒后再次尝试
                    Thread.sleep(1000);
                    // 已重试次数增加1
                    retry++;
                }
            }

        } catch (Exception e) {
            // 打印异常堆栈信息
            e.printStackTrace();

        } finally {
            // 确保输入流在使用完毕后被正确关闭
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    // 输出关闭输入流时发生的异常信息
                    System.out.println(e.getMessage());
                }
            }
        }
        // 返回从输入流中读取到的字符串
        return outputString.toString();
    }

}