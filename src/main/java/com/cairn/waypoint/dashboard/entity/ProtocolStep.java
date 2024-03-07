package com.cairn.waypoint.dashboard.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@Entity
@SuperBuilder
@EqualsAndHashCode(callSuper = false, exclude = "parentProtocol")
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "protocol_step")
public class ProtocolStep extends BaseEntity {

  private String name;
  private String description;
  private String notes;
  private Integer ordinalIndex;

  @JoinColumn(name = "step_status_id")
  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
  private StepStatus status;

  @JoinColumn(name = "step_template_id", nullable = false)
  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
  private StepTemplate template;

  @JoinColumn(name = "linked_step_task_id")
  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
  private StepTask linkedTask;

  @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
  @JoinTable(name = "protocol_step_homework",
      joinColumns = @JoinColumn(name = "protocol_step_id", referencedColumnName = "id"),
      inverseJoinColumns = @JoinColumn(name = "homework_id", referencedColumnName = "id"))
  private Homework linkedHomework;

  @JoinColumn(name = "parent_protocol_id", nullable = false)
  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
  private Protocol parentProtocol;
}
