package com.cairn.waypoint.dashboard.endpoints.protocolstep;

import com.cairn.waypoint.dashboard.endpoints.ErrorMessage;
import com.cairn.waypoint.dashboard.entity.Protocol;
import com.cairn.waypoint.dashboard.entity.ProtocolStep;
import com.cairn.waypoint.dashboard.entity.ProtocolStepAttachment;
import com.cairn.waypoint.dashboard.service.data.ProtocolDataService;
import com.cairn.waypoint.dashboard.service.data.ProtocolStepDataService;
import com.cairn.waypoint.dashboard.utility.fileupload.S3FileUpload;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@Tag(name = "Protocol Step")
public class AttachFileToProtocolStepEndpoint {

  public static final String PATH = "/api/protocol/{protocolId}/protocol-step/{protocolStepId}/attachment";
  private final S3FileUpload s3FileUpload;
  private final ProtocolDataService protocolDataService;
  private final ProtocolStepDataService protocolStepDataService;
  @Value("${waypoint.dashboard.s3.step-attachment-key-prefix}")
  private String baseKey;

  public AttachFileToProtocolStepEndpoint(ProtocolDataService protocolDataService,
      ProtocolStepDataService protocolStepDataService, S3FileUpload s3FileUpload) {
    this.protocolDataService = protocolDataService;
    this.protocolStepDataService = protocolStepDataService;
    this.s3FileUpload = s3FileUpload;
  }

  @Transactional
  @PostMapping(value = PATH, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @PreAuthorize("hasAnyAuthority('SCOPE_protocol.full', 'SCOPE_admin.full')")
  @Operation(
      summary = "Allows a user to attach a file a protocol step.",
      description = "Allows a user to attach a file a protocol step. Requires the `protocol.full` permission.",
      security = @SecurityRequirement(name = "oAuth2JwtBearer"),
      responses = {
          @ApiResponse(responseCode = "200",
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = String.class))}),
          @ApiResponse(responseCode = "400", description = "Bad Request",
              content = {@Content(schema = @Schema(hidden = true))}),
          @ApiResponse(responseCode = "401", description = "Unauthorized",
              content = {@Content(schema = @Schema(hidden = true))}),
          @ApiResponse(responseCode = "403", description = "Forbidden",
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ErrorMessage.class))}),
          @ApiResponse(responseCode = "404", description = "Not Found",
              content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ErrorMessage.class))})})
  public ResponseEntity<?> attachFileToProtocolStep(@PathVariable Long protocolId,
      @PathVariable Long protocolStepId,
      @RequestParam MultipartFile file, Principal principal) throws IOException {
    log.info(
        "User [{}] is attaching a file with name [{}] to Protocol Step with ID [{}] on Protocol with ID [{}]",
        principal.getName(), file.getOriginalFilename(), protocolStepId, protocolId);

    Optional<Protocol> optionalProtocolToUpdate = this.protocolDataService.getProtocolById(
        protocolId);
    Optional<ProtocolStep> optionalProtocolStepToUpdate = this.protocolStepDataService.getProtocolStepById(
        protocolStepId);

    if (optionalProtocolToUpdate.isEmpty()) {
      return generateFailureResponse("Protocol with ID [" +
          protocolId + "] not found", HttpStatus.NOT_FOUND);
    } else if (optionalProtocolStepToUpdate.isEmpty()) {
      return generateFailureResponse("Protocol Step with ID [" +
          protocolStepId + "] not found", HttpStatus.NOT_FOUND);
    } else if (!optionalProtocolToUpdate.get().getProtocolSteps()
        .contains(optionalProtocolStepToUpdate.get())) {
      return generateFailureResponse("The Protocol Step ID [" + protocolStepId
              + "] is not a Protocol Step ID present on Protocol with ID [" + protocolId + "]",
          HttpStatus.UNPROCESSABLE_ENTITY);
    } else {
      ProtocolStep protocolStepToUpdate = optionalProtocolStepToUpdate.get();

      String s3Key = s3FileUpload.uploadFile(file, principal.getName(), baseKey);

      protocolStepToUpdate.setModifiedBy(principal.getName());
      protocolStepToUpdate.getAttachments().add(ProtocolStepAttachment.builder()
          .modifiedBy(principal.getName())
          .protocolStep(protocolStepToUpdate)
          .originalUploader(principal.getName())
          .fileGuid(UUID.randomUUID().toString())
          .filename(file.getOriginalFilename())
          .s3Key(s3Key)
          .build());

      ProtocolStep updatedProtocolStep = this.protocolStepDataService.saveProtocolStep(
          protocolStepToUpdate);

      log.info(
          "Protocol Step with ID [{}] on Protocol with ID [{}] has been updated with the provided attachment [{}]",
          updatedProtocolStep.getId(), protocolId, file.getOriginalFilename());

      return ResponseEntity.ok("Protocol Step with ID [" +
          updatedProtocolStep.getId() + "] on Protocol with ID [" + protocolId
          + "] has been updated with the provided attachment [" +
          file.getOriginalFilename() + "]");
    }
  }

  private ResponseEntity<ErrorMessage> generateFailureResponse(String message, HttpStatus status) {
    log.warn(message);
    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
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
