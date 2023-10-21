package space.atnibam.common.redis.utils;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import space.atnibam.common.redis.domain.RedisData;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static space.atnibam.common.redis.constant.RedisConstants.*;

/**
 * @ClassName: CacheClient
 * @Description: Redis操作的工具类，提供基础set和get方法以及解决缓存中常见问题的一些方法：逻辑过期、缓存穿透、缓存击穿。
 * @Author: AtnibamAitay
 * @CreateTime: 2023-10-21 14:47
 **/
@Slf4j
@Component
public class CacheClient {
    /**
     * 用于重建缓存的线程池
     */
    private static final ExecutorService CACHE_REBUILD_EXECUTOR = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    /**
     * StringRedisTemplate是Spring Data Redis模块的一个类，用来简化Redis的操作
     */
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 构造函数，初始化stringRedisTemplate
     */
    public CacheClient(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 向Redis写入数据
     *
     * @param key   数据的键
     * @param value 数据的值
     * @param time  过期时间的值
     * @param unit  过期时间的单位
     */
    public void set(String key, Object value, Long time, TimeUnit unit) {
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(value), time, unit);
    }

    /**
     * 设置逻辑过期，即使数据在Redis中未过期，但是如果超过设定的逻辑过期时间，也视为过期
     *
     * @param key   数据的键
     * @param value 数据的值
     * @param time  过期时间的值
     * @param unit  过期时间的单位
     */
    public void setWithLogicalExpire(String key, Object value, Long time, TimeUnit unit) {
        // 创建一个RedisData对象，用于存放数据和逻辑过期时间
        RedisData redisData = new RedisData();
        redisData.setData(value);
        redisData.setExpireTime(LocalDateTime.now().plusSeconds(unit.toSeconds(time)));
        // 将RedisData对象写入Redis
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(redisData));
    }

    /**
     * 查询数据，解决缓存穿透问题。
     * 缓存穿透指查询一个一定不存在的数据，常通过禁止外部直接查询某个具体的key，或者将查询结果为空也缓存起来，来解决这个问题。
     *
     * @param keyPrefix  key的前缀
     * @param id         查询数据库时使用的id
     * @param type       返回的数据类型
     * @param dbFallback 数据库查询逻辑，由调用者提供
     * @param time       缓存数据的过期时间值
     * @param unit       缓存数据的过期时间单位
     * @param <R>        返回的数据类型
     * @param <ID>       id的类型
     * @return 查询到的数据
     */
    public <R, ID> R queryWithPassThrough(
            String keyPrefix, ID id, Class<R> type, Function<ID, R> dbFallback, Long time, TimeUnit unit) {
        // 构造完整的key
        String key = keyPrefix + id;
        // 从Redis中查询数据
        String json = stringRedisTemplate.opsForValue().get(key);
        // 判断结果是否存在
        if (StrUtil.isNotBlank(json)) {
            // 如果存在，直接返回
            return JSONUtil.toBean(json, type);
        }
        // 判断命中的是否是空值
        if (json != null) {
            // 如果是空值，返回错误信息
            return null;
        }

        // 从数据库中查询数据
        R r = dbFallback.apply(id);
        // 如果查询结果为空
        if (r == null) {
            // 将空值写入Redis，并设置过期时间，防止缓存穿透
            stringRedisTemplate.opsForValue().set(key, "", CACHE_NULL_TTL, TimeUnit.MINUTES);
            // 返回错误信息
            return null;
        }
        // 如果查询结果不为空，将结果写入Redis
        this.set(key, r, time, unit);
        // 返回查询结果
        return r;
    }

    /**
     * 查询数据，解决缓存击穿问题。
     * 缓存击穿指一个热点key在某些时刻突然失效，导致大量的请求都去查询数据库，可以通过设置互斥锁或者逻辑过期解决。
     *
     * @param keyPrefix  key的前缀
     * @param id         查询数据库时使用的id
     * @param type       返回的数据类型
     * @param dbFallback 数据库查询逻辑，由调用者提供
     * @param time       缓存数据的过期时间值
     * @param unit       缓存数据的过期时间单位
     * @param <R>        返回的数据类型
     * @param <ID>       id的类型
     * @return 查询到的数据
     */
    public <R, ID> R queryWithLogicalExpire(
            String keyPrefix, ID id, Class<R> type, Function<ID, R> dbFallback, Long time, TimeUnit unit) {
        String key = keyPrefix + id;
        // 从Redis中查询数据
        String json = stringRedisTemplate.opsForValue().get(key);
        // 判断结果是否存在
        if (StrUtil.isBlank(json)) {
            // 如果不存在，返回错误信息
            return null;
        }
        // 将json数据反序列化为RedisData对象
        RedisData redisData = JSONUtil.toBean(json, RedisData.class);
        // 获取并反序列化保存在RedisData中的data数据
        R r = JSONUtil.toBean((JSONObject) redisData.getData(), type);
        // 获取保存在RedisData中的逻辑过期时间
        LocalDateTime expireTime = redisData.getExpireTime();
        // 判断数据是否已经逻辑过期
        if (expireTime.isAfter(LocalDateTime.now())) {
            // 如果未过期，直接返回结果
            return r;
        }
        // 如果已过期，需要重建缓存
        // 获取互斥锁的key
        String lockKey = LOCK_KEY + id;
        // 尝试获取互斥锁
        boolean isLock = tryLock(lockKey);
        // 判断是否获取锁成功
        if (isLock) {
            // 如果成功，开启一个新的线程来重建缓存
            CACHE_REBUILD_EXECUTOR.submit(() -> {
                try {
                    // 查询数据库
                    R newR = dbFallback.apply(id);
                    // 将查询结果写入Redis，并设置逻辑过期时间
                    this.setWithLogicalExpire(key, newR, time, unit);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    // 释放锁
                    unlock(lockKey);
                }
            });
        }
        // 返回原先过期的信息
        return r;
    }

    /**
     * 查询数据，解决缓存击穿问题。
     * 缓存击穿指一个热点key在某些时刻突然失效，导致大量的请求都去查询数据库，可以通过设置互斥锁或者逻辑过期解决。
     *
     * @param keyPrefix  key的前缀
     * @param id         查询数据库时使用的id
     * @param type       返回的数据类型
     * @param dbFallback 数据库查询逻辑，由调用者提供
     * @param time       缓存数据的过期时间值
     * @param unit       缓存数据的过期时间单位
     * @param <R>        返回的数据类型
     * @param <ID>       id的类型
     * @return 查询到的数据
     */
    public <R, ID> R queryWithMutex(
            String keyPrefix, ID id, Class<R> type, Function<ID, R> dbFallback, Long time, TimeUnit unit) {
        String key = keyPrefix + id;
        // 从Redis中查询数据
        String cacheData = stringRedisTemplate.opsForValue().get(key);
        // 判断结果是否存在
        if (StrUtil.isNotBlank(cacheData)) {
            // 如果存在，直接返回结果
            return JSONUtil.toBean(cacheData, type);
        }
        // 判断命中的是否是空值
        if (cacheData != null) {
            // 如果是空值，返回错误信息
            return null;
        }

        // 获取互斥锁的key
        String lockKey = LOCK_KEY + id;
        R r = null;
        try {
            // 尝试获取互斥锁
            boolean isLock = tryLock(lockKey);
            // 判断是否获取成功
            if (!isLock) {
                // 如果没有获取成功，线程休眠一段时间之后重试整个查询操作
                Thread.sleep(SLEEP_TIME_IN_MILLIS);
                return queryWithMutex(keyPrefix, id, type, dbFallback, time, unit);
            }
            // 如果获取成功，根据id查询数据库
            r = dbFallback.apply(id);
            // 如果查询结果为空
            if (r == null) {
                // 将空值写入Redis，并设置过期时间，防止缓存穿透
                stringRedisTemplate.opsForValue().set(key, "", CACHE_NULL_TTL, TimeUnit.MINUTES);
                // 返回错误信息
                return null;
            }
            // 如果查询结果不为空，将结果写入Redis
            this.set(key, r, time, unit);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            // 释放锁
            unlock(lockKey);
        }
        // 返回查询结果
        return r;
    }

    /**
     * 尝试获取互斥锁
     *
     * @param key 锁的key
     * @return 是否成功获取到锁
     */
    private boolean tryLock(String key) {
        Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent(key, "1", LOCK_TIMEOUT, TimeUnit.SECONDS);
        return BooleanUtil.isTrue(flag);
    }

    /**
     * 释放互斥锁
     *
     * @param key 锁的key
     */
    private void unlock(String key) {
        stringRedisTemplate.delete(key);
    }
}