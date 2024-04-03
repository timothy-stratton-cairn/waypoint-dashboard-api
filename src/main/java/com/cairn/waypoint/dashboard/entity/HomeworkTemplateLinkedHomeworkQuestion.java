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
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLRestriction;

@Data
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@SQLRestriction("active=1")
@Table(name = "homework_template_linked_homework_question")
public class HomeworkTemplateLinkedHomeworkQuestion extends BaseEntity {

  @ManyToOne
  @JoinColumn(name = "homework_template_id", nullable = false)
  private HomeworkTemplate homeworkTemplate;

  @JoinColumn(name = "homework_question_id", nullable = false)
  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
  private HomeworkQuestion homeworkQuestion;

  private Integer ordinalIndex;

  public boolean equals(Object o) {
    if (!(o instanceof HomeworkTemplateLinkedHomeworkQuestion)) {
      return false;
    } else if (!((HomeworkTemplateLinkedHomeworkQuestion) o).getHomeworkTemplate().getId()
        .equals(this.homeworkTemplate.getId())) {
      return false;
    } else {
      return ((HomeworkTemplateLinkedHomeworkQuestion) o).getHomeworkQuestion().getId()
          .equals(this.homeworkQuestion.getId());
    }
  }

  public int hashCode() {
    return this.homeworkTemplate.getId().hashCode() + this.homeworkQuestion.getId().hashCode();
  }
}
