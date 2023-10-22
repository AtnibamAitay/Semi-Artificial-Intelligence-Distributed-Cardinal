package space.atnibam.minio.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import space.atnibam.minio.mapper.FileProcessMapper;
import space.atnibam.minio.model.entity.FileProcess;
import space.atnibam.minio.service.FileProcessService;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName: FileProcessServiceImpl
 * @Description: 针对表【file_process】的数据库操作Service实现
 * @Author: AtnibamAitay
 * @CreateTime: 2023-10-22 21:37
 **/
@Service
public class FileProcessServiceImpl extends ServiceImpl<FileProcessMapper, FileProcess>
        implements FileProcessService {

    @Resource
    private FileProcessMapper fileProcessMapper;

    /**
     * 根据分片索引、总分片数以及个数来获取媒体处理列表
     *
     * @param shardIndex 分片索引
     * @param shardTotal 总分片数
     * @param count      要获取的个数
     * @return 文件处理列表
     */
    @Override
    public List<FileProcess> getFileProcessList(int shardIndex, int shardTotal, int count) {
        return fileProcessMapper.selectListByShardIndex(shardTotal, shardIndex, count);
    }

}