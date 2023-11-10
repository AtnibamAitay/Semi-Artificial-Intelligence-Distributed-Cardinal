package space.atnibam.common.redis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName: RedisService
 * @Description: 提供用于操作Redis的工具服务，包含了对Redis各种数据结构的基础操作方法
 * @Author: AtnibamAitay
 * @CreateTime: 2023-10-15 14:30
 **/
@SuppressWarnings(value = {"unchecked", "rawtypes"})
@Component
public class RedisService {

    @Autowired
    public RedisTemplate redisTemplate;

    /**
     * 缓存基本类型的数据，例如：String、Integer、实体类等
     *
     * @param key   缓存数据使用的key
     * @param value 需要缓存的值
     */
    public <T> void setCacheObject(final String key, final T value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 缓存基本类型的数据，例如：String、Integer、实体类等，并设置其过期时间
     *
     * @param key      缓存数据使用的key
     * @param value    需要缓存的值
     * @param timeout  过期时间
     * @param timeUnit 时间单位
     */
    public <T> void setCacheObject(final String key, final T value, final Long timeout, final TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }

    /**
     * 为指定的key设置过期时间
     *
     * @param key     需要设置过期时间的key
     * @param timeout 过期时间
     * @return 设置是否成功
     */
    public boolean expire(final String key, final long timeout) {
        return expire(key, timeout, TimeUnit.SECONDS);
    }

    /**
     * 为指定的key设置过期时间
     *
     * @param key     需要设置过期时间的key
     * @param timeout 过期时间
     * @param unit    时间单位
     * @return 设置是否成功
     */
    public boolean expire(final String key, final long timeout, final TimeUnit unit) {
        return redisTemplate.expire(key, timeout, unit);
    }

    /**
     * 获取指定key的过期时间
     *
     * @param key 需要获取过期时间的key
     * @return 过期时间
     */
    public long getExpire(final String key) {
        return redisTemplate.getExpire(key);
    }

    /**
     * 判断指定key是否存在
     *
     * @param key 需要检查的key
     * @return true:存在; false:不存在
     */
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 获取缓存中的基本对象
     *
     * @param key 缓存数据的key
     * @return 缓存中的对象
     */
    public <T> T getCacheObject(final String key) {
        ValueOperations<String, T> operation = redisTemplate.opsForValue();
        return operation.get(key);
    }

    /**
     * 从缓存中删除指定key的对象
     *
     * @param key 需要删除的对象的key
     * @return 删除是否成功
     */
    public boolean deleteObject(final String key) {
        return redisTemplate.delete(key);
    }

    /**
     * 从缓存中删除一组对象
     *
     * @param collection 需要删除的对象的集合
     * @return 删除是否成功
     */
    public boolean deleteObject(final Collection collection) {
        return redisTemplate.delete(collection) > 0;
    }

    /**
     * 将List数据缓存到Redis中
     *
     * @param key      用于缓存数据的key
     * @param dataList 需要缓存的数据
     * @return 缓存的对象数量
     */
    public <T> long setCacheList(final String key, final List<T> dataList) {
        Long count = redisTemplate.opsForList().rightPushAll(key, dataList);
        return count == null ? 0 : count;
    }

    /**
     * 获取缓存中的List对象
     *
     * @param key 用于获取数据的key
     * @return 缓存中的对象
     */
    public <T> List<T> getCacheList(final String key) {
        return redisTemplate.opsForList().range(key, 0, -1);
    }

    /**
     * 缓存Set数据结构
     *
     * @param key     用于缓存数据的key
     * @param dataSet 需要缓存的数据
     * @return 缓存数据的对象
     */
    public <T> BoundSetOperations<String, T> setCacheSet(final String key, final Set<T> dataSet) {
        BoundSetOperations<String, T> setOperation = redisTemplate.boundSetOps(key);
        Iterator<T> it = dataSet.iterator();
        while (it.hasNext()) {
            setOperation.add(it.next());
        }
        return setOperation;
    }

    /**
     * 获取缓存的Set数据结构
     *
     * @param key 用于获取数据的key
     * @return 缓存中的对象
     */
    public <T> Set<T> getCacheSet(final String key) {
        return redisTemplate.opsForSet().members(key);
    }

    /**
     * 缓存Map数据结构
     *
     * @param key     用于缓存数据的key
     * @param dataMap 需要缓存的数据
     */
    public <T> void setCacheMap(final String key, final Map<String, T> dataMap) {
        if (dataMap != null) {
            redisTemplate.opsForHash().putAll(key, dataMap);
        }
    }

    /**
     * 获取缓存的Map数据结构
     *
     * @param key 用于获取数据的key
     * @return 缓存中的对象
     */
    public <T> Map<String, T> getCacheMap(final String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * 往Hash数据结构中存入数据
     *
     * @param key   Redis的key
     * @param hKey  Hash的key
     * @param value 需要存储的值
     */
    public <T> void setCacheMapValue(final String key, final String hKey, final T value) {
        redisTemplate.opsForHash().put(key, hKey, value);
    }

    /**
     * 获取Hash数据结构中的数据
     *
     * @param key  Redis的key
     * @param hKey Hash的key
     * @return Hash中的对象
     */
    public <T> T getCacheMapValue(final String key, final String hKey) {
        HashOperations<String, String, T> opsForHash = redisTemplate.opsForHash();
        return opsForHash.get(key, hKey);
    }

    /**
     * 获取多个Hash数据结构中的数据
     *
     * @param key   Redis的key
     * @param hKeys Hash的key的集合
     * @return Hash对象的集合
     */
    public <T> List<T> getMultiCacheMapValue(final String key, final Collection<Object> hKeys) {
        return redisTemplate.opsForHash().multiGet(key, hKeys);
    }

    /**
     * 删除Hash数据结构中的数据
     *
     * @param key  Redis的key
     * @param hKey Hash的key
     * @return 删除是否成功
     */
    public boolean deleteCacheMapValue(final String key, final String hKey) {
        return redisTemplate.opsForHash().delete(key, hKey) > 0;
    }

    /**
     * 获取符合前缀pattern的key的集合
     *
     * @param pattern 前缀表达式
     * @return 对象的key的集合
     */
    public Collection<String> keys(final String pattern) {
        return redisTemplate.keys(pattern);
    }
}
