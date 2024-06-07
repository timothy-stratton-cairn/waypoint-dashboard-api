package com.cairn.waypoint.dashboard.endpoints.steptemplatecategory;

import com.cairn.waypoint.dashboard.endpoints.steptemplatecategory.dto.ChildTemplateCategoryDto;
import com.cairn.waypoint.dashboard.endpoints.steptemplatecategory.dto.ChildTemplateCategoryListDto;
import com.cairn.waypoint.dashboard.endpoints.steptemplatecategory.dto.TemplateCategoryDto;
import com.cairn.waypoint.dashboard.endpoints.steptemplatecategory.dto.TemplateCategoryListDto;
import com.cairn.waypoint.dashboard.service.data.TemplateCategoryDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.security.Principal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Tag(name = "Protocol Step Template Category")
public class GetAllStepTemplateCategoriesEndpoint {

  public static final String PATH = "/api/protocol-step-template-category";

  private final TemplateCategoryDataService templateCategoryDataService;

  public GetAllStepTemplateCategoriesEndpoint(
      TemplateCategoryDataService templateCategoryDataService) {
    this.templateCategoryDataService = templateCategoryDataService;
  }

  @GetMapping(PATH)
  @PreAuthorize("hasAnyAuthority('SCOPE_protocol.step.template.category.full', 'SCOPE_admin.full')")
  @Operation(
      summary = "Retrieves all step template categories.",
      description = "Retrieves all step template categories. Requires the `protocol.step.template.category.full` permission.",
      security = @SecurityRequirement(name = "oAuth2JwtBearer"),
      responses = {
          @ApiResponse(responseCode = "200",
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = TemplateCategoryListDto.class))}),
          @ApiResponse(responseCode = "401", description = "Unauthorized",
              content = {@Content(schema = @Schema(hidden = true))}),
          @ApiResponse(responseCode = "403", description = "Forbidden",
              content = {@Content(schema = @Schema(hidden = true))})})
  public ResponseEntity<TemplateCategoryListDto> getAllStepTemplateCategories(Principal principal) {
    log.info("User [{}] is Retrieving All Step Template Categories", principal.getName());
    return ResponseEntity.ok(
        TemplateCategoryListDto.builder()
            .templateCategories(
                this.templateCategoryDataService.getAllParentTemplateCategories().stream()
                    .map(category -> TemplateCategoryDto.builder()
                        .id(category.getId())
                        .name(category.getName())
                        .description(category.getDescription())
                        .childCategories(ChildTemplateCategoryListDto.builder()
                            .templateCategories(category.getChildCategories().stream()
                                .map(childCategory -> ChildTemplateCategoryDto.builder()
                                    .id(childCategory.getId())
                                    .name(childCategory.getName())
                                    .description(childCategory.getDescription())
                                    .build())
                                .toList())
                            .build())
                        .build())
                    .toList())
            .build()
    );
  }

}
