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

@Data
@Entity
@SuperBuilder
@EqualsAndHashCode(callSuper = true, exclude = {"stepTemplate", "homeworkTemplate"})
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "step_template_linked_homework_template")
public class StepTemplateLinkedHomeworkTemplate extends BaseEntity {

  @ManyToOne
  @JoinColumn(name = "protocol_step_template_id", nullable = false)
  private StepTemplate stepTemplate;

  @JoinColumn(name = "homework_template_id", nullable = false)
  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
  private HomeworkTemplate homeworkTemplate;
}
