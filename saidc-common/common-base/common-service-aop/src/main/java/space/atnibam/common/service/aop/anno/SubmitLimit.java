package space.atnibam.common.service.aop.anno;

import java.lang.annotation.*;

/**
 * @ClassName: SubmitLimit
 * @Description: 提交限制注解，用于防止用户重复提交。定义了等待时间、自定义请求头和自定义提示消息。
 * @Author: AtnibamAitay
 * @CreateTime: 2023-10-19 15:40
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface SubmitLimit {

    /**
     * 指定时间内不可重复提交（仅相对上一次发起请求时间差），单位毫秒
     *
     * @return 返回设置的等待时间，默认为1000毫秒
     */
    int waitTime() default 1000;

    /**
     * 指定请求头部key，可以组合生成签名
     *
     * @return 返回设置的请求头部key，默认为空数组
     */
    String[] customerHeaders() default {};

    /**
     * 自定义重复提交提示语
     *
     * @return 返回设置的提示信息，默认为空字符串
     */
    String customerTipMsg() default "";
}
