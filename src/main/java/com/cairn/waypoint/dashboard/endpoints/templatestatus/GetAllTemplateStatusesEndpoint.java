package com.cairn.waypoint.dashboard.endpoints.templatestatus;

import com.cairn.waypoint.dashboard.endpoints.templatestatus.dto.TemplateStatusDto;
import com.cairn.waypoint.dashboard.endpoints.templatestatus.dto.TemplateStatusListDto;
import com.cairn.waypoint.dashboard.entity.enumeration.TemplateStatusEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.security.Principal;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Tag(name = "Protocol Step Status")
public class GetAllTemplateStatusesEndpoint {

  public static final String PATH = "/api/protocol-template/template-status";

  @GetMapping(PATH)
  @PreAuthorize("hasAnyAuthority('SCOPE_protocol.template.full', 'SCOPE_admin.full')")
  @Operation(
      summary = "Retrieves all template statuses.",
      description = "Retrieves all template statuses. Requires the `protocol.template.full` permission.",
      security = @SecurityRequirement(name = "oAuth2JwtBearer"),
      responses = {
          @ApiResponse(responseCode = "200",
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = TemplateStatusListDto.class))}),
          @ApiResponse(responseCode = "401", description = "Unauthorized",
              content = {@Content(schema = @Schema(hidden = true))}),
          @ApiResponse(responseCode = "403", description = "Forbidden",
              content = {@Content(schema = @Schema(hidden = true))})})
  public ResponseEntity<TemplateStatusListDto> getAllTemplateStatuses(Principal principal) {
    log.info("User [{}] is Retrieving All Template Statuses", principal.getName());
    return ResponseEntity.ok(
        TemplateStatusListDto.builder()
            .statuses(Stream.of(TemplateStatusEnum.values())
                .map(templateStatusEnum -> TemplateStatusDto.builder()
                    .id(templateStatusEnum.getInstance().getId())
                    .status(templateStatusEnum.name())
                    .description(templateStatusEnum.getInstance().getDescription())
                    .build())
                .toList())
            .build()
    );
  }

}
