package com.cairn.waypoint.dashboard.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.LinkedHashSet;
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
@EqualsAndHashCode(callSuper = true, exclude = "stepTemplateLinkedHomeworks")
@NoArgsConstructor
@AllArgsConstructor
@SQLRestriction("active=1")
@Table(name = "protocol_step_template")
public class StepTemplate extends BaseEntity {

  private String name;
  private String description;

  @JoinColumn(name = "linked_step_task_id")
  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
  private StepTask linkedTask;

  @JoinColumn(name = "protocol_step_template_category_id")
  @OneToOne(fetch = FetchType.LAZY)
  private TemplateCategory category;

  @OneToMany(mappedBy = "stepTemplate",
      cascade = {CascadeType.MERGE, CascadeType.PERSIST})
  private Set<StepTemplateLinkedHomeworkTemplate> stepTemplateLinkedHomeworks;

  public Set<StepTemplateLinkedHomeworkTemplate> getStepTemplateLinkedHomeworks() {
    if (stepTemplateLinkedHomeworks == null) {
      stepTemplateLinkedHomeworks = new LinkedHashSet<>();
    }
    return stepTemplateLinkedHomeworks;
  }
}
