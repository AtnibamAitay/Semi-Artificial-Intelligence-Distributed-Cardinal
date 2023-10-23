package space.atnibam.minio.service.jobhandler;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import space.atnibam.common.core.exception.MinioException;
import space.atnibam.minio.model.entity.FileProcess;
import space.atnibam.minio.service.FileInfoService;
import space.atnibam.minio.service.FileProcessHistoryService;
import space.atnibam.minio.service.FileProcessService;
import space.atnibam.minio.utils.Mp4VideoUtil;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static space.atnibam.common.core.enums.ResultCode.*;
import static space.atnibam.minio.constant.FileServiceConstants.SLASH_SEPARATOR;
import static space.atnibam.minio.constant.VideoTaskConstants.*;

/**
 * @ClassName: VideoTask
 * @Description: 视频处理任务
 * @Author: AtnibamAitay
 * @CreateTime: 2023/10/23 11:39
 */
@Slf4j
@Component
public class VideoTask {

    @Resource
    private FileInfoService fileInfoService;

    @Resource
    private FileProcessService fileProcessService;

    @Resource
    private FileProcessHistoryService fileProcessHistoryService;

    /**
     * 处理视频任务
     */
    @XxlJob("videoJobHandler")
    public void videoJobHandler() throws InterruptedException {
        // 分片序号
        int shardIndex = XxlJobHelper.getShardIndex();
        // 分片总数
        int shardTotal = XxlJobHelper.getShardTotal();
        log.debug("分片序号：{}，分片总数：{}", shardIndex, shardTotal);

        // 查询待处理任务，一次处理的任务数与cpu核心数相同
        List<FileProcess> fileProcessList = fileProcessService.getFileProcessList(shardTotal, shardIndex, 12);
        CountDownLatch countDownLatch = new CountDownLatch(fileProcessList.size());
        // 未查询到待处理任务，结束方法
        if (fileProcessList == null || fileProcessList.size() == 0) {
            log.debug("查询到的待处理任务数为0");
            return;
        }

        // 要处理的任务数
        int size = fileProcessList.size();
        // 查询到任务，创建size个线程去处理
        ExecutorService threadPool = Executors.newFixedThreadPool(size);

        fileProcessList.forEach(fileProcess -> threadPool.execute(() -> {
            String status = fileProcess.getStatus();
            // 避免重复执行任务
            if (TASK_STATUS_PROCESSED.equals(status)) {
                log.debug("该视频已经被处理，无需再次处理。视频信息：{}", fileProcess);
                countDownLatch.countDown();
                return;
            }

            // 桶
            String bucket = fileProcess.getBucket();
            // 文件路径
            String filePath = fileProcess.getFilePath();
            // 原始文件的md5
            String fileId = fileProcess.getFileId();

            File originalFile;
            File mp4File;

            try {
                // 将原始视频下载到本地，创建临时文件
                originalFile = File.createTempFile(TEMP_FILE_PREFIX_ORIGINAL, null);
                // 处理完成后的文件
                mp4File = File.createTempFile(TEMP_FILE_PREFIX_MP4, TEMP_FILE_SUFFIX_MP4);
            } catch (IOException e) {
                log.error("处理视频前创建临时文件失败");
                countDownLatch.countDown();
                throw new MinioException(MINIO_CREATE_TEMP_FILE_BEFORE_PROCESS_ERROR);
            }
            try {
                fileInfoService.downloadFileFromMinio(originalFile, bucket, filePath);
            } catch (Exception e) {
                log.error("下载原始文件过程中出错：{}，文件信息：{}", e.getMessage(), fileProcess);
                countDownLatch.countDown();
                throw new MinioException(MINIO_DOWNLOAD_ORIGINAL_FILE_ERROR);
            }

            // 调用工具类将avi转为mp4
            String result;
            try {
                // 获取转换结果，转换成功返回success 转换失败返回错误信息
                result = Mp4VideoUtil.generateMp4(originalFile.getAbsolutePath(), mp4File.getAbsolutePath());
            } catch (Exception e) {
                log.error("处理视频失败，视频地址：{}，错误信息：{}", originalFile.getAbsolutePath(), e.getMessage());
                countDownLatch.countDown();
                throw new MinioException(MINIO_PROCESS_VIDEO_ERROR);
            }

            // 转换成功，上传到MinIO
            // 设置默认状态为失败
            status = TASK_STATUS_FAILED;
            String url = null;
            if (CONVERT_RESULT_SUCCESS.equals(result)) {
                // 根据文件md5，生成objectName
                String objectName = fileInfoService.getFilePathByMd5(fileId, TEMP_FILE_SUFFIX_MP4);
                try {
                    // 上传到MinIO
                    fileInfoService.uploadFileToMinio(mp4File.getAbsolutePath(), bucket, objectName);
                } catch (Exception e) {
                    log.error("上传文件失败：{}", e.getMessage());
                    throw new MinioException(MINIO_UPLOAD_FILE_ERROR);
                }
                // 处理成功，将状态设为成功
                status = TASK_STATUS_PROCESSED;
                // 拼接url，准备更新数据
                url = SLASH_SEPARATOR + bucket + SLASH_SEPARATOR + objectName;
            }
            // 记录任务处理结果url
            fileProcessHistoryService.saveProcessFinishStatus(fileProcess.getId(), status, fileId, url, result);
            // 删除临时文件
            countDownLatch.countDown();
        }));

        // 等待，为了防止无线等待，这里设置一个超时时间为30分钟（很充裕了），若到时间还未处理完，则结束任务
        countDownLatch.await(AWAIT_TIMEOUT_MINUTES, TimeUnit.MINUTES);
    }
}