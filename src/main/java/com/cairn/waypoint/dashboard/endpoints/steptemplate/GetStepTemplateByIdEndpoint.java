package com.cairn.waypoint.dashboard.endpoints.steptemplate;

import com.cairn.waypoint.dashboard.endpoints.ErrorMessage;
import com.cairn.waypoint.dashboard.endpoints.steptemplate.dto.HomeworkTemplateDetailsDto;
import com.cairn.waypoint.dashboard.endpoints.steptemplate.dto.HomeworkTemplateDetailsListDto;
import com.cairn.waypoint.dashboard.endpoints.steptemplate.dto.StepTaskDetailsDto;
import com.cairn.waypoint.dashboard.endpoints.steptemplate.dto.StepTemplateCategoryDetailsDto;
import com.cairn.waypoint.dashboard.endpoints.steptemplate.dto.StepTemplateDetailsDto;
import com.cairn.waypoint.dashboard.endpoints.steptemplate.mapper.StepTemplateMapper;
import com.cairn.waypoint.dashboard.entity.StepTemplate;
import com.cairn.waypoint.dashboard.entity.StepTemplateLinkedHomeworkTemplate;
import com.cairn.waypoint.dashboard.service.data.StepTemplateDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.security.Principal;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Tag(name = "Protocol Step Template")
public class GetStepTemplateByIdEndpoint {

  public static final String PATH = "/api/protocol-step-template/{stepTemplateId}";

  private final StepTemplateDataService stepTemplateDataService;

  public GetStepTemplateByIdEndpoint(StepTemplateDataService stepTemplateDataService) {
    this.stepTemplateDataService = stepTemplateDataService;
  }

  @GetMapping(PATH)
  @PreAuthorize("hasAnyAuthority('SCOPE_protocol.step.template.full', 'SCOPE_admin.full')")
  @Operation(
      summary = "Retrieves a step Template by it's ID.",
      description = "Retrieves a step Template by it's ID. Requires the `protocol.step.template.full` permission.",
      security = @SecurityRequirement(name = "oAuth2JwtBearer"),
      responses = {
          @ApiResponse(responseCode = "200",
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = StepTemplateDetailsDto.class))}),
          @ApiResponse(responseCode = "401", description = "Unauthorized",
              content = {@Content(schema = @Schema(hidden = true))}),
          @ApiResponse(responseCode = "403", description = "Forbidden",
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ErrorMessage.class))}),
          @ApiResponse(responseCode = "404", description = "Not Found",
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ErrorMessage.class))})})
  public ResponseEntity<?> getStepTemplateById(@PathVariable Long stepTemplateId,
      Principal principal) {
    log.info("User [{}] is Retrieving Step Template with ID [{}]", principal.getName(),
        stepTemplateId);

    final ResponseEntity<?>[] response = new ResponseEntity<?>[1];
    this.stepTemplateDataService.getStepTemplateById(stepTemplateId)
        .ifPresentOrElse(
            returnedStepTemplate -> response[0] = generateSuccessResponse(
                returnedStepTemplate),
            () -> response[0] = generateFailureResponse(stepTemplateId)
        );

    return response[0];
  }

  public ResponseEntity<StepTemplateDetailsDto> generateSuccessResponse(
      StepTemplate returnedStepTemplate) {

    StepTaskDetailsDto stepTaskDetailsDto = null;
    HomeworkTemplateDetailsListDto homeworkTemplateDetailsListDto = null;
    StepTemplateCategoryDetailsDto categoryDetailsDto = null;

    if (returnedStepTemplate.getLinkedTask() != null) {
      stepTaskDetailsDto = StepTaskDetailsDto.builder()
          .id(returnedStepTemplate.getLinkedTask().getId())
          .name(returnedStepTemplate.getLinkedTask().getName())
          .executableReference(returnedStepTemplate.getLinkedTask().getExecutableReference())
          .build();
    }
/*
    if (returnedStepTemplate.getStepTemplateLinkedHomeworks() != null
        && !returnedStepTemplate.getStepTemplateLinkedHomeworks().isEmpty()) {
      homeworkTemplateDetailsListDto = HomeworkTemplateDetailsListDto.builder()
          .homeworkTemplates(
              returnedStepTemplate.getStepTemplateLinkedHomeworks().stream()
                  .map(StepTemplateLinkedHomeworkTemplate::getHomeworkTemplate)
                  .map(homeworkTemplate -> HomeworkTemplateDetailsDto.builder()
                      .id(homeworkTemplate.getId())
                      .name(homeworkTemplate.getName())
                      .build())
                  .toList()
          ).build();

    }*/

    if (returnedStepTemplate.getCategory() != null) {
      categoryDetailsDto = StepTemplateCategoryDetailsDto.builder()
          .id(returnedStepTemplate.getCategory().getId())
          .name(returnedStepTemplate.getCategory().getName())
          .description(returnedStepTemplate.getCategory().getDescription())
          .build();
    }

    return ResponseEntity.ok(StepTemplateMapper.INSTANCE.toDetailsDto(returnedStepTemplate));
  }

  public ResponseEntity<ErrorMessage> generateFailureResponse(Long stepTemplateId) {
    log.info("Step Template with ID [{}] not found", stepTemplateId);
    return new ResponseEntity<>(
        ErrorMessage.builder()
            .path(PATH)
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.NOT_FOUND.value())
            .error("Step Template with ID [" + stepTemplateId + "] not found")
            .build(),
        HttpStatus.NOT_FOUND
    );
  }

}
