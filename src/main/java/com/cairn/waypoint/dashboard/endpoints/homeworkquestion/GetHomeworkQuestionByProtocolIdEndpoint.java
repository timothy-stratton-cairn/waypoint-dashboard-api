package com.cairn.waypoint.dashboard.endpoints.homeworkquestion;

import com.cairn.waypoint.dashboard.endpoints.ErrorMessage;
import com.cairn.waypoint.dashboard.endpoints.homework.dto.HomeworkListDto;
import com.cairn.waypoint.dashboard.endpoints.homework.dto.HomeworkQuestionListDto;
import com.cairn.waypoint.dashboard.entity.HomeworkQuestion;
import com.cairn.waypoint.dashboard.service.data.HomeworkQuestionDataService;
import com.cairn.waypoint.dashboard.service.helper.HomeworkQuestionHelperService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.security.Principal;
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
public class GetHomeworkQuestionByProtocolIdEndpoint {

  public static final String PATH = "/api/homework-question/protocol/{protocolId}";

  private final HomeworkQuestionDataService homeworkQuestionDataService;
  private final HomeworkQuestionHelperService homeworkQuestionHelperService;

  public GetHomeworkQuestionByProtocolIdEndpoint(
      HomeworkQuestionHelperService homeworkQuestionHelperService,
      HomeworkQuestionDataService homeworkQuestionDataService) {
    this.homeworkQuestionDataService = homeworkQuestionDataService;
    this.homeworkQuestionHelperService = homeworkQuestionHelperService;
  }


  @GetMapping(PATH)
  @PreAuthorize("hasAnyAuthority('SCOPE_homework.full', 'SCOPE_admin.full')")
  @Operation(
      summary = "Retrieves all homework questions associated with the protocol ID.",
      description = "Retrieves all homework questions associated with the protocol ID. Requires the `homework.full` permission.",
      security = @SecurityRequirement(name = "oAuth2JwtBearer"),
      responses = {
          @ApiResponse(responseCode = "200",
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = HomeworkListDto.class))}),
          @ApiResponse(responseCode = "401", description = "Unauthorized",
              content = {@Content(schema = @Schema(hidden = true))}),
          @ApiResponse(responseCode = "403", description = "Forbidden",
              content = {@Content(schema = @Schema(hidden = true))})})

  public ResponseEntity<?> getAllQuestionsByProtocId(@PathVariable Long protocolId,
      Principal principal) {
    log.info("User [{}] is retrieving all homework of the protocol [{}]", principal.getName(),
        protocolId);

    List<HomeworkQuestion> questions = homeworkQuestionDataService.getHomeworkQuestionsByProtocolId(
        protocolId);
    if (questions.isEmpty()) {
      return generateFailureResponse("There are no Homeworks for the given protocol [" +
          protocolId + "]", HttpStatus.NOT_FOUND);
    } else {
      return ResponseEntity.ok(HomeworkQuestionListDto.builder()
          .questions(questions.stream()
              .map(homeworkQuestionHelperService::generateHomeworkQuestionDto)
              .collect(Collectors.toList()))
          .build());
    }
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
