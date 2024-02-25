package com.cairn.waypoint.dashboard.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
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
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "protocol_step_template")
public class StepTemplate extends BaseEntity {

  private String name;
  private String description;

  @JoinColumn(name = "linked_step_task_id")
  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
  private StepTask linkedTask;

  @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
  @JoinTable(name = "step_template_homework_template",
      joinColumns = @JoinColumn(name = "protocol_step_template_id", referencedColumnName = "id"),
      inverseJoinColumns = @JoinColumn(name = "homework_template_id", referencedColumnName = "id"))
  private HomeworkTemplate linkedHomeworkTemplate;

  @JoinColumn(name = "parent_protocol_template_id", nullable = false)
  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
  private ProtocolTemplate parentProtocolTemplate;
}
