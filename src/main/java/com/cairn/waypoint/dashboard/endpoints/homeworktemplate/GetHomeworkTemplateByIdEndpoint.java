package com.cairn.waypoint.dashboard.endpoints.homeworktemplate;

import com.cairn.waypoint.dashboard.endpoints.ErrorMessage;
import com.cairn.waypoint.dashboard.endpoints.homeworktemplate.dto.ExpectedResponseDetailsDto;
import com.cairn.waypoint.dashboard.endpoints.homeworktemplate.dto.ExpectedResponseDetailsListDto;
import com.cairn.waypoint.dashboard.endpoints.homeworktemplate.dto.HomeworkQuestionDetailsDto;
import com.cairn.waypoint.dashboard.endpoints.homeworktemplate.dto.HomeworkQuestionDetailsListDto;
import com.cairn.waypoint.dashboard.endpoints.homeworktemplate.dto.HomeworkTemplateDetailsDto;
import com.cairn.waypoint.dashboard.endpoints.homeworktemplate.dto.TriggeredProtocolTemplateDetailsDto;
import com.cairn.waypoint.dashboard.entity.ExpectedResponse;
import com.cairn.waypoint.dashboard.entity.HomeworkTemplate;
import com.cairn.waypoint.dashboard.entity.ProtocolTemplate;
import com.cairn.waypoint.dashboard.service.data.HomeworkTemplateDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Tag(name = "Homework Template")
public class GetHomeworkTemplateByIdEndpoint {

  public static final String PATH = "/api/homework-template/{homeworkTemplateId}";

  private final HomeworkTemplateDataService homeworkTemplateDataService;

  public GetHomeworkTemplateByIdEndpoint(HomeworkTemplateDataService homeworkTemplateDataService) {
    this.homeworkTemplateDataService = homeworkTemplateDataService;
  }

  @GetMapping(PATH)
  @PreAuthorize("hasAnyAuthority('SCOPE_homework.template.full', 'SCOPE_admin.full')")
  @Operation(
      summary = "Retrieves a homework template by ID.",
      description = "Retrieves a homework template by ID. Requires the `homework.template.full` permission.",
      security = @SecurityRequirement(name = "oAuth2JwtBearer"),
      responses = {
          @ApiResponse(responseCode = "200",
              content = {@Content(mediaType = "application/json",
                  schema = @Schema(implementation = HomeworkTemplateDetailsDto.class))}),
          @ApiResponse(responseCode = "401", description = "Unauthorized",
              content = {@Content(schema = @Schema(hidden = true))}),
          @ApiResponse(responseCode = "403", description = "Forbidden",
              content = {@Content(mediaType = "application/json",
                  schema = @Schema(implementation = ErrorMessage.class))}),
          @ApiResponse(responseCode = "404", description = "Not Found",
              content = {@Content(mediaType = "application/json",
                  schema = @Schema(implementation = ErrorMessage.class))})})
  public ResponseEntity<?> getAllHomeworkTemplates(@PathVariable Long homeworkTemplateId,
      Principal principal) {
    log.info("User [{}] is Retrieving Homework Template with ID [{}]", principal.getName(),
        homeworkTemplateId);

    final ResponseEntity<?>[] response = new ResponseEntity<?>[1];
    this.homeworkTemplateDataService.getHomeworkTemplateById(homeworkTemplateId)
        .ifPresentOrElse(
            returnedHomeworkTemplate -> response[0] = generateSuccessResponse(
                returnedHomeworkTemplate),
            () -> response[0] = generateFailureResponse(homeworkTemplateId)
        );

    return response[0];
  }

  public ResponseEntity<HomeworkTemplateDetailsDto> generateSuccessResponse(
      HomeworkTemplate returnedHomeworkTemplate) {
    return ResponseEntity.ok(
        HomeworkTemplateDetailsDto.builder()
            .id(returnedHomeworkTemplate.getId())
            .name(returnedHomeworkTemplate.getName())
            .description(returnedHomeworkTemplate.getDescription())
            .isMultiResponse(returnedHomeworkTemplate.getMultiResponse())
            .homeworkQuestions(HomeworkQuestionDetailsListDto.builder()
                .questions(returnedHomeworkTemplate.getHomeworkQuestions().stream()
                    .map(
                        homeworkTemplateLinkedHomeworkQuestion -> HomeworkQuestionDetailsDto.builder()
                            .questionAbbreviation(
                                homeworkTemplateLinkedHomeworkQuestion.getHomeworkQuestion()
                                    .getQuestionAbbreviation())
                            .question(homeworkTemplateLinkedHomeworkQuestion.getHomeworkQuestion()
                                .getQuestion())
                            .questionType(
                                homeworkTemplateLinkedHomeworkQuestion.getHomeworkQuestion()
                                    .getQuestionType().name())
                            .isRequired(homeworkTemplateLinkedHomeworkQuestion.getHomeworkQuestion()
                                .getRequired())
                            .expectedHomeworkResponses(this.getExpectedResponseDetailsListDto(
                                homeworkTemplateLinkedHomeworkQuestion.getHomeworkQuestion()
                                    .getExpectedHomeworkResponses()))
                            .triggersProtocolCreation(
                                homeworkTemplateLinkedHomeworkQuestion.getHomeworkQuestion()
                                    .getTriggersProtocolCreation())
                            .triggeringResponse(this.getExpectedResponseDetailsDto(
                                homeworkTemplateLinkedHomeworkQuestion.getHomeworkQuestion()
                                    .getTriggeringResponse()))
                            .triggeredProtocol(this.getTriggeredProtocol(
                                homeworkTemplateLinkedHomeworkQuestion.getHomeworkQuestion()
                                    .getTriggeredProtocol()))
                            .build())
                    .toList())
                .build())
            .build());
  }

  private ExpectedResponseDetailsListDto getExpectedResponseDetailsListDto(
      List<ExpectedResponse> expectedResponses) {
    if (expectedResponses.isEmpty()) {
      return null;
    } else {
      return ExpectedResponseDetailsListDto.builder()
          .responses(expectedResponses.stream()
              .map(expectedResponse -> ExpectedResponseDetailsDto.builder()
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
          .response(expectedResponse.getResponse())
          .tooltip(expectedResponse.getTooltip())
          .build();
    }
  }

  private TriggeredProtocolTemplateDetailsDto getTriggeredProtocol(
      ProtocolTemplate protocolTemplate) {
    if (Objects.isNull(protocolTemplate)) {
      return null;
    } else {
      return TriggeredProtocolTemplateDetailsDto.builder()
          .id(protocolTemplate.getId())
          .name(protocolTemplate.getName())
          .description(protocolTemplate.getDescription())
          .build();
    }
  }

  public ResponseEntity<ErrorMessage> generateFailureResponse(Long protocolId) {
    log.info("Protocol with ID [{}] not found", protocolId);
    return new ResponseEntity<>(
        ErrorMessage.builder()
            .path(PATH)
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.NOT_FOUND.value())
            .error("Protocol with ID [" + protocolId + "] not found")
            .build(),
        HttpStatus.NOT_FOUND
    );
  }
}
