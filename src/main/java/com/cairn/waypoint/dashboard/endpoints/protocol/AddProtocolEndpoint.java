package com.cairn.waypoint.dashboard.endpoints.protocol;

import com.cairn.waypoint.dashboard.dto.authorization.HouseholdDetailsDto;
import com.cairn.waypoint.dashboard.endpoints.ErrorMessage;
import com.cairn.waypoint.dashboard.endpoints.protocol.dto.AddProtocolDetailsDto;
import com.cairn.waypoint.dashboard.endpoints.protocol.dto.ProtocolDetailsDto;
import com.cairn.waypoint.dashboard.entity.Protocol;
import com.cairn.waypoint.dashboard.entity.ProtocolCommentary;
import com.cairn.waypoint.dashboard.entity.ProtocolStep;
import com.cairn.waypoint.dashboard.entity.ProtocolTemplate;
import com.cairn.waypoint.dashboard.entity.StepCategory;
import com.cairn.waypoint.dashboard.entity.enumeration.ProtocolCommentTypeEnum;
import com.cairn.waypoint.dashboard.entity.enumeration.ProtocolStatusEnum;
import com.cairn.waypoint.dashboard.entity.enumeration.StepStatusEnum;
import com.cairn.waypoint.dashboard.entity.enumeration.TemplateStatusEnum;
import com.cairn.waypoint.dashboard.mapper.ProtocolMapper;
import com.cairn.waypoint.dashboard.service.data.HouseholdDataService;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

  private final ProtocolTemplateHelperService protocolTemplateHelperService;
  private final HouseholdDataService householdDataService;

  private final ProtocolMapper protocolMapper = ProtocolMapper.INSTANCE;

  public AddProtocolEndpoint(ProtocolDataService protocolDataService,
      ProtocolTemplateDataService protocolTemplateDataService,
      StepTemplateDataService stepTemplateDataService,
     //HomeworkDataService homeworkDataService, 
      HouseholdDataService householdDataService) {
    this.protocolDataService = protocolDataService;
    this.protocolTemplateDataService = protocolTemplateDataService;

    this.protocolTemplateHelperService = new ProtocolTemplateHelperService( protocolDataService, stepTemplateDataService);
    this.householdDataService = householdDataService;
  }

  @Transactional
  @PostMapping(PATH)
  @SuppressWarnings("OptionalGetWithoutIsPresent")
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
              description = "Created - Protocol creation was successful",
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ProtocolDetailsDto.class))}),
          @ApiResponse(responseCode = "400", description = "Bad Request",
              content = {@Content(schema = @Schema(hidden = true))}),
          @ApiResponse(responseCode = "401", description = "Unauthorized",
              content = {@Content(schema = @Schema(hidden = true))}),
          @ApiResponse(responseCode = "403", description = "Forbidden",
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ErrorMessage.class))}),
          @ApiResponse(responseCode = "404", description = "Not Found",
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ErrorMessage.class))}),
          @ApiResponse(responseCode = "409", description = "Conflict",
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ErrorMessage.class))}),
          @ApiResponse(responseCode = "422", description = "Unprocessable Entity",
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ErrorMessage.class))})})
  public ResponseEntity<?> assignProtocol(
      @RequestBody AddProtocolDetailsDto addProtocolDetailsDto,
      Principal principal) {
    log.info("User [{}] is associating Protocol Template with ID [{}] for Household ID [{}]",
        principal.getName(), addProtocolDetailsDto.getProtocolTemplateId(),
        addProtocolDetailsDto.getAssignedHouseholdId());

    Optional<HouseholdDetailsDto> householdDetailsDtoOptional;
    Optional<ProtocolTemplate> protocolTemplateOptional;
    if (this.protocolDataService.getByProtocolTemplateIdAndHouseholdId(
        addProtocolDetailsDto.getProtocolTemplateId(),
        addProtocolDetailsDto.getAssignedHouseholdId()).isPresent()) {
      return generateFailureResponse("Account [" +
          addProtocolDetailsDto.getAssignedHouseholdId()
          + "] already associated with Protocol Template [" +
          addProtocolDetailsDto.getProtocolTemplateId() + "]", HttpStatus.CONFLICT);
    } else if ((householdDetailsDtoOptional = this.householdDataService.getHouseholdDetails(
        addProtocolDetailsDto.getAssignedHouseholdId())).isEmpty()) {
      return generateFailureResponse("Account with ID [" +
              addProtocolDetailsDto.getAssignedHouseholdId() + "] does not exists",
          HttpStatus.NOT_FOUND);
    } else if ((protocolTemplateOptional = this.protocolTemplateDataService.getProtocolTemplateById(
        addProtocolDetailsDto.getProtocolTemplateId())).isEmpty()) {
      return generateFailureResponse("Protocol Template with ID [" +
              addProtocolDetailsDto.getProtocolTemplateId() + "] does not exists",
          HttpStatus.NOT_FOUND);
    } else if (!protocolTemplateOptional.get().getStatus().equals(TemplateStatusEnum.LIVE)) {
      return generateFailureResponse("Protocol Template with ID [" +
              addProtocolDetailsDto.getProtocolTemplateId() + "] is not live"
              + " and cannot be assigned",
          HttpStatus.UNPROCESSABLE_ENTITY);
    } else {
      Protocol protocolToBeCreated = setupProtocolToBeCreated(protocolTemplateOptional.get(),
          principal.getName());

      if (addProtocolDetailsDto.getProtocolName() != null) {
        protocolToBeCreated.setName(addProtocolDetailsDto.getProtocolName());
      }

      if (addProtocolDetailsDto.getDueDate() != null) {//Override the default due date if present
        protocolToBeCreated.setDueDate(addProtocolDetailsDto.getDueDate());
      } else if (protocolTemplateOptional.get().getDefaultDueByInYears() == null
          && protocolTemplateOptional.get().getDefaultDueByInMonths() == null
          && protocolTemplateOptional.get().getDefaultDueByInDays() == null) {
        protocolToBeCreated.setDueDate(null);
      }

      if (Objects.nonNull(addProtocolDetailsDto.getRecurrenceType())) {
        protocolToBeCreated.setRecurrenceType(addProtocolDetailsDto.getRecurrenceType());
        protocolToBeCreated.setTriggeringStatus(addProtocolDetailsDto.getTriggeringStatus());
        protocolToBeCreated.setReoccurInYears(addProtocolDetailsDto.getReoccurInYears());
        protocolToBeCreated.setReoccurInMonths(addProtocolDetailsDto.getReoccurInMonths());
        protocolToBeCreated.setReoccurInDays(addProtocolDetailsDto.getReoccurInDays());
      }

      protocolToBeCreated.setGoal(addProtocolDetailsDto.getGoal());

      protocolToBeCreated.setProtocolSteps(setupProtocolSteps(protocolTemplateOptional.get(),
          principal.getName()));

      protocolToBeCreated.setAssignedHouseholdId(householdDetailsDtoOptional.get().getId());

      try {
        if (addProtocolDetailsDto.getComment() != null &&
            addProtocolDetailsDto.getCommentType() != null) {
          addProtocolDetailsDto.setProtocolCommentType(ProtocolCommentTypeEnum
              .valueOf(addProtocolDetailsDto.getCommentType()));
        } else if (addProtocolDetailsDto.getComment() != null) {
          addProtocolDetailsDto.setProtocolCommentType(ProtocolCommentTypeEnum.COMMENT);
        }
      } catch (IllegalArgumentException e) {
        return generateFailureResponse("Provided comment type [" +
                addProtocolDetailsDto.getCommentType() + "] does not exist",
            HttpStatus.BAD_REQUEST);
      }

      protocolToBeCreated.setComments(
          setupProtocolComment(addProtocolDetailsDto, principal.getName()));

      Protocol createdProtocol = this.protocolDataService.saveProtocol(protocolToBeCreated);

      //protocolTemplateHelperService.generateAndSaveClientHomework(createdProtocol,
      //    principal.getName());

      createdProtocol = this.protocolDataService.getProtocolById(createdProtocol.getId()).get();

      log.info("Protocol Template with ID [{}] was successfully associated with Account ID [{}]",
          addProtocolDetailsDto.getProtocolTemplateId(),
          addProtocolDetailsDto.getAssignedHouseholdId());
      return ResponseEntity.status(HttpStatus.CREATED)
          .body(protocolMapper.toDetailsDto(createdProtocol));
    }
  }

  private Set<ProtocolCommentary> setupProtocolComment(AddProtocolDetailsDto addProtocolDetailsDto,
      String modifiedBy) {
    if (addProtocolDetailsDto.getComment() == null || addProtocolDetailsDto.getComment()
        .isEmpty()) {
      return Set.of();
    } else {
      return Set.of(ProtocolCommentary.builder()
          .modifiedBy(modifiedBy)
          .originalCommenter(modifiedBy)
          .comment(addProtocolDetailsDto.getComment())
          .commentType(addProtocolDetailsDto.getProtocolCommentType())
          .build());
    }
  }

  private Protocol setupProtocolToBeCreated(ProtocolTemplate protocolTemplate, String modifiedBy) {
    Protocol protocolToBeCreated = ProtocolMapper.INSTANCE.protocolTemplateToProtocol(
        protocolTemplate);

    protocolToBeCreated.setModifiedBy(modifiedBy);
    protocolToBeCreated.setProtocolTemplate(protocolTemplate);
    protocolToBeCreated.setMarkedForAttention(Boolean.FALSE);
    protocolToBeCreated.setLastStatusUpdateTimestamp(LocalDateTime.now());
    protocolToBeCreated.setDueDate(
        protocolTemplate.getDefaultDueByInYears() == null
            && protocolTemplate.getDefaultDueByInMonths() == null
            && protocolTemplate.getDefaultDueByInDays() == null ?
            null :
            LocalDate.now() //Set DueDate based on the default on the template
                .plusYears(protocolTemplate.getDefaultDueByInYears() == null ? 0
                    : protocolTemplate.getDefaultDueByInYears())
                .plusMonths(protocolTemplate.getDefaultDueByInMonths() == null ? 0
                    : protocolTemplate.getDefaultDueByInMonths())
                .plusDays(protocolTemplate.getDefaultDueByInDays() == null ? 0
                    : protocolTemplate.getDefaultDueByInDays()));
    protocolToBeCreated.setRecurrenceType(protocolTemplate.getDefaultRecurrenceType());
    protocolToBeCreated.setTriggeringStatus(protocolTemplate.getDefaultTriggeringStatus());
    protocolToBeCreated.setReoccurInYears(protocolTemplate.getDefaultReoccurInYears());
    protocolToBeCreated.setReoccurInMonths(protocolTemplate.getDefaultReoccurInMonths());
    protocolToBeCreated.setReoccurInDays(protocolTemplate.getDefaultReoccurInDays());
    //IMPORTANT - Must go AFTER setRecurrenceType()
    protocolToBeCreated.setStatus(ProtocolStatusEnum.IN_PROGRESS);

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
              .stepTemplateCategory(stepTemplateLink.getStepTemplate().getCategory())
              .build());
          protocolStep.setStatus(StepStatusEnum.TODO);

          return protocolStep;
        })
        .collect(Collectors.toCollection(LinkedHashSet::new));
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
