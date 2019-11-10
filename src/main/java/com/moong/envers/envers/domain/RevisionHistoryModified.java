package com.moong.envers.envers.domain;

import com.moong.envers.envers.types.RevisionEventStatus;
import com.moong.envers.envers.types.RevisionTarget;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.envers.RevisionType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "rev_history_modified")
@NoArgsConstructor
@Getter
public class RevisionHistoryModified {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "revision_number")
    private RevisionHistory revision;

    @Column(name = "entity_id", updatable = false, nullable = false)
    private String entityId;

    @Enumerated(EnumType.STRING)
    @Column(name = "target", updatable = false)
    private RevisionTarget revisionTarget;

    @Enumerated(EnumType.STRING)
    @Column(name = "revision_type", updatable = false, nullable = false)
    private RevisionType revisionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_status", nullable = false)
    private RevisionEventStatus revisionEventStatus;

    @Builder
    private RevisionHistoryModified(RevisionHistory revision, Serializable entityId, RevisionTarget revisionTarget, RevisionType revisionType, RevisionEventStatus revisionEventStatus) {
        this.revision = revision;
        this.entityId = revisionTarget.convertToRevisionEntityID(entityId);
        this.revisionTarget = revisionTarget;
        this.revisionType = revisionType;
        this.revisionEventStatus = revisionEventStatus;
    }

}
