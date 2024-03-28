package com.cairn.waypoint.dashboard.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
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
@EqualsAndHashCode(callSuper = true, exclude = "parentHomeworkQuestion")
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "homework_question_expected_response")
public class ExpectedResponse extends BaseEntity {

  private String name;
  private String description;
  private Integer ordinalIndex;

  @JoinColumn(name = "parent_homework_question_id", nullable = false)
  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
  private HomeworkQuestion parentHomeworkQuestion;
}
