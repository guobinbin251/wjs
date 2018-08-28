package com.android.wjs.exception;

/**
 * Created by Andy Guo on 2017/5/15.
 */

public class JsonErrorException extends Exception {

    private static final long serialVersionUID = 8177270157475316752L;
    /**
     * 错误编码
     */
    private int errorCode;

    public JsonErrorException(String msg) {
        super(msg);
    }

    public JsonErrorException(String msg, int code) {
        super(msg);
        this.setErrorCode(code);
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
}
