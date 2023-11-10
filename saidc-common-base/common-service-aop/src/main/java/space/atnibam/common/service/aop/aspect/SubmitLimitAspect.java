package space.atnibam.common.service.aop.aspect;

import org.apache.commons.codec.digest.DigestUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import space.atnibam.common.core.domain.R;
import space.atnibam.common.core.exception.base.BaseException;
import space.atnibam.common.core.utils.JacksonUtils;
import space.atnibam.common.core.utils.text.StringUtils;
import space.atnibam.common.redis.service.RedisLockService;
import space.atnibam.common.service.aop.anno.SubmitLimit;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Objects;

import static space.atnibam.common.core.enums.ResultCode.*;
import static space.atnibam.common.redis.constant.RedisConstants.DEFAULT_LOCK_VALUE;
import static space.atnibam.common.redis.constant.RedisConstants.REDIS_SEPARATOR;

/**
 * @ClassName: SubmitLimitAspect
 * @Description: 防止重复提交切面类，通过redis实现锁机制，避免同一用户在短时间内对同一接口进行重复请求
 * @Author: AtnibamAitay
 * @CreateTime: 2023-10-19 15:41:46
 **/
@Order(1)
@Aspect
@Component
public class SubmitLimitAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(SubmitLimitAspect.class);

    /**
     * 应用名称，通过spring.application.name获取
     */
    @Value("${spring.application.name}")
    private String applicationName;

    /**
     * Redis分布式锁服务
     */
    @Resource
    private RedisLockService redisLockService;

    /**
     * 方法调用环绕拦截，当注解为SubmitLimit时触发
     *
     * @param joinPoint 切点
     * @return 执行结果
     */
    @Around(value = "@annotation(space.atnibam.common.service.aop.anno.SubmitLimit)")
    public Object doAround(ProceedingJoinPoint joinPoint) {
        HttpServletRequest request = getHttpServletRequest();
        if (Objects.isNull(request)) {
            throw new BaseException(PARAM_IS_BLANK);
        }

        // 获取注解配置的参数
        SubmitLimit submitLimit = getSubmitLimit(joinPoint);
        // 组合生成key，通过key实现加锁和解锁
        String lockKey = buildSubmitLimitKey(joinPoint, request, submitLimit.customerHeaders());
        // 尝试在指定的时间内加锁
        boolean lock = redisLockService.tryLock(lockKey, DEFAULT_LOCK_VALUE, Duration.ofMillis(submitLimit.waitTime()));
        if (!lock) {
            String tipMsg = StringUtils.isEmpty(submitLimit.customerTipMsg()) ? REQUEST_REPEAT.getMessage() : submitLimit.customerTipMsg();
            return R.failure(REQUEST_REPEAT, tipMsg);
        }

        try {
            // 继续执行后续流程
            return execute(joinPoint);
        } finally {
            // 执行完毕之后，手动将锁释放
            redisLockService.releaseLock(lockKey, DEFAULT_LOCK_VALUE);
        }
    }

    /**
     * 执行任务，并处理异常
     *
     * @param joinPoint 切点
     * @return 执行结果
     */
    private Object execute(ProceedingJoinPoint joinPoint) {
        try {
            return joinPoint.proceed();
        } catch (Exception e) {
            throw new BaseException();
        } catch (Throwable e) {
            LOGGER.error("业务处理发生异常，错误信息：", e);
            throw new BaseException(BUSINESS_ERROR);
        }
    }

    /**
     * 获取请求对象
     *
     * @return HttpServletRequest 请求对象
     */
    private HttpServletRequest getHttpServletRequest() {
        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes sra = (ServletRequestAttributes) ra;
        return sra.getRequest();
    }

    /**
     * 获取注解值
     *
     * @param joinPoint 切点
     * @return SubmitLimit 注解实例
     */
    private SubmitLimit getSubmitLimit(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        return method.getAnnotation(SubmitLimit.class);
    }

    /**
     * 根据请求信息，生成锁的Key
     * 生成规则：项目名+接口名+方法名+请求参数签名（对请求头部参数+请求body参数，取SHA1值）
     *
     * @param joinPoint       切点
     * @param request         HttpServletRequest 请求对象
     * @param customerHeaders 自定义请求头部参数
     * @return 锁的Key
     */
    private String buildSubmitLimitKey(JoinPoint joinPoint, HttpServletRequest request, String[] customerHeaders) {
        // 请求参数=请求头部+请求body
        String requestHeader = getRequestHeader(request, customerHeaders);
        String requestBody = getRequestBody(joinPoint.getArgs());
        String requestParamSign = DigestUtils.sha1Hex(requestHeader + requestBody);
        return new StringBuilder()
                .append(applicationName)
                .append(REDIS_SEPARATOR)
                .append(joinPoint.getSignature().getDeclaringType().getSimpleName())
                .append(REDIS_SEPARATOR)
                .append(joinPoint.getSignature().getName())
                .append(REDIS_SEPARATOR)
                .append(requestParamSign)
                .toString();
    }

    /**
     * 获取指定请求头部参数
     *
     * @param request         HttpServletRequest 请求对象
     * @param customerHeaders 自定义请求头部参数
     * @return 拼接后的请求头部参数
     */
    private String getRequestHeader(HttpServletRequest request, String[] customerHeaders) {
        if (Objects.isNull(customerHeaders)) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (String headerKey : customerHeaders) {
            sb.append(request.getHeader(headerKey));
        }
        return sb.toString();
    }

    /**
     * 获取请求body参数，将其转换为JSON字符串
     *
     * @param args 请求的参数列表
     * @return 拼接后的请求参数
     */
    private String getRequestBody(Object[] args) {
        if (Objects.isNull(args)) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (Object arg : args) {
            if (arg instanceof HttpServletRequest
                    || arg instanceof HttpServletResponse
                    || arg instanceof MultipartFile
                    || arg instanceof BindResult
                    || arg instanceof MultipartFile[]
                    || arg instanceof ModelMap
                    || arg instanceof Model
                    || arg instanceof byte[]) {
                continue;
            }
            sb.append(JacksonUtils.toJson(arg));
        }
        return sb.toString();
    }
}
