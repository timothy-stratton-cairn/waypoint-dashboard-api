package com.cairn.waypoint.dashboard.endpoints.steptemplate;

import com.cairn.waypoint.dashboard.endpoints.ErrorMessage;
import com.cairn.waypoint.dashboard.endpoints.steptemplate.dto.HomeworkTemplateDetailsDto;
import com.cairn.waypoint.dashboard.endpoints.steptemplate.dto.StepTaskDetailsDto;
import com.cairn.waypoint.dashboard.endpoints.steptemplate.dto.StepTemplateDetailsDto;
import com.cairn.waypoint.dashboard.entity.StepTemplate;
import com.cairn.waypoint.dashboard.service.StepTemplateService;
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

  private final StepTemplateService stepTemplateService;

  public GetStepTemplateByIdEndpoint(StepTemplateService stepTemplateService) {
    this.stepTemplateService = stepTemplateService;
  }

  @GetMapping(PATH)
  @PreAuthorize("hasAuthority('SCOPE_protocol.step.template.read')")
  @Operation(
      summary = "Retrieves a step Template by it's ID.",
      description = "Retrieves a step Template by it's ID. Requires the `protocol.step.template.read` permission.",
      security = @SecurityRequirement(name = "oAuth2JwtBearer"),
      responses = {
          @ApiResponse(responseCode = "200",
              content = {@Content(mediaType = "application/json",
                  schema = @Schema(implementation = StepTemplateDetailsDto.class))}),
          @ApiResponse(responseCode = "401", description = "Unauthorized",
              content = {@Content(schema = @Schema(hidden = true))}),
          @ApiResponse(responseCode = "403", description = "Forbidden",
              content = {@Content(mediaType = "application/json",
                  schema = @Schema(implementation = ErrorMessage.class))}),
          @ApiResponse(responseCode = "404", description = "Not Found",
              content = {@Content(mediaType = "application/json",
                  schema = @Schema(implementation = ErrorMessage.class))})})
  public ResponseEntity<?> getStepTemplateById(@PathVariable Long stepTemplateId,
      Principal principal) {
    log.info("User [{}] is Retrieving Step Template with ID [{}]", principal.getName(),
        stepTemplateId);

    final ResponseEntity<?>[] response = new ResponseEntity<?>[1];
    this.stepTemplateService.getStepTemplateById(stepTemplateId)
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
    HomeworkTemplateDetailsDto homeworkTemplateDetailsDto = null;

    if (returnedStepTemplate.getLinkedTask() != null) {
      stepTaskDetailsDto = StepTaskDetailsDto.builder()
          .id(returnedStepTemplate.getLinkedTask().getId())
          .name(returnedStepTemplate.getLinkedTask().getName())
          .executableReference(returnedStepTemplate.getLinkedTask().getExecutableReference())
          .build();
    }

    if (returnedStepTemplate.getLinkedHomeworkTemplate() != null) {
      homeworkTemplateDetailsDto = HomeworkTemplateDetailsDto.builder()
          .id(returnedStepTemplate.getLinkedHomeworkTemplate().getId())
          .name(returnedStepTemplate.getLinkedHomeworkTemplate().getName())
          .build();
    }

    return ResponseEntity.ok(
        StepTemplateDetailsDto.builder()
            .id(returnedStepTemplate.getId())
            .name(returnedStepTemplate.getName())
            .description(returnedStepTemplate.getDescription())
            .linkedStepTask(stepTaskDetailsDto)
            .linkedHomeworkTemplate(homeworkTemplateDetailsDto)
            .build()
    );
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
