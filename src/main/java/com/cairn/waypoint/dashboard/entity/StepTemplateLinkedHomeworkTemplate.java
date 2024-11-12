package com.cairn.waypoint.dashboard.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

// TODO TO BE DELETED
@Data
//@Entity   
@SuperBuilder
@EqualsAndHashCode(callSuper = true, exclude = {"stepTemplate", "homeworkTemplate"})
abstract
//@NoArgsConstructor
//@AllArgsConstructor
//@SQLRestriction("active=1")
//@Table(name = "step_template_linked_homework_template")
public class StepTemplateLinkedHomeworkTemplate extends BaseEntity {
/*
  @ManyToOne
  @JoinColumn(name = "protocol_step_template_id", nullable = false)
  private StepTemplate stepTemplate;

  @JoinColumn(name = "homework_template_id", nullable = false)
  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
  private HomeworkTemplate homeworkTemplate;
  */
}
