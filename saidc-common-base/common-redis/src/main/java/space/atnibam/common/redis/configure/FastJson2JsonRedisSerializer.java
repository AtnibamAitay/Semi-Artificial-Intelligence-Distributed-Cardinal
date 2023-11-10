package space.atnibam.common.redis.configure;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.filter.Filter;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import space.atnibam.common.core.constant.CommonConstants;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @ClassName: FastJson2JsonRedisSerializer
 * @Description: Redis使用FastJson进行序列化操作
 * @Author: AtnibamAitay
 * @CreateTime: 2023-10-15 16:00
 **/
public class FastJson2JsonRedisSerializer<T> implements RedisSerializer<T> {
    /**
     * 设置默认字符集为UTF-8
     */
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    /**
     * 设置json读取时的自动类型过滤器，只接受白名单内的类型
     */
    static final Filter AUTO_TYPE_FILTER = JSONReader.autoTypeFilter(CommonConstants.JSON_WHITELIST_STR);

    /**
     * 泛型类Class对象
     */
    private final Class<T> clazz;

    /**
     * 构造函数
     *
     * @param clazz 泛型类Class对象
     */
    public FastJson2JsonRedisSerializer(Class<T> clazz) {
        super();
        this.clazz = clazz;
    }

    /**
     * 实现RedisSerializer接口中的序列化方法
     *
     * @param t 要序列化的对象
     * @return 序列化后的字节数组
     * @throws SerializationException 序列化异常
     */
    @Override
    public byte[] serialize(T t) throws SerializationException {
        if (t == null) {
            return new byte[0];
        }
        return JSON.toJSONString(t, JSONWriter.Feature.WriteClassName).getBytes(DEFAULT_CHARSET);
    }

    /**
     * 实现RedisSerializer接口中的反序列化方法
     *
     * @param bytes 要反序列化的字节数组
     * @return 反序列化后的对象
     * @throws SerializationException 反序列化异常
     */
    @Override
    public T deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null || bytes.length <= 0) {
            return null;
        }
        String str = new String(bytes, DEFAULT_CHARSET);

        return JSON.parseObject(str, clazz, AUTO_TYPE_FILTER);
    }
}
