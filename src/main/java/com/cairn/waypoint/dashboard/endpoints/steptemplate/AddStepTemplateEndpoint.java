package com.cairn.waypoint.dashboard.endpoints.steptemplate;

import com.cairn.waypoint.dashboard.endpoints.ErrorMessage;
import com.cairn.waypoint.dashboard.endpoints.steptemplate.dto.AddStepTemplateDetailsDto;
import com.cairn.waypoint.dashboard.endpoints.steptemplate.mapper.StepTemplateMapper;
import com.cairn.waypoint.dashboard.entity.HomeworkTemplate;
import com.cairn.waypoint.dashboard.entity.StepTask;
import com.cairn.waypoint.dashboard.entity.StepTemplate;
import com.cairn.waypoint.dashboard.entity.TemplateCategory;
import com.cairn.waypoint.dashboard.service.HomeworkTemplateService;
import com.cairn.waypoint.dashboard.service.StepTaskService;
import com.cairn.waypoint.dashboard.service.StepTemplateService;
import com.cairn.waypoint.dashboard.service.TemplateCategoryService;
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

  private final StepTemplateService stepTemplateService;
  private final StepTaskService stepTaskService;
  private final HomeworkTemplateService homeworkTemplateService;
  private final TemplateCategoryService templateCategoryService;

  public AddStepTemplateEndpoint(StepTemplateService stepTemplateService,
      StepTaskService stepTaskService, HomeworkTemplateService homeworkTemplateService,
      TemplateCategoryService templateCategoryService) {
    this.stepTemplateService = stepTemplateService;
    this.stepTaskService = stepTaskService;
    this.homeworkTemplateService = homeworkTemplateService;
    this.templateCategoryService = templateCategoryService;
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
              description = "Created - Step Template creation was successful"),
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
  public ResponseEntity<?> addStepTemplate(
      @RequestBody AddStepTemplateDetailsDto addStepTemplateDetailsDto,
      Principal principal) {
    log.info("User [{}] is attempted to create a Step Template with name [{}]",
        principal.getName(), addStepTemplateDetailsDto.getName());

    Optional<StepTask> linkedStepTask = Optional.empty();
    Optional<HomeworkTemplate> linkedHomeworkTemplate = Optional.empty();
    Optional<TemplateCategory> stepTemplateCategory = Optional.empty();

    if (this.stepTemplateService.findStepTemplateByName(addStepTemplateDetailsDto.getName())
        .isPresent()) {
      return generateFailureResponse("Step Template with name [" +
          addStepTemplateDetailsDto.getName() + "] already exists", HttpStatus.CONFLICT);
    } else if (addStepTemplateDetailsDto.getLinkedStepTaskId() != null &&
        (linkedStepTask = this.stepTaskService.getStepTaskById(
            addStepTemplateDetailsDto.getLinkedStepTaskId()))
            .isEmpty()) {
      return generateFailureResponse("Step Task with ID [" +
          addStepTemplateDetailsDto.getLinkedStepTaskId() + "] not found", HttpStatus.NOT_FOUND);
    } else if (addStepTemplateDetailsDto.getLinkedHomeworkTemplateId() != null &&
        (linkedHomeworkTemplate = this.homeworkTemplateService.getHomeworkTemplateById(
            addStepTemplateDetailsDto.getLinkedStepTaskId())).isEmpty()) {
      return generateFailureResponse("Homework Template with ID [" +
              addStepTemplateDetailsDto.getLinkedHomeworkTemplateId() + "] not found",
          HttpStatus.NOT_FOUND);
    } else if (addStepTemplateDetailsDto.getStepTemplateCategoryId() != null &&
        (stepTemplateCategory = this.templateCategoryService.getTemplateCategoryById(
            addStepTemplateDetailsDto.getStepTemplateCategoryId())).isEmpty()) {
      return generateFailureResponse("Step Template Category with ID [" +
              addStepTemplateDetailsDto.getStepTemplateCategoryId() + "] not found",
          HttpStatus.NOT_FOUND);
    } else {
      Long createdStepTemplateId = createStepTemplate(addStepTemplateDetailsDto,
          principal.getName(), linkedStepTask.orElse(null),
          linkedHomeworkTemplate.orElse(null), stepTemplateCategory.orElse(null));

      log.info("Step Task [{}] created successfully with ID [{}]",
          addStepTemplateDetailsDto.getName(),
          createdStepTemplateId);
      return ResponseEntity.status(HttpStatus.CREATED)
          .body("Step Template ["
              + addStepTemplateDetailsDto.getName() + "] created successfully");
    }
  }

  private Long createStepTemplate(AddStepTemplateDetailsDto addStepTemplateDetailsDto,
      String modifiedBy, StepTask linkedStepTask, HomeworkTemplate linkedHomeworkTemplate,
      TemplateCategory templateCategory) {

    StepTemplate stepTemplateToCreate = StepTemplateMapper.INSTANCE
        .toEntity(addStepTemplateDetailsDto);

    stepTemplateToCreate.setModifiedBy(modifiedBy);
    stepTemplateToCreate.setLinkedTask(linkedStepTask);
    stepTemplateToCreate.setLinkedHomeworkTemplate(linkedHomeworkTemplate);
    stepTemplateToCreate.setCategory(templateCategory);

    return this.stepTemplateService.saveStepTemplate(stepTemplateToCreate);
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
