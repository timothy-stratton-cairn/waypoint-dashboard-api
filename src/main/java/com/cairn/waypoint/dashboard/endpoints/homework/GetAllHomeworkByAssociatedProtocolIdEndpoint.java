package com.cairn.waypoint.dashboard.endpoints.homework;

import com.cairn.waypoint.dashboard.endpoints.ErrorMessage;
import com.cairn.waypoint.dashboard.endpoints.homework.dto.HomeworkDto;
import com.cairn.waypoint.dashboard.endpoints.homework.dto.HomeworkListDto;
import com.cairn.waypoint.dashboard.endpoints.homework.dto.HomeworkQuestionDto;
import com.cairn.waypoint.dashboard.endpoints.homework.dto.HomeworkQuestionListDto;
import com.cairn.waypoint.dashboard.endpoints.homework.dto.ProtocolTemplateDto;
import com.cairn.waypoint.dashboard.entity.HomeworkQuestion;
import com.cairn.waypoint.dashboard.entity.Protocol;
import com.cairn.waypoint.dashboard.entity.ProtocolStep;
import com.cairn.waypoint.dashboard.service.data.ProtocolDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Tag(name = "Homework")
public class GetAllHomeworkByAssociatedProtocolIdEndpoint {

  public static final String PATH = "/api/homework/protocol/{protocolId}";

  private final ProtocolDataService protocolDataService;

  public GetAllHomeworkByAssociatedProtocolIdEndpoint(ProtocolDataService protocolDataService) {
    this.protocolDataService = protocolDataService;
  }

  @GetMapping(PATH)
  @PreAuthorize("hasAnyAuthority('SCOPE_homework.full', 'SCOPE_admin.full')")
  @Operation(
      summary = "Retrieves all homework associated with the provided protocol ID.",
      description = "Retrieves all homework associated with the provided protocol ID. Requires the `homework.full` permission.",
      security = @SecurityRequirement(name = "oAuth2JwtBearer"),
      responses = {
          @ApiResponse(responseCode = "200",
              content = {@Content(mediaType = "application/json",
                  schema = @Schema(implementation = HomeworkListDto.class))}),
          @ApiResponse(responseCode = "401", description = "Unauthorized",
              content = {@Content(schema = @Schema(hidden = true))}),
          @ApiResponse(responseCode = "403", description = "Forbidden",
              content = {@Content(schema = @Schema(hidden = true))})})
  public ResponseEntity<?> getAllHomeworkTemplates(@PathVariable Long protocolId,
      Principal principal) {
    log.info("User [{}] is retrieving all homework associated with Protocol with ID [{}]",
        principal.getName(), protocolId);

    final ResponseEntity<?>[] response = new ResponseEntity<?>[1];
    this.protocolDataService.getProtocolById(protocolId)
        .ifPresentOrElse(
            returnedProtocol -> response[0] = generateSuccessResponse(returnedProtocol),
            () -> response[0] = generateFailureResponse("Protocol with id [" +
                protocolId + "] not found", HttpStatus.NOT_FOUND)
        );

    return response[0];
  }

  public ResponseEntity<HomeworkListDto> generateSuccessResponse(Protocol returnedProtocol) {
    return ResponseEntity.ok(
        HomeworkListDto.builder()
            .homeworks(returnedProtocol.getProtocolSteps().stream()
                .map(ProtocolStep::getLinkedHomework)
                .filter(Objects::nonNull)
                .map(homework -> HomeworkDto.builder()
                    .name(homework.getName())
                    .description(homework.getDescription())
                    .parentProtocolId(
                        homework.getAssociatedProtocolStep().getParentProtocol().getId())
                    .parentProtocolStepId(homework.getAssociatedProtocolStep().getId())
                    .homeworkQuestions(HomeworkQuestionListDto.builder()
                        .questions(homework.getHomeworkQuestions().stream()
                            .map(homeworkResponse -> HomeworkQuestionDto.builder()
                                .questionAbbr(homeworkResponse.getHomeworkQuestion()
                                    .getQuestionAbbreviation())
                                .question(homeworkResponse.getHomeworkQuestion().getQuestion())
                                .userResponse(homeworkResponse.getResponse())
                                .isRequired(homeworkResponse.getHomeworkQuestion().getRequired())
                                .questionType(
                                    homeworkResponse.getHomeworkQuestion().getQuestionType().name())
                                .triggersProtocolCreation(homeworkResponse.getHomeworkQuestion()
                                    .getTriggersProtocolCreation())
                                .triggeredProtocol(
                                    getHomeworkTemplateDto(homeworkResponse.getHomeworkQuestion()))
                                .build())
                            .toList())
                        .build())
                    .build())
                .collect(Collectors.toList()))
            .build());
  }

  private ProtocolTemplateDto getHomeworkTemplateDto(HomeworkQuestion homeworkQuestion) {
    if (!homeworkQuestion.getTriggersProtocolCreation()) {
      return null;
    } else if (homeworkQuestion.getTriggeredProtocol() == null) {
      return null;
    } else {
      return ProtocolTemplateDto.builder()
          .id(homeworkQuestion.getTriggeredProtocol().getId())
          .name(homeworkQuestion.getTriggeredProtocol().getName())
          .build();
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
