package com.cairn.waypoint.dashboard.endpoints.homeworkquestion;

import com.cairn.waypoint.dashboard.endpoints.ErrorMessage;
import com.cairn.waypoint.dashboard.service.data.HomeworkQuestionDataService;
import com.cairn.waypoint.dashboard.service.helper.HomeworkQuestionHelperService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.time.LocalDateTime;

@Slf4j
@RestController
@Tag(name = "Homework")
public class JoinQuestionToProtocolTemplate {

    public static final String PATH = "/api/protocol-template/{protocolTemplateId}/link-question/{questionId}";

    private final HomeworkQuestionDataService homeworkQuestionDataService;
    private final HomeworkQuestionHelperService homeworkQuestionHelperService;

    public JoinQuestionToProtocolTemplate(HomeworkQuestionHelperService homeworkQuestionHelperService,
                                                HomeworkQuestionDataService homeworkQuestionDataService) {
        this.homeworkQuestionDataService = homeworkQuestionDataService;
        this.homeworkQuestionHelperService = homeworkQuestionHelperService;
    }

    @PostMapping(PATH)
    @PreAuthorize("hasAnyAuthority('SCOPE_homework.full', 'SCOPE_admin.full')")
    @Operation(
        summary = "Links a question to a protocol template.",
        description = "Creates a link between a question and a protocol template. Requires the `homework.full` permission.",
        security = @SecurityRequirement(name = "oAuth2JwtBearer"),
        responses = {
            @ApiResponse(responseCode = "200", description = "Link created successfully.",
                content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "Template or question not found",
                content = @Content(schema = @Schema(hidden = true)))
        }
    )
    public ResponseEntity<?> linkQuestionToProtocolTemplate(@PathVariable("protocolTemplateId") Long protocolTemplateId,
                                                            @PathVariable("questionId") Long questionId,
                                                            Principal principal) {
        log.info("User [{}] is linking question [{}] to protocol template [{}]", principal.getName(), questionId, protocolTemplateId);

        try {
            homeworkQuestionDataService.linkQuestionToProtocolTemplate(questionId, protocolTemplateId);
            return ResponseEntity.ok("Question linked to Protocol Template successfully.");
        } catch (IllegalArgumentException e) {
            return generateFailureResponse(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Error linking question to protocol template", e);
            return generateFailureResponse("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
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
