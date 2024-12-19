package com.cairn.waypoint.dashboard.endpoints.homeworkresponse.dto;

import com.cairn.waypoint.dashboard.endpoints.ErrorMessage;
import com.cairn.waypoint.dashboard.entity.HomeworkCategory;
import com.cairn.waypoint.dashboard.entity.HomeworkQuestion;
import com.cairn.waypoint.dashboard.entity.HomeworkResponse;
import com.cairn.waypoint.dashboard.mapper.HomeworkResponseMapper;
import com.cairn.waypoint.dashboard.service.data.HomeworkCategoryDataService;
import com.cairn.waypoint.dashboard.service.data.HomeworkQuestionDataService;
import com.cairn.waypoint.dashboard.service.data.HomeworkResponseDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
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
@Tag(name = "Homework Response")
public class AddHomeworkResponseEndpoint {

  public static final String PATH = "/api/homework-response";

  private final HomeworkResponseDataService homeworkResponseDataService;
  private final HomeworkQuestionDataService homeworkQuestionDataService;
  private final HomeworkCategoryDataService homeworkCategoryDataService;
  private final HomeworkResponseMapper homeworkResponseMapper = HomeworkResponseMapper.INSTANCE;

  public AddHomeworkResponseEndpoint(HomeworkResponseDataService homeworkResponseDataService,
      HomeworkQuestionDataService homeworkQuestionDataService,
      HomeworkCategoryDataService homeworkCategoryDataService
  ) {
    this.homeworkResponseDataService = homeworkResponseDataService;
    this.homeworkQuestionDataService = homeworkQuestionDataService;
    this.homeworkCategoryDataService = homeworkCategoryDataService;
  }

  @Transactional
  @PostMapping(PATH)
  @PreAuthorize("hasAnyAuthority('SCOPE_homework.full', 'SCOPE_admin.full')")
  @Operation(
      summary = "Allows a user to add a homework response",
      description = "Allows a user to add a new homework response linked to a specific question. Requires the `homework.full` permission.",
      security = @SecurityRequirement(name = "oAuth2JwtBearer"),
      responses = {
          @ApiResponse(responseCode = "201",
              description = "Created - Homework response creation was successful",
              content = {@Content(mediaType = "application/json",
                  schema = @Schema(implementation = HomeworkResponseDto.class))}),
          @ApiResponse(responseCode = "400", description = "Bad Request",
              content = {@Content(schema = @Schema(hidden = true))}),
          @ApiResponse(responseCode = "404", description = "Not Found",
              content = {@Content(mediaType = "application/json",
                  schema = @Schema(implementation = ErrorMessage.class))}),
          @ApiResponse(responseCode = "409", description = "Conflict",
              content = {@Content(mediaType = "application/json",
                  schema = @Schema(implementation = ErrorMessage.class))}),
      })
  public ResponseEntity<?> addHomeworkResponse(
      @RequestBody AddHomeworkQuestionResponseDto addHomeworkResponseDto, Principal principal) {

    log.info("Creating homework response for question ID [{}]",
        addHomeworkResponseDto.getQuestionId());

    Optional<HomeworkQuestion> homeworkQuestionOpt =
        homeworkQuestionDataService.getHomeworkQuestionById(addHomeworkResponseDto.getQuestionId());

    if (homeworkQuestionOpt.isEmpty()) {
      return generateFailureResponse("Homework question with ID [" +
              addHomeworkResponseDto.getQuestionId() + "] does not exist",
          HttpStatus.NOT_FOUND);
    }


    HomeworkResponse homeworkResponse = HomeworkResponse.builder()
        .response(addHomeworkResponseDto.getResponse())
        .homeworkQuestion(homeworkQuestionOpt.get())
        .userId(addHomeworkResponseDto.getUserId())
        .modifiedBy(principal.getName())
        .updated(LocalDateTime.now())
        .ordinalIndex(2)
        .fileGuid(addHomeworkResponseDto.getFileGuid())
        .active(true)
        .build();

    HomeworkResponse createdResponse = homeworkResponseDataService.saveHomeworkResponse(
        homeworkResponse);

    HomeworkResponseDto responseDto = HomeworkResponseDto.builder()
        .responseId(createdResponse.getId())
        .questionId(createdResponse.getHomeworkQuestion().getId())
        .userId(createdResponse.getUserId())
        .response(createdResponse.getResponse())
        .fileGuid(createdResponse.getFileGuid())
        .build();

    log.info("Homework response successfully created for question ID [{}]",
        addHomeworkResponseDto.getQuestionId());

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(responseDto);
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
