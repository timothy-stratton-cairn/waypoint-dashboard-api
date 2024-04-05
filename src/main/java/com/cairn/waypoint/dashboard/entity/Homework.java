package com.cairn.waypoint.dashboard.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
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
@EqualsAndHashCode(callSuper = true, exclude = {"homeworkQuestions", "associatedUsers",
    "associatedProtocolStep"})
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "homework")
@SQLRestriction("active=1")
public class Homework extends BaseEntity {

  private String name;
  private String description;

  @OneToMany(mappedBy = "homework", cascade = CascadeType.MERGE)
  private Set<HomeworkResponse> homeworkQuestions;

  @JoinColumn(name = "homework_template_id", nullable = false)
  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
  private HomeworkTemplate homeworkTemplate;

  @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
  @JoinTable(name = "protocol_step_homework",
      joinColumns = @JoinColumn(name = "homework_id", referencedColumnName = "id"),
      inverseJoinColumns = @JoinColumn(name = "protocol_step_id", referencedColumnName = "id"))
  private ProtocolStep associatedProtocolStep;

  @OneToMany(mappedBy = "homework", cascade = CascadeType.MERGE)
  private Set<HomeworkUser> associatedUsers;
}

