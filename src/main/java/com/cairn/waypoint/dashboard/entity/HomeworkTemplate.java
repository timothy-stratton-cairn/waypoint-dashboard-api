package com.cairn.waypoint.dashboard.entity;

import com.cairn.waypoint.dashboard.entity.converter.TemplateStatusConverter;
import com.cairn.waypoint.dashboard.entity.enumeration.TemplateStatusEnum;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
@Table(name = "homework_template")
public class HomeworkTemplate extends BaseEntity {

  private String name;
  private String description;

  private Boolean multiResponse;

  @OneToMany(mappedBy = "homeworkTemplate",
      cascade = {CascadeType.MERGE, CascadeType.PERSIST})
  private Set<HomeworkTemplateLinkedHomeworkQuestion> homeworkQuestions;

  @Builder.Default
  @Column(name = "status_id")
  @Convert(converter = TemplateStatusConverter.class)
  private TemplateStatusEnum status = TemplateStatusEnum.INACTIVE;
}
