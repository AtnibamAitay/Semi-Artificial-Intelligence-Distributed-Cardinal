package space.atnibam.minio.service;

import com.baomidou.mybatisplus.extension.service.IService;
import space.atnibam.minio.model.entity.FileProcess;

import java.util.List;

/**
 * @ClassName: FileProcessServiceImpl
 * @Description: 针对表【file_process】的数据库操作Service
 * @Author: Atnibam Aitay
 * @CreateTime: 2023-10-22 21:37:19
 **/
public interface FileProcessService extends IService<FileProcess> {

    /**
     * 根据分片索引、总分片数以及个数来获取媒体处理列表
     *
     * @param shardIndex 分片索引
     * @param shardTotal 总分片数
     * @param count      要获取的个数
     * @return 文件处理列表
     */
    List<FileProcess> getFileProcessList(int shardIndex, int shardTotal, int count);
}