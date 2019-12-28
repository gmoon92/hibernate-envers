package com.moong.envers.revision.types;

import com.moong.envers.common.domain.BaseEntity;
import com.moong.envers.common.vo.EntityCompareVO;
import com.moong.envers.member.domain.Member;
import com.moong.envers.member.vo.MemberCompareVO;
import com.moong.envers.revision.core.utils.RevisionConverter;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;

@Slf4j
class RevisionTargetTest {

    @Test
    void testOfCompareVO() {
        Member entity = Member.builder()
                .name("member1")
                .age(1)
                .build();
        MemberCompareVO compareVO1 = RevisionConverter.ofCompareVO(entity, MemberCompareVO.class);
        MemberCompareVO compareVO2 = (MemberCompareVO) RevisionTarget.MEMBER.ofCompareVO(entity);

        log.info("compareVO1 {}", compareVO1);
        Assertions.assertThat(compareVO1.getName())
                .isEqualTo(compareVO2.getName())
                .isEqualTo(entity.getName());

        Assertions.assertThat(compareVO1)
                .isEqualTo(compareVO2);
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