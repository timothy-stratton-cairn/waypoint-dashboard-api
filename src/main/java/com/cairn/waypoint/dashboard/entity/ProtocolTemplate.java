package com.cairn.waypoint.dashboard.entity;

import com.cairn.waypoint.dashboard.entity.converter.ProtocolStatusConverter;
import com.cairn.waypoint.dashboard.entity.converter.RecurrenceTypeConverter;
import com.cairn.waypoint.dashboard.entity.converter.TemplateCategoryConverter;
import com.cairn.waypoint.dashboard.entity.converter.TemplateStatusConverter;
import com.cairn.waypoint.dashboard.entity.enumeration.ProtocolStatusEnum;
import com.cairn.waypoint.dashboard.entity.enumeration.RecurrenceTypeEnum;
import com.cairn.waypoint.dashboard.entity.enumeration.TemplateCategoryEnum;
import com.cairn.waypoint.dashboard.entity.enumeration.TemplateStatusEnum;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.LinkedHashSet;
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
@EqualsAndHashCode(callSuper = false, exclude = "protocolTemplateSteps")
@NoArgsConstructor
@AllArgsConstructor
@SQLRestriction("active=1")
@Table(name = "protocol_template")
public class ProtocolTemplate extends BaseEntity {

  private String name;
  private String description;

  @OneToMany(mappedBy = "protocolTemplate",
      cascade = {CascadeType.MERGE, CascadeType.PERSIST})
  private Set<ProtocolTemplateLinkedStepTemplate> protocolTemplateSteps;

  @Column(name = "status_id")
  @Convert(converter = TemplateStatusConverter.class)
  private TemplateStatusEnum status;

  @Column(name = "category_id")
  @Convert(converter = TemplateCategoryConverter.class)
  private TemplateCategoryEnum templateCategory;

  private Integer defaultDueByInYears;
  private Integer defaultDueByInMonths;
  private Integer defaultDueByInDays;

  @Column(name = "default_recurrence_type_id")
  @Convert(converter = RecurrenceTypeConverter.class)
  private RecurrenceTypeEnum defaultRecurrenceType;

  @Column(name = "default_triggering_protocol_status_id")
  @Convert(converter = ProtocolStatusConverter.class)
  private ProtocolStatusEnum defaultTriggeringStatus;

  private Integer defaultReoccurInYears;
  private Integer defaultReoccurInMonths;
  private Integer defaultReoccurInDays;

  public Set<ProtocolTemplateLinkedStepTemplate> getProtocolTemplateSteps() {
    if (protocolTemplateSteps == null) {
      protocolTemplateSteps = new LinkedHashSet<>();
    }
    return protocolTemplateSteps;
  }
}
