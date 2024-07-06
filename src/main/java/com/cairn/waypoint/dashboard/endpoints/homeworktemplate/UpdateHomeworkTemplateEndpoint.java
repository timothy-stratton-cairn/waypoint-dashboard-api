package com.cairn.waypoint.dashboard.endpoints.homeworktemplate;

import com.cairn.waypoint.dashboard.endpoints.ErrorMessage;
import com.cairn.waypoint.dashboard.endpoints.homeworktemplate.dto.UpdateHomeworkTemplateDetailsDto;
import com.cairn.waypoint.dashboard.entity.HomeworkQuestion;
import com.cairn.waypoint.dashboard.entity.HomeworkTemplate;
import com.cairn.waypoint.dashboard.entity.HomeworkTemplateLinkedHomeworkQuestion;
import com.cairn.waypoint.dashboard.service.data.HomeworkQuestionDataService;
import com.cairn.waypoint.dashboard.service.data.HomeworkTemplateDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Tag(name = "Homework Template")
public class UpdateHomeworkTemplateEndpoint {

  public static final String PATH = "/api/homework-template/{homeworkTemplateId}";

  private final HomeworkTemplateDataService homeworkTemplateDataService;
  private final HomeworkQuestionDataService homeworkQuestionDataService;

  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  public UpdateHomeworkTemplateEndpoint(HomeworkTemplateDataService homeworkTemplateDataService,
      HomeworkQuestionDataService homeworkQuestionDataService) {
    this.homeworkTemplateDataService = homeworkTemplateDataService;
    this.homeworkQuestionDataService = homeworkQuestionDataService;
  }

