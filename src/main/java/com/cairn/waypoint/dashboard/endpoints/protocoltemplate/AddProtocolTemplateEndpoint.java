package com.cairn.waypoint.dashboard.endpoints.protocoltemplate;

import com.cairn.waypoint.dashboard.endpoints.ErrorMessage;
import com.cairn.waypoint.dashboard.endpoints.protocoltemplate.dto.AddProtocolTemplateDetailsDto;
import com.cairn.waypoint.dashboard.endpoints.protocoltemplate.dto.AssociatedStepTemplatesListDto;
import com.cairn.waypoint.dashboard.endpoints.protocoltemplate.dto.ProtocolStepTemplateDto;
import com.cairn.waypoint.dashboard.endpoints.protocoltemplate.dto.ProtocolTemplateDetailsDto;
import com.cairn.waypoint.dashboard.endpoints.protocoltemplate.dto.RecurrenceTypeDetailsDto;
import com.cairn.waypoint.dashboard.endpoints.protocoltemplate.dto.StepTemplateCategoryDto;
import com.cairn.waypoint.dashboard.endpoints.protocoltemplate.mapper.ProtocolTemplateMapper;
import com.cairn.waypoint.dashboard.entity.ProtocolTemplate;
import com.cairn.waypoint.dashboard.entity.ProtocolTemplateLinkedStepTemplate;
import com.cairn.waypoint.dashboard.entity.StepTemplate;
import com.cairn.waypoint.dashboard.entity.enumeration.TemplateStatusEnum;
import com.cairn.waypoint.dashboard.service.data.ProtocolTemplateDataService;
import com.cairn.waypoint.dashboard.service.data.StepTemplateDataService;
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
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

  private final ProtocolTemplateDataService protocolTemplateDataService;
  private final StepTemplateDataService stepTemplateDataService;

  public AddProtocolTemplateEndpoint(ProtocolTemplateDataService protocolTemplateDataService,
      StepTemplateDataService stepTemplateDataService) {
    this.protocolTemplateDataService = protocolTemplateDataService;
    this.stepTemplateDataService = stepTemplateDataService;
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
              description = "Created - Protocol Template creation was successful",
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ProtocolTemplateDetailsDto.class))}),
          @ApiResponse(responseCode = "401", description = "Unauthorized",
              content = {@Content(schema = @Schema(hidden = true))}),
          @ApiResponse(responseCode = "403", description = "Forbidden",
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ErrorMessage.class))}),
          @ApiResponse(responseCode = "404", description = "Not Found",
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ErrorMessage.class))}),
          @ApiResponse(responseCode = "409", description = "Conflict",
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ErrorMessage.class))})})
  public ResponseEntity<?> addProtocolTemplate(
      @RequestBody AddProtocolTemplateDetailsDto addProtocolTemplateDetailsDto,
      Principal principal) {
    log.info("User [{}] is attempted to create a Protocol Template with name [{}]",
        principal.getName(), addProtocolTemplateDetailsDto.getName());

    if (this.protocolTemplateDataService.findProtocolTemplateByName(
            addProtocolTemplateDetailsDto.getName())
        .isPresent()) {
      return generateFailureResponse("Protocol Template with name [" +
          addProtocolTemplateDetailsDto.getName() + "] already exists", HttpStatus.CONFLICT);
    } else {
      try {
        if (addProtocolTemplateDetailsDto.getDefaultTriggeringStatusValue() != null) {
          addProtocolTemplateDetailsDto.getDefaultTriggeringStatus();
        }
      } catch (IllegalArgumentException e) {
        return generateFailureResponse("Triggering Status with name [" +
                addProtocolTemplateDetailsDto.getDefaultTriggeringStatusValue() + "] does not exist",
            HttpStatus.NOT_FOUND);
      }
      try {
        addProtocolTemplateDetailsDto.getDefaultRecurrenceType();
      } catch (IllegalArgumentException e) {
        return generateFailureResponse("Recurrence Type with name [" +
                addProtocolTemplateDetailsDto.getRecurrenceTypeValue() + "] does not exist",
            HttpStatus.NOT_FOUND);
      }
      try {
        addProtocolTemplateDetailsDto.getTemplateCategory();
      } catch (IllegalArgumentException e) {
        return generateFailureResponse("Template Category with name [" +
                addProtocolTemplateDetailsDto.getTemplateCategoryValue() + "] does not exist",
            HttpStatus.NOT_FOUND);
      }

      LinkedHashSet<StepTemplate> stepTemplates;
      try {
        stepTemplates = this.stepTemplateDataService
            .getStepTemplateEntitiesFromIdCollection(
                addProtocolTemplateDetailsDto.getAssociatedStepTemplateIds());
      } catch (EntityNotFoundException e) {
        return generateFailureResponse(e.getMessage(), HttpStatus.NOT_FOUND);
      }

      ProtocolTemplate createdProtocolTemplate = createProtocolTemplate(
          addProtocolTemplateDetailsDto,
          principal.getName(), stepTemplates);

      log.info("Protocol Template [{}] created successfully with ID [{}]",
          addProtocolTemplateDetailsDto.getName(),
          createdProtocolTemplate.getId());
      return ResponseEntity.status(HttpStatus.CREATED)
          .body(ProtocolTemplateDetailsDto.builder()
              .id(createdProtocolTemplate.getId())
              .name(createdProtocolTemplate.getName())
              .description(createdProtocolTemplate.getDescription())
              .status(createdProtocolTemplate.getStatus().name())
              .defaultDueByInYears(createdProtocolTemplate.getDefaultDueByInYears())
              .defaultDueByInMonths(createdProtocolTemplate.getDefaultDueByInMonths())
              .defaultDueByInDays(createdProtocolTemplate.getDefaultDueByInDays())
              .defaultProtocolRecurrence(RecurrenceTypeDetailsDto.builder()
                  .recurrenceType(createdProtocolTemplate.getDefaultRecurrenceType().name())
                  .defaultTriggeringStatus(
                      createdProtocolTemplate.getDefaultTriggeringStatus() == null ? null
                          : createdProtocolTemplate.getDefaultTriggeringStatus().name())
                  .defaultReoccurInYears(createdProtocolTemplate.getDefaultReoccurInYears())
                  .defaultReoccurInMonths(createdProtocolTemplate.getDefaultReoccurInMonths())
                  .defaultReoccurInDays(createdProtocolTemplate.getDefaultReoccurInDays())
                  .build())
              .associatedSteps(AssociatedStepTemplatesListDto.builder()
                  .steps(createdProtocolTemplate.getProtocolTemplateSteps().stream()
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
  }

  private ProtocolTemplate createProtocolTemplate(
      AddProtocolTemplateDetailsDto addProtocolTemplateDetailsDto,
      String modifiedBy, LinkedHashSet<StepTemplate> stepTemplates) {

    ProtocolTemplate protocolTemplateToCreate = ProtocolTemplateMapper.INSTANCE
        .toEntity(addProtocolTemplateDetailsDto);

    protocolTemplateToCreate.setModifiedBy(modifiedBy);
    protocolTemplateToCreate.setStatus(TemplateStatusEnum.INACTIVE);

    ProtocolTemplate createdProtocolTemplate = this.protocolTemplateDataService.saveProtocolTemplate(
        protocolTemplateToCreate);

    createdProtocolTemplate.setProtocolTemplateSteps(
        createProtocolStepTemplates(createdProtocolTemplate, stepTemplates, modifiedBy));
    return this.protocolTemplateDataService.saveProtocolTemplate(createdProtocolTemplate);
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
