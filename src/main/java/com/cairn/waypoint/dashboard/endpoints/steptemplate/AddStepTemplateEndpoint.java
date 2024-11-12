package com.cairn.waypoint.dashboard.endpoints.steptemplate;

import com.cairn.waypoint.dashboard.endpoints.ErrorMessage;
import com.cairn.waypoint.dashboard.endpoints.steptemplate.dto.AddStepTemplateDetailsDto;
import com.cairn.waypoint.dashboard.endpoints.steptemplate.dto.StepTaskDetailsDto;
import com.cairn.waypoint.dashboard.endpoints.steptemplate.dto.StepTemplateCategoryDetailsDto;
import com.cairn.waypoint.dashboard.endpoints.steptemplate.dto.SuccessfulStepTemplateCreationResponseDto;
import com.cairn.waypoint.dashboard.endpoints.steptemplate.mapper.StepTemplateMapper;
import com.cairn.waypoint.dashboard.entity.StepTask;
import com.cairn.waypoint.dashboard.entity.StepTemplate;
import com.cairn.waypoint.dashboard.entity.StepTemplateCategory;
import com.cairn.waypoint.dashboard.entity.enumeration.TemplateStatusEnum;
import com.cairn.waypoint.dashboard.service.data.StepTaskDataService;
import com.cairn.waypoint.dashboard.service.data.StepTemplateCategoryDataService;
import com.cairn.waypoint.dashboard.service.data.StepTemplateDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;
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
@Tag(name = "Protocol Step Template")
public class AddStepTemplateEndpoint {

  public static final String PATH = "/api/protocol-step-template";

  private final StepTemplateDataService stepTemplateDataService;
  private final StepTaskDataService stepTaskDataService;
  private final StepTemplateCategoryDataService stepTemplateCategoryDataService;

  public AddStepTemplateEndpoint(StepTemplateDataService stepTemplateDataService,
      StepTaskDataService stepTaskDataService,
      StepTemplateCategoryDataService stepTemplateCategoryDataService) {
    this.stepTemplateDataService = stepTemplateDataService;
    this.stepTaskDataService = stepTaskDataService;
    this.stepTemplateCategoryDataService = stepTemplateCategoryDataService;
  }

  @PostMapping(PATH)
  @PreAuthorize("hasAnyAuthority('SCOPE_protocol.step.template.full', 'SCOPE_admin.full')")
  @Operation(
      summary = "Allows a user to create new step template,"
          + " linking the provided step tasks and homework templates to the newly created step template.",
      description = "Allows a user to create new step template,"
          + " linking the provided step tasks and homework templates to the newly created step template."
          + " Requires the `protocol.step.template.full` permission.",
      security = @SecurityRequirement(name = "oAuth2JwtBearer"),
      responses = {
          @ApiResponse(responseCode = "201",
              description = "Created - Step Template creation was successful",
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = SuccessfulStepTemplateCreationResponseDto.class))}),
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
  public ResponseEntity<?> addStepTemplate(
      @RequestBody AddStepTemplateDetailsDto addStepTemplateDetailsDto,
      Principal principal) {
    log.info("User [{}] is attempted to create a Step Template with name [{}]",
        principal.getName(), addStepTemplateDetailsDto.getName());

    Optional<StepTask> linkedStepTask = Optional.empty();
    Optional<StepTemplateCategory> stepTemplateCategory = Optional.empty();

    if (this.stepTemplateDataService.findStepTemplateByName(addStepTemplateDetailsDto.getName())
        .isPresent()) {
      return generateFailureResponse("Step Template with name [" +
          addStepTemplateDetailsDto.getName() + "] already exists", HttpStatus.CONFLICT);
    } else if (addStepTemplateDetailsDto.getLinkedStepTaskId() != null &&
        (linkedStepTask = this.stepTaskDataService.getStepTaskById(
            addStepTemplateDetailsDto.getLinkedStepTaskId()))
            .isEmpty()) {
      return generateFailureResponse("Step Task with ID [" +
          addStepTemplateDetailsDto.getLinkedStepTaskId() + "] not found", HttpStatus.NOT_FOUND);
    } else if (addStepTemplateDetailsDto.getStepTemplateCategoryId() != null &&
        (stepTemplateCategory = this.stepTemplateCategoryDataService.getTemplateCategoryById(
            addStepTemplateDetailsDto.getStepTemplateCategoryId())).isEmpty()) {
      return generateFailureResponse("Step Template Category with ID [" +
              addStepTemplateDetailsDto.getStepTemplateCategoryId() + "] not found",
          HttpStatus.NOT_FOUND);
    } else {
      StepTemplate createdStepTemplate = createStepTemplate(addStepTemplateDetailsDto,
          principal.getName(), linkedStepTask.orElse(null), stepTemplateCategory.orElse(null));

      log.info("Step Template [{}] created successfully with ID [{}]",
          addStepTemplateDetailsDto.getName(),
          createdStepTemplate.getId());
      return ResponseEntity.status(HttpStatus.CREATED)
          .body(SuccessfulStepTemplateCreationResponseDto.builder()
              .stepTemplateId(createdStepTemplate.getId())
              .stepTemplateName(createdStepTemplate.getName())
              .stepTemplateDescription(createdStepTemplate.getDescription())
              .status(createdStepTemplate.getStatus().name())
              .linkedStepTask(getStepTaskDetailsDto(createdStepTemplate.getLinkedTask()))
              .category(getStepTemplateCategoryDetailsDto(createdStepTemplate.getCategory()))
              .build());
    }
  }

  private StepTaskDetailsDto getStepTaskDetailsDto(StepTask stepTask) {
    StepTaskDetailsDto stepTaskDetailsDto = null;
    if (stepTask != null) {
      stepTaskDetailsDto = StepTaskDetailsDto.builder()
          .id(stepTask.getId())
          .name(stepTask.getName())
          .executableReference(stepTask.getExecutableReference())
          .build();
    }
    return stepTaskDetailsDto;
  }

  private StepTemplateCategoryDetailsDto getStepTemplateCategoryDetailsDto(
      StepTemplateCategory stepTemplateCategory) {
    StepTemplateCategoryDetailsDto stepTemplateCategoryDetailsDto = null;
    if (stepTemplateCategory != null) {
      stepTemplateCategoryDetailsDto = StepTemplateCategoryDetailsDto.builder()
          .id(stepTemplateCategory.getId())
          .name(stepTemplateCategory.getName())
          .description(stepTemplateCategory.getDescription())
          .build();
    }
    return stepTemplateCategoryDetailsDto;
  }

  private StepTemplate createStepTemplate(AddStepTemplateDetailsDto addStepTemplateDetailsDto,
      String modifiedBy, StepTask linkedStepTask,
      StepTemplateCategory stepTemplateCategory) {

    StepTemplate stepTemplateToCreate = StepTemplateMapper.INSTANCE
        .toEntity(addStepTemplateDetailsDto);

    stepTemplateToCreate.setModifiedBy(modifiedBy);
    stepTemplateToCreate.setLinkedTask(linkedStepTask);
    stepTemplateToCreate.setCategory(stepTemplateCategory);
    stepTemplateToCreate.setStatus(TemplateStatusEnum.INACTIVE);

    StepTemplate createdStepTemplate = this.stepTemplateDataService.saveStepTemplate(
        stepTemplateToCreate);

    return this.stepTemplateDataService.saveStepTemplate(createdStepTemplate);

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
