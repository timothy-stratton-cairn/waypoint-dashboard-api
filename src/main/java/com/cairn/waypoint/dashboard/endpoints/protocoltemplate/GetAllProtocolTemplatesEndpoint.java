package com.cairn.waypoint.dashboard.endpoints.protocoltemplate;


import com.cairn.waypoint.dashboard.endpoints.protocoltemplate.dto.ProtocolTemplateListDto;
import com.cairn.waypoint.dashboard.endpoints.protocoltemplate.mapper.ProtocolTemplateMapper;
import com.cairn.waypoint.dashboard.service.data.ProtocolTemplateDataService;
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
@Tag(name = "Protocol Template")
public class GetAllProtocolTemplatesEndpoint {

  public static final String PATH = "/api/protocol-template";

  private final ProtocolTemplateDataService protocolTemplateDataService;

  public GetAllProtocolTemplatesEndpoint(ProtocolTemplateDataService protocolTemplateDataService) {
    this.protocolTemplateDataService = protocolTemplateDataService;
  }

  @GetMapping(PATH)
  @PreAuthorize("hasAnyAuthority('SCOPE_protocol.template.full', 'SCOPE_admin.full')")
  @Operation(
      summary = "Retrieves all protocol templates.",
      description = "Retrieves all protocol templates. Requires the `protocol.template.full` permission.",
      security = @SecurityRequirement(name = "oAuth2JwtBearer"),
      responses = {
          @ApiResponse(responseCode = "200",
              content = {@Content(mediaType = "application/json",
                  schema = @Schema(implementation = ProtocolTemplateListDto.class))}),
          @ApiResponse(responseCode = "401", description = "Unauthorized",
              content = {@Content(schema = @Schema(hidden = true))}),
          @ApiResponse(responseCode = "403", description = "Forbidden",
              content = {@Content(schema = @Schema(hidden = true))})})
  public ResponseEntity<ProtocolTemplateListDto> getAllProtocolTemplates(Principal principal) {
    log.info("User [{}] is Retrieving All Protocol Templates", principal.getName());
    return ResponseEntity.ok(
        ProtocolTemplateListDto.builder()
            .protocolTemplates(
                this.protocolTemplateDataService.getAllProtocolTemplates().stream()
                    .map(ProtocolTemplateMapper.INSTANCE::toDto)
                    .toList())
            .build()
    );
  }

}
