package com.moong.envers.global.vo;

import com.moong.envers.revision.vo.compare.ApplyFormCompareVO;
import com.moong.envers.revision.vo.compare.ApproveCompareVO;
import com.moong.envers.revision.vo.compare.MemberCompareVO;
import com.moong.envers.revision.vo.compare.TeamCompareVO;

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
 * @see MemberCompareVO
 * @see TeamCompareVO
 * @see ApplyFormCompareVO
 * @see ApproveCompareVO
 * @author moong
 */
public interface EntityCompareVO {
}
