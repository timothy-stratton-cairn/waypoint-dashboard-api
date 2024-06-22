package com.cairn.waypoint.dashboard.dto.mail;

import com.cairn.waypoint.dashboard.dto.mail.enumeration.MailRequestEnum;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MailRequestQueueDto {

  private MailRequestEnum requestType;
  private String recipient;
  @Builder.Default
  private List<String> attachments = new ArrayList<>();
  private LinksDto links;
  private TemplateParametersDto parameters;
}
