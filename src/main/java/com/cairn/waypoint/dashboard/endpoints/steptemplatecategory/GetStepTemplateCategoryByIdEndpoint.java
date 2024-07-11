package com.cairn.waypoint.dashboard.endpoints.steptemplatecategory;

import com.cairn.waypoint.dashboard.dto.authorization.AccountDto;
import com.cairn.waypoint.dashboard.dto.authorization.AccountListDto;
import com.cairn.waypoint.dashboard.dto.authorization.RoleDto;
import com.cairn.waypoint.dashboard.dto.authorization.RoleListDto;
import com.cairn.waypoint.dashboard.endpoints.ErrorMessage;
import com.cairn.waypoint.dashboard.endpoints.steptemplatecategory.dto.LinkedAccountDto;
import com.cairn.waypoint.dashboard.endpoints.steptemplatecategory.dto.LinkedRoleDto;
import com.cairn.waypoint.dashboard.endpoints.steptemplatecategory.dto.ParentStepTemplateCategoryDto;
import com.cairn.waypoint.dashboard.endpoints.steptemplatecategory.dto.StepTemplateCategoryDetailsDto;
import com.cairn.waypoint.dashboard.entity.StepTemplateCategory;
import com.cairn.waypoint.dashboard.service.data.AccountDataService;
import com.cairn.waypoint.dashboard.service.data.RoleDataService;
import com.cairn.waypoint.dashboard.service.data.StepTemplateCategoryDataService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Tag(name = "Protocol Step Template Category")
public class GetStepTemplateCategoryByIdEndpoint {

  public static final String PATH = "/api/protocol-step-template-category/{stepTemplateCategoryId}";

  private final StepTemplateCategoryDataService stepTemplateCategoryDataService;
  private final AccountDataService accountDataService;
  private final RoleDataService roleDataService;

  public GetStepTemplateCategoryByIdEndpoint(
      StepTemplateCategoryDataService stepTemplateCategoryDataService,
      AccountDataService accountDataService, RoleDataService roleDataService) {
    this.stepTemplateCategoryDataService = stepTemplateCategoryDataService;
    this.accountDataService = accountDataService;
    this.roleDataService = roleDataService;
  }

  @GetMapping(PATH)
  public ResponseEntity<?> getStepTemplateCategoryById(@PathVariable Long stepTemplateCategoryId,
      Principal principal) {
    log.info("User [{}] is Retrieving Protocol Step Template Category with ID [{}]",
        principal.getName(),
        stepTemplateCategoryId);

    final ResponseEntity<?>[] response = new ResponseEntity<?>[1];
    this.stepTemplateCategoryDataService.getTemplateCategoryById(stepTemplateCategoryId)
        .ifPresentOrElse(
            returnedProtocolTemplate -> response[0] = generateSuccessResponse(
                returnedProtocolTemplate),
            () -> response[0] = generateFailureResponse(stepTemplateCategoryId)
        );

    return response[0];
  }

  public ResponseEntity<StepTemplateCategoryDetailsDto> generateSuccessResponse(
      StepTemplateCategory returnedStepTemplateCategory) {

    List<Long> linkedAccounts = Stream.of(returnedStepTemplateCategory.getResponsibleUserId(),
        returnedStepTemplateCategory.getAccountableUserId(),
        returnedStepTemplateCategory.getConsultedUserId(),
        returnedStepTemplateCategory.getInformedUserId()).filter(Objects::nonNull).toList();

    List<Long> linkedRoles = Stream.of(returnedStepTemplateCategory.getResponsibleRoleId(),
        returnedStepTemplateCategory.getAccountableRoleId(),
        returnedStepTemplateCategory.getConsultedRoleId(),
        returnedStepTemplateCategory.getInformedRoleId()).filter(Objects::nonNull).toList();

    AccountListDto accounts = accountDataService.getAccountsById(linkedAccounts);

    RoleListDto roles = roleDataService.getRolesById(linkedRoles);
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
            .id(returnedStepTemplateCategory.getId())
            .name(returnedStepTemplateCategory.getName())
            .description(returnedStepTemplateCategory.getDescription())
            .parentCategory(returnedStepTemplateCategory.getParentCategory().getId() != null
                ? ParentStepTemplateCategoryDto.builder()
                .id(returnedStepTemplateCategory.getParentCategory().getId())
                .name(returnedStepTemplateCategory.getParentCategory().getName())
                .description(returnedStepTemplateCategory.getParentCategory().getDescription())
                .build() : null)
            .responsibleUser(getAccountById.apply(
                returnedStepTemplateCategory.getResponsibleUserId()))
            .accountableUser(getAccountById.apply(
                returnedStepTemplateCategory.getAccountableUserId()))
            .consultedUser(getAccountById.apply(
                returnedStepTemplateCategory.getConsultedUserId()))
            .informedUser(getAccountById.apply(
                returnedStepTemplateCategory.getInformedUserId()))
            .responsibleRole(getRoleById.apply(
                returnedStepTemplateCategory.getResponsibleRoleId()))
            .accountableRole(getRoleById.apply(
                returnedStepTemplateCategory.getAccountableRoleId()))
            .consultedRole(getRoleById.apply(
                returnedStepTemplateCategory.getConsultedRoleId()))
            .informedRole(getRoleById.apply(
                returnedStepTemplateCategory.getInformedRoleId()))
            .build());
  }

  public ResponseEntity<ErrorMessage> generateFailureResponse(Long protocolTemplateId) {
    log.info("Protocol Template with ID [{}] not found", protocolTemplateId);
    return new ResponseEntity<>(
        ErrorMessage.builder()
            .path(PATH)
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.NOT_FOUND.value())
            .error("Protocol Template with ID [" + protocolTemplateId + "] not found")
            .build(),
        HttpStatus.NOT_FOUND
    );
  }
}
