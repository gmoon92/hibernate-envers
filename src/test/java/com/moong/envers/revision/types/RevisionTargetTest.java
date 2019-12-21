package com.moong.envers.revision.types;

import com.moong.envers.common.domain.BaseEntity;
import com.moong.envers.common.vo.EntityCompareVO;
import com.moong.envers.member.domain.Member;
import com.moong.envers.member.vo.MemberCompareVO;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;

@Slf4j
class RevisionTargetTest {

    @Test
    void testNewInstanceCompareVO() {
        Member entity = Member.builder()
                .name("member1")
                .age(1)
                .build();
        MemberCompareVO compareVO = RevisionTarget.newInstanceCompareVO(entity, MemberCompareVO.class);

        log.info("compareVO {}", compareVO);
        Assertions.assertThat(compareVO.getName())
                .isEqualTo(entity.getName());
    }

    private <T extends BaseEntity, R extends EntityCompareVO> R newInstanceCompareVO(T entity, Class<R> compareVOClass) {
        Object compareVO = null;
        try {
            compareVO = compareVOClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            log.error("{}", e);
        }

        BeanUtils.copyProperties(entity, compareVO);
        return (R) compareVO;
    }
}