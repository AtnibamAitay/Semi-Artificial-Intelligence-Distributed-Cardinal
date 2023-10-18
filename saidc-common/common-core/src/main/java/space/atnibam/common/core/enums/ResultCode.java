package space.atnibam.common.core.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * @ClassName: ResultCode
 * @Description: 状态码枚举，包含API响应状态码，提示信息，以及HTTP状态码等
 * @Author: AtnibamAitay
 * @CreateTime: 2023-10-17 15:02
 **/
@Getter
public enum ResultCode {
    /**
     * 成功状态码
     */
    SUCCESS(200, "成功", HttpStatus.OK),

    /**
     * 失败状态码
     */
    FAIL(400, "失败", HttpStatus.BAD_REQUEST),

    /**
     * 参数错误：1001-1999
     */
    PARAM_IS_INVALID(1001, "参数无效", HttpStatus.BAD_REQUEST),
    PARAM_IS_BLANK(1002, "参数为空", HttpStatus.BAD_REQUEST),
    PARAM_TYPE_ERROR(1003, "参数类型错误", HttpStatus.BAD_REQUEST),
    PARAM_MISSING(1004, "参数缺失", HttpStatus.BAD_REQUEST),

    /**
     * 用户错误：2001-2999
     */
    USER_NOT_LOGGED_IN(2001, "用户未登录，请先登录", HttpStatus.UNAUTHORIZED),
    USER_LOGIN_ERROR(2002, "用户登录异常", HttpStatus.UNAUTHORIZED),
    USER_ACCOUNT_FORBIDDEN(2003, "账号已被禁用", HttpStatus.UNAUTHORIZED),
    USER_HAS_EXISTED(2004, "用户已存在", HttpStatus.CONFLICT),
    USER_VERIFY_ERROR(2005, "验证码校验失败，请重新获取", HttpStatus.UNAUTHORIZED),
    USER_INSERT_ERROR(2006, "用户数据插入异常", HttpStatus.CONFLICT),
    USER_NOT_EXIST_BY_CODE(2007, "账号不存在", HttpStatus.NOT_FOUND),
    USER_UPDATE_ERROR(2008, "用户数据更新异常", HttpStatus.CONFLICT),
    PHONE_CODE_OOT(2009, "当天手机号发送验证码次数以达到上限，请24小时后重试", HttpStatus.TOO_MANY_REQUESTS),
    SMS_INTERRUPTED_OR_EXECUTION_ERROR(2010, "阿里云服务被中断或执行错误", HttpStatus.BAD_REQUEST),
    ACCOUNT_CANCELLED(2011, "账号已被注销", HttpStatus.FORBIDDEN),
    PHONE_NUM_NON_COMPLIANCE(2012, "手机号不符合规范", HttpStatus.BAD_REQUEST),
    EMAIL_NUM_NON_COMPLIANCE(2013, "邮箱号不符合规范", HttpStatus.BAD_REQUEST),
    USER_INFO_NON_EXIST(2014, "账号信息不存在", HttpStatus.OK),

    /**
     * 服务器内部错误：3001-3300
     */
    INTERNAL_ERROR(3001, "服务器内部错误，请联系开发人员", HttpStatus.INTERNAL_SERVER_ERROR),
    SERVER_BUSY(3002, "服务器繁忙，请稍后重试", HttpStatus.TOO_MANY_REQUESTS),
    MESSAGE_SERVICE_ERROR(3003, "短信服务异常，请稍后重试", HttpStatus.INTERNAL_SERVER_ERROR),

    /**
     * MinIO错误：3301-3500
     */
    MINIO_UPLOAD_ERROR(3301, "上传过程中出错", HttpStatus.INTERNAL_SERVER_ERROR),
    MINIO_SAVE_FILE_INFO_ERROR(3302, "保存文件信息失败", HttpStatus.INTERNAL_SERVER_ERROR),
    MINIO_GET_FILE_ERROR(3303, "获取文件异常", HttpStatus.INTERNAL_SERVER_ERROR),
    MINIO_GET_FILE_BLOCK_ERROR(3304, "获取文件块异常", HttpStatus.INTERNAL_SERVER_ERROR),
    MINIO_CREATE_MERGE_TEMP_FILE_ERROR(3305, "创建合并临时文件出错", HttpStatus.INTERNAL_SERVER_ERROR),
    MINIO_MERGE_FILE_ERROR(3306, "合并文件过程中出错", HttpStatus.INTERNAL_SERVER_ERROR),
    MINIO_MERGE_FILE_CHECK_ERROR(3307, "合并文件校验失败", HttpStatus.INTERNAL_SERVER_ERROR),
    MINIO_MERGE_FILE_CHECK_EXCEPTION(3308, "合并文件校验异常", HttpStatus.INTERNAL_SERVER_ERROR),
    MINIO_DELETE_TEMP_FILE_ERROR(3309, "临时分块文件删除错误", HttpStatus.INTERNAL_SERVER_ERROR),
    MINIO_DELETE_TEMP_MERGE_FILE_ERROR(3310, "临时合并文件删除错误", HttpStatus.INTERNAL_SERVER_ERROR),
    MINIO_CREATE_TEMP_FILE_ERROR(3311, "创建临时分块文件出错", HttpStatus.INTERNAL_SERVER_ERROR),
    MINIO_QUERY_FILE_BLOCK_ERROR(3312, "查询文件分块出错", HttpStatus.INTERNAL_SERVER_ERROR),
    MINIO_MEDIA_FILE_INSERT_ERROR(3313, "媒资文件入库出错", HttpStatus.INTERNAL_SERVER_ERROR);

    /**
     * 状态码
     */
    private final Integer code;

    /**
     * 接口调用提示信息
     */
    private final String message;

    /**
     * http状态码
     */
    private final HttpStatus status;

    /**
     * 构造方法，用于创建枚举实例
     *
     * @param code    状态码
     * @param message 接口调用提示信息
     * @param status  http状态码
     */
    ResultCode(Integer code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }

}
