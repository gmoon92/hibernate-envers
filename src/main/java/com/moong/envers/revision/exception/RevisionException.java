package com.moong.envers.revision.exception;

import com.moong.envers.common.exception.BaseException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Rev Exception
 *
 * @author moong
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RevisionException extends BaseException {

    private final String DEFAULT_ERROR_MESSAGE = "Revision exception occurs : %s";

    public RevisionException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
