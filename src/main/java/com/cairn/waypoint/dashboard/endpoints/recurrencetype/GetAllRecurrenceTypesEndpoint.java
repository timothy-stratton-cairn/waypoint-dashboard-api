package com.cairn.waypoint.dashboard.endpoints.recurrencetype;

import com.cairn.waypoint.dashboard.endpoints.recurrencetype.dto.RecurrenceTypeDto;
import com.cairn.waypoint.dashboard.endpoints.recurrencetype.dto.RecurrenceTypeListDto;
import com.cairn.waypoint.dashboard.endpoints.stepstatus.dto.StepStatusDto;
import com.cairn.waypoint.dashboard.endpoints.stepstatus.dto.StepStatusListDto;
import com.cairn.waypoint.dashboard.entity.enumeration.RecurrenceTypeEnum;
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
public class GetAllRecurrenceTypesEndpoint {

  public static final String PATH = "/api/protocol-template/recurrence-type";

  @GetMapping(PATH)
  @PreAuthorize("hasAnyAuthority('SCOPE_protocol.template.full', 'SCOPE_admin.full')")
  @Operation(
      summary = "Retrieves all recurrence types.",
      description = "Retrieves all recurrence types. Requires the `protocol.template.full` permission.",
      security = @SecurityRequirement(name = "oAuth2JwtBearer"),
      responses = {
          @ApiResponse(responseCode = "200",
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = RecurrenceTypeListDto.class))}),
          @ApiResponse(responseCode = "401", description = "Unauthorized",
              content = {@Content(schema = @Schema(hidden = true))}),
          @ApiResponse(responseCode = "403", description = "Forbidden",
              content = {@Content(schema = @Schema(hidden = true))})})
  public ResponseEntity<RecurrenceTypeListDto> getAllRecurrenceTypes(Principal principal) {
    log.info("User [{}] is Retrieving All Recurrence Types", principal.getName());
    return ResponseEntity.ok(
        RecurrenceTypeListDto.builder()
            .recurrenceTypes(Stream.of(RecurrenceTypeEnum.values())
                .map(recurrenceTypeEnum -> RecurrenceTypeDto.builder()
                    .id(recurrenceTypeEnum.getInstance().getId())
                    .recurrenceType(recurrenceTypeEnum.name())
                    .description(recurrenceTypeEnum.getInstance().getDescription())
                    .build())
                .toList())
            .build()
    );
  }

}
