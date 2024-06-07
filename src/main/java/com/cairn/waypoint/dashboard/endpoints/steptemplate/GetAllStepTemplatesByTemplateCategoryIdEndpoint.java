package com.cairn.waypoint.dashboard.endpoints.steptemplate;

import com.cairn.waypoint.dashboard.endpoints.ErrorMessage;
import com.cairn.waypoint.dashboard.endpoints.steptemplate.dto.HomeworkTemplateDetailsDto;
import com.cairn.waypoint.dashboard.endpoints.steptemplate.dto.HomeworkTemplateDetailsListDto;
import com.cairn.waypoint.dashboard.endpoints.steptemplate.dto.StepTemplateCategoryDetailsDto;
import com.cairn.waypoint.dashboard.endpoints.steptemplate.dto.StepTemplateDto;
import com.cairn.waypoint.dashboard.endpoints.steptemplate.dto.StepTemplateListDto;
import com.cairn.waypoint.dashboard.entity.StepTemplateLinkedHomeworkTemplate;
import com.cairn.waypoint.dashboard.service.data.StepTemplateDataService;
import com.cairn.waypoint.dashboard.service.data.TemplateCategoryDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.security.Principal;
import java.time.LocalDateTime;
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
@Tag(name = "Protocol Step Template")
public class GetAllStepTemplatesByTemplateCategoryIdEndpoint {

  public static final String PATH = "/api/protocol-step-template/template-category/{templateCategoryId}";

  private final StepTemplateDataService stepTemplateDataService;
  private final TemplateCategoryDataService templateCategoryDataService;

  public GetAllStepTemplatesByTemplateCategoryIdEndpoint(
      StepTemplateDataService stepTemplateDataService,
      TemplateCategoryDataService templateCategoryDataService) {
    this.stepTemplateDataService = stepTemplateDataService;
    this.templateCategoryDataService = templateCategoryDataService;
  }

  @GetMapping(PATH)
  @PreAuthorize("hasAnyAuthority('SCOPE_protocol.step.template.full', 'SCOPE_admin.full')")
  @Operation(
      summary = "Retrieves all step templates associated with the provided Template Category ID.",
      description = "Retrieves all step templates associated with the provided Template Category ID. Requires the `protocol.step.template.full` permission.",
      security = @SecurityRequirement(name = "oAuth2JwtBearer"),
      responses = {
          @ApiResponse(responseCode = "200",
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = StepTemplateListDto.class))}),
          @ApiResponse(responseCode = "401", description = "Unauthorized",
              content = {@Content(schema = @Schema(hidden = true))}),
          @ApiResponse(responseCode = "403", description = "Forbidden",
              content = {@Content(schema = @Schema(hidden = true))}),
          @ApiResponse(responseCode = "404", description = "Not Found",
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ErrorMessage.class))})})
  public ResponseEntity<?> getAllStepTemplatesByCategory(@PathVariable Long templateCategoryId,
      Principal principal) {
    log.info(
        "User [{}] is Retrieving All Step Templates associated with Template Category with ID [{}]",
        principal.getName(), templateCategoryId);

    if (this.templateCategoryDataService.getTemplateCategoryById(templateCategoryId).isEmpty()) {
      return generateFailureResponse("Step Template Category with ID [" +
          templateCategoryId + "] does not exist", HttpStatus.NOT_FOUND);
    } else {
      return ResponseEntity.ok(
          StepTemplateListDto.builder()
              .stepTemplates(
                  this.stepTemplateDataService.getAllStepTemplatesByTemplateCategoryId(
                          templateCategoryId).stream()
                      .map(stepTemplate -> StepTemplateDto.builder()
                          .id(stepTemplate.getId())
                          .name(stepTemplate.getName())
                          .description(stepTemplate.getDescription())
                          .linkedHomeworkTemplates(
                              stepTemplate.getStepTemplateLinkedHomeworks() == null ||
                                  stepTemplate.getStepTemplateLinkedHomeworks().isEmpty() ?
                                  null : HomeworkTemplateDetailsListDto.builder()
                                  .homeworkTemplates(
                                      stepTemplate.getStepTemplateLinkedHomeworks().stream()
                                          .map(
                                              StepTemplateLinkedHomeworkTemplate::getHomeworkTemplate)
                                          .map(
                                              homeworkTemplate -> HomeworkTemplateDetailsDto.builder()
                                                  .id(homeworkTemplate.getId())
                                                  .name(homeworkTemplate.getName())
                                                  .build())
                                          .toList()
                                  ).build())
                          .category(StepTemplateCategoryDetailsDto.builder()
                              .id(stepTemplate.getCategory().getId())
                              .name(stepTemplate.getCategory().getName())
                              .description(stepTemplate.getCategory().getDescription())
                              .build())
                          .build())
                      .toList())
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
