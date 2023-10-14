package space.atnibam.gateway.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import space.atnibam.common.core.utils.ServletUtils;

import static space.atnibam.common.core.constant.ResultConstants.NOT_FOUND_MSG;
import static space.atnibam.common.core.constant.ResultConstants.SERVER_ERROR_MSG;

/**
 * @ClassName: GatewayExceptionHandler
 * @Description: 网关异常处理器，用于拦截和处理在网关级别发生的所有异常。
 * @Author: AtnibamAitay
 * @CreateTime: 2023-10-13 00:13
 **/
@Order(-1)
@Configuration
public class GatewayExceptionHandler implements ErrorWebExceptionHandler {
    /**
     * 日志记录工具
     */
    private static final Logger log = LoggerFactory.getLogger(GatewayExceptionHandler.class);

    /**
     * 处理函数，用以处理服务器的异常，并返回对应的错误信息
     *
     * @param exchange 当前web交互对象
     * @param ex       抛出的异常
     * @return Mono<Void> 返回一个Mono执行流，不返回任何结果
     */
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        // 获取服务器响应对象
        ServerHttpResponse response = exchange.getResponse();

        // 如果响应已经提交，直接返回异常
        if (exchange.getResponse().isCommitted()) {
            return Mono.error(ex);
        }

        String msg;

        // 根据异常类型设置返回消息
        if (ex instanceof NotFoundException) {
            msg = NOT_FOUND_MSG;
        } else if (ex instanceof ResponseStatusException) {
            ResponseStatusException responseStatusException = (ResponseStatusException) ex;
            msg = responseStatusException.getMessage();
        } else {
            msg = SERVER_ERROR_MSG;
        }

        // 记录异常日志，包括完整的异常信息
        log.error("[网关异常处理]请求路径:{},异常信息:{}", exchange.getRequest().getPath(), msg, ex);

        // 写入响应并返回
        return ServletUtils.webFluxResponseWriter(response, msg);
    }
}
