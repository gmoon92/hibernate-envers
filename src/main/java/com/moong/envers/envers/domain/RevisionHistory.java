package com.moong.envers.envers.domain;

import com.moong.envers.envers.config.RevisionHistoryListener;
import com.moong.envers.envers.types.RevisionEventStatus;
import com.moong.envers.envers.types.RevisionTarget;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;
import org.hibernate.envers.RevisionType;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "rev_history")
@RevisionEntity(RevisionHistoryListener.class)
@Getter @Setter
@ToString(exclude = {"modifiedEntities"})
@EqualsAndHashCode(of = {"id"}, exclude = {"modifiedEntities"})
@NoArgsConstructor
public class RevisionHistory implements Serializable {

    @Id
    @GeneratedValue
    @RevisionNumber
    private Long id;

    /*
     * @RevisionTimestamp LocalDateTime issue
     * https://hibernate.atlassian.net/browse/HHH-10827
     * https://hibernate.atlassian.net/browse/HHH-10828
     * https://hibernate.atlassian.net/browse/HHH-10496
     * */
    @RevisionTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date createdDt;

    private String updatedBy;

    private String updatedByUsername;

    @OneToMany(mappedBy = "revisionHistory",cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
    private Set<RevisionHistoryModified> modifiedEntities = new HashSet<>();

    public void addModifiedEntity(Serializable entityId, RevisionType revisionType, RevisionTarget revisionTarget, RevisionEventStatus eventStatus) {
        modifiedEntities.add(RevisionHistoryModified.builder()
                .revisionHistory(this)
                .entityId(entityId)
                .revisionEventStatus(eventStatus)
                .revisionTarget(revisionTarget)
                .revisionType(revisionType)
                .build());
    }

    public LocalDateTime getRevisionCreatedDt() {
        return LocalDateTime.ofInstant(createdDt.toInstant(), ZoneId.systemDefault());
    }
}
