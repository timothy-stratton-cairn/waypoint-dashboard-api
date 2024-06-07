package com.cairn.waypoint.dashboard.endpoints.steptemplatecategory;

import com.cairn.waypoint.dashboard.endpoints.ErrorMessage;
import com.cairn.waypoint.dashboard.endpoints.steptemplatecategory.dto.ChildTemplateCategoryDto;
import com.cairn.waypoint.dashboard.endpoints.steptemplatecategory.dto.ChildTemplateCategoryListDto;
import com.cairn.waypoint.dashboard.entity.TemplateCategory;
import com.cairn.waypoint.dashboard.service.data.TemplateCategoryDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;
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
@Tag(name = "Protocol Step Template Category")
public class GetAllStepTemplateCategoriesByParentCategoryIdEndpoint {

  public static final String PATH = "/api/protocol-step-template-category/{templateCategoryId}/children";

  private final TemplateCategoryDataService templateCategoryDataService;

  public GetAllStepTemplateCategoriesByParentCategoryIdEndpoint(
      TemplateCategoryDataService templateCategoryDataService) {
    this.templateCategoryDataService = templateCategoryDataService;
  }

  @GetMapping(PATH)
  @PreAuthorize("hasAnyAuthority('SCOPE_protocol.step.template.category.full', 'SCOPE_admin.full')")
  @Operation(
      summary = "Retrieves all step template categories that are children of the provided parent step template category ID.",
      description = "Retrieves all step template categories that are children of the provided parent step template category ID. Requires the `protocol.step.template.category.full` permission.",
      security = @SecurityRequirement(name = "oAuth2JwtBearer"),
      responses = {
          @ApiResponse(responseCode = "200",
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ChildTemplateCategoryListDto.class))}),
          @ApiResponse(responseCode = "401", description = "Unauthorized",
              content = {@Content(schema = @Schema(hidden = true))}),
          @ApiResponse(responseCode = "403", description = "Forbidden",
              content = {@Content(schema = @Schema(hidden = true))}),
          @ApiResponse(responseCode = "404", description = "Not Found",
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ErrorMessage.class))})})
  public ResponseEntity<?> getAllStepTemplateCategoriesByParent(
      @PathVariable Long templateCategoryId, Principal principal) {
    log.info(
        "User [{}] is Retrieving All Step Template Categories that are children of Parent Step Template Category with ID [{}]",
        principal.getName(), templateCategoryId);

    Optional<TemplateCategory> optionalParentTemplateCategory;

    if ((optionalParentTemplateCategory = this.templateCategoryDataService.getTemplateCategoryById(
        templateCategoryId)).isEmpty()) {
      return generateFailureResponse("Step Template Category with ID [" +
          templateCategoryId + "] does not exist", HttpStatus.NOT_FOUND);
    } else {
      return ResponseEntity.ok(ChildTemplateCategoryListDto.builder()
          .templateCategories(optionalParentTemplateCategory.get().getChildCategories().stream()
              .map(childCategory -> ChildTemplateCategoryDto.builder()
                  .id(childCategory.getId())
                  .name(childCategory.getName())
                  .description(childCategory.getDescription())
                  .build())
              .toList())
          .build());
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
