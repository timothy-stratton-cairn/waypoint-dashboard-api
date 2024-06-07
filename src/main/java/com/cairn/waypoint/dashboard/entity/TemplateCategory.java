package com.cairn.waypoint.dashboard.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SQLRestriction("active=1")
@Table(name = "protocol_step_template_category")
public class TemplateCategory extends BaseEntity {

  private String name;
  private String description;

  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
  @JoinColumn(name = "parent_protocol_step_template_category_id")
  private TemplateCategory parentCategory;

  @OneToMany(fetch = FetchType.EAGER, mappedBy = "parentCategory", cascade = CascadeType.MERGE)
  private Set<TemplateCategory> childCategories;

  private Long responsibleUserId;
  private Long responsibleRoleId;
  private Long accountableUserId;
  private Long accountableRoleId;
  private Long consultedUserId;
  private Long consultedRoleId;
  private Long informedUserId;
  private Long informedRoleId;
}
