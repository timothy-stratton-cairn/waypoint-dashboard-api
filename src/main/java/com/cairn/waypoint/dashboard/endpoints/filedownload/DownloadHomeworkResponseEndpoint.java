package com.cairn.waypoint.dashboard.endpoints.filedownload;

import com.cairn.waypoint.dashboard.endpoints.ErrorMessage;
import com.cairn.waypoint.dashboard.entity.HomeworkResponse;
import com.cairn.waypoint.dashboard.service.data.HomeworkResponseDataService;
import com.cairn.waypoint.dashboard.utility.fileupload.S3FileUpload;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Tag(name = "File Download")
public class DownloadHomeworkResponseEndpoint {

  public static final String PATH = "/api/file/homework-response/{fileGuid}";
  private final S3FileUpload s3FileUpload;
  private final HomeworkResponseDataService homeworkResponseDataService;
  @Value("${waypoint.dashboard.s3.homework-response-key-prefix}")
  private String homeworkResponseKeyPrefix;

  public DownloadHomeworkResponseEndpoint(S3FileUpload s3FileUpload,
      HomeworkResponseDataService homeworkResponseDataService) {
    this.s3FileUpload = s3FileUpload;
    this.homeworkResponseDataService = homeworkResponseDataService;
  }

  @GetMapping(PATH)
  @PreAuthorize("permitAll()")
  @Operation(
      summary = "Retrieves a file added as a Homework Response by the provided GUID.",
      description = "Retrieves a file added as a Homework Response by the provided GUID.",
      security = @SecurityRequirement(name = "oAuth2JwtBearer"),
      responses = {
          @ApiResponse(responseCode = "200",
              description = "The file requested, downloaded to the user's browser"),
          @ApiResponse(responseCode = "404", description = "Not Found",
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ErrorMessage.class))})})
  public ResponseEntity<?> downloadFile(@PathVariable("fileGuid") String fileGuid) {
    log.info("Downloading homework response file with GUID [{}] from S3", fileGuid);

    Optional<HomeworkResponse> homeworkResponse = homeworkResponseDataService.getHomeworkResponseByFileGuid(
        fileGuid);

    if (homeworkResponse.isPresent()) {
      try {
        Resource resource = (Resource) s3FileUpload.downloadFile(
            homeworkResponse.get().getResponse());

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=" + homeworkResponse.get().getResponse()
                .replace(homeworkResponseKeyPrefix, ""));
        return ResponseEntity.ok()
            .headers(headers)
            .contentLength(resource.getFile().length())
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(resource);
      } catch (IOException e) {
        return generateFailureResponse("Error Downloading the File", HttpStatus.NOT_FOUND);
      }
    } else {
      return generateFailureResponse("No Homework Response associated with provided guid",
          HttpStatus.NOT_FOUND);
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
