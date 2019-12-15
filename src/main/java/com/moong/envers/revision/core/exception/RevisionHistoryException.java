package com.moong.envers.revision.core.exception;

import com.moong.envers.common.exception.BaseException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Rev Exception
 *
 * @author moong
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RevisionHistoryException extends BaseException {

    private final String DEFAULT_ERROR_MESSAGE = "Revision exception occurs : %s";

    public RevisionHistoryException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
