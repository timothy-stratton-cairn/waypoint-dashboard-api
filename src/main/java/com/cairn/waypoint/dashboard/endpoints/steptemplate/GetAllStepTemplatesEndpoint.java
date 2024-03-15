package com.cairn.waypoint.dashboard.endpoints.steptemplate;

import com.cairn.waypoint.dashboard.endpoints.steptemplate.dto.StepTemplateListDto;
import com.cairn.waypoint.dashboard.endpoints.steptemplate.mapper.StepTemplateMapper;
import com.cairn.waypoint.dashboard.service.StepTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.security.Principal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Tag(name = "Protocol Step Template")
public class GetAllStepTemplatesEndpoint {

  public static final String PATH = "/api/protocol-step-template";

  private final StepTemplateService stepTemplateService;

  public GetAllStepTemplatesEndpoint(StepTemplateService stepTemplateService) {
    this.stepTemplateService = stepTemplateService;
  }

  @GetMapping(PATH)
  @PreAuthorize("hasAnyAuthority('SCOPE_protocol.step.template.full', 'SCOPE_admin.full')")
  @Operation(
      summary = "Retrieves all step templates.",
      description = "Retrieves all step templates. Requires the `protocol.step.template.full` permission.",
      security = @SecurityRequirement(name = "oAuth2JwtBearer"),
      responses = {
          @ApiResponse(responseCode = "200",
              content = {@Content(mediaType = "application/json",
                  schema = @Schema(implementation = StepTemplateListDto.class))}),
          @ApiResponse(responseCode = "401", description = "Unauthorized",
              content = {@Content(schema = @Schema(hidden = true))}),
          @ApiResponse(responseCode = "403", description = "Forbidden",
              content = {@Content(schema = @Schema(hidden = true))})})
  public ResponseEntity<StepTemplateListDto> getAllStepTemplates(Principal principal) {
    log.info("User [{}] is Retrieving All Step Templates", principal.getName());
    return ResponseEntity.ok(
        StepTemplateListDto.builder()
            .stepTemplates(
                this.stepTemplateService.getAllStepTemplates().stream()
                    .map(StepTemplateMapper.INSTANCE::toDto)
                    .toList())
            .build()
    );
  }

}
