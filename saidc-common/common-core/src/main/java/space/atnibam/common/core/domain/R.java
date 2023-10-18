package space.atnibam.common.core.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import space.atnibam.common.core.enums.ResultCode;

import java.io.Serializable;

/**
 * @ClassName: R
 * @Description: 这是一个通用的结果类，用于包装服务层返回给前端的数据。它包含了状态码，消息以及具体的业务数据。
 * @Author: AtnibamAitay
 * @CreateTime: 2023-10-18 16:26
 **/
@Data
public class R<T> implements Serializable {

    /**
     * 状态码
     */
    private Integer code;

    /**
     * 消息
     */
    private String message;

    /**
     * 具体的业务数据，在序列化为 JSON 时，如果 data 为 null 则忽略该字段
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    /**
     * 默认构造函数
     */
    public R() {
    }

    /**
     * 构造函数
     *
     * @param code    状态码
     * @param message 消息
     * @param data    具体的业务数据
     */
    public R(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 构造函数
     *
     * @param resultCode 状态对象，包含状态码和消息
     * @param data       具体的业务数据
     */
    public R(ResultCode resultCode, T data) {
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
        this.data = data;
    }

    /**
     * 创建一个表示成功的 Result 对象
     *
     * @return 表示成功的 Result 对象
     */
    public static <T> R<T> success() {
        R r = new R();
        r.setResultCode(ResultCode.SUCCESS);
        return r;
    }

    /**
     * 创建一个表示成功的 Result 对象，并携带具体的业务数据
     *
     * @param data 具体的业务数据
     * @return 表示成功的 Result 对象
     */
    public static <T> R<T> success(T data) {
        R r = new R();
        r.setResultCode(ResultCode.SUCCESS);
        r.setData(data);
        return r;
    }

    /**
     * 创建一个表示成功的 Result 对象，并携带状态对象
     *
     * @param resultCode 状态对象，包含状态码和消息
     * @return 表示成功的 Result 对象
     */
    public static <T> R<T> success(ResultCode resultCode) {
        R r = new R();
        r.setResultCode(resultCode);
        return r;
    }

    /**
     * 创建一个表示失败的 Result 对象，并携带状态对象
     *
     * @param resultCode 状态对象，包含状态码和消息
     * @return 表示失败的 Result 对象
     */
    public static <T> R<T> failure(ResultCode resultCode) {
        R r = new R();
        r.setResultCode(resultCode);
        return r;
    }

    /**
     * 创建一个表示失败的 Result 对象，并携带状态对象和具体的业务数据
     *
     * @param resultCode 状态对象，包含状态码和消息
     * @param data       具体的业务数据
     * @return 表示失败的 Result 对象
     */
    public static <T> R<T> failure(ResultCode resultCode, T data) {
        R r = new R();
        r.setResultCode(resultCode);
        r.setData(data);
        return r;
    }

    /**
     * 创建一个表示失败的 Result 对象，并携带状态码和消息
     *
     * @param code    状态码
     * @param message 消息
     * @return 表示失败的 Result 对象
     */
    public static <T> R<T> failure(Integer code, String message) {
        R r = new R();
        r.setCode(code);
        r.setMessage(message);
        return r;
    }

    /**
     * 设置状态码和消息
     *
     * @param resultCode 状态对象，包含状态码和消息
     */
    public void setResultCode(ResultCode resultCode) {
        this.setCode(resultCode.getCode());
        this.setMessage(resultCode.getMessage());
    }
}