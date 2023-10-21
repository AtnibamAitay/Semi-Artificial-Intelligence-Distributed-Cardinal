package space.atnibam.common.redis.domain;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * @ClassName: RedisData
 * @Description: 用于存储在Redis中的数据，包括过期时间和实际数据
 * @Author: AtnibamAitay
 * @CreateTime: 2023-10-21 14:34
 **/
@Data
public class RedisData {
    /**
     * 过期时间
     */
    private LocalDateTime expireTime;
    
    /**
     * 实际的数据对象
     */
    private Object data;
}