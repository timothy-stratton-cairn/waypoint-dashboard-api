package com.cairn.waypoint.dashboard.entity;

import com.cairn.waypoint.dashboard.entity.converter.StepStatusConverter;
import com.cairn.waypoint.dashboard.entity.enumeration.StepStatusEnum;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLRestriction;

@Data
@Entity
@SuperBuilder
@EqualsAndHashCode(callSuper = false, exclude = {"parentProtocol", "notes", "attachments"})
@NoArgsConstructor
@AllArgsConstructor
@SQLRestriction("active=1")
@Table(name = "protocol_step")
public class ProtocolStep extends BaseEntity {

  private String name;
  private String description;

  @OneToMany(fetch = FetchType.EAGER, mappedBy = "protocolStep", cascade = {CascadeType.MERGE,
      CascadeType.PERSIST})
  private Set<ProtocolStepNote> notes;

  @OneToMany(fetch = FetchType.EAGER, mappedBy = "protocolStep", cascade = {CascadeType.MERGE,
      CascadeType.PERSIST})
  private Set<ProtocolStepAttachment> attachments;

  private Integer ordinalIndex;

  @Column(name = "step_status_id")
  @Convert(converter = StepStatusConverter.class)
  private StepStatusEnum status;

  @JoinColumn(name = "step_category_id")
  @OneToOne(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
  private StepCategory category;

  @JoinColumn(name = "step_template_id", nullable = false)
  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
  private StepTemplate template;

  @JoinColumn(name = "linked_step_task_id")
  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
  private StepTask linkedTask;

  public Set<ProtocolStepNote> getNotes() {
    if (notes == null) {
      notes = new HashSet<>();
    }
    return notes;
  }

  public Set<ProtocolStepAttachment> getAttachments() {
    if (attachments == null) {
      attachments = new HashSet<>();
    }
    return attachments;
  }

  @JoinColumn(name = "parent_protocol_id", nullable = false)
  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
  private Protocol parentProtocol;

}
