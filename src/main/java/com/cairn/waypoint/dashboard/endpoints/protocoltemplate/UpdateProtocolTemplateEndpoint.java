package com.cairn.waypoint.dashboard.endpoints.protocoltemplate;

import com.cairn.waypoint.dashboard.endpoints.ErrorMessage;
import com.cairn.waypoint.dashboard.endpoints.protocoltemplate.dto.UpdateProtocolTemplateDetailsDto;
import com.cairn.waypoint.dashboard.endpoints.protocoltemplate.mapper.ProtocolTemplateMapper;
import com.cairn.waypoint.dashboard.entity.ProtocolTemplate;
import com.cairn.waypoint.dashboard.entity.ProtocolTemplateLinkedStepTemplate;
import com.cairn.waypoint.dashboard.entity.StepTemplate;
import com.cairn.waypoint.dashboard.service.ProtocolTemplateLinkedStepTemplateService;
import com.cairn.waypoint.dashboard.service.ProtocolTemplateService;
import com.cairn.waypoint.dashboard.service.StepTemplateService;
import com.cairn.waypoint.dashboard.utility.BeanUtils;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Tag(name = "Protocol Template")
public class UpdateProtocolTemplateEndpoint {

  public static final String PATH = "/api/protocol-template/{protocolTemplateId}";

  private final ProtocolTemplateService protocolTemplateService;
  private final StepTemplateService stepTemplateService;
  private final ProtocolTemplateLinkedStepTemplateService protocolTemplateLinkedStepTemplateService;

  public UpdateProtocolTemplateEndpoint(ProtocolTemplateService protocolTemplateService,
      StepTemplateService stepTemplateService,
      ProtocolTemplateLinkedStepTemplateService protocolTemplateLinkedStepTemplateService) {
    this.protocolTemplateService = protocolTemplateService;
    this.stepTemplateService = stepTemplateService;
    this.protocolTemplateLinkedStepTemplateService = protocolTemplateLinkedStepTemplateService;
  }

  @PatchMapping(PATH)
  @PreAuthorize("hasAnyAuthority('SCOPE_protocol.template.full', 'SCOPE_admin.full')")
  @Operation(
      summary = "Allows a user to update a protocol template, "
          + "updating properties and the provided protocol step templates to associated protocol template.",
      description = "Allows a user to update a protocol template, "
          + "updating properties and the provided protocol step templates to associated protocol template."
          + " Requires the `protocol.template.full` permission.",
      security = @SecurityRequirement(name = "oAuth2JwtBearer"),
      responses = {
          @ApiResponse(responseCode = "200",
              description = "Updated - Protocol Template update was successful"),
          @ApiResponse(responseCode = "401", description = "Unauthorized",
              content = {@Content(schema = @Schema(hidden = true))}),
          @ApiResponse(responseCode = "403", description = "Forbidden",
              content = {@Content(mediaType = "application/json",
                  schema = @Schema(implementation = ErrorMessage.class))}),
          @ApiResponse(responseCode = "404", description = "Not Found",
              content = {@Content(mediaType = "application/json",
                  schema = @Schema(implementation = ErrorMessage.class))}),
          @ApiResponse(responseCode = "409", description = "Conflict",
              content = {@Content(mediaType = "application/json",
                  schema = @Schema(implementation = ErrorMessage.class))})})
  public ResponseEntity<?> updateProtocolTemplate(@PathVariable Long protocolTemplateId,
      @RequestBody UpdateProtocolTemplateDetailsDto updateProtocolTemplateDetailsDto,
      Principal principal) {
    log.info("User [{}] is attempted to update a Protocol Template with id [{}]",
        principal.getName(), protocolTemplateId);

    Optional<ProtocolTemplate> protocolTemplateToBeUpdated = this.protocolTemplateService.getProtocolTemplateById(
        protocolTemplateId);
    Optional<ProtocolTemplate> protocolTemplateByName = this.protocolTemplateService.findProtocolTemplateByName(
        updateProtocolTemplateDetailsDto.getName());

    if (protocolTemplateToBeUpdated.isEmpty()) {
      return generateFailureResponse("Protocol Template with id [" +
          protocolTemplateId + "] not found", HttpStatus.NOT_FOUND);
    } else if (protocolTemplateByName.isPresent() &&
        //Protocol Template Name has been updated
        // but the new Protocol Template Name already exists
        !protocolTemplateByName.get().getName()
            .equals(protocolTemplateToBeUpdated.get().getName())
    ) {
      return generateFailureResponse("Protocol Template with name [" +
          updateProtocolTemplateDetailsDto.getName() + "] already exists", HttpStatus.CONFLICT);
    } else {
      LinkedHashSet<StepTemplate> stepTemplates;

      try {
        stepTemplates = populateAssociatedProtocolStepTemplateIfExists(
            updateProtocolTemplateDetailsDto);
      } catch (EntityNotFoundException e) {
        return generateFailureResponse(e.getMessage(), HttpStatus.NOT_FOUND);
      }

      Long createdProtocolTemplateId = updateProtocolTemplate(updateProtocolTemplateDetailsDto,
          principal.getName(), stepTemplates, protocolTemplateToBeUpdated.get());

      log.info("Protocol Template with ID [{}] and name [{}] updated successfully ",
          createdProtocolTemplateId, protocolTemplateToBeUpdated.get().getName());
      return ResponseEntity.status(HttpStatus.OK)
          .body("Protocol Template with ID [" + protocolTemplateId + "] and name ["
              + protocolTemplateToBeUpdated.get().getName() + "] updated successfully");
    }
  }

  private LinkedHashSet<StepTemplate> populateAssociatedProtocolStepTemplateIfExists(
      UpdateProtocolTemplateDetailsDto updateProtocolTemplateDetailsDto) {
    if (updateProtocolTemplateDetailsDto.getAssociatedStepTemplateIds() != null) {
      return this.stepTemplateService
          .getStepTemplateEntitiesFromIdCollection(
              updateProtocolTemplateDetailsDto.getAssociatedStepTemplateIds());
    } else {
      return null;
    }
  }

  private Long updateProtocolTemplate(
      UpdateProtocolTemplateDetailsDto updateProtocolTemplateDetailsDto,
      String modifiedBy, LinkedHashSet<StepTemplate> stepTemplates,
      ProtocolTemplate targetProtocolTemplate) {
    ProtocolTemplate updatedProtocolTemplate = ProtocolTemplateMapper.INSTANCE.toEntity(
        updateProtocolTemplateDetailsDto);

    updatedProtocolTemplate.setModifiedBy(modifiedBy);
    updatedProtocolTemplate.setProtocolTemplateSteps(
        updateProtocolStepTemplates(targetProtocolTemplate, stepTemplates, modifiedBy));

    this.protocolTemplateLinkedStepTemplateService
        .deleteCollectionOfProtocolTemplateLinkedStepTemplates(
            targetProtocolTemplate.getProtocolTemplateSteps());

    BeanUtils.copyPropertiesIgnoreNulls(updatedProtocolTemplate, targetProtocolTemplate);

    return this.protocolTemplateService.saveProtocolTemplate(targetProtocolTemplate);
  }

  private Set<ProtocolTemplateLinkedStepTemplate> updateProtocolStepTemplates(
      ProtocolTemplate protocolTemplate, LinkedHashSet<StepTemplate> stepTemplates,
      String modifiedBy) {
    if (stepTemplates == null) {
      return null;
    }

    //TODO here we need to first pulled the existing entities, reorder to the provided order
    // and then reassociate those with the Protocol Template
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
