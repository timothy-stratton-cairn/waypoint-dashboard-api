package com.cairn.waypoint.dashboard.endpoints.protocol;

import com.cairn.waypoint.dashboard.dto.AccountDetailsDto;
import com.cairn.waypoint.dashboard.endpoints.ErrorMessage;
import com.cairn.waypoint.dashboard.endpoints.protocol.dto.AddProtocolDetailsDto;
import com.cairn.waypoint.dashboard.endpoints.protocol.mapper.ProtocolMapper;
import com.cairn.waypoint.dashboard.entity.Protocol;
import com.cairn.waypoint.dashboard.entity.ProtocolCommentary;
import com.cairn.waypoint.dashboard.entity.ProtocolStep;
import com.cairn.waypoint.dashboard.entity.ProtocolTemplate;
import com.cairn.waypoint.dashboard.entity.ProtocolUser;
import com.cairn.waypoint.dashboard.entity.StepCategory;
import com.cairn.waypoint.dashboard.entity.enumeration.StepStatusEnum;
import com.cairn.waypoint.dashboard.service.data.AccountDataService;
import com.cairn.waypoint.dashboard.service.data.HomeworkDataService;
import com.cairn.waypoint.dashboard.service.data.ProtocolDataService;
import com.cairn.waypoint.dashboard.service.data.ProtocolTemplateDataService;
import com.cairn.waypoint.dashboard.service.data.StepTemplateDataService;
import com.cairn.waypoint.dashboard.service.helper.ProtocolTemplateHelperService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.LinkedHashSet;
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

  private final ProtocolDataService protocolDataService;
  private final ProtocolTemplateDataService protocolTemplateDataService;
  private final AccountDataService accountDataService;

  private final ProtocolTemplateHelperService protocolTemplateHelperService;

  public AddProtocolEndpoint(ProtocolDataService protocolDataService, AccountDataService accountDataService,
      ProtocolTemplateDataService protocolTemplateDataService, StepTemplateDataService stepTemplateDataService,
      HomeworkDataService homeworkDataService) {
    this.protocolDataService = protocolDataService;
    this.protocolTemplateDataService = protocolTemplateDataService;
    this.accountDataService = accountDataService;

    this.protocolTemplateHelperService = new ProtocolTemplateHelperService(
        protocolDataService, stepTemplateDataService, homeworkDataService);
  }

  @Transactional
  @PostMapping(PATH)
  @PreAuthorize("hasAnyAuthority('SCOPE_protocol.full', 'SCOPE_admin.full')")
  @Operation(
      summary = "Allows a user to create new protocol, "
          + "linking the provided user account to the newly created protocol.",
      description = "Allows a user to create new protocol,"
          + " linking the provided user account to the newly created protocol."
          + " Requires the `protocol.full` permission.",
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
          @ApiResponse(responseCode = "409", description = "Conflict",
              content = {@Content(mediaType = "application/json",
                  schema = @Schema(implementation = ErrorMessage.class))})})
  public ResponseEntity<?> addProtocolTemplate(
      @RequestBody AddProtocolDetailsDto addProtocolDetailsDto,
      Principal principal) {
    log.info("User [{}] is associating Protocol Template with ID [{}] with Account ID [{}]",
        principal.getName(), addProtocolDetailsDto.getProtocolTemplateId(),
        addProtocolDetailsDto.getAssociatedAccountId());

    Optional<AccountDetailsDto> accountDetailsDtoOptional;
    Optional<ProtocolTemplate> protocolTemplateOptional;
    if (this.protocolDataService.getByProtocolTemplateIdAndUserId(
        addProtocolDetailsDto.getProtocolTemplateId(),
        addProtocolDetailsDto.getAssociatedAccountId()).isPresent()) {
      return generateFailureResponse("Account [" +
          addProtocolDetailsDto.getAssociatedAccountId()
          + "] already associated with Protocol Template [" +
          addProtocolDetailsDto.getProtocolTemplateId() + "]", HttpStatus.CONFLICT);
    } else if ((accountDetailsDtoOptional = this.accountDataService.getAccountDetails(
        addProtocolDetailsDto.getAssociatedAccountId())).isEmpty()) {
      return generateFailureResponse("Account with ID [" +
              addProtocolDetailsDto.getAssociatedAccountId() + "] does not exists",
          HttpStatus.NOT_FOUND);
    } else if ((protocolTemplateOptional = this.protocolTemplateDataService.getProtocolTemplateById(
        addProtocolDetailsDto.getProtocolTemplateId())).isEmpty()) {
      return generateFailureResponse("Protocol Template with ID [" +
              addProtocolDetailsDto.getProtocolTemplateId() + "] does not exists",
          HttpStatus.NOT_FOUND);
    } else {
      Protocol protocolToBeCreated = setupProtocolToBeCreated(protocolTemplateOptional.get(),
          principal.getName());

      protocolToBeCreated.setGoal(addProtocolDetailsDto.getGoal());

      protocolToBeCreated.setProtocolSteps(setupProtocolSteps(protocolTemplateOptional.get(),
          principal.getName()));

      protocolToBeCreated.setAssociatedUsers(setupProtocolUsers(addProtocolDetailsDto,
          principal.getName(), accountDetailsDtoOptional.get()));

      protocolToBeCreated.setComments(
          setupProtocolComment(addProtocolDetailsDto, principal.getName()));

      Protocol createdProtocol = this.protocolDataService.saveProtocol(protocolToBeCreated);

      protocolTemplateHelperService.generateAndSaveClientHomework(createdProtocol, principal.getName());

      log.info("Protocol Template with ID [{}] was successfully associated with Account ID [{}]",
          addProtocolDetailsDto.getProtocolTemplateId(),
          addProtocolDetailsDto.getAssociatedAccountId());
      return ResponseEntity.status(HttpStatus.CREATED)
          .body("Protocol [" + createdProtocol.getName()
              + "] was created successfully and assigned to Account with ID [" +
              addProtocolDetailsDto.getAssociatedAccountId() + "]");
    }
  }

  private Set<ProtocolCommentary> setupProtocolComment(AddProtocolDetailsDto addProtocolDetailsDto,
      String modifiedBy) {
    return Set.of(ProtocolCommentary.builder()
        .modifiedBy(modifiedBy)
        .originalCommenter(modifiedBy)
        .comment(addProtocolDetailsDto.getComment())
        .build());
  }

  private Protocol setupProtocolToBeCreated(ProtocolTemplate protocolTemplate, String modifiedBy) {
    Protocol protocolToBeCreated = ProtocolMapper.INSTANCE.protocolTemplateToProtocol(
        protocolTemplate);

    protocolToBeCreated.setModifiedBy(modifiedBy);
    protocolToBeCreated.setProtocolTemplate(protocolTemplate);
    protocolToBeCreated.setMarkedForAttention(Boolean.FALSE);
    protocolToBeCreated.setLastStatusUpdateTimestamp(LocalDateTime.now());

    return protocolToBeCreated;
  }

  private Set<ProtocolStep> setupProtocolSteps(ProtocolTemplate protocolTemplate,
      String modifiedBy) {
    return protocolTemplate.getProtocolTemplateSteps()
        .stream()
        .map(stepTemplateLink -> {
          ProtocolStep protocolStep = ProtocolMapper.INSTANCE.protocolStepTemplateToProtocolStep(
              stepTemplateLink.getStepTemplate());
          protocolStep.setModifiedBy(modifiedBy);
          protocolStep.setTemplate(stepTemplateLink.getStepTemplate());
          protocolStep.setOrdinalIndex(stepTemplateLink.getOrdinalIndex());
          protocolStep.setCategory(StepCategory.builder()
              .modifiedBy(modifiedBy)
              .templateCategory(stepTemplateLink.getStepTemplate().getCategory())
              .build());
          protocolStep.setStatus(StepStatusEnum.TODO);

          return protocolStep;
        })
        .collect(Collectors.toCollection(LinkedHashSet::new));
  }

  private Set<ProtocolUser> setupProtocolUsers(AddProtocolDetailsDto addProtocolDetailsDto,
      String modifiedBy, AccountDetailsDto accountDetailsDto) {
    Set<ProtocolUser> protocolUsers = new HashSet<>();

    protocolUsers.add(ProtocolUser.builder()
        .modifiedBy(modifiedBy)
        .userId(addProtocolDetailsDto.getAssociatedAccountId())
        .build());

    if (accountDetailsDto.getCoClient() != null) {
      protocolUsers.add(ProtocolUser.builder()
          .modifiedBy(modifiedBy)
          .userId(accountDetailsDto.getCoClient().getId())
          .build());
    }

    return protocolUsers;
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
