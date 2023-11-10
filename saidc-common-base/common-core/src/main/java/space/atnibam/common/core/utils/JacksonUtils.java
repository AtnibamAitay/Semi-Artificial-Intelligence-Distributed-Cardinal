package space.atnibam.common.core.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;


/**
 * @ClassName: JacksonUtils
 * @Description: 提供了一个基于Jackson库的工具类，用于处理JSON序列化和反序列化的操作。
 * @Author: AtnibamAitay
 * @CreateTime: 2023-10-19 17:16
 **/
public class JacksonUtils {

    /**
     * 日志对象
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(JacksonUtils.class);

    /**
     * Jackson对象映射器，它提供了将Java对象转换为JSON字符串，以及将JSON字符串转换为Java对象的功能
     */
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        // 对象的所有字段全部列入序列化
        objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);

        // 反序列化时，忽略未知的字段
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // 反序列化时，读取不认识的枚举值时，当null值处理
        objectMapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);

        // 序列化时，忽略未知属性
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        // 忽略字段大小写
        objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);

        // 自动关闭处理的输入源（文件、URL、Socket等）
        objectMapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true);

        SimpleModule module = new SimpleModule();
        // 为Long类型和long基本类型注册ToStringSerializer，用于将其序列化为字符串形式
        module.addSerializer(Long.class, ToStringSerializer.instance);
        module.addSerializer(Long.TYPE, ToStringSerializer.instance);

        // 注册自定义模块到映射器中
        objectMapper.registerModule(module);
    }

    /**
     * 将Java对象转换成JSON字符串。
     *
     * @param object 需要转换的对象
     * @return 对象的JSON字符串表示，失败返回null
     */
    public static String toJson(Object object) {
        if (object == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            LOGGER.error("序列化失败", e);
        }
        return null;
    }

    /**
     * 将JSON字符串转换为指定类型的Java对象。
     *
     * @param json JSON字符串
     * @param classOfT 需要转换的目标类型
     * @return 转换后的对象，失败返回null
     */
    public static <T> T fromJson(String json, Class<T> classOfT) {
        if (json == null) {
            return null;
        }
        try {
            return objectMapper.readValue(json, classOfT);
        } catch (Exception e) {
            LOGGER.error("反序列化失败", e);
        }
        return null;
    }

    /**
     * 将JSON字符串转换为指定类型的Java对象。
     *
     * @param json JSON字符串
     * @param typeOfT 需要转换的目标类型信息
     * @return 转换后的对象，失败返回null
     */
    public static <T> T fromJson(String json, Type typeOfT) {
        if (json == null) {
            return null;
        }
        try {
            // 使用对象映射器构造目标类型并进行反序列化
            return objectMapper.readValue(json, objectMapper.constructType(typeOfT));
        } catch (Exception e) {
            LOGGER.error("反序列化失败", e);
        }
        return null;
    }
}