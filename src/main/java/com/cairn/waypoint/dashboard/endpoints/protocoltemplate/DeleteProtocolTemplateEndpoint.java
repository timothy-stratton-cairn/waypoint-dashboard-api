package com.cairn.waypoint.dashboard.endpoints.protocoltemplate;

import com.cairn.waypoint.dashboard.endpoints.ErrorMessage;
import com.cairn.waypoint.dashboard.entity.HomeworkQuestion;
import com.cairn.waypoint.dashboard.entity.Protocol;
import com.cairn.waypoint.dashboard.entity.ProtocolTemplate;
import com.cairn.waypoint.dashboard.service.data.HomeworkQuestionDataService;
import com.cairn.waypoint.dashboard.service.data.ProtocolDataService;
import com.cairn.waypoint.dashboard.service.data.ProtocolTemplateDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Tag(name = "Protocol Template")
public class DeleteProtocolTemplateEndpoint {

  public static final String PATH = "/api/protocol-template/{protocolTemplateId}";

  private final ProtocolTemplateDataService protocolTemplateDataService;
  private final ProtocolDataService protocolDataService;
  private final HomeworkQuestionDataService homeworkQuestionDataService;

  public DeleteProtocolTemplateEndpoint(ProtocolTemplateDataService protocolTemplateDataService,
      ProtocolDataService protocolDataService,
      HomeworkQuestionDataService homeworkQuestionDataService) {
    this.protocolTemplateDataService = protocolTemplateDataService;
    this.protocolDataService = protocolDataService;
    this.homeworkQuestionDataService = homeworkQuestionDataService;
  }

  @Transactional
  @DeleteMapping(PATH)
  @PreAuthorize("hasAnyAuthority('SCOPE_protocol.template.full', 'SCOPE_admin.full')")
  @Operation(
      summary = "Allows a user to deactivate a protocol template.",
      description = "Allows a user to deactivate a protocol template."
          + " Requires the `protocol.template.full` permission.",
      security = @SecurityRequirement(name = "oAuth2JwtBearer"),
      responses = {
          @ApiResponse(responseCode = "200",
              content = {@Content(mediaType = MediaType.TEXT_PLAIN_VALUE,
                  schema = @Schema(implementation = String.class))}),
          @ApiResponse(responseCode = "401", description = "Unauthorized",
              content = {@Content(schema = @Schema(hidden = true))}),
          @ApiResponse(responseCode = "403", description = "Forbidden",
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ErrorMessage.class))}),
          @ApiResponse(responseCode = "404", description = "Not Found",
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ErrorMessage.class))})})
  public ResponseEntity<?> deleteProtocolTemplate(@PathVariable Long protocolTemplateId,
      Principal principal) {
    log.info("User [{}] is attempting to deactivate a Protocol Template with id [{}]",
        principal.getName(), protocolTemplateId);

    final ResponseEntity<?>[] response = new ResponseEntity<?>[1];
    this.protocolTemplateDataService.getProtocolTemplateById(protocolTemplateId)
        .ifPresentOrElse(
            returnedProtocolTemplate -> response[0] = generateSuccessResponse(
                returnedProtocolTemplate,
                principal.getName()),
            () -> response[0] = generateFailureResponse(protocolTemplateId)
        );

    return response[0];
  }

  public ResponseEntity<String> generateSuccessResponse(
      ProtocolTemplate protocolTemplateToDelete, String modifiedBy) {
    List<HomeworkQuestion> associatedHomeworkQuestions = homeworkQuestionDataService.findByTriggeredProtocol(
        protocolTemplateToDelete);
    associatedHomeworkQuestions.forEach(homeworkQuestion -> {
      homeworkQuestion.setModifiedBy(modifiedBy);
      homeworkQuestion.setTriggeredProtocol(null);
      homeworkQuestionDataService.saveHomeworkQuestion(homeworkQuestion);
    });

    List<Protocol> associatedProtocols = protocolDataService.getByProtocolTemplateId(
        protocolTemplateToDelete.getId());

    associatedProtocols.forEach(protocolToDelete -> {
      protocolToDelete.setActive(Boolean.FALSE);
      protocolToDelete.setModifiedBy(modifiedBy);

      protocolToDelete.getComments().forEach(comment -> {
        comment.setActive(Boolean.FALSE);
        comment.setModifiedBy(modifiedBy);
      });
      protocolToDelete.getProtocolSteps().forEach(step -> {
        step.getNotes().forEach(note -> {
          note.setActive(Boolean.FALSE);
          note.setModifiedBy(modifiedBy);
        });
        step.getLinkedHomework().forEach(homeworkLink -> {
          homeworkLink.setActive(Boolean.FALSE);
          homeworkLink.setModifiedBy(modifiedBy);
        });

        step.setActive(Boolean.FALSE);
        step.setModifiedBy(modifiedBy);
      });

      protocolDataService.updateProtocol(protocolToDelete);
    });

    protocolTemplateToDelete.setActive(Boolean.FALSE);
    protocolTemplateToDelete.setModifiedBy(modifiedBy);

    protocolTemplateToDelete.getProtocolTemplateSteps().forEach(templateStep -> {
      templateStep.setActive(Boolean.FALSE);
      templateStep.setModifiedBy(modifiedBy);
    });

    protocolTemplateDataService.saveProtocolTemplate(protocolTemplateToDelete);

    log.info("Protocol Template with ID [{}] has been deactivated",
        protocolTemplateToDelete.getId());
    return ResponseEntity.ok("Protocol Template with ID [" + protocolTemplateToDelete.getId()
        + "] has been deactivated");
  }

  public ResponseEntity<ErrorMessage> generateFailureResponse(Long protocolTemplateId) {
    log.warn("Protocol Template with ID [{}] not found", protocolTemplateId);
    return new ResponseEntity<>(
        ErrorMessage.builder()
            .path(PATH)
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.NOT_FOUND.value())
            .error("Protocol Template with ID [" + protocolTemplateId + "] not found")
            .build(),
        HttpStatus.NOT_FOUND
    );
  }
}
