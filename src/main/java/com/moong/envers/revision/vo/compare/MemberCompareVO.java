package com.moong.envers.revision.vo.compare;

import com.moong.envers.global.vo.EntityCompareVO;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Setter
@Getter
@EqualsAndHashCode(exclude = "id")
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class MemberCompareVO implements EntityCompareVO {

    private Long id;

    private String name;

    private String password;

    @Builder
    private MemberCompareVO(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
