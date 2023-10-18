package space.atnibam.common.core.utils;

import com.alibaba.fastjson2.JSON;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import reactor.core.publisher.Mono;
import space.atnibam.common.core.constant.CommonConstants;
import space.atnibam.common.core.domain.R;
import space.atnibam.common.core.utils.text.Convert;
import space.atnibam.common.core.utils.text.StringUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: ServletUtils
 * @Description: 提供请求相关的实用方法集，如获取参数、请求头，判断是否为Ajax请求等。
 * @Author: atnibamaitay
 * @CreateTime: 2023-10-13 00:54
 **/
public class ServletUtils {
    /**
     * 获取请求中指定名称的参数，并以字符串形式返回。
     *
     * @param name 参数名
     * @return 以String形式返回的参数值
     */
    public static String getParameter(String name) {
        return getRequest().getParameter(name);
    }

    /**
     * 获取请求中指定名称的参数，并以字符串形式返回。如果该参数不存在，则返回默认值。
     *
     * @param name         参数名
     * @param defaultValue 默认值
     * @return 以String形式返回的参数值
     */
    public static String getParameter(String name, String defaultValue) {
        return Convert.toStr(getRequest().getParameter(name), defaultValue);
    }

    /**
     * 获取请求中指定名称的参数，并转化为Integer类型返回。
     *
     * @param name 参数名
     * @return 以Integer形式返回的参数值
     */
    public static Integer getParameterToInt(String name) {
        return Convert.toInt(getRequest().getParameter(name));
    }

    /**
     * 获取请求中指定名称的参数，并转化为Integer类型返回。如果该参数不存在，则返回默认值。
     *
     * @param name         参数名
     * @param defaultValue 默认值
     * @return 以Integer形式返回的参数值
     */
    public static Integer getParameterToInt(String name, Integer defaultValue) {
        return Convert.toInt(getRequest().getParameter(name), defaultValue);
    }

    /**
     * 获取请求中指定名称的参数，并转化为Boolean类型返回。
     *
     * @param name 参数名
     * @return 以Boolean形式返回的参数值
     */
    public static Boolean getParameterToBool(String name) {
        return Convert.toBool(getRequest().getParameter(name));
    }

    /**
     * 获取请求中指定名称的参数，并转化为Boolean类型返回。如果该参数不存在，则返回默认值。
     *
     * @param name         参数名
     * @param defaultValue 默认值
     * @return 以Boolean形式返回的参数值
     */
    public static Boolean getParameterToBool(String name, Boolean defaultValue) {
        return Convert.toBool(getRequest().getParameter(name), defaultValue);
    }

    /**
     * 获取所有请求参数，返回一个不可修改的Map，键是参数名，值是参数值数组。
     *
     * @param request 请求对象{@link ServletRequest}
     * @return 包含所有参数的Map
     */
    public static Map<String, String[]> getParams(ServletRequest request) {
        final Map<String, String[]> map = request.getParameterMap();
        return Collections.unmodifiableMap(map);
    }

    /**
     * 获取所有请求参数，返回一个Map，键是参数名，值是由数组元素组成的字符串，用逗号分隔。
     *
     * @param request 请求对象{@link ServletRequest}
     * @return 包含所有参数的Map
     */
    public static Map<String, String> getParamMap(ServletRequest request) {
        Map<String, String> params = new HashMap<>();
        for (Map.Entry<String, String[]> entry : getParams(request).entrySet()) {
            params.put(entry.getKey(), StringUtils.join(entry.getValue(), ","));
        }
        return params;
    }

