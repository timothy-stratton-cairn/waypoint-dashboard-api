package com.cairn.waypoint.dashboard.endpoints.homework;

import com.cairn.waypoint.dashboard.endpoints.ErrorMessage;

import com.cairn.waypoint.dashboard.endpoints.homework.dto.HomeworkListDto;
import com.cairn.waypoint.dashboard.service.data.HomeworkDataService;
import com.cairn.waypoint.dashboard.service.data.HouseholdDataService;
import com.cairn.waypoint.dashboard.service.helper.HomeworkHelperService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

//TODO delete 
/*
@Slf4j
@RestController
@Tag(name = "Homework")
public class GetHomeworkByCategoryEndpoint {

  public static final String PATH = "/api/homework/category/{categoryId}";

  private final HomeworkHelperService homeworkHelperService;

  private final HomeworkDataService homeworkDataService;

  public GetHomeworkByCategoryEndpoint(HomeworkHelperService homeworkHelperService,
      HouseholdDataService householdDataService, HomeworkDataService homeworkDataService) {
    this.homeworkHelperService = homeworkHelperService;
    this.homeworkDataService = homeworkDataService;
  }

  @GetMapping(PATH)
  @PreAuthorize("hasAnyAuthority('SCOPE_homework.full', 'SCOPE_admin.full')")
  @Operation(
      summary = "Retrieves all homework associated with the category ID.",
      description = "Retrieves all homework associated with the category ID. Requires the `homework.full` permission.",
      security = @SecurityRequirement(name = "oAuth2JwtBearer"),
      responses = {
          @ApiResponse(responseCode = "200",
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = HomeworkListDto.class))}),
          @ApiResponse(responseCode = "401", description = "Unauthorized",
              content = {@Content(schema = @Schema(hidden = true))}),
          @ApiResponse(responseCode = "403", description = "Forbidden",
              content = {@Content(schema = @Schema(hidden = true))})})
  
  
  public ResponseEntity<?> getAllHomeworkBycategoryId(@PathVariable Long categoryId,
	      Principal principal) {
	    log.info("User [{}] is retrieving all homework of the proper category [{}]",
	        principal.getName(), categoryId);

	    if (this.homeworkDataService.getHomeworkByCategory(categoryId).isEmpty()) {
	      return generateFailureResponse("There are no Homeworks for the given category [" +
	              categoryId + "]",
	          HttpStatus.NOT_FOUND);
	    } else {
	      return ResponseEntity.ok(HomeworkListDto.builder()
	          .homeworks(this.homeworkDataService.getHomeworkByCategory(categoryId).stream()
	              .map(homeworkHelperService::generateHomeworkDto)
	              .collect(Collectors.toList()))
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
  
  */