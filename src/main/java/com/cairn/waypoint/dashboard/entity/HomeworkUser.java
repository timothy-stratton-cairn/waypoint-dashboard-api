package com.cairn.waypoint.dashboard.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "homework_user")
public class HomeworkUser extends BaseEntity {

  @JoinColumn(name = "homework_id", nullable = false)
  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
  private Homework homework;

  @Column(name = "user_id", nullable = false)
  private Long userId;
}
