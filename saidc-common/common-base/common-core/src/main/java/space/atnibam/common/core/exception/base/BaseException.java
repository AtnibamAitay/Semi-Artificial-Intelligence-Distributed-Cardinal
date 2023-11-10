package space.atnibam.common.core.exception.base;

import lombok.Getter;
import lombok.Setter;
import space.atnibam.common.core.enums.ResultCode;

/**
 * @ClassName: BaseException
 * @Description: 基础异常类，用于封装异常信息，包括错误码等。
 * @Author: AtnibamAitay
 * @CreateTime: 2023-10-17 12:57
 **/
@Getter
@Setter
public class BaseException extends RuntimeException {
    /**
     * 序列化版本号，用于在反序列化时验证发送方和接收方是否加载了与序列化兼容的版本的类。
     */
    private static final long serialVersionUID = -1L;

    /**
     * 结果代码，用于标识特定的错误类型。
     */
    private ResultCode resultCode;

    /**
     * 默认构造方法，创建一个无特定结果代码的异常对象。
     */
    public BaseException() {
    }

    /**
     * 带有结果代码的构造方法，创建一个带有特定结果代码的异常对象。
     *
     * @param resultCode 结果代码
     */
    public BaseException(ResultCode resultCode) {
        this.resultCode = resultCode;
    }
}
