package com.cairn.waypoint.dashboard.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLRestriction;

@Data
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@SQLRestriction("active=1")
@EqualsAndHashCode(callSuper = true, exclude = {"protocolTemplate", "stepTemplate"})
@Table(name = "protocol_template_linked_step_template")
public class ProtocolTemplateLinkedStepTemplate extends BaseEntity {

  @ManyToOne
  @JoinColumn(name = "protocol_template_id", nullable = false)
  private ProtocolTemplate protocolTemplate;

  @JoinColumn(name = "step_template_id", nullable = false)

  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
  private StepTemplate stepTemplate;

  private Integer ordinalIndex;
}