    /**
     * 获取HttpServletRequest对象。
     *
     * @return 当前请求的HttpServletRequest对象，如果获取失败，则返回null
     */
    public static HttpServletRequest getRequest() {
        try {
            return (HttpServletRequest) getRequestAttributes().getRequest();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取HttpServletResponse对象。
     *
     * @return 当前请求的HttpServletResponse对象，如果获取失败，则返回null
     */
    public static HttpServletResponse getResponse() {
        try {
            return (HttpServletResponse) getRequestAttributes().getResponse();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取HttpSession对象。
     *
     * @return 当前请求的HttpSession对象
     */
    public static HttpSession getSession() {
        return getRequest().getSession();
    }

    /**
     * 获取ServletRequestAttributes对象。
     *
     * @return 当前请求的ServletRequestAttributes对象，如果获取失败，则返回null
     */
    public static ServletRequestAttributes getRequestAttributes() {
        try {
            RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
            return (ServletRequestAttributes) attributes;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 从HttpRequest中获取指定名称的头部信息，并进行URL解码。
     *
     * @param request HttpRequest对象
     * @param name    头部信息的名称
     * @return 头部信息的值，如果不存在或者在解码过程中发生错误，则返回空字符串
     */
    public static String getHeader(HttpServletRequest request, String name) {
        String value = request.getHeader(name);
        if (StringUtils.isEmpty(value)) {
            return StringUtils.EMPTY;
        }
        return urlDecode(value);
    }

    /**
     * 获取HttpRequest中所有的头部信息，返回一个不区分大小写的LinkedHashMap。
     *
     * @param request HttpRequest对象
     * @return 包含所有头部信息的Map，键是头部名称，值是头部值
     */
    public static Map<String, String> getHeaders(HttpServletRequest request) {
        Map<String, String> map = new LinkedCaseInsensitiveMap<>();
        Enumeration<String> enumeration = request.getHeaderNames();
        if (enumeration != null) {
            while (enumeration.hasMoreElements()) {
                String key = enumeration.nextElement();
                String value = request.getHeader(key);
                map.put(key, value);
            }
        }
        return map;
    }

    /**
     * 将指定字符串渲染到客户端，响应格式为JSON，字符集为UTF-8。
     *
     * @param response HttpServletResponse对象
     * @param string   待渲染的字符串
     */
    public static void renderString(HttpServletResponse response, String string) {
        try {
            response.setStatus(200);
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            response.getWriter().print(string);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断当前请求是否为Ajax异步请求。
     *
     * @param request HttpRequest对象
     * @return 如果是Ajax异步请求，则返回true，否则返回false
     */
    public static boolean isAjaxRequest(HttpServletRequest request) {
        String accept = request.getHeader("accept");
        if (accept != null && accept.contains("application/json")) {
            return true;
        }

        String xRequestedWith = request.getHeader("X-Requested-With");
        if (xRequestedWith != null && xRequestedWith.contains("XMLHttpRequest")) {
            return true;
        }

        String uri = request.getRequestURI();
        if (StringUtils.inStringIgnoreCase(uri, ".json", ".xml")) {
            return true;
        }

        String ajax = request.getParameter("__ajax");
        return StringUtils.inStringIgnoreCase(ajax, "json", "xml");
    }

    /**
     * 对指定的字符串进行URL编码。
     *
     * @param str 待编码的字符串
     * @return 编码后的字符串，如果在编码过程中发生错误，则返回空字符串
     */
    public static String urlEncode(String str) {
        try {
            return URLEncoder.encode(str, CommonConstants.UTF8);
        } catch (UnsupportedEncodingException e) {
            return StringUtils.EMPTY;
        }
    }

    /**
     * 对指定的字符串进行URL解码。
     *
     * @param str 待解码的字符串
     * @return 解码后的字符串，如果在解码过程中发生错误，则返回空字符串
     */
    public static String urlDecode(String str) {
        try {
            return URLDecoder.decode(str, CommonConstants.UTF8);
        } catch (UnsupportedEncodingException e) {
            return StringUtils.EMPTY;
        }
    }

    /**
     * 设置WebFlux模型响应。默认的HTTP状态码为200，响应状态码为自定义的失败状态码。
     *
     * @param response ServerHttpResponse对象
     * @param value    响应内容
     * @return Mono<Void> 对象
     */
    public static Mono<Void> webFluxResponseWriter(ServerHttpResponse response, Object value) {
        return webFluxResponseWriter(response, HttpStatus.OK, value, CommonConstants.FAIL);
    }

    /**
     * 设置WebFlux模型响应。默认的HTTP状态码为200。
     *
     * @param response ServerHttpResponse对象
     * @param value    响应内容
     * @param code     自定义的响应状态码
     * @return Mono<Void> 对象
     */
    public static Mono<Void> webFluxResponseWriter(ServerHttpResponse response, Object value, int code) {
        return webFluxResponseWriter(response, HttpStatus.OK, value, code);
    }

    /**
     * 设置WebFlux模型响应。
     *
     * @param response ServerHttpResponse对象
     * @param status   HTTP状态码
     * @param value    响应内容
     * @param code     自定义的响应状态码
     * @return Mono<Void> 对象
     */
    public static Mono<Void> webFluxResponseWriter(ServerHttpResponse response, HttpStatus status, Object value, int code) {
        return webFluxResponseWriter(response, MediaType.APPLICATION_JSON_VALUE, status, value, code);
    }

    /**
     * 设置WebFlux模型响应。
     *
     * @param response    ServerHttpResponse对象
     * @param contentType Content-Type类型
     * @param status      HTTP状态码
     * @param value       响应内容
     * @param code        自定义的响应状态码
     * @return Mono<Void> 对象
     */
    public static Mono<Void> webFluxResponseWriter(ServerHttpResponse response, String contentType, HttpStatus status, Object value, int code) {
        response.setStatusCode(status);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, contentType);
        R<?> result = R.failure(code, value.toString());
        DataBuffer dataBuffer = response.bufferFactory().wrap(JSON.toJSONString(result).getBytes());
        return response.writeWith(Mono.just(dataBuffer));
    }
}
