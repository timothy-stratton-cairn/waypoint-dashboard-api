package com.cairn.waypoint.dashboard.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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
@Table(name = "protocol_step_template_category")
public class TemplateCategory extends BaseEntity {

  private String name;
  private String description;

  @JoinColumn(name = "parent_protocol_step_template_category_id")
  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
  private TemplateCategory parentCategory;

  private Long responsibleUserId;
  private Long responsibleRoleId;
  private Long accountableUserId;
  private Long accountableRoleId;
  private Long consultedUserId;
  private Long consultedRoleId;
  private Long informedUserId;
  private Long informedRoleId;
}
