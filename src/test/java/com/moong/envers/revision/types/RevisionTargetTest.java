package com.moong.envers.revision.types;

import com.moong.envers.global.domain.BaseTrackingEntity;
import com.moong.envers.global.vo.EntityCompareVO;
import com.moong.envers.member.domain.Member;
import com.moong.envers.revision.utils.RevisionConverter;
import com.moong.envers.revision.vo.compare.MemberCompareVO;
import com.moong.envers.team.domain.Team;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class RevisionTargetTest {

    @Test
    void testGetCompareVO() {
        Team web1 = Team.newTeam("web1");
        Member moon = Member.newMember("member1", "pa$$word", web1);
        MemberCompareVO compareVO1 = RevisionConverter.convertTo(moon, MemberCompareVO.class);
        MemberCompareVO compareVO2 = (MemberCompareVO) RevisionTarget.MEMBER.getCompareVO(moon);

        log.info("compareVO1 {}", compareVO1);
        assertThat(compareVO1.getName())
                .isEqualTo(compareVO2.getName())
                .isEqualTo(moon.getName());

        assertThat(compareVO1)
                .isEqualTo(compareVO2);
    }

    private <T extends BaseTrackingEntity, R extends EntityCompareVO> R convertTo(T entity, Class<R> compareVOClass) {
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