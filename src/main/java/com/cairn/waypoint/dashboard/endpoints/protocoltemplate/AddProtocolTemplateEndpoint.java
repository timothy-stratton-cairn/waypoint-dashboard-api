package com.cairn.waypoint.dashboard.endpoints.protocoltemplate;

import com.cairn.waypoint.dashboard.endpoints.ErrorMessage;
import com.cairn.waypoint.dashboard.endpoints.protocoltemplate.dto.AddProtocolTemplateDetailsDto;
import com.cairn.waypoint.dashboard.endpoints.protocoltemplate.mapper.ProtocolTemplateMapper;
import com.cairn.waypoint.dashboard.entity.ProtocolTemplate;
import com.cairn.waypoint.dashboard.entity.ProtocolTemplateLinkedStepTemplate;
import com.cairn.waypoint.dashboard.entity.StepTemplate;
import com.cairn.waypoint.dashboard.service.ProtocolTemplateService;
import com.cairn.waypoint.dashboard.service.StepTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Tag(name = "Protocol Template")
public class AddProtocolTemplateEndpoint {

  public static final String PATH = "/api/protocol-template";

  private final ProtocolTemplateService protocolTemplateService;
  private final StepTemplateService stepTemplateService;

  public AddProtocolTemplateEndpoint(ProtocolTemplateService protocolTemplateService,
      StepTemplateService stepTemplateService) {
    this.protocolTemplateService = protocolTemplateService;
    this.stepTemplateService = stepTemplateService;
  }

  @PostMapping(PATH)
  @PreAuthorize("hasAnyAuthority('SCOPE_protocol.template.full', 'SCOPE_admin.full')")
  @Operation(
      summary = "Allows a user to create new protocol template, "
          + "linking the provided protocol step templates to the newly created protocol template.",
      description = "Allows a user to create new protocol template,"
          + " linking the provided protocol step templates to the newly created protocol template."
          + " Requires the `protocol.template.full` permission.",
      security = @SecurityRequirement(name = "oAuth2JwtBearer"),
      responses = {
          @ApiResponse(responseCode = "201",
              description = "Created - Protocol Template creation was successful"),
          @ApiResponse(responseCode = "401", description = "Unauthorized",
              content = {@Content(schema = @Schema(hidden = true))}),
          @ApiResponse(responseCode = "403", description = "Forbidden",
              content = {@Content(mediaType = "application/json",
                  schema = @Schema(implementation = ErrorMessage.class))}),
          @ApiResponse(responseCode = "404", description = "Not Found",
              content = {@Content(mediaType = "application/json",
                  schema = @Schema(implementation = ErrorMessage.class))}),
          @ApiResponse(responseCode = "409", description = "Not Found",
              content = {@Content(mediaType = "application/json",
                  schema = @Schema(implementation = ErrorMessage.class))})})
  public ResponseEntity<?> addProtocolTemplate(
      @RequestBody AddProtocolTemplateDetailsDto addProtocolTemplateDetailsDto,
      Principal principal) {
    log.info("User [{}] is attempted to create a Protocol Template with name [{}]",
        principal.getName(), addProtocolTemplateDetailsDto.getName());

    if (this.protocolTemplateService.findProtocolTemplateByName(
            addProtocolTemplateDetailsDto.getName())
        .isPresent()) {
      return generateFailureResponse("Protocol Template with name [" +
          addProtocolTemplateDetailsDto.getName() + "] already exists", HttpStatus.CONFLICT);
    } else {
      LinkedHashSet<StepTemplate> stepTemplates;
      try {
        stepTemplates = this.stepTemplateService
            .getStepTemplateEntitiesFromIdCollection(
                addProtocolTemplateDetailsDto.getAssociatedStepTemplateIds());
      } catch (EntityNotFoundException e) {
        return generateFailureResponse(e.getMessage(), HttpStatus.NOT_FOUND);
      }

      Long createdProtocolTemplateId = createProtocolTemplate(addProtocolTemplateDetailsDto,
          principal.getName(), stepTemplates);

      log.info("Protocol Template [{}] created successfully with ID [{}]",
          addProtocolTemplateDetailsDto.getName(),
          createdProtocolTemplateId);
      return ResponseEntity.status(HttpStatus.CREATED)
          .body("Protocol Template ["
              + addProtocolTemplateDetailsDto.getName() + "] created successfully");
    }
  }

  private Long createProtocolTemplate(AddProtocolTemplateDetailsDto addProtocolTemplateDetailsDto,
      String modifiedBy, LinkedHashSet<StepTemplate> stepTemplates) {

    ProtocolTemplate protocolTemplateToCreate = ProtocolTemplateMapper.INSTANCE
        .toEntity(addProtocolTemplateDetailsDto);

    protocolTemplateToCreate.setModifiedBy(modifiedBy);

    Long createdProtocolTemplateId = this.protocolTemplateService.saveProtocolTemplate(
        protocolTemplateToCreate).getId();
    Optional<ProtocolTemplate> protocolTemplateEntity = this.protocolTemplateService.getProtocolTemplateById(
        createdProtocolTemplateId);

    if (protocolTemplateEntity.isEmpty()) {
      //TODO handle this gracefully
      return null;
    }

    protocolTemplateEntity.get().setProtocolTemplateSteps(
        createProtocolStepTemplates(protocolTemplateToCreate, stepTemplates, modifiedBy));
    return this.protocolTemplateService.saveProtocolTemplate(protocolTemplateToCreate).getId();
  }

  private Set<ProtocolTemplateLinkedStepTemplate> createProtocolStepTemplates(
      ProtocolTemplate protocolTemplate, LinkedHashSet<StepTemplate> stepTemplates,
      String modifiedBy) {
    AtomicInteger ordinalIndex = new AtomicInteger(0);
    return stepTemplates.stream()
        .map(stepTemplate -> ProtocolTemplateLinkedStepTemplate.builder()
            .modifiedBy(modifiedBy)
            .protocolTemplate(protocolTemplate)
            .stepTemplate(stepTemplate)
            .ordinalIndex(ordinalIndex.getAndIncrement())
            .build())
        .collect(Collectors.toCollection(LinkedHashSet::new));
  }

  private ResponseEntity<ErrorMessage> generateFailureResponse(String message, HttpStatus status) {
    log.warn(message);
    return new ResponseEntity<>(
        ErrorMessage.builder()
            .path(PATH)
            .timestamp(LocalDateTime.now())
            .status(status.value())
            .error(message)
            .build(),
        status
    );
  }
}
