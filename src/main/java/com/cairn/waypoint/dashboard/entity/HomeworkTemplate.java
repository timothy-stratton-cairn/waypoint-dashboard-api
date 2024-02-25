package com.cairn.waypoint.dashboard.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.Set;
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
@Table(name = "homework_template")
public class HomeworkTemplate extends BaseEntity {

  private String name;
  private String description;

  @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
  @JoinTable(name = "homework_template_homework_question",
      joinColumns = @JoinColumn(name = "homework_template_id", referencedColumnName = "id"),
      inverseJoinColumns = @JoinColumn(name = "homework_question_id", referencedColumnName = "id"))
  private Set<HomeworkQuestion> homeworkQuestions;
}
