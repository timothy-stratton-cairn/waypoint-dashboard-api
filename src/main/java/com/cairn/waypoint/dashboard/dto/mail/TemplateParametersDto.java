package com.cairn.waypoint.dashboard.dto.mail;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TemplateParametersDto {

  private String databaseName;
}
