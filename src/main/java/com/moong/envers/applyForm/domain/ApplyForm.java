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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.springframework.util.CollectionUtils;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PreRemove;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
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
@EqualsAndHashCode(of = "id")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ApplyForm extends BaseEntity {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apply_member_id", updatable = false, foreignKey = @ForeignKey(name = "fk_apply_member_id"))
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apply_team_id", updatable = false, foreignKey = @ForeignKey(name = "fk_apply_team_id"))
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private Team team;

    /**
     * Hibernate Collection
     * PersistentBag
     * PersistentSet
     * PersistentList
     * <p>
     * Cascade 설정
     * CascadeType.PERSIST 등록만 가능하도록 설정했다.
     * 또한, 다음 하이버네이트 애노테이션으로도 설정 가능하다.
     * @Cascade(value = org.hibernate.annotations.CascadeType.PERSIST)
     *
     * @author moong
     */
    @OneToMany(mappedBy = "applyForm")
    private Set<Approve> approves = new HashSet<>();

    @Enumerated(EnumType.STRING)
    private ApproveStatus status;

    private String content;

    @Builder(access = AccessLevel.PROTECTED)
    private ApplyForm(Member member, Team team, Set<Approve> approves, ApproveStatus status, String content) {
        if (CollectionUtils.isEmpty(approves))
            approves = new HashSet<>();
        this.member = member;
        this.team = team;
        this.approves = approves;
        this.status = status;
        this.content = content;
    }

    @PreRemove
    private void remove() {
        this.getApproves().forEach(approve -> approve.setApplyForm(null));
    }

    /**
     * List, Set, Map 초기화를 할 수 없다.
     *
     * @Singular 의 목적은 값을 넣기 전에 초기화가 아니라
     * 값에 대한 넣을 때 초기화가 된다.
     * approves 를 넣지 않았기 때문에 초기화가 되지 않는다.
     * @author moong
     */
    public static ApplyForm write(Member applyMember, Team applyTeam, String content) {
        return ApplyForm.builder()
                .member(applyMember)
                .team(applyTeam)
                .content(StringUtils.defaultString(content, String.format("%s 부서에 신청합니다.", applyTeam.getName())))
                .status(ApproveStatus.WAIT)
                .build();
    }

    public static ApplyForm write(Member applyMember, Team applyTeam) {
        return write(applyMember, applyTeam, String.format("%s 부서에 신청합니다.", applyTeam.getName()));
    }

    public static List<ApplyForm> write(List<Member> applyMembers, Team applyTeam) {
        return applyMembers.stream()
                .map(applyMember -> write(applyMember, applyTeam))
                .collect(Collectors.toList());
    }


    public ApplyForm changeApproveStatus(ApproveStatus status) {
        this.status = status;
        return this;
    }

    public ApplyForm notifyForApprover(Set<Approve> approves) {
        approves.forEach(this::addNotifyForApprover);
        return this;
    }

    /**
     * cascade에 대한 오해
     *
     * cascade는 fk를 관리하지 않는 도메인 객체에서 영속상태를 전파하기 위한 옵션이다.
     * 내가 오해를 했던 부분은 다음과 같다.
     *
     * 신청서 도메인을 insert 할 때
     * 기존에 등록되어 있던 승인자 도메인에 설정된 fk(신청서 id)를 update 하고 싶었다.
     *
     * 1] 팀을 기준으로 승인자들을 조회한다.
     * Set<Approve> approves = approveRepository.findByTeam(team);
     *
     * 2] 조회된 승인자들을 신청서 도메인 객체에 저장한다.
     * approves.forEach(none::addNotifyForApprover);
     *
     * 3] 신청서를 저장한다.
     * applyFormRepository.save(saveApplyForm);
     *
     * 예상된 결과는 applyForm 을 저장하는 순간,
     * 조회한 Approve 의 ApplyForm fk 를 cascade 설정에 의해 update 쿼리가 발생할줄 알았다.
     *
     * 하지만 ApplyForm은 persist 하는 순간 fk 설정과 관련된 update 쿼리가 발생하지 않았다.
     * 임의로 다음과 같이 관계를 설정했고, 이는 Cascade 옵션과는 무관하다.
     * 트랜잭션이 끝이나면 dirty checking 이 되도록 설정하였다.
     * @author moong
     * */
    public ApplyForm addNotifyForApprover(Approve approve) {
        if (approve.getApplyForm() != null) {
            approve.getApplyForm().getApproves()
                    .remove(approve);
        }
        approve.setApplyForm(this);
        this.approves.add(approve);
        return this;
    }
}
