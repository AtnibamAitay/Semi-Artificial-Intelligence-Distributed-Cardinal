package space.atnibam.minio.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import space.atnibam.minio.mapper.FileProcessHistoryMapper;
import space.atnibam.minio.mapper.FileProcessMapper;
import space.atnibam.minio.model.entity.FileProcess;
import space.atnibam.minio.model.entity.FileProcessHistory;
import space.atnibam.minio.service.FileProcessHistoryService;

import javax.annotation.Resource;
import java.time.LocalDateTime;

import static space.atnibam.minio.constant.FileServiceConstants.STATUS_FAILURE;
import static space.atnibam.minio.constant.FileServiceConstants.STATUS_SUCCESS;

/**
 * @ClassName: FileProcessHistoryServiceImpl
 * @Description: 针对表【file_process_history】的数据库操作Service实现
 * @Author: AtnibamAitay
 * @CreateTime: 2023-10-22 21:37
 **/
@Slf4j
@Service
public class FileProcessHistoryServiceImpl extends ServiceImpl<FileProcessHistoryMapper, FileProcessHistory>
        implements FileProcessHistoryService {

    @Resource
    private FileProcessMapper fileProcessMapper;

    @Resource
    private FileProcessHistoryMapper fileProcessHistoryMapper;

    /**
     * 更新任务状态
     *
     * @param taskId   任务ID
     * @param status   任务状态
     * @param fileId   文件标识
     * @param url      文件访问地址
     * @param errorMsg 错误信息
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveProcessFinishStatus(Long taskId, String status, String fileId, String url, String errorMsg) {
        // 根据任务ID查询任务信息
        FileProcess fileProcess = fileProcessMapper.selectById(taskId);

        // 如果任务不存在，打印日志并退出方法
        if (fileProcess == null) {
            log.debug("更新任务状态时，此任务：{}，为空", taskId);
            return;
        }

        // 构建查询条件
        LambdaQueryWrapper<FileProcess> queryWrapper = new LambdaQueryWrapper<FileProcess>().eq(FileProcess::getId, taskId);

        // 如果任务状态为失败，更新任务状态和错误信息，并返回
        if (STATUS_FAILURE.equals(status)) {
            log.debug("任务失败：{}", taskId);
            FileProcess fileProcessToUpdate = new FileProcess();
            fileProcessToUpdate.setStatus(STATUS_FAILURE);
            fileProcessToUpdate.setErrorMsg(errorMsg);
            fileProcessToUpdate.setFinishDate(LocalDateTime.now());
            fileProcessMapper.update(fileProcessToUpdate, queryWrapper);
            return;
        }

        // 如果任务状态为成功，更新任务状态和完成时间，并将任务从待处理任务表中删除，同时新增历史处理表记录
        if (STATUS_SUCCESS.equals(status)) {
            fileProcess.setStatus(STATUS_SUCCESS);
            fileProcess.setUrl(url);
            fileProcess.setFinishDate(LocalDateTime.now());
            fileProcessMapper.update(fileProcess, queryWrapper);

            FileProcessHistory fileProcessHistory = new FileProcessHistory();

            // 复制任务信息到历史处理表对象中
            BeanUtils.copyProperties(fileProcess, fileProcessHistory);

            // 向历史处理表插入数据
            fileProcessHistoryMapper.insert(fileProcessHistory);

            // 删除待处理任务表中的数据
            fileProcessMapper.deleteById(taskId);
        }
    }

}
