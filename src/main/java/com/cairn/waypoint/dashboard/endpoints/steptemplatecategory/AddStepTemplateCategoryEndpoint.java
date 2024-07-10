package com.cairn.waypoint.dashboard.endpoints.steptemplatecategory;

import com.cairn.waypoint.dashboard.dto.authorization.AccountDto;
import com.cairn.waypoint.dashboard.dto.authorization.AccountListDto;
import com.cairn.waypoint.dashboard.dto.authorization.RoleDto;
import com.cairn.waypoint.dashboard.dto.authorization.RoleListDto;
import com.cairn.waypoint.dashboard.endpoints.ErrorMessage;
import com.cairn.waypoint.dashboard.endpoints.steptemplatecategory.dto.AddStepTemplateCategoryDto;
import com.cairn.waypoint.dashboard.endpoints.steptemplatecategory.dto.LinkedAccountDto;
import com.cairn.waypoint.dashboard.endpoints.steptemplatecategory.dto.LinkedRoleDto;
import com.cairn.waypoint.dashboard.endpoints.steptemplatecategory.dto.ParentStepTemplateCategoryDto;
import com.cairn.waypoint.dashboard.endpoints.steptemplatecategory.dto.StepTemplateCategoryDetailsDto;
import com.cairn.waypoint.dashboard.endpoints.steptemplatecategory.dto.TemplateCategoryListDto;
import com.cairn.waypoint.dashboard.entity.StepTemplateCategory;
import com.cairn.waypoint.dashboard.service.data.AccountDataService;
import com.cairn.waypoint.dashboard.service.data.RoleDataService;
import com.cairn.waypoint.dashboard.service.data.StepTemplateCategoryDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
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
@Tag(name = "Protocol Step Template Category")
public class AddStepTemplateCategoryEndpoint {

  public static final String PATH = "/api/protocol-step-template-category";

  private final StepTemplateCategoryDataService stepTemplateCategoryDataService;
  private final RoleDataService roleDataService;
  private final AccountDataService accountDataService;

  public AddStepTemplateCategoryEndpoint(
      StepTemplateCategoryDataService stepTemplateCategoryDataService,
      RoleDataService roleDataService, AccountDataService accountDataService) {
    this.stepTemplateCategoryDataService = stepTemplateCategoryDataService;
    this.roleDataService = roleDataService;
    this.accountDataService = accountDataService;
  }

