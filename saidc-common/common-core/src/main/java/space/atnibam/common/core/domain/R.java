package space.atnibam.common.core.domain;

import space.atnibam.common.core.constant.CommonConstants;

import java.io.Serializable;

/**
 * @ClassName: R
 * @Description: 通用响应实体类，包含了常用的响应方法和状态码，可以携带泛型数据
 * @Author: AtnibamAitay
 * @CreateTime: 2023-10-13 14:08
 **/
public class R<T> implements Serializable {
    /**
     * 成功状态码
     */
    public static final int SUCCESS = CommonConstants.SUCCESS;

    /**
     * 失败状态码
     */
    public static final int FAIL = CommonConstants.FAIL;

    /**
     * 序列化ID
     */
    private static final long serialVersionUID = 1L;

    /**
     * 响应代码
     */
    private int code;

    /**
     * 响应信息
     */
    private String msg;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 创建一个表示成功的响应，不带任何数据和信息
     *
     * @param <T> 数据类型
     * @return 包含成功状态码的响应
     */
    public static <T> R<T> ok() {
        return restResult(null, SUCCESS, null);
    }

    /**
     * 创建一个表示成功的响应，带有数据
     *
     * @param data 响应数据
     * @param <T>  数据类型
     * @return 包含成功状态码和数据的响应
     */
    public static <T> R<T> ok(T data) {
        return restResult(data, SUCCESS, null);
    }

    /**
     * 创建一个表示成功的响应，带有数据和消息
     *
     * @param data 响应数据
     * @param msg  响应消息
     * @param <T>  数据类型
     * @return 包含成功状态码、数据和消息的响应
     */
    public static <T> R<T> ok(T data, String msg) {
        return restResult(data, SUCCESS, msg);
    }

    /**
     * 创建一个表示失败的响应，不带任何数据和信息
     *
     * @param <T> 数据类型
     * @return 包含失败状态码的响应
     */
    public static <T> R<T> fail() {
        return restResult(null, FAIL, null);
    }

    /**
     * 创建一个表示失败的响应，带有消息
     *
     * @param msg 响应消息
     * @param <T> 数据类型
     * @return 包含失败状态码和消息的响应
     */
    public static <T> R<T> fail(String msg) {
        return restResult(null, FAIL, msg);
    }

    /**
     * 创建一个表示失败的响应，带有数据
     *
     * @param data 响应数据
     * @param <T>  数据类型
     * @return 包含失败状态码和数据的响应
     */
    public static <T> R<T> fail(T data) {
        return restResult(data, FAIL, null);
    }

    /**
     * 创建一个表示失败的响应，带有数据和消息
     *
     * @param data 响应数据
     * @param msg  响应消息
     * @param <T>  数据类型
     * @return 包含失败状态码、数据和消息的响应
     */
    public static <T> R<T> fail(T data, String msg) {
        return restResult(data, FAIL, msg);
    }

    /**
     * 创建一个表示失败的响应，带有状态码和消息
     *
     * @param code 自定义的错误状态码
     * @param msg  响应消息
     * @param <T>  数据类型
     * @return 包含自定义错误状态码和消息的响应
     */
    public static <T> R<T> fail(int code, String msg) {
        return restResult(null, code, msg);
    }

    /**
     * 封装响应结果
     *
     * @param data 响应数据
     * @param code 响应代码
     * @param msg  响应消息
     * @param <T>  数据类型
     * @return 封装后的响应结果
     */
    private static <T> R<T> restResult(T data, int code, String msg) {
        R<T> apiResult = new R<>();
        apiResult.setCode(code);
        apiResult.setData(data);
        apiResult.setMsg(msg);
        return apiResult;
    }

    /**
     * 判断响应是否为错误
     *
     * @param ret 响应实体
     * @param <T> 数据类型
     * @return 如果是错误则返回true，否则false
     */
    public static <T> Boolean isError(R<T> ret) {
        return !isSuccess(ret);
    }

    /**
     * 判断响应是否为成功
     *
     * @param ret 响应实体
     * @param <T> 数据类型
     * @return 如果是成功则返回true，否则false
     */
    public static <T> Boolean isSuccess(R<T> ret) {
        return R.SUCCESS == ret.getCode();
    }

    /**
     * 获取响应代码
     *
     * @return 响应代码
     */
    public int getCode() {
        return code;
    }

    /**
     * 设置响应代码
     *
     * @param code 响应代码
     */
    public void setCode(int code) {
        this.code = code;
    }

    /**
     * 获取响应消息
     *
     * @return 响应消息
     */
    public String getMsg() {
        return msg;
    }

    /**
     * 设置响应消息
     *
     * @param msg 响应消息
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }

    /**
     * 获取响应数据
     *
     * @return 响应数据
     */
    public T getData() {
        return data;
    }

    /**
     * 设置响应数据
     *
     * @param data 响应数据
     */
    public void setData(T data) {
        this.data = data;
    }
}