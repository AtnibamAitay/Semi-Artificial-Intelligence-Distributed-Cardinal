package space.atnibam.minio.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import space.atnibam.minio.model.entity.FileProcess;

import java.util.List;

/**
 * @ClassName: FileProcessMapper
 * @Description: 文件处理映射接口，定义了对FileProcess实体的数据库操作
 * @Author: atnibamaitay
 * @CreateTime: 2023-10-22 21:46
 **/
public interface FileProcessMapper extends BaseMapper<FileProcess> {

    /**
     * 根据分片索引和数量限制查询文件处理列表
     *
     * @param shardTotal 分片总数
     * @param shardIndex 分片索引
     * @param count      查询数量上限
     * @return 匹配条件的文件处理列表
     */
    @Select("SELECT * FROM file_process WHERE id % #{shardTotal} = #{shardIndex} AND status = '1' LIMIT #{count}")
    List<FileProcess> selectListByShardIndex(@Param("shardTotal") int shardTotal,
                                             @Param("shardIndex") int shardIndex,
                                             @Param("count") int count);
}