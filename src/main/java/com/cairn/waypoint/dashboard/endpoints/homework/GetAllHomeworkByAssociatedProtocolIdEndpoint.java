package com.cairn.waypoint.dashboard.endpoints.homework;

import com.cairn.waypoint.dashboard.endpoints.ErrorMessage;
import com.cairn.waypoint.dashboard.endpoints.homework.dto.HomeworkListDto;
import com.cairn.waypoint.dashboard.entity.Protocol;
import com.cairn.waypoint.dashboard.entity.ProtocolStep;
import com.cairn.waypoint.dashboard.entity.ProtocolStepLinkedHomework;
import com.cairn.waypoint.dashboard.service.data.ProtocolDataService;
import com.cairn.waypoint.dashboard.service.helper.HomeworkHelperService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;
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
@Tag(name = "Homework")
public class GetAllHomeworkByAssociatedProtocolIdEndpoint {

  public static final String PATH = "/api/homework/protocol/{protocolId}";

  private final ProtocolDataService protocolDataService;
  private final HomeworkHelperService homeworkHelperService;

  public GetAllHomeworkByAssociatedProtocolIdEndpoint(ProtocolDataService protocolDataService,
      HomeworkHelperService homeworkHelperService) {
    this.protocolDataService = protocolDataService;
    this.homeworkHelperService = homeworkHelperService;
  }

  //TODO add ability to assign homework to user directly
  //TODO add homework IDs to getProtocolById endpoint

  @GetMapping(PATH)
  @PreAuthorize("hasAnyAuthority('SCOPE_homework.full', 'SCOPE_admin.full')")
  @Operation(
      summary = "Retrieves all homework associated with the provided protocol ID.",
      description = "Retrieves all homework associated with the provided protocol ID. Requires the `homework.full` permission.",
      security = @SecurityRequirement(name = "oAuth2JwtBearer"),
      responses = {
          @ApiResponse(responseCode = "200",
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
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
                .flatMap(Set::stream)
                .map(ProtocolStepLinkedHomework::getHomework)
                .filter(Objects::nonNull)
                .map(homeworkHelperService::generateHomeworkDto)
                .collect(Collectors.toList()))
            .build());
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
