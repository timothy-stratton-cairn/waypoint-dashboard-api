package com.cairn.waypoint.dashboard.endpoints.homeworkquestion;

import com.cairn.waypoint.dashboard.endpoints.ErrorMessage;
import com.cairn.waypoint.dashboard.entity.HomeworkQuestion;
import com.cairn.waypoint.dashboard.entity.HomeworkQuestionLinkedProtocolTemplate;
import com.cairn.waypoint.dashboard.entity.HomeworkResponse;

import com.cairn.waypoint.dashboard.service.data.HomeworkQuestionDataService;
import com.cairn.waypoint.dashboard.service.data.HomeworkQuestionLinkedProtocolTemplateDataService;
import com.cairn.waypoint.dashboard.service.data.HomeworkResponseDataService;
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
@Tag(name = "Homework Question")
public class DeleteHomeworkQuestionByIdEndpoint {

  public static final String PATH = "/api/homework-question/{homeworkQuestionId}";
  private final HomeworkQuestionDataService homeworkQuestionDataService;
  //private final HomeworkTemplateLinkedHomeworkQuestionDataService homeworkTemplateLinkedHomeworkQuestionDataService;
  private final HomeworkQuestionLinkedProtocolTemplateDataService questionProtocolTemplateDataService;
  private final HomeworkResponseDataService homeworkResponseDataService;

  public DeleteHomeworkQuestionByIdEndpoint(HomeworkQuestionDataService homeworkQuestionDataService,
	  HomeworkQuestionLinkedProtocolTemplateDataService questionProtocolTemplateDataService,
      //HomeworkTemplateLinkedHomeworkQuestionDataService homeworkTemplateLinkedHomeworkQuestionDataService,
      HomeworkResponseDataService homeworkResponseDataService) {
    this.homeworkQuestionDataService = homeworkQuestionDataService;
    this.questionProtocolTemplateDataService = questionProtocolTemplateDataService;
    //this.homeworkTemplateLinkedHomeworkQuestionDataService = homeworkTemplateLinkedHomeworkQuestionDataService;
    this.homeworkResponseDataService = homeworkResponseDataService;
  }

  @DeleteMapping(PATH)
  @PreAuthorize("hasAnyAuthority('SCOPE_homework.full', 'SCOPE_admin.full')")
  @Operation(
      summary = "Deactivates a homework question and its responses by the provided homework question ID.",
      description = "Deactivates a homework question and its responses by the provided homework question ID. Requires the `homework.full` permission.",
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
  public ResponseEntity<?> deactivateHomeworkQuestionById(@PathVariable Long homeworkQuestionId,
      Principal principal) {
    log.info("User [{}] is Deactivating Homework Question with ID [{}]", principal.getName(),
        homeworkQuestionId);

    final ResponseEntity<?>[] response = new ResponseEntity<?>[1];
    this.homeworkQuestionDataService.getHomeworkQuestionById(homeworkQuestionId)
        .ifPresentOrElse(
            returnedHomeworkQuestion -> response[0] = generateSuccessResponse(
                returnedHomeworkQuestion, principal.getName()),
            () -> response[0] = generateFailureResponse(homeworkQuestionId)
        );

    return response[0];
  }

  private ResponseEntity<String> generateSuccessResponse(HomeworkQuestion homeworkQuestion,
      String modifiedBy) {
    List<HomeworkQuestion> protocolTemplateLinkedHomeworkQuestionList =
    		questionProtocolTemplateDataService.findAllQuestionsByQuestionId(homeworkQuestion.getId());

    protocolTemplateLinkedHomeworkQuestionList.forEach(protocolTemplateLinkedHomeworkQuestion -> {
      protocolTemplateLinkedHomeworkQuestion.setModifiedBy(modifiedBy);
      protocolTemplateLinkedHomeworkQuestion.setActive(Boolean.FALSE);
      homeworkQuestionDataService.saveHomeworkQuestion(protocolTemplateLinkedHomeworkQuestion);
    });

    List<HomeworkResponse> homeworkResponses = homeworkResponseDataService.getAllHomeworkResponsesByHomeworkQuestion(
        homeworkQuestion);

    homeworkResponses.forEach(homeworkResponse -> {
      homeworkResponse.setModifiedBy(modifiedBy);
      homeworkResponse.setActive(Boolean.FALSE);
      homeworkResponseDataService.saveHomeworkResponse(homeworkResponse);
    });

    homeworkQuestion.setModifiedBy(modifiedBy);
    homeworkQuestion.setActive(Boolean.FALSE);

    homeworkQuestionDataService.saveHomeworkQuestion(homeworkQuestion);

    log.info("Homework Question with ID [{}] has been deactivated", homeworkQuestion.getId());
    return ResponseEntity.ok(
        "Homework Question with ID [" + homeworkQuestion.getId() + "] has been deactivated");
  }

  public ResponseEntity<ErrorMessage> generateFailureResponse(Long homeworkQuestionId) {
    log.warn("Homework Question with ID [{}] not found", homeworkQuestionId);
    return new ResponseEntity<>(
        ErrorMessage.builder()
            .path(PATH)
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.NOT_FOUND.value())
            .error("Homework Question with ID [" + homeworkQuestionId + "] not found")
            .build(),
        HttpStatus.NOT_FOUND
    );
  }
}
