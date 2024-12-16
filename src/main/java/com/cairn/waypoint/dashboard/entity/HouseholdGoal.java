package com.cairn.waypoint.dashboard.entity;
import jakarta.persistence.Entity;
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
@Table(name = "household_goal")
public class HouseholdGoal extends BaseEntity {

  private String name;
  private String description;
  private Long goalTemplateId;
  private Long householdId;
}
