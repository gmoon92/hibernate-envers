package com.moong.envers.member.vo;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(exclude = "id")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberVO {
    private Long id;
    private String name;

    @Builder
    private MemberVO(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
