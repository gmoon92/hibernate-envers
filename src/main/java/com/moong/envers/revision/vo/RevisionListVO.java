package com.moong.envers.revision.vo;

import com.moong.envers.common.vo.BaseSearchVO;
import com.moong.envers.revision.types.RevisionTarget;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Sort;

import java.io.Serializable;
import java.util.Date;

public class RevisionListVO {

    @Getter @Setter
    private SearchVO searchVO;

    @Getter
    public static class SearchVO extends BaseSearchVO {

        public enum SearchType {
            EMPTY, MEMBER_NAME, TARGET_TEAM_NAME, TARGET_MEMBER_NAME
        }

        private RevisionTarget revisionTarget;

        private SearchType searchType;

        public SearchVO() {
            super(Sort.by(Sort.Direction.DESC, "createdDt"));
        }
    }

    @Getter
    public static class DataVO implements Serializable {
        private final long serialVersionUID = 4214996561651068387L;

        private Long rev;

        private Date revDate;

        private String memberId;

        private String memberName;

        private RevisionTarget revisionTarget;

        private String entityId;

        private String targetTeamName;

        private String targetMemberName;

        @QueryProjection
        public DataVO(Long rev, Date revDate, String memberId, String memberName, RevisionTarget revisionTarget, String entityId, String targetTeamName, String targetMemberName) {
            this.rev = rev;
            this.revDate = revDate;
            this.memberId = memberId;
            this.memberName = memberName;
            this.revisionTarget = revisionTarget;
            this.entityId = entityId;
            this.targetTeamName = targetTeamName;
            this.targetMemberName = targetMemberName;
        }
    }


}
