package com.cairn.waypoint.dashboard.endpoints.protocoltemplate;

import com.cairn.waypoint.dashboard.endpoints.ErrorMessage;
import com.cairn.waypoint.dashboard.endpoints.protocoltemplate.dto.AssociatedStepTemplatesListDto;
import com.cairn.waypoint.dashboard.endpoints.protocoltemplate.dto.ProtocolStepTemplateDto;
import com.cairn.waypoint.dashboard.endpoints.protocoltemplate.dto.ProtocolTemplateDetailsDto;
import com.cairn.waypoint.dashboard.endpoints.protocoltemplate.dto.StepTemplateCategoryDto;
import com.cairn.waypoint.dashboard.entity.ProtocolTemplate;
import com.cairn.waypoint.dashboard.service.data.ProtocolTemplateDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Tag(name = "Protocol Template")
public class GetProtocolTemplateByIdEndpoint {

  public static final String PATH = "/api/protocol-template/{protocolTemplateId}";

  private final ProtocolTemplateDataService protocolTemplateDataService;

  public GetProtocolTemplateByIdEndpoint(ProtocolTemplateDataService protocolTemplateDataService) {
    this.protocolTemplateDataService = protocolTemplateDataService;
  }

  @GetMapping(PATH)
  @PreAuthorize("hasAnyAuthority('SCOPE_protocol.template.full', 'SCOPE_admin.full')")
  @Operation(
      summary = "Retrieves a protocol template by it's ID.",
      description = "Retrieves a protocol template by it's ID. Requires the `protocol.template.full` permission.",
      security = @SecurityRequirement(name = "oAuth2JwtBearer"),
      responses = {
          @ApiResponse(responseCode = "200",
              content = {@Content(mediaType = "application/json",
                  schema = @Schema(implementation = ProtocolTemplateDetailsDto.class))}),
          @ApiResponse(responseCode = "401", description = "Unauthorized",
              content = {@Content(schema = @Schema(hidden = true))}),
          @ApiResponse(responseCode = "403", description = "Forbidden",
              content = {@Content(mediaType = "application/json",
                  schema = @Schema(implementation = ErrorMessage.class))}),
          @ApiResponse(responseCode = "404", description = "Not Found",
              content = {@Content(mediaType = "application/json",
                  schema = @Schema(implementation = ErrorMessage.class))})})
  public ResponseEntity<?> getProtocolTemplateById(@PathVariable Long protocolTemplateId,
      Principal principal) {
    log.info("User [{}] is Retrieving Protocol Template with ID [{}]", principal.getName(),
        protocolTemplateId);

    final ResponseEntity<?>[] response = new ResponseEntity<?>[1];
    this.protocolTemplateDataService.getProtocolTemplateById(protocolTemplateId)
        .ifPresentOrElse(
            returnedProtocolTemplate -> response[0] = generateSuccessResponse(
                returnedProtocolTemplate),
            () -> response[0] = generateFailureResponse(protocolTemplateId)
        );

    return response[0];
  }

  public ResponseEntity<ProtocolTemplateDetailsDto> generateSuccessResponse(
      ProtocolTemplate returnedProtocolTemplate) {
    return ResponseEntity.ok(
        ProtocolTemplateDetailsDto.builder()
            .id(returnedProtocolTemplate.getId())
            .name(returnedProtocolTemplate.getName())
            .description(returnedProtocolTemplate.getDescription())
            .associatedSteps(AssociatedStepTemplatesListDto.builder()
                .steps(returnedProtocolTemplate.getProtocolTemplateSteps().stream()
                    .map(protocolStepTemplate -> ProtocolStepTemplateDto.builder()
                        .id(protocolStepTemplate.getStepTemplate().getId())
                        .name(protocolStepTemplate.getStepTemplate().getName())
                        .description(protocolStepTemplate.getStepTemplate().getDescription())
                        .category(StepTemplateCategoryDto.builder()
                            .id(protocolStepTemplate.getStepTemplate().getCategory().getId())
                            .name(protocolStepTemplate.getStepTemplate().getCategory().getName())
                            .description(protocolStepTemplate.getStepTemplate().getCategory()
                                .getDescription())
                            .build())
                        .build())
                    .collect(Collectors.toList()))
                .build())
            .build());
  }

  public ResponseEntity<ErrorMessage> generateFailureResponse(Long protocolTemplateId) {
    log.info("Protocol Template with ID [{}] not found", protocolTemplateId);
    return new ResponseEntity<>(
        ErrorMessage.builder()
            .path(PATH)
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.NOT_FOUND.value())
            .error("Protocol Template with ID [" + protocolTemplateId + "] not found")
            .build(),
        HttpStatus.NOT_FOUND
    );
  }

}
