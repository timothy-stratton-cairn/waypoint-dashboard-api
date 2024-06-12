package com.cairn.waypoint.dashboard.endpoints.homework;

import com.cairn.waypoint.dashboard.endpoints.ErrorMessage;
import com.cairn.waypoint.dashboard.entity.Homework;
import com.cairn.waypoint.dashboard.service.data.HomeworkDataService;
import com.cairn.waypoint.dashboard.service.data.ProtocolStepDataService;
import com.cairn.waypoint.dashboard.service.data.ProtocolStepLinkedHomeworkService;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Tag(name = "Homework")
public class DeleteHomeworkByIdEndpoint {

  public static final String PATH = "/api/homework/{homeworkId}";
  private final HomeworkDataService homeworkDataService;
  private final ProtocolStepDataService protocolStepDataService;
  private final ProtocolStepLinkedHomeworkService protocolStepLinkedHomeworkService;

  public DeleteHomeworkByIdEndpoint(HomeworkDataService homeworkDataService,
      ProtocolStepDataService protocolStepDataService,
      ProtocolStepLinkedHomeworkService protocolStepLinkedHomeworkService) {
    this.homeworkDataService = homeworkDataService;
    this.protocolStepDataService = protocolStepDataService;
    this.protocolStepLinkedHomeworkService = protocolStepLinkedHomeworkService;
  }

  @DeleteMapping(PATH)
  @PreAuthorize("hasAnyAuthority('SCOPE_homework.full', 'SCOPE_admin.full')")
  @Operation(
      summary = "Deactivates a homework and its responses by the provided homework ID.",
      description = "Deactivates a homework and its responses by the provided homework ID. Requires the `homework.full` permission.",
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
  public ResponseEntity<?> deactivateHomeworkById(@PathVariable Long homeworkId,
      Principal principal) {
    log.info("User [{}] is Deactivating Homework with ID [{}]", principal.getName(),
        homeworkId);

    final ResponseEntity<?>[] response = new ResponseEntity<?>[1];
    this.homeworkDataService.getHomeworkById(homeworkId)
        .ifPresentOrElse(
            returnedHomework -> response[0] = generateSuccessResponse(
                returnedHomework, principal.getName()),
            () -> response[0] = generateFailureResponse(homeworkId)
        );

    return response[0];
  }

  private ResponseEntity<String> generateSuccessResponse(Homework homework, String modifiedBy) {
    protocolStepLinkedHomeworkService.getProtocolStepLinkedHomeworkByHomework(homework).stream()
        .peek(protocolStepLinkedHomework -> {
          protocolStepLinkedHomework.setModifiedBy(modifiedBy);
          protocolStepLinkedHomework.setActive(Boolean.FALSE);
        })
        .forEach(protocolStepLinkedHomeworkService::saveProtocolStepLinkedHomework);

    homework.setActive(Boolean.FALSE);
    homework.setModifiedBy(modifiedBy);

    homework.getHomeworkQuestions().forEach(response -> {
      response.setModifiedBy(modifiedBy);
      response.setActive(Boolean.FALSE);
    });

    homeworkDataService.saveHomework(homework);

    log.info("Homework with ID [{}] has been deactivated", homework.getId());
    return ResponseEntity.ok("Homework with ID [" + homework.getId() + "] has been deactivated");
  }

  public ResponseEntity<ErrorMessage> generateFailureResponse(Long homeworkId) {
    log.warn("Homework with ID [{}] not found", homeworkId);
    return new ResponseEntity<>(
        ErrorMessage.builder()
            .path(PATH)
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.NOT_FOUND.value())
            .error("Homework with ID [" + homeworkId + "] not found")
            .build(),
        HttpStatus.NOT_FOUND
    );
  }
}
