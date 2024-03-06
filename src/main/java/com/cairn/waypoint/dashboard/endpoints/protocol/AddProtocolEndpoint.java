package com.cairn.waypoint.dashboard.endpoints.protocol;

import com.cairn.waypoint.dashboard.dto.AccountDetailsDto;
import com.cairn.waypoint.dashboard.endpoints.ErrorMessage;
import com.cairn.waypoint.dashboard.endpoints.protocol.dto.AddProtocolDetailsDto;
import com.cairn.waypoint.dashboard.endpoints.protocol.mapper.ProtocolMapper;
import com.cairn.waypoint.dashboard.entity.Protocol;
import com.cairn.waypoint.dashboard.entity.ProtocolStep;
import com.cairn.waypoint.dashboard.entity.ProtocolTemplate;
import com.cairn.waypoint.dashboard.entity.ProtocolUser;
import com.cairn.waypoint.dashboard.service.AccountService;
import com.cairn.waypoint.dashboard.service.ProtocolService;
import com.cairn.waypoint.dashboard.service.ProtocolTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Tag(name = "Protocol")
public class AddProtocolEndpoint {


  public static final String PATH = "/api/protocol";

  private final ProtocolService protocolService;
  private final ProtocolTemplateService protocolTemplateService;
  private final AccountService accountService;

  public AddProtocolEndpoint(ProtocolService protocolService, AccountService accountService,
      ProtocolTemplateService protocolTemplateService) {
    this.protocolService = protocolService;
    this.protocolTemplateService = protocolTemplateService;
    this.accountService = accountService;
  }

  @PostMapping(PATH)
  @PreAuthorize("hasAuthority('SCOPE_protocol.create')")
  @Operation(
      summary = "Allows a user to create new protocol, "
          + "linking the provided user account to the newly created protocol.",
      description = "Allows a user to create new protocol,"
          + " linking the provided user account to the newly created protocol."
          + " Requires the `protocol.create` permission.",
      security = @SecurityRequirement(name = "oAuth2JwtBearer"),
      responses = {
          @ApiResponse(responseCode = "201",
              description = "Created - Protocol creation was successful"),
          @ApiResponse(responseCode = "401", description = "Unauthorized",
              content = {@Content(schema = @Schema(hidden = true))}),
          @ApiResponse(responseCode = "403", description = "Forbidden",
              content = {@Content(mediaType = "application/json",
                  schema = @Schema(implementation = ErrorMessage.class))}),
          @ApiResponse(responseCode = "404", description = "Not Found",
              content = {@Content(mediaType = "application/json",
                  schema = @Schema(implementation = ErrorMessage.class))}),
          @ApiResponse(responseCode = "409", description = "Not Found",
              content = {@Content(mediaType = "application/json",
                  schema = @Schema(implementation = ErrorMessage.class))})})
  public ResponseEntity<?> addProtocolTemplate(
      @RequestBody AddProtocolDetailsDto addProtocolDetailsDto,
      Principal principal) {
    Optional<AccountDetailsDto> accountDetailsDtoOptional;
    Optional<ProtocolTemplate> protocolTemplateOptional;

    if ((accountDetailsDtoOptional = this.accountService.getAccountDetails(
        addProtocolDetailsDto.getAssociatedAccountId())).isEmpty()) {
      return generateFailureResponse("Account with ID [" +
              addProtocolDetailsDto.getAssociatedAccountId() + "] does not exists",
          HttpStatus.NOT_FOUND);
    } else if ((protocolTemplateOptional = this.protocolTemplateService.getProtocolTemplateById(
        addProtocolDetailsDto.getProtocolTemplateId())).isEmpty()) {
      return generateFailureResponse("Protocol Template with ID [" +
              addProtocolDetailsDto.getProtocolTemplateId() + "] does not exists",
          HttpStatus.NOT_FOUND);
    } else {
      Protocol protocolToBeCreated = ProtocolMapper.INSTANCE.protocolTemplateToProtocol(
          protocolTemplateOptional.get());

      protocolToBeCreated.setModifiedBy(principal.getName());
      protocolToBeCreated.setProtocolTemplate(protocolTemplateOptional.get());
      protocolToBeCreated.setComment(addProtocolDetailsDto.getComment());

      Set<ProtocolStep> protocolSteps = protocolTemplateOptional.get().getProtocolTemplateSteps()
          .stream()
          .map(stepTemplateLink -> {
            ProtocolStep protocolStep = ProtocolMapper.INSTANCE.protocolStepTemplateToProtocolStep(
                stepTemplateLink.getStepTemplate());
            protocolStep.setModifiedBy(principal.getName());
            protocolStep.setTemplate(stepTemplateLink.getStepTemplate());

            return protocolStep;
          })
          .collect(Collectors.toSet());

      protocolToBeCreated.setProtocolSteps(protocolSteps);

      Set<ProtocolUser> protocolUsers = new HashSet<>();

      protocolUsers.add(ProtocolUser.builder()
          .modifiedBy(principal.getName())
          .userId(addProtocolDetailsDto.getAssociatedAccountId())
          .build());

      protocolUsers.add(ProtocolUser.builder()
          .modifiedBy(principal.getName())
          .userId(accountDetailsDtoOptional.get().getCoClient().getId())
          .build());

      accountDetailsDtoOptional.get().getDependents().stream()
          .map(dependentAccountDetails -> ProtocolUser.builder()
              .modifiedBy(principal.getName())
              .userId(dependentAccountDetails.getId())
              .build())
          .forEach(protocolUsers::add);

      protocolToBeCreated.setAssociatedUsers(protocolUsers);

      Protocol createdProtocol = this.protocolService.saveProtocol(protocolToBeCreated);

      return ResponseEntity.status(HttpStatus.CREATED)
          .body("Protocol [" + createdProtocol.getName()
              + "] was created successfully and assigned to Account with ID [" +
              addProtocolDetailsDto.getAssociatedAccountId() + "]");
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