  @Transactional
  @PatchMapping(PATH)
  @PreAuthorize("hasAnyAuthority('SCOPE_homework.template.full', 'SCOPE_admin.full')")
  @Operation(
      summary = "Allows a user to update an existing homework template",
      description = "Allows a user to update an existing homework template."
          + " Requires the `homework.template.full` permission.",
      security = @SecurityRequirement(name = "oAuth2JwtBearer"),
      responses = {
          @ApiResponse(responseCode = "200",
              description = "Updated - Homework Template creation was successful",
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = String.class))}),
          @ApiResponse(responseCode = "400", description = "Bad Request",
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ErrorMessage.class))}),
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
  public ResponseEntity<?> addHomeworkTemplate(@PathVariable Long homeworkTemplateId,
      @RequestBody UpdateHomeworkTemplateDetailsDto updateHomeworkTemplateDetailsDto,
      Principal principal) {
    log.info("User [{}] is attempting to update Homework Template with Name [{}]",
        principal.getName(), updateHomeworkTemplateDetailsDto.getName());

    Optional<HomeworkTemplate> homeworkTemplateToUpdateOptional = homeworkTemplateDataService.getHomeworkTemplateById(
        homeworkTemplateId);
    Set<ConstraintViolation<UpdateHomeworkTemplateDetailsDto>> violations = validator.validate(
        updateHomeworkTemplateDetailsDto);

    if (homeworkTemplateToUpdateOptional.isEmpty()) {
      return generateFailureResponse("Homework Template with ID [" +
              homeworkTemplateId + "] does not exist",
          HttpStatus.NOT_FOUND);
    } else if (!violations.isEmpty()) {
      return generateFailureResponse(
          violations.stream().map(ConstraintViolation::getMessage).collect(
              Collectors.joining(", ")), HttpStatus.BAD_REQUEST);
    } else if (this.homeworkTemplateDataService.findHomeworkTemplateByName(
        updateHomeworkTemplateDetailsDto.getName()).isPresent() &&
        !homeworkTemplateToUpdateOptional.get().getName()
            .equals(updateHomeworkTemplateDetailsDto.getName())) {
      return generateFailureResponse("Homework Template with name [" +
              updateHomeworkTemplateDetailsDto.getName() + "] already exist",
          HttpStatus.CONFLICT);
    } else {
      HomeworkTemplate updatedHomeworkTemplate = updateHomeworkTemplate(
          homeworkTemplateToUpdateOptional.get(), updateHomeworkTemplateDetailsDto,
          principal.getName());

      if (updateHomeworkTemplateDetailsDto.getHomeworkQuestionIds() != null &&
          !updateHomeworkTemplateDetailsDto.getHomeworkQuestionIds().isEmpty()) {

        updatedHomeworkTemplate.getHomeworkQuestions()
            .forEach(homeworkTemplateLinkedHomeworkQuestion ->
                homeworkTemplateLinkedHomeworkQuestion.setActive(Boolean.FALSE));

        for (Long homeworkQuestionId : updateHomeworkTemplateDetailsDto.getHomeworkQuestionIds()) {
          Optional<HomeworkTemplateLinkedHomeworkQuestion> homeworkTemplateLinkedHomeworkQuestionOptional =
              updatedHomeworkTemplate.getHomeworkQuestions().stream()
                  .filter(homeworkTemplateLinkedHomeworkQuestion ->
                      homeworkTemplateLinkedHomeworkQuestion
                          .getHomeworkQuestion().getId().equals(homeworkQuestionId))
                  .findFirst();

          if (homeworkTemplateLinkedHomeworkQuestionOptional.isPresent()) {
            homeworkTemplateLinkedHomeworkQuestionOptional.get().setActive(Boolean.TRUE);
            homeworkTemplateLinkedHomeworkQuestionOptional.get().getHomeworkQuestion()
                .setStatus(updateHomeworkTemplateDetailsDto.getTemplateStatus());
          } else {
            try {
              HomeworkQuestion homeworkQuestion = homeworkQuestionDataService.getHomeworkQuestionById(
                  homeworkQuestionId).orElseThrow();
              homeworkQuestion.setStatus(updateHomeworkTemplateDetailsDto.getTemplateStatus());

              updatedHomeworkTemplate.getHomeworkQuestions()
                  .add(HomeworkTemplateLinkedHomeworkQuestion.builder()
                      .modifiedBy(principal.getName())
                      .homeworkTemplate(updatedHomeworkTemplate)
                      .homeworkQuestion(homeworkQuestion)
                      .ordinalIndex(updatedHomeworkTemplate.getHomeworkQuestions().size())
                      .build());
            } catch (Exception e) {
              return generateFailureResponse("Homework Question with ID [" +
                      homeworkQuestionId + "] does not exist",
                  HttpStatus.NOT_FOUND);
            }
          }
        }
      } else {
        updatedHomeworkTemplate.getHomeworkQuestions().stream().map(HomeworkTemplateLinkedHomeworkQuestion::getHomeworkQuestion)
            .forEach(homeworkQuestion -> homeworkQuestion.setStatus(updatedHomeworkTemplate.getStatus()));
      }

      this.homeworkTemplateDataService.saveHomeworkTemplate(updatedHomeworkTemplate);

      log.info("Homework Template with ID [{}] was successfully created",
          updatedHomeworkTemplate.getId());
      return ResponseEntity.status(HttpStatus.OK)
          .body("Homework Template with ID [" + homeworkTemplateId + "] and name ["
              + updatedHomeworkTemplate.getName() + "] updated successfully");
    }
  }

  private HomeworkTemplate updateHomeworkTemplate(HomeworkTemplate homeworkTemplateToUpdate,
      UpdateHomeworkTemplateDetailsDto updateHomeworkTemplateDetailsDto, String modifiedBy) {

    if (updateHomeworkTemplateDetailsDto.getName() != null
        && !updateHomeworkTemplateDetailsDto.getName().isEmpty()) {
      homeworkTemplateToUpdate.setName(updateHomeworkTemplateDetailsDto.getName());
    }

    if (updateHomeworkTemplateDetailsDto.getDescription() != null
        && !updateHomeworkTemplateDetailsDto.getDescription().isEmpty()) {
      homeworkTemplateToUpdate.setDescription(updateHomeworkTemplateDetailsDto.getDescription());
    }

    if (updateHomeworkTemplateDetailsDto.getIsMultiResponse() != null) {
      homeworkTemplateToUpdate.setMultiResponse(
          updateHomeworkTemplateDetailsDto.getIsMultiResponse());
    }

    if (updateHomeworkTemplateDetailsDto.getStatus() != null) {
      homeworkTemplateToUpdate.setStatus(
          updateHomeworkTemplateDetailsDto.getTemplateStatus());
    }

    homeworkTemplateToUpdate.setModifiedBy(modifiedBy);

    return this.homeworkTemplateDataService.saveHomeworkTemplate(homeworkTemplateToUpdate);
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
