package com.moong.envers.common.exception;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Rev Exception
 *
 * @author moong
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RevisionException extends RuntimeException {

    private static final String DEFAULT_ERROR_MESSAGE = "Revision exception occurs : %s";

    public RevisionException(String message) {
        super(String.format(DEFAULT_ERROR_MESSAGE, message));
    }

    public RevisionException(String format, Object... args) {
        super(String.format(format, args));
    }

    public RevisionException(Throwable cause, String format, Object... args) {
        super(String.format(format, args), cause);
    }

    public RevisionException(String message, Throwable cause) {
        super(String.format(DEFAULT_ERROR_MESSAGE, message), cause);
    }

    public RevisionException(Throwable cause) {
        super(cause);
    }

    public RevisionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(String.format(DEFAULT_ERROR_MESSAGE, message), cause, enableSuppression, writableStackTrace);
    }
}
