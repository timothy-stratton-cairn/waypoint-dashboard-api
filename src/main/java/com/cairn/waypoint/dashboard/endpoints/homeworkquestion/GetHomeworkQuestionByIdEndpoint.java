package com.cairn.waypoint.dashboard.endpoints.homeworkquestion;

import com.cairn.waypoint.dashboard.endpoints.ErrorMessage;
import com.cairn.waypoint.dashboard.endpoints.homeworkquestion.dto.ExpectedResponseDetailsDto;
import com.cairn.waypoint.dashboard.endpoints.homeworkquestion.dto.ExpectedResponseDetailsListDto;
import com.cairn.waypoint.dashboard.endpoints.homeworkquestion.dto.HomeworkQuestionDetailsDto;
import com.cairn.waypoint.dashboard.endpoints.homeworkquestion.dto.ProtocolTemplateDetailsDto;
import com.cairn.waypoint.dashboard.entity.ExpectedResponse;
import com.cairn.waypoint.dashboard.entity.HomeworkQuestion;
import com.cairn.waypoint.dashboard.entity.ProtocolTemplate;
import com.cairn.waypoint.dashboard.service.data.HomeworkQuestionDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;
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
@Tag(name = "Homework Question")
public class GetHomeworkQuestionByIdEndpoint {

  public static final String PATH = "/api/homework-question/{homeworkQuestionId}";
  private final HomeworkQuestionDataService homeworkQuestionDataService;

  public GetHomeworkQuestionByIdEndpoint(HomeworkQuestionDataService homeworkQuestionDataService) {
    this.homeworkQuestionDataService = homeworkQuestionDataService;
  }

  @GetMapping(PATH)
  @PreAuthorize("hasAnyAuthority('SCOPE_homework.full', 'SCOPE_admin.full')")
  @Operation(
      summary = "Retrieves a homework question by the provided homework question ID.",
      description = "Retrieves a homework question by the provided homework question ID. Requires the `homework.full` permission.",
      security = @SecurityRequirement(name = "oAuth2JwtBearer"),
      responses = {
          @ApiResponse(responseCode = "200",
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = HomeworkQuestionDetailsDto.class))}),
          @ApiResponse(responseCode = "401", description = "Unauthorized",
              content = {@Content(schema = @Schema(hidden = true))}),
          @ApiResponse(responseCode = "403", description = "Forbidden",
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ErrorMessage.class))}),
          @ApiResponse(responseCode = "404", description = "Not Found",
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ErrorMessage.class))})})
  public ResponseEntity<?> getHomeworkQuestionById(@PathVariable Long homeworkQuestionId,
      Principal principal) {
    log.info("User [{}] is Retrieving Homework Question with ID [{}]", principal.getName(),
        homeworkQuestionId);

    final ResponseEntity<?>[] response = new ResponseEntity<?>[1];
    this.homeworkQuestionDataService.getHomeworkQuestionById(homeworkQuestionId)
        .ifPresentOrElse(
            returnedHomeworkQuestion -> response[0] = generateSuccessResponse(
                returnedHomeworkQuestion),
            () -> response[0] = generateFailureResponse(homeworkQuestionId)
        );

    return response[0];
  }

  private ResponseEntity<HomeworkQuestionDetailsDto> generateSuccessResponse(
      HomeworkQuestion homeworkQuestion) {
    log.info("Returning Homework Question with ID [{}]", homeworkQuestion.getId());
    return ResponseEntity.ok(HomeworkQuestionDetailsDto.builder()
        .questionId(homeworkQuestion.getId())
        .questionAbbr(homeworkQuestion.getQuestionAbbreviation())
        .question(homeworkQuestion.getQuestion())
        .questionType(homeworkQuestion.getQuestionType().name())
        .status(homeworkQuestion.getStatus().name())
        .isRequired(homeworkQuestion.getRequired())
        .triggersProtocolCreation(homeworkQuestion.getTriggersProtocolCreation())
        .triggeredProtocol(getTriggeredProtocol(homeworkQuestion.getTriggeredProtocol()))
        .expectedHomeworkResponses(
            getExpectedResponseDetailsListDto(homeworkQuestion.getExpectedHomeworkResponses()))
        .triggeringResponse(getExpectedResponseDetailsDto(homeworkQuestion.getTriggeringResponse()))
        .build());
  }

  private ExpectedResponseDetailsListDto getExpectedResponseDetailsListDto(
      Set<ExpectedResponse> expectedResponses) {
    if (expectedResponses == null || expectedResponses.isEmpty()) {
      return null;
    } else {
      return ExpectedResponseDetailsListDto.builder()
          .responses(expectedResponses.stream()
              .map(expectedResponse -> ExpectedResponseDetailsDto.builder()
                  .id(expectedResponse.getId())
                  .response(expectedResponse.getResponse())
                  .tooltip(expectedResponse.getTooltip())
                  .build())
              .toList())
          .build();
    }
  }

  private ExpectedResponseDetailsDto getExpectedResponseDetailsDto(
      ExpectedResponse expectedResponse) {
    if (Objects.isNull(expectedResponse)) {
      return null;
    } else {
      return ExpectedResponseDetailsDto.builder()
          .id(expectedResponse.getId())
          .response(expectedResponse.getResponse())
          .tooltip(expectedResponse.getTooltip())
          .build();
    }
  }

  private ProtocolTemplateDetailsDto getTriggeredProtocol(
      ProtocolTemplate protocolTemplate) {
    if (Objects.isNull(protocolTemplate)) {
      return null;
    } else {
      return ProtocolTemplateDetailsDto.builder()
          .id(protocolTemplate.getId())
          .name(protocolTemplate.getName())
          .description(protocolTemplate.getDescription())
          .build();
    }
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
