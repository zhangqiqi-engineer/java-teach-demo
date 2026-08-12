package com.example.teach.common;

/**
 * 业务异常：Service 层抛出，由全局异常处理器统一转换成 ApiResult
 */
public class BizException extends RuntimeException {

    private final int code;

    public BizException(String message) {
        this(400, message);
    }

    public BizException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
