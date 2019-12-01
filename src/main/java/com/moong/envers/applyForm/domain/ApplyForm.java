package com.moong.envers.applyForm.domain;

import com.moong.envers.approve.domain.Approve;
import com.moong.envers.approve.types.ApproveStatus;
import com.moong.envers.common.domain.BaseEntity;
import com.moong.envers.member.domain.Member;
import com.moong.envers.team.domain.Team;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Set;

@Audited
@Entity
@Table(indexes = {
          @Index(name = "idx_member_id", columnList = "apply_member_id")
        , @Index(name = "idx_team_id", columnList = "apply_team_id")
        , @Index(name = "idx_apply_date", columnList = "apply_date")
})
@AttributeOverrides( {
        @AttributeOverride(name = "createdDt", column = @Column(name = "apply_date"))
})
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ApplyForm extends BaseEntity {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "apply_member_id", updatable = false, foreignKey = @ForeignKey(name = "fk_apply_member_id"))
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private Member member;

    @ManyToOne
    @JoinColumn(name = "apply_team_id", updatable = false, foreignKey = @ForeignKey(name = "fk_apply_team_id"))
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private Team team;

    @OneToMany(mappedBy = "applyForm")
    private Set<Approve> approves;

    @Enumerated(EnumType.STRING)
    private ApproveStatus status;

    private String content;

    @Builder(access = AccessLevel.PROTECTED)
    private ApplyForm(Member member, Team team, Set<Approve> approves, ApproveStatus status, String content) {
        this.member = member;
        this.team = team;
        this.approves = approves;
        this.status = status;
        this.content = content;
    }

    public static ApplyForm write(Member applyMember, Team applyTeam, String content) {
        return ApplyForm.builder()
                .member(applyMember)
                .team(applyTeam)
                .content(content)
                .status(ApproveStatus.WAIT)
                .build();
    }

    public ApplyForm changeApproveStatus(ApproveStatus status) {
        this.status = status;
        return this;
    }


}
