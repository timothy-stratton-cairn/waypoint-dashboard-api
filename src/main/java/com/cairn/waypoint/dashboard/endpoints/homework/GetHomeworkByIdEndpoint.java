package com.cairn.waypoint.dashboard.endpoints.homework;

import com.cairn.waypoint.dashboard.endpoints.ErrorMessage;
import com.cairn.waypoint.dashboard.endpoints.homework.dto.HomeworkDto;
//import com.cairn.waypoint.dashboard.entity.Homework;
import com.cairn.waypoint.dashboard.service.data.HomeworkDataService;
import com.cairn.waypoint.dashboard.service.helper.HomeworkHelperService;
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
/*
@Slf4j
@RestController
@Tag(name = "Homework")
public class GetHomeworkByIdEndpoint {

  public static final String PATH = "/api/homework/{homeworkId}";
  private final HomeworkDataService homeworkDataService;
  private final HomeworkHelperService homeworkHelperService;

  public GetHomeworkByIdEndpoint(HomeworkDataService homeworkDataService,
      HomeworkHelperService homeworkHelperService) {
    this.homeworkDataService = homeworkDataService;
    this.homeworkHelperService = homeworkHelperService;
  }

  @GetMapping(PATH)
  @PreAuthorize("hasAnyAuthority('SCOPE_homework.full', 'SCOPE_admin.full')")
  @Operation(
      summary = "Retrieves a homework with its responses by the provided homework ID.",
      description = "Retrieves a homework with its responses by the provided homework ID. Requires the `homework.full` permission.",
      security = @SecurityRequirement(name = "oAuth2JwtBearer"),
      responses = {
          @ApiResponse(responseCode = "200",
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = HomeworkDto.class))}),
          @ApiResponse(responseCode = "401", description = "Unauthorized",
              content = {@Content(schema = @Schema(hidden = true))}),
          @ApiResponse(responseCode = "403", description = "Forbidden",
              content = {@Content(schema = @Schema(hidden = true))})})
  public ResponseEntity<?> getHomeworkById(@PathVariable Long homeworkId, Principal principal) {
    log.info("User [{}] is Retrieving Homework with ID [{}]", principal.getName(),
        homeworkId);

    final ResponseEntity<?>[] response = new ResponseEntity<?>[1];
    this.homeworkDataService.getHomeworkById(homeworkId)
        .ifPresentOrElse(
            returnedHomework -> response[0] = generateSuccessResponse(
                returnedHomework),
            () -> response[0] = generateFailureResponse(homeworkId)
        );

    return response[0];
  }

  private ResponseEntity<HomeworkDto> generateSuccessResponse(Homework homework) {
    return ResponseEntity.ok(homeworkHelperService.generateHomeworkDto(homework));
  }

  public ResponseEntity<ErrorMessage> generateFailureResponse(Long homeworkId) {
    log.info("Homework with ID [{}] not found", homeworkId);
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
}*/
