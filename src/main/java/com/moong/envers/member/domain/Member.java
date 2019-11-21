package com.moong.envers.member.domain;

import com.moong.envers.common.domain.BaseEntity;
import com.moong.envers.team.domain.Team;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Audited
@Entity
@Getter
@ToString(exclude = {"team"}) @EqualsAndHashCode(of = {"id", "name"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String password;

    private Integer age;

    /**
     * @apiNote @NotAudited VS @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
     * 두 애노테이션의 공통점은 aud 테이블에서 제외할 때 사용한다.
     *
     * 1) @NotAudited : Evners에 의해 생성된 member_aud 테이블 자체에서 컬럼을 제외할 때 사용.
     *
     * 2) RelationTargetAuditMode.NOT_AUDITED : 대상이 되는 엔티티가 @Audited 엔티티가 아닌 경우 사용한다.
     *  일반적으로 Hibernate envers는 @Audited 애노테이션이 선언된 엔티티(Member)의 모든 필드를 감지 대상으로 간주한다.
     *  따라서 연관 관계를 맺은 엔티티(Team)에 대해서도 감지하고 추적하게 되는데, 문제는 Team 엔티티도 @Audited 엔티티로 설정되지 않았다면 에러가 발생한다.
     *  하지만 NOT_AUDITED 옵션을 통해 @Audited 애노테이션이 선언되지 않는 Team 엔티티를 Member_aud 테이블에서 추적할 수 있다.
     *  참고로 Team은 FK로 추적된다.
     *
     * Team 자체의 FK 까지 추적할 의미가 없다고 판단하여 @NotAudited 사용했다.
     * @author moong
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEAM_ID")
    @NotAudited
    private Team team;

    @Builder
    private Member(String name, String password, Integer age, Team team) {
        this.name = name;
        this.password = password;
        this.age = age;
        this.team = team;
    }
}
