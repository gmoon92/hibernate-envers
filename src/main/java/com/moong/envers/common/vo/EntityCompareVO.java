package com.moong.envers.common.vo;

/**
 * BeanUtils.copyProperties 메서드는
 * property 값을 복사할때 내부적으로 세터 메서드를 가지고 복사해준다.
 * 따라서 @Setter 는 BeanUtils.copyProperties 때문에 반드시 필요하다.
 *
 * 또한, RevisionTarget.newInstanceCompareVO 메서드에서
 * Class.newInstance를 통해 compare vo 를 인스턴스 함으로
 * 기본 생성자가 필요하다.
 *
 * @see com.moong.envers.revision.types.RevisionTarget
 * @see com.moong.envers.member.vo.MemberCompareVO
 * @see com.moong.envers.team.vo.TeamCompareVO
 * @see com.moong.envers.applyForm.vo.ApplyFormVO
 * @see com.moong.envers.approve.vo.ApproveVO
 * @author moong
 */
public interface EntityCompareVO {
}
