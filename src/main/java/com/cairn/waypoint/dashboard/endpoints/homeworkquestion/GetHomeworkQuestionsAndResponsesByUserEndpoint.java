package com.cairn.waypoint.dashboard.endpoints.homeworkquestion;

import com.cairn.waypoint.dashboard.endpoints.ErrorMessage;
import com.cairn.waypoint.dashboard.endpoints.homeworkresponse.dto.QuestionResponsePairDto;
import com.cairn.waypoint.dashboard.endpoints.homeworkresponse.dto.QuestionResponsePairListDto;
import com.cairn.waypoint.dashboard.endpoints.homeworkresponse.dto.SimplifiedHomeworkResponseDto;
import com.cairn.waypoint.dashboard.entity.HomeworkQuestion;
import com.cairn.waypoint.dashboard.entity.HomeworkResponse;
import com.cairn.waypoint.dashboard.entity.Protocol;
import com.cairn.waypoint.dashboard.service.data.HomeworkQuestionLinkedProtocolTemplateDataService;
import com.cairn.waypoint.dashboard.service.data.HomeworkResponseDataService;
import com.cairn.waypoint.dashboard.service.data.ProtocolDataService;
import com.cairn.waypoint.dashboard.service.data.QuestionResponsePairDataService;
import com.cairn.waypoint.dashboard.service.helper.SimplifiedMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
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
public class GetHomeworkQuestionsAndResponsesByUserEndpoint {

  public static final String PATH = "/api/homework-question-response/user/{userId}";

  private final QuestionResponsePairDataService questionResponsePairDataService;
  private final ProtocolDataService protocolDataService;
  private final HomeworkQuestionLinkedProtocolTemplateDataService homeworkQuestionLinkedProtocolTemplateDataService;
  private final SimplifiedMapper simplifiedMapper;
  private final HomeworkResponseDataService homeworkResponseDataService;

  public GetHomeworkQuestionsAndResponsesByUserEndpoint(
      QuestionResponsePairDataService questionResponsePairDataService,
      ProtocolDataService protocolDataService,
      HomeworkQuestionLinkedProtocolTemplateDataService homeworkQuestionLinkedProtocolTemplateDataService,
      SimplifiedMapper simplifiedMapper,
      HomeworkResponseDataService homeworkResponseDataService) {
    this.questionResponsePairDataService = questionResponsePairDataService;
    this.protocolDataService = protocolDataService;
    this.homeworkQuestionLinkedProtocolTemplateDataService = homeworkQuestionLinkedProtocolTemplateDataService;
    this.simplifiedMapper = simplifiedMapper;
    this.homeworkResponseDataService = homeworkResponseDataService;
  }

  @GetMapping(PATH)
  @PreAuthorize("hasAnyAuthority('SCOPE_homework.full', 'SCOPE_admin.full')")
  @Operation(
      summary = "Retrieves all homework questions and responses associated with the user ID.",
      description = "Retrieves all homework questions and their responses associated with the user ID. If there’s no response for a question, the response will be null.",
      security = @SecurityRequirement(name = "oAuth2JwtBearer"),
      responses = {
          @ApiResponse(responseCode = "200",
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = QuestionResponsePairListDto.class))}),
          @ApiResponse(responseCode = "401", description = "Unauthorized",
              content = {@Content(schema = @Schema(hidden = true))}),
          @ApiResponse(responseCode = "403", description = "Forbidden",
              content = {@Content(schema = @Schema(hidden = true))}),
          @ApiResponse(responseCode = "404", description = "User not found",
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ErrorMessage.class))})
      })
  public ResponseEntity<?> getHomeworkQuestionsAndResponsesByUser(@PathVariable Long userId) {
    try {
      List<HomeworkQuestion> questions = findAllQuestionsByUser(userId);

      List<HomeworkResponse> responses = homeworkResponseDataService.getAllResponsesByUser_Id(
          userId);

      List<QuestionResponsePairDto> questionResponsePairs = questions.stream()
          .map(question -> {
            SimplifiedHomeworkQuestionDto simQuestion = simplifiedMapper.simplifyQuestion(question);

            HomeworkResponse matchedResponse = responses.stream()
                .filter(response -> response.getHomeworkQuestion().getId().equals(question.getId()))
                .findFirst()
                .orElse(null);

            SimplifiedHomeworkResponseDto simResponse = matchedResponse != null
                ? simplifiedMapper.simplifyResponse(matchedResponse)
                : null;

            return new QuestionResponsePairDto(simQuestion, simResponse);
          })
          .collect(Collectors.toList());

      QuestionResponsePairListDto filteredResponse = new QuestionResponsePairListDto(
          filterLatestResponses(
              new QuestionResponsePairListDto(questionResponsePairs)).getQuestions());

      if (filteredResponse.getNumberOfPairs() == 0) {
        return generateFailureResponse("No questions or responses found for user ID: " + userId,
            HttpStatus.NOT_FOUND);
      }

      return ResponseEntity.ok(filteredResponse);

    } catch (EntityNotFoundException e) {
      return generateFailureResponse("User with ID " + userId + " not found.",
          HttpStatus.NOT_FOUND);

    } catch (Exception e) {
      log.error(
          "An error occurred while retrieving homework questions and responses for user ID: {}",
          userId, e);
      return generateFailureResponse("An unexpected error occurred.",
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  private List<HomeworkQuestion> findAllQuestionsByUser(Long userId) {

    List<Protocol> protocols = protocolDataService.getByUserId(userId);

    if (protocols.isEmpty()) {
      throw new EntityNotFoundException("No protocols found for user ID: " + userId);
    }

    List<Long> protocolTemplateIds = protocols.stream()
        .map(protocol -> protocol.getProtocolTemplate().getId())
        .distinct()
        .collect(Collectors.toList());

    return protocolTemplateIds.stream()
        .flatMap(templateId ->
            homeworkQuestionLinkedProtocolTemplateDataService.findAllByProtocolTemplate(templateId)
                .stream()
                .map(linked -> linked.getQuestion())
        )
        .distinct()
        .collect(Collectors.toList());
  }


  private QuestionResponsePairListDto filterLatestResponses(QuestionResponsePairListDto pairs) {
    List<QuestionResponsePairDto> filteredPairs = pairs.getQuestions().stream()
        .collect(Collectors.toMap(
            pair -> pair.getQuestion().getId(),
            pair -> pair,
            (pair1, pair2) -> {
              if (pair1.getResponse() == null) {
                return pair2;
              }
              if (pair2.getResponse() == null) {
                return pair1;
              }
              return pair1.getResponse().getUpdated().isAfter(pair2.getResponse().getUpdated())
                  ? pair1 : pair2;
            }
        ))
        .values()
        .stream()
        .collect(Collectors.toList());
    return new QuestionResponsePairListDto(filteredPairs);
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
