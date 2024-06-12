package com.cairn.waypoint.dashboard.endpoints.homeworktemplate;

import com.cairn.waypoint.dashboard.endpoints.ErrorMessage;
import com.cairn.waypoint.dashboard.entity.Homework;
import com.cairn.waypoint.dashboard.entity.HomeworkTemplate;
import com.cairn.waypoint.dashboard.service.data.HomeworkDataService;
import com.cairn.waypoint.dashboard.service.data.HomeworkTemplateDataService;
import com.cairn.waypoint.dashboard.service.data.ProtocolStepLinkedHomeworkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Tag(name = "Homework Template")
public class DeleteHomeworkTemplateByIdEndpoint {

  public static final String PATH = "/api/homework-template/{homeworkTemplateId}";

  private final HomeworkTemplateDataService homeworkTemplateDataService;
  private final HomeworkDataService homeworkDataService;
  private final ProtocolStepLinkedHomeworkService protocolStepLinkedHomeworkService;

  public DeleteHomeworkTemplateByIdEndpoint(
      HomeworkTemplateDataService homeworkTemplateDataService,
      HomeworkDataService homeworkDataService,
      ProtocolStepLinkedHomeworkService protocolStepLinkedHomeworkService) {
    this.homeworkTemplateDataService = homeworkTemplateDataService;
    this.homeworkDataService = homeworkDataService;
    this.protocolStepLinkedHomeworkService = protocolStepLinkedHomeworkService;
  }

  @DeleteMapping(PATH)
  @PreAuthorize("hasAnyAuthority('SCOPE_homework.template.full', 'SCOPE_admin.full')")
  @Operation(
      summary = "Allows a user to deactivate a homework template referenced by the given ID.",
      description = "Allows a user to deactivate a homework template referenced by the given ID. Requires the `homework.template.full` permission.",
      security = @SecurityRequirement(name = "oAuth2JwtBearer"),
      responses = {
          @ApiResponse(responseCode = "200",
              content = {@Content(mediaType = MediaType.TEXT_PLAIN_VALUE,
                  schema = @Schema(implementation = String.class))}),
          @ApiResponse(responseCode = "401", description = "Unauthorized",
              content = {@Content(schema = @Schema(hidden = true))}),
          @ApiResponse(responseCode = "403", description = "Forbidden",
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ErrorMessage.class))}),
          @ApiResponse(responseCode = "404", description = "Not Found",
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ErrorMessage.class))})})
  public ResponseEntity<?> getAllHomeworkTemplates(@PathVariable Long homeworkTemplateId,
      Principal principal) {
    log.info("User [{}] is Deactivating Homework Template with ID [{}]", principal.getName(),
        homeworkTemplateId);

    final ResponseEntity<?>[] response = new ResponseEntity<?>[1];
    this.homeworkTemplateDataService.getHomeworkTemplateById(homeworkTemplateId)
        .ifPresentOrElse(
            returnedHomeworkTemplate -> response[0] = generateSuccessResponse(
                returnedHomeworkTemplate, principal.getName()),
            () -> response[0] = generateFailureResponse(homeworkTemplateId)
        );

    return response[0];
  }

  public ResponseEntity<String> generateSuccessResponse(
      HomeworkTemplate returnedHomeworkTemplate, String modifiedBy) {
    List<Homework> associatedHomeworks = homeworkDataService.getHomeworkByHomeworkTemplate(
        returnedHomeworkTemplate);

    associatedHomeworks.forEach(homework -> {
      protocolStepLinkedHomeworkService.getProtocolStepLinkedHomeworkByHomework(homework).stream()
          .peek(protocolStepLinkedHomework -> {
            protocolStepLinkedHomework.setModifiedBy(modifiedBy);
            protocolStepLinkedHomework.setActive(Boolean.FALSE);
          })
          .forEach(protocolStepLinkedHomeworkService::saveProtocolStepLinkedHomework);

      homework.setActive(Boolean.FALSE);
      homework.setModifiedBy(modifiedBy);

      homework.getHomeworkQuestions().forEach(response -> {
        response.setModifiedBy(modifiedBy);
        response.setActive(Boolean.FALSE);
      });

      homeworkDataService.saveHomework(homework);
    });

    returnedHomeworkTemplate.setModifiedBy(modifiedBy);
    returnedHomeworkTemplate.setActive(Boolean.FALSE);

    returnedHomeworkTemplate.getHomeworkQuestions()
        .forEach(homeworkTemplateLinkedHomeworkQuestion -> {
          homeworkTemplateLinkedHomeworkQuestion.setModifiedBy(modifiedBy);
          homeworkTemplateLinkedHomeworkQuestion.setActive(Boolean.FALSE);
        });

    this.homeworkTemplateDataService.saveHomeworkTemplate(returnedHomeworkTemplate);

    log.info("Homework Template with ID [{}] has been deactivated",
        returnedHomeworkTemplate.getId());
    return ResponseEntity.ok("Homework Template with ID [" + returnedHomeworkTemplate.getId()
        + "] has been deactivated");
  }

  public ResponseEntity<ErrorMessage> generateFailureResponse(Long homeworkTemplateId) {
    log.warn("Homework Template with ID [{}] not found", homeworkTemplateId);
    return new ResponseEntity<>(
        ErrorMessage.builder()
            .path(PATH)
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.NOT_FOUND.value())
            .error("Homework Template with ID [" + homeworkTemplateId + "] not found")
            .build(),
        HttpStatus.NOT_FOUND
    );
  }
}