  @PostMapping(PATH)
  @PreAuthorize("hasAnyAuthority('SCOPE_protocol.step.template.category.full', 'SCOPE_admin.full')")
  @Operation(
      summary = "Creates a step template category.",
      description = "Creates a step template category. Requires the `protocol.step.template.category.full` permission.",
      security = @SecurityRequirement(name = "oAuth2JwtBearer"),
      responses = {
          @ApiResponse(responseCode = "200",
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = TemplateCategoryListDto.class))}),
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
                  schema = @Schema(implementation = ErrorMessage.class))})})
  public ResponseEntity<?> addStepTemplateCategory(
      @RequestBody AddStepTemplateCategoryDto addStepTemplateCategoryDto, Principal principal) {
    log.info("User [{}] is created a new Step Template Category with name [{}]",
        principal.getName(), addStepTemplateCategoryDto.getName());

    List<Long> linkedAccounts = Stream.of(addStepTemplateCategoryDto.getResponsibleUserId(),
        addStepTemplateCategoryDto.getAccountableUserId(),
        addStepTemplateCategoryDto.getConsultedUserId(),
        addStepTemplateCategoryDto.getInformedUserId()).filter(Objects::nonNull).toList();

    List<Long> linkedRoles = Stream.of(addStepTemplateCategoryDto.getResponsibleRoleId(),
        addStepTemplateCategoryDto.getAccountableRoleId(),
        addStepTemplateCategoryDto.getConsultedRoleId(),
        addStepTemplateCategoryDto.getInformedRoleId()).filter(Objects::nonNull).toList();

    AccountListDto accounts = accountDataService.getAccountsById(linkedAccounts);

    RoleListDto roles = roleDataService.getRolesById(linkedRoles);

    if (stepTemplateCategoryDataService.findByName(addStepTemplateCategoryDto.getName())
        .isPresent()) {
      return generateFailureResponse("Step Template Category with Name [" +
          addStepTemplateCategoryDto.getName() + "] already exists", HttpStatus.CONFLICT);
    } else if (!linkedAccounts.isEmpty() && accounts.getAccounts().stream().map(AccountDto::getId)
        .noneMatch(linkedAccounts::contains)) {
      return generateFailureResponse("User ID(s) [" +
          linkedAccounts + "] does no exists", HttpStatus.NOT_FOUND);
    } else if (!linkedRoles.isEmpty() && roles.getRoles().stream().map(RoleDto::getId).noneMatch(
        linkedRoles::contains)) {
      return generateFailureResponse("Role ID(s) [" +
          linkedRoles + "] does no exists", HttpStatus.NOT_FOUND);
    } else {
      StepTemplateCategory newStepTemplateCategory = StepTemplateCategory.builder()
          .name(addStepTemplateCategoryDto.getName())
          .description(addStepTemplateCategoryDto.getDescription())
          .responsibleUserId(addStepTemplateCategoryDto.getResponsibleUserId())
          .accountableUserId(addStepTemplateCategoryDto.getAccountableUserId())
          .consultedUserId(addStepTemplateCategoryDto.getConsultedUserId())
          .informedUserId(addStepTemplateCategoryDto.getInformedUserId())
          .responsibleRoleId(addStepTemplateCategoryDto.getResponsibleRoleId())
          .accountableRoleId(addStepTemplateCategoryDto.getAccountableRoleId())
          .consultedRoleId(addStepTemplateCategoryDto.getConsultedRoleId())
          .informedRoleId(addStepTemplateCategoryDto.getInformedRoleId())
          .build();

      if (addStepTemplateCategoryDto.getParentCategoryId() != null) {
        Optional<StepTemplateCategory> parentStepTemplateCategoryOptional;
        if ((parentStepTemplateCategoryOptional = stepTemplateCategoryDataService
            .getTemplateCategoryById(addStepTemplateCategoryDto.getParentCategoryId())).isEmpty()) {
          return generateFailureResponse("Parent Step Template Category with ID [" +
                  addStepTemplateCategoryDto.getParentCategoryId() + "] does no exists",
              HttpStatus.NOT_FOUND);
        }
        newStepTemplateCategory.setParentCategory(parentStepTemplateCategoryOptional.get());
      }

      StepTemplateCategory createdStepTemplateCategory = stepTemplateCategoryDataService.saveTemplateCategory(
          newStepTemplateCategory);

      Function<Long, LinkedAccountDto> getAccountById = (accountId) -> {
        if (accountId == null) {
          return null;
        }

        AccountDto account = accounts.getAccounts().stream()
            .filter(accountDto -> accountDto.getId().equals(accountId))
            .findFirst()
            .orElseThrow();

        return LinkedAccountDto.builder()
            .id(account.getId())
            .firstName(account.getFirstName())
            .lastName(account.getLastName())
            .email(account.getEmail())
            .build();
      };

      Function<Long, LinkedRoleDto> getRoleById = (roleId) -> {
        if (roleId == null) {
          return null;
        }

        RoleDto role = roles.getRoles().stream()
            .filter(roleDto -> roleDto.getId().equals(roleId))
            .findFirst()
            .orElseThrow();

        return LinkedRoleDto.builder()
            .id(role.getId())
            .name(role.getName())
            .build();
      };

      return ResponseEntity.ok(
          StepTemplateCategoryDetailsDto.builder()
              .id(createdStepTemplateCategory.getId())
              .name(createdStepTemplateCategory.getName())
              .description(createdStepTemplateCategory.getDescription())
              .parentCategory(addStepTemplateCategoryDto.getParentCategoryId() != null
                  ? ParentStepTemplateCategoryDto.builder()
                  .id(createdStepTemplateCategory.getParentCategory().getId())
                  .name(createdStepTemplateCategory.getParentCategory().getName())
                  .description(createdStepTemplateCategory.getParentCategory().getDescription())
                  .build() : null)
              .responsibleUser(getAccountById.apply(
                  createdStepTemplateCategory.getResponsibleUserId()))
              .accountableUser(getAccountById.apply(
                  createdStepTemplateCategory.getAccountableUserId()))
              .consultedUser(getAccountById.apply(
                  createdStepTemplateCategory.getConsultedUserId()))
              .informedUser(getAccountById.apply(
                  createdStepTemplateCategory.getInformedUserId()))
              .responsibleRole(getRoleById.apply(
                  createdStepTemplateCategory.getResponsibleRoleId()))
              .accountableRole(getRoleById.apply(
                  createdStepTemplateCategory.getAccountableRoleId()))
              .consultedRole(getRoleById.apply(
                  createdStepTemplateCategory.getConsultedRoleId()))
              .informedRole(getRoleById.apply(
                  createdStepTemplateCategory.getInformedRoleId()))
              .build()
      );
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
