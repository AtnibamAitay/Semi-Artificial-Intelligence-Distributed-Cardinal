package space.atnibam.minio.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.transaction.annotation.Transactional;
import space.atnibam.minio.model.entity.FileProcessHistory;

/**
 * @ClassName: FileProcessHistoryService
 * @Description: 针对表【file_process_history】的数据库操作Service
 * @Author: AtnibamAitay
 * @CreateTime: 2023-10-22 21:37
 **/
public interface FileProcessHistoryService extends IService<FileProcessHistory> {

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
    void saveProcessFinishStatus(Long taskId, String status, String fileId, String url, String errorMsg);
}
