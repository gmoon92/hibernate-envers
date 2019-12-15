package com.moong.envers.revision.vo;

import com.moong.envers.revision.types.RevisionTarget;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
public class RevisionListVO {

    private SearchVO search;

    @Getter
    public static class SearchVO {

        private LocalDateTime startDt;

        private LocalDateTime endDt;

        private RevisionTarget revisionTarget;

        private SearchType searchType;

        private String searchText;

        public enum SearchType {
            EMPTY, USER_NAME, TARGET_TEAM_NAME, TARGET_MEMBER_NAME
        }
    }

    @Getter
    public static class DataVO implements Serializable {
        private final long serialVersionUID = 4214996561651068387L;

        private Long rev;

        private String revDate;

        private String memberId;

        private String memberName;

        private RevisionTarget revisionTarget;

        private String targetId;

        private String targetTeamName;

        private String targetMemberName;

        @QueryProjection
        public DataVO(Long rev, String revDate, String memberId, String memberName, RevisionTarget revisionTarget, String targetId, String targetTeamName, String targetMemberName) {
            this.rev = rev;
            this.revDate = revDate;
            this.memberId = memberId;
            this.memberName = memberName;
            this.revisionTarget = revisionTarget;
            this.targetId = targetId;
            this.targetTeamName = targetTeamName;
            this.targetMemberName = targetMemberName;
        }
    }


}
