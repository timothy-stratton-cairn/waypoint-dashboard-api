package com.cairn.waypoint.dashboard.endpoints.homeworktemplate;

import com.cairn.waypoint.dashboard.endpoints.homeworktemplate.dto.HomeworkTemplateDto;
import com.cairn.waypoint.dashboard.endpoints.homeworktemplate.dto.HomeworkTemplateListDto;
import com.cairn.waypoint.dashboard.service.data.HomeworkTemplateDataService;
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
@Tag(name = "Homework Template")
public class GetAllHomeworkTemplatesEndpoint {

  public static final String PATH = "/api/homework-template";

  private final HomeworkTemplateDataService homeworkTemplateDataService;

  public GetAllHomeworkTemplatesEndpoint(HomeworkTemplateDataService homeworkTemplateDataService) {
    this.homeworkTemplateDataService = homeworkTemplateDataService;
  }

  @GetMapping(PATH)
  @PreAuthorize("hasAnyAuthority('SCOPE_homework.template.full', 'SCOPE_admin.full')")
  @Operation(
      summary = "Retrieves all homework templates.",
      description = "Retrieves all homework templates. Requires the `homework.template.full` permission.",
      security = @SecurityRequirement(name = "oAuth2JwtBearer"),
      responses = {
          @ApiResponse(responseCode = "200",
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = HomeworkTemplateListDto.class))}),
          @ApiResponse(responseCode = "401", description = "Unauthorized",
              content = {@Content(schema = @Schema(hidden = true))}),
          @ApiResponse(responseCode = "403", description = "Forbidden",
              content = {@Content(schema = @Schema(hidden = true))})})
  public ResponseEntity<HomeworkTemplateListDto> getAllHomeworkTemplates(Principal principal) {

    log.info("User [{}] is Retrieving All Homework Templates", principal.getName());
    return ResponseEntity.ok(
        HomeworkTemplateListDto.builder()
            .homeworkTemplates(this.homeworkTemplateDataService.getAllHomeworkTemplates().stream()
                .map(homeworkTemplate -> HomeworkTemplateDto.builder()
                    .id(homeworkTemplate.getId())
                    .name(homeworkTemplate.getName())
                    .description(homeworkTemplate.getDescription())
                    .isMultiResponse(homeworkTemplate.getMultiResponse())
                    .numberOfQuestionsInHomework(homeworkTemplate.getHomeworkQuestions().size())
                    .build())
                .toList())
            .build()
    );
  }
}
