package com.cairn.waypoint.dashboard.endpoints.protocoltemplate.dto;

import com.cairn.waypoint.dashboard.entity.enumeration.ProtocolStatusEnum;
import com.cairn.waypoint.dashboard.entity.enumeration.RecurrenceTypeEnum;
import com.cairn.waypoint.dashboard.entity.enumeration.TemplateCategoryEnum;
import com.cairn.waypoint.dashboard.entity.enumeration.TemplateStatusEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.LinkedHashSet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProtocolTemplateDetailsDto {

  private String name;
  private String description;
  private String status;
  private String templateCategoryValue;
  private Integer defaultDueByInYears;
  private Integer defaultDueByInMonths;
  private Integer defaultDueByInDays;
  private String recurrenceTypeValue;
  private String defaultTriggeringStatusValue;
  private Integer defaultReoccurInYears;
  private Integer defaultReoccurInMonths;
  private Integer defaultReoccurInDays;
  private LinkedHashSet<Long> associatedStepTemplateIds;

  @JsonIgnore
  public TemplateStatusEnum getTemplateStatus() {
    try {
      return TemplateStatusEnum.valueOf(status);
    } catch (NullPointerException | IllegalArgumentException e) {
      return null;
    }
  }

  @JsonIgnore
  public RecurrenceTypeEnum getDefaultRecurrenceType() {
    if (recurrenceTypeValue != null) {
      return RecurrenceTypeEnum.valueOf(recurrenceTypeValue);
    } else {
      return null;
    }
  }

  @JsonIgnore
  public ProtocolStatusEnum getDefaultTriggeringStatus() {
    if (defaultTriggeringStatusValue != null) {
      return ProtocolStatusEnum.valueOf(defaultTriggeringStatusValue);
    } else {
      return null;
    }
  }

  @JsonIgnore
  public TemplateCategoryEnum getTemplateCategory() {
    if (templateCategoryValue != null) {
      return TemplateCategoryEnum.valueOf(templateCategoryValue);
    } else {
      return null;
    }
  }
}
