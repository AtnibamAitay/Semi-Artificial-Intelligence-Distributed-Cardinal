package space.atnibam.common.redis.constant;

/**
 * @ClassName: RedisConstants
 * @Description: Redis的常量
 * @Author: AtnibamAitay
 * @CreateTime: 2023/10/19 0019 17:24
 **/
public class RedisConstants {
    /**
     * Redis分割符
     */
    public static final String REDIS_SEPARATOR = ":";

    /**
     * 默认锁对应的值
     */
    public static final String DEFAULT_LOCK_VALUE = "DEFAULT_SUBMIT_LOCK_VALUE";

    /**
     * Redis 锁的默认过期时间，单位：秒
     */
    public static final Long CACHE_NULL_TTL = 2L;

    /**
     * Redis 锁的键前缀
     */
    public static final String LOCK_KEY = "lock:";

    /**
     * 线程池大小
     */
    public static final int THREAD_POOL_SIZE = 10;

    /**
     * 获取互斥锁的超时时间（10秒）
     */
    public static final long LOCK_TIMEOUT = 10L;

    /**
     * 线程休眠的时间（50毫秒）
     */
    public static final long SLEEP_TIME_IN_MILLIS = 50L;
}
