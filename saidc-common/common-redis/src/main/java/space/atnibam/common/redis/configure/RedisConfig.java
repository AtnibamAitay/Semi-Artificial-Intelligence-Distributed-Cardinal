package space.atnibam.common.redis.configure;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @ClassName: RedisConfig
 * @Description: Redis的配置类，使用自定义的FastJson2JsonRedisSerializer来序列化和反序列化redis的key值
 * @Author: AtnibamAitay
 * @CreateTime: 2023-10-15 14:55
 **/
@Configuration
@EnableCaching
@AutoConfigureBefore(RedisAutoConfiguration.class)
public class RedisConfig extends CachingConfigurerSupport {

    /**
     * 配置RedisTemplate，设置序列化工具
     *
     * @param connectionFactory redis连接工厂
     * @return 返回配置好的RedisTemplate实例
     */
    @Bean
    @SuppressWarnings(value = {"unchecked", "rawtypes"})
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        FastJson2JsonRedisSerializer serializer = new FastJson2JsonRedisSerializer(Object.class);

        // 使用StringRedisSerializer来序列化和反序列化redis的key值
        template.setKeySerializer(new StringRedisSerializer());
        // 设置值的序列化方式为FastJson2JsonRedisSerializer
        template.setValueSerializer(serializer);

        // Hash的key也采用StringRedisSerializer的序列化方式
        template.setHashKeySerializer(new StringRedisSerializer());
        // 设置Hash值的序列化方式为FastJson2JsonRedisSerializer
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();

        return template;
    }
}
