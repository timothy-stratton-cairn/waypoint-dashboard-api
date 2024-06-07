package com.cairn.waypoint.dashboard.endpoints.questiontype;

import com.cairn.waypoint.dashboard.endpoints.questiontype.dto.QuestionTypeDto;
import com.cairn.waypoint.dashboard.endpoints.questiontype.dto.QuestionTypeListDto;
import com.cairn.waypoint.dashboard.endpoints.stepstatus.dto.StepStatusListDto;
import com.cairn.waypoint.dashboard.entity.enumeration.QuestionTypeEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.security.Principal;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Tag(name = "Homework Question Type")
public class GetAllQuestionTypesEndpoint {

  public static final String PATH = "/api/homework-question/type";

  @GetMapping(PATH)
  @PreAuthorize("hasAnyAuthority('SCOPE_homework.question.full', 'SCOPE_admin.full')")
  @Operation(
      summary = "Retrieves all homework question types.",
      description = "Retrieves all homework question types. Requires the `homework.question.full` permission.",
      security = @SecurityRequirement(name = "oAuth2JwtBearer"),
      responses = {
          @ApiResponse(responseCode = "200",
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = StepStatusListDto.class))}),
          @ApiResponse(responseCode = "401", description = "Unauthorized",
              content = {@Content(schema = @Schema(hidden = true))}),
          @ApiResponse(responseCode = "403", description = "Forbidden",
              content = {@Content(schema = @Schema(hidden = true))})})
  public ResponseEntity<QuestionTypeListDto> getAllHomeworkQuestionTypes(Principal principal) {
    log.info("User [{}] is Retrieving All Homework Question Types", principal.getName());
    return ResponseEntity.ok(QuestionTypeListDto.builder()
        .statuses(Stream.of(QuestionTypeEnum.values())
            .map(questionTypeEnum -> QuestionTypeDto.builder()
                .status(questionTypeEnum.name())
                .build())
            .toList())
        .build()
    );
  }

}
