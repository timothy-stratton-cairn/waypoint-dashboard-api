package com.cairn.waypoint.dashboard.endpoints.homeworkquestion;

import com.cairn.waypoint.dashboard.endpoints.ErrorMessage;
import com.cairn.waypoint.dashboard.endpoints.homeworkquestion.dto.HomeworkQuestionDto;
import com.cairn.waypoint.dashboard.endpoints.homeworkquestion.dto.HomeworkQuestionListDto;
import com.cairn.waypoint.dashboard.service.data.HomeworkQuestionDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.security.Principal;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Tag(name = "Homework Question")
public class GetAllHomeworkQuestionsEndpoint {

  public static final String PATH = "/api/homework-question";
  private final HomeworkQuestionDataService homeworkQuestionDataService;

  public GetAllHomeworkQuestionsEndpoint(HomeworkQuestionDataService homeworkQuestionDataService) {
    this.homeworkQuestionDataService = homeworkQuestionDataService;
  }

  @GetMapping(PATH)
  @PreAuthorize("hasAnyAuthority('SCOPE_homework.full', 'SCOPE_admin.full')")
  @Operation(
      summary = "Retrieves all homework questions.",
      description = "Retrieves all homework questions. Requires the `homework.full` permission.",
      security = @SecurityRequirement(name = "oAuth2JwtBearer"),
      responses = {
          @ApiResponse(responseCode = "200",
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = HomeworkQuestionListDto.class))}),
          @ApiResponse(responseCode = "401", description = "Unauthorized",
              content = {@Content(schema = @Schema(hidden = true))}),
          @ApiResponse(responseCode = "403", description = "Forbidden",
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ErrorMessage.class))}),
          @ApiResponse(responseCode = "404", description = "Not Found",
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ErrorMessage.class))})})
  public ResponseEntity<HomeworkQuestionListDto> getHomeworkQuestionById(Principal principal) {
    log.info("User [{}] is Retrieving All Homework Questions", principal.getName());

    return ResponseEntity.ok(
        HomeworkQuestionListDto.builder()
            .questions(
                this.homeworkQuestionDataService.getAllHomeworkQuestions().stream()
                    .map(homeworkQuestion -> HomeworkQuestionDto.builder()
                        .questionId(homeworkQuestion.getId())
                        .question(homeworkQuestion.getQuestion())
                        .questionAbbr(homeworkQuestion.getQuestionAbbreviation())
                        .categoryId(homeworkQuestion.getCategory()!=null? homeworkQuestion.getCategory().getId():null)
                        .status(homeworkQuestion.getStatus().name())
                        .build())
                    .collect(Collectors.toList()))
            .build()
    );
  }
}
