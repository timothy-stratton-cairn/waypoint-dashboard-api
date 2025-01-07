package com.cairn.waypoint.dashboard.entity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "household_goals")
public class HouseholdGoal extends BaseEntity {

  private String name;
  private String description;
  private Long householdId;

  @ManyToOne
  @JoinColumn(name = "goal_template_id", referencedColumnName = "id", insertable = false, updatable = false)
  private GoalTemplate goalTemplate;
}
