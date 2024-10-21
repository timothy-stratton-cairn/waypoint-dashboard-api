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
@EqualsAndHashCode(callSuper = true, exclude = "homework")
@NoArgsConstructor
@AllArgsConstructor
@SQLRestriction("active=1")
@Table(name = "homework_response")
public class HomeworkResponse extends BaseEntity {

  private String response;

  @JoinColumn(name = "homework_question_id", nullable = false)
  @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
  private HomeworkQuestion homeworkQuestion;

  @JoinColumn(name = "homework_id", nullable = false)
  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
  private Homework homework;

  private String fileGuid;
  private Integer ordinalIndex;
  
  @JoinColumn(name = "category_id", nullable =false )
  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
  private HomeworkCategory category;
  
  private Boolean active;

  //usage: new HomeworkResponse().getResponse(QuestionTypeEnum.DATE.getDataType());
  @SuppressWarnings("unchecked")
  public <T> T getResponse(T returnType) {
    return (T) this.homeworkQuestion.getQuestionType().createInstance(this.response);
  }
}
