package com.cairn.waypoint.dashboard.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLRestriction;

@Data
@Entity
@SuperBuilder
@EqualsAndHashCode(callSuper = true, exclude = {"homeworkQuestions",
    "associatedProtocolStep", "homeworkTemplate"})
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "homework")
@SQLRestriction("active=1")
public class Homework extends BaseEntity {

  private String name;
  private String description;
  private Boolean active;


  @OneToMany(mappedBy = "homework", fetch = FetchType.EAGER, cascade = {CascadeType.MERGE,
      CascadeType.PERSIST})
  private Set<HomeworkResponse> homeworkQuestions;

  @JoinColumn(name = "homework_template_id", nullable = false)
  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
  private HomeworkTemplate homeworkTemplate;

  @OneToOne(mappedBy = "homework", cascade = CascadeType.ALL, optional = false)
  private ProtocolStepLinkedHomework protocolStepLinkedHomework;

  private Long assignedHouseholdId;

  public Set<HomeworkResponse> getHomeworkQuestions() {
    return homeworkQuestions.stream()
        .sorted(Comparator.comparing(HomeworkResponse::getOrdinalIndex))
        .collect(Collectors.toCollection(LinkedHashSet::new));
  }

}

