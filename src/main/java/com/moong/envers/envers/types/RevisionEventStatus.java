package com.moong.envers.envers.types;

import lombok.Getter;

@Getter
public enum RevisionEventStatus {
    WAIT, ERROR, NOT_SUITABLE, SUITABLE
}
