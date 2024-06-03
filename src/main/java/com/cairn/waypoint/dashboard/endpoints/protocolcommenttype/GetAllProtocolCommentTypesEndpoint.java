package com.cairn.waypoint.dashboard.endpoints.protocolcommenttype;

import com.cairn.waypoint.dashboard.endpoints.protocolcommenttype.dto.ProtocolCommentTypeDto;
import com.cairn.waypoint.dashboard.endpoints.protocolcommenttype.dto.ProtocolCommentTypeListDto;
import com.cairn.waypoint.dashboard.entity.enumeration.ProtocolCommentTypeEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.security.Principal;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Tag(name = "Protocol Comment Type")
public class GetAllProtocolCommentTypesEndpoint {

  public static final String PATH = "/api/protocol/comment-type";

  @GetMapping(PATH)
  @PreAuthorize("hasAnyAuthority('SCOPE_protocol.comment.type.full', 'SCOPE_admin.full')")
  @Operation(
      summary = "Retrieves all protocol comment types.",
      description = "Retrieves all protocol comment types. Requires the `protocol.comment.type.full` permission.",
      security = @SecurityRequirement(name = "oAuth2JwtBearer"),
      responses = {
          @ApiResponse(responseCode = "200",
              content = {@Content(mediaType = "application/json",
                  schema = @Schema(implementation = ProtocolCommentTypeListDto.class))}),
          @ApiResponse(responseCode = "401", description = "Unauthorized",
              content = {@Content(schema = @Schema(hidden = true))}),
          @ApiResponse(responseCode = "403", description = "Forbidden",
              content = {@Content(schema = @Schema(hidden = true))})})
  public ResponseEntity<ProtocolCommentTypeListDto> getAllProtocolCommentTypes(
      Principal principal) {
    log.info("User [{}] is Retrieving All Protocol Comment Type", principal.getName());
    return ResponseEntity.ok(
        ProtocolCommentTypeListDto.builder()
            .commentTypes(Stream.of(ProtocolCommentTypeEnum.values())
                .map(protocolCommentTypeEnum -> ProtocolCommentTypeDto.builder()
                    .type(protocolCommentTypeEnum.name())
                    .description(protocolCommentTypeEnum.getInstance().getDescription())
                    .build())
                .toList())
            .build()
    );
  }

}
