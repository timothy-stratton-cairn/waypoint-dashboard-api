package com.cairn.waypoint.dashboard.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "goal_template_linked_protocol_templates")
public class HouseholdLinkedProtocolGoalTemplates extends BaseEntity {


  @ManyToOne
  @JoinColumn(name = "protocol_template_id", nullable = false)
  private ProtocolTemplate protocolTemplate;

  @ManyToOne
  @JoinColumn(name = "goal_template_id",nullable = false)
  private GoalTemplate goalTemplate;

}
