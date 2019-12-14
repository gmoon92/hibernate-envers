package com.moong.envers.revision.types;

import lombok.Getter;

@Getter
public enum RevisionEventStatus {
    WAIT, ERROR, NOT_DISPLAY, DISPLAY
}
