package com.cairn.waypoint.dashboard.endpoints.filedownload;

import com.cairn.waypoint.dashboard.endpoints.ErrorMessage;
import com.cairn.waypoint.dashboard.utility.fileupload.S3FileUpload;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.time.LocalDateTime;
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
public class DownloadDBBackupFileEndpoint {

  public static final String PATH = "/api/file/db-backup/{s3Key}";
  private final S3FileUpload s3FileUpload;

  @Value("${waypoint.dashboard.s3.database-dump-prefix}")
  private String databaseDumpPrefix;

  public DownloadDBBackupFileEndpoint(S3FileUpload s3FileUpload) {
    this.s3FileUpload = s3FileUpload;
  }

  @GetMapping(PATH)
  @PreAuthorize("permitAll()")
  @Operation(
      summary = "Retrieves a database backup file from S3 and downloads to requester's browser.",
      description = "Retrieves a database backup file from S3 and downloads to requester's browser.",
      security = @SecurityRequirement(name = "oAuth2JwtBearer"),
      responses = {
          @ApiResponse(responseCode = "200",
              description = "The file requested, downloaded to the user's browser"),
          @ApiResponse(responseCode = "404", description = "Not Found",
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ErrorMessage.class))})})
  public ResponseEntity<?> downloadFile(@PathVariable("s3Key") String s3Key) {
    log.info("Downloading file [{}] from S3", s3Key);
    try {
      Resource resource = (Resource) s3FileUpload.downloadFile(databaseDumpPrefix + s3Key);

      HttpHeaders headers = new HttpHeaders();
      headers.add(HttpHeaders.CONTENT_DISPOSITION,
          "attachment; filename=" + s3Key);
      return ResponseEntity.ok()
          .headers(headers)
          .contentLength(resource.getFile().length())
          .contentType(MediaType.APPLICATION_OCTET_STREAM)
          .body(resource);
    } catch (IOException e) {
      return generateFailureResponse("Error Downloading the File", HttpStatus.NOT_FOUND);
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
