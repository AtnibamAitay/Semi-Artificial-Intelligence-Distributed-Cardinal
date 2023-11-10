package space.atnibam.common.redis.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.Collections;

/**
 * @ClassName: RedisLockService
 * @Description: 提供Redis分布式锁的服务，采用LUA脚本实现，保证加锁、解锁操作原子性。
 * @Author: AtnibamAitay
 * @CreateTime: 2023-10-19 17:16
 **/
@Component
public class RedisLockService {

    /**
     * 默认分布式锁过期时间，单位秒
     */
    private static final Long DEFAULT_LOCK_EXPIRE_TIME = 60L;

    /**
     * Spring提供的操作字符串Redis的模板类
     */
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 尝试获取锁，如果在超时时间内获取到锁返回true，否则返回false。
     *
     * @param key 锁的键值
     * @param value 锁的值
     * @param timeout 超时时间
     * @return 如果在超时时间内成功获取到锁返回true，否则返回false
     */
    public boolean tryLock(String key, String value, Duration timeout) {
        // 将超时时间转换为毫秒
        long waitMills = timeout.toMillis();
        // 记录当前系统时间
        long currentTimeMillis = System.currentTimeMillis();

        do {
            // 调用lock方法尝试获取锁，且锁的过期时间为默认值
            boolean lock = lock(key, value, DEFAULT_LOCK_EXPIRE_TIME);

            if (lock) {
                // 如果获取到锁，则立即返回true
                return true;
            }

            // 没有获取到锁，线程暂停1毫秒后再次尝试获取
            try {
                Thread.sleep(1L);
            } catch (InterruptedException e) {
                // 清除中断标志，以便于下一次中断能够被正确处理
                Thread.interrupted();
            }
            // 当前时间小于开始时间加上超时时间则继续循环，也就是在超时时间内继续尝试获取锁
        } while (System.currentTimeMillis() < currentTimeMillis + waitMills);

        // 超时时间内没有获取到锁，返回false
        return false;
    }

    /**
     * 直接尝试获取锁
     *
     * @param key    锁的键
     * @param value  锁的值
     * @param expire 锁的过期时间，单位秒
     * @return 是否成功获取到锁
     */
    public boolean lock(String key, String value, Long expire) {
        String luaScript = "if redis.call('setnx', KEYS[1], ARGV[1]) == 1 then return redis.call('expire', KEYS[1], ARGV[2]) else return 0 end";
        RedisScript<Long> redisScript = new DefaultRedisScript<>(luaScript, Long.class);
        Long result = stringRedisTemplate.execute(redisScript, Collections.singletonList(key), value, String.valueOf(expire));

        return result.equals(Long.valueOf(1));
    }

    /**
     * 释放锁
     *
     * @param key   锁的键
     * @param value 锁的值
     * @return 是否成功释放锁
     */
    public boolean releaseLock(String key, String value) {
        String luaScript = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        RedisScript<Long> redisScript = new DefaultRedisScript<>(luaScript, Long.class);
        Long result = stringRedisTemplate.execute(redisScript, Collections.singletonList(key), value);

        return result.equals(Long.valueOf(1));
    }
}
