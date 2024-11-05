package com.cairn.waypoint.dashboard.endpoints.homeworkquestion;
import com.cairn.waypoint.dashboard.endpoints.ErrorMessage;
import com.cairn.waypoint.dashboard.endpoints.homeworkresponse.dto.HomeworkResponseListDto;
import com.cairn.waypoint.dashboard.endpoints.homeworkresponse.dto.QuestionResponsePairListDto;
import com.cairn.waypoint.dashboard.service.data.QuestionResponsePairDataService;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Tag(name = "HomeworkQuestion")
public class GetHomeworkQuestionsAndResponsesByUserEndpoint {

    public static final String PATH = "/api/homework-question-response/user/{userId}";

    private final QuestionResponsePairDataService questionResponsePairDataService;

    public GetHomeworkQuestionsAndResponsesByUserEndpoint(QuestionResponsePairDataService questionResponsePairDataService) {
        this.questionResponsePairDataService = questionResponsePairDataService;
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
                                    schema = @Schema(implementation = HomeworkResponseListDto.class))}),
                    @ApiResponse(responseCode = "401", description = "Unauthorized",
                            content = {@Content(schema = @Schema(hidden = true))}),
                    @ApiResponse(responseCode = "403", description = "Forbidden",
                            content = {@Content(schema = @Schema(hidden = true))})
            })
    public ResponseEntity<QuestionResponsePairListDto> getAllQuestionsByUserId(@PathVariable Long userId, Principal principal) {
        log.info("User [{}] is retrieving all questions and responses for user ID [{}]", principal.getName(), userId);
        QuestionResponsePairListDto questionResponsePairs = questionResponsePairDataService.getQuestionResponsePairsByUserId(userId);

        return ResponseEntity.ok(questionResponsePairs);
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


