package com.moong.envers.global.exception;

public abstract class BaseException extends RuntimeException {

    protected BaseException() {
        super();
    }

    protected BaseException(String message) {
        super(message);
    }

    protected BaseException(String format, Object... args) {
        super(String.format(format, args));
    }

    protected BaseException(Throwable cause, String format, Object... args) {
        super(String.format(format, args), cause);
    }

    protected BaseException(String message, Throwable cause) {
        super(message, cause);
    }

    protected BaseException(Throwable cause) {
        super(cause);
    }

    protected BaseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
