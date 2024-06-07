package com.cairn.waypoint.dashboard.endpoints.stepstatus;

import com.cairn.waypoint.dashboard.endpoints.stepstatus.dto.StepStatusDto;
import com.cairn.waypoint.dashboard.endpoints.stepstatus.dto.StepStatusListDto;
import com.cairn.waypoint.dashboard.entity.enumeration.StepStatusEnum;
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
@Tag(name = "Protocol Step Status")
public class GetAllStepStatusesEndpoint {

  public static final String PATH = "/api/protocol-step/status";

  @GetMapping(PATH)
  @PreAuthorize("hasAnyAuthority('SCOPE_protocol.step.status.full', 'SCOPE_admin.full')")
  @Operation(
      summary = "Retrieves all step statuses.",
      description = "Retrieves all step statuses. Requires the `protocol.step.status.full` permission.",
      security = @SecurityRequirement(name = "oAuth2JwtBearer"),
      responses = {
          @ApiResponse(responseCode = "200",
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = StepStatusListDto.class))}),
          @ApiResponse(responseCode = "401", description = "Unauthorized",
              content = {@Content(schema = @Schema(hidden = true))}),
          @ApiResponse(responseCode = "403", description = "Forbidden",
              content = {@Content(schema = @Schema(hidden = true))})})
  public ResponseEntity<StepStatusListDto> getAllStepStatuses(Principal principal) {
    log.info("User [{}] is Retrieving All Step Statuses", principal.getName());
    return ResponseEntity.ok(
        StepStatusListDto.builder()
            .statuses(Stream.of(StepStatusEnum.values())
                .map(stepStatusEnum -> StepStatusDto.builder()
                    .status(stepStatusEnum.name())
                    .build())
                .toList())
            .build()
    );
  }

}
