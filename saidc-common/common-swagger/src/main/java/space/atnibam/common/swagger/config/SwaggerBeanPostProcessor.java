package space.atnibam.common.swagger.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.servlet.mvc.method.RequestMappingInfoHandlerMapping;
import springfox.documentation.spring.web.plugins.WebFluxRequestHandlerProvider;
import springfox.documentation.spring.web.plugins.WebMvcRequestHandlerProvider;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName: SwaggerBeanPostProcessor
 * @Description: 处理swagger在springboot 2.6.x中不兼容问题的Bean后置处理器
 * @Author: AtnibamAitay
 * @CreateTime: 2023-10-15 00:30
 **/
public class SwaggerBeanPostProcessor implements BeanPostProcessor {
    /**
     * 在Spring容器初始化bean之后，对特定类型的bean进行处理
     *
     * @param bean     初始化后的bean对象
     * @param beanName bean的名称
     * @return 返回处理后的bean对象
     * @throws BeansException 可能抛出的异常
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof WebMvcRequestHandlerProvider || bean instanceof WebFluxRequestHandlerProvider) {
            customizeSpringfoxHandlerMappings(getHandlerMappings(bean));
        }
        return bean;
    }

    /**
     * 自定义Springfox的处理映射
     *
     * @param mappings RequestMappingInfoHandlerMapping的列表
     */
    private <T extends RequestMappingInfoHandlerMapping> void customizeSpringfoxHandlerMappings(List<T> mappings) {
        List<T> copy = mappings.stream().filter(mapping -> mapping.getPatternParser() == null)
                .collect(Collectors.toList());
        mappings.clear();
        mappings.addAll(copy);
    }

    /**
     * 利用反射获取bean中的处理映射列表
     *
     * @param bean 要获取处理映射的bean对象
     * @return 返回处理映射列表
     */
    @SuppressWarnings("unchecked")
    private List<RequestMappingInfoHandlerMapping> getHandlerMappings(Object bean) {
        try {
            Field field = ReflectionUtils.findField(bean.getClass(), "handlerMappings");
            field.setAccessible(true);
            return (List<RequestMappingInfoHandlerMapping>) field.get(bean);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }
}
