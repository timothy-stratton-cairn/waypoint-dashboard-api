package com.cairn.waypoint.dashboard.endpoints.ops;

import com.cairn.waypoint.dashboard.dto.authorization.AccountDetailsDto;
import com.cairn.waypoint.dashboard.dto.mail.LinksDto;
import com.cairn.waypoint.dashboard.dto.mail.MailRequestQueueDto;
import com.cairn.waypoint.dashboard.dto.mail.TemplateParametersDto;
import com.cairn.waypoint.dashboard.dto.mail.enumeration.MailRequestEnum;
import com.cairn.waypoint.dashboard.endpoints.filedownload.DownloadDBBackupFileEndpoint;
import com.cairn.waypoint.dashboard.service.data.AccountDataService;
import com.cairn.waypoint.dashboard.utility.fileupload.CustomMultipartFile;
import com.cairn.waypoint.dashboard.utility.fileupload.S3FileUpload;
import com.cairn.waypoint.dashboard.utility.sqs.SqsUtility;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.Principal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Tag(name = "Ops")
public class SnapshotDatabaseEndpoint {

  public static final String PATH = "/api/ops/snapshot-database";
  private final S3FileUpload s3FileUpload;
  private final SqsUtility sqsUtility;
  private final AccountDataService accountDataService;

  @Value("${spring.datasource.url}")
  private String dbUrl;

  @Value("${spring.datasource.username}")
  private String dbUsername;

  @Value("${spring.datasource.password}")
  private String dbPassword;

  @Value("${waypoint.dashboard.base-url}")
  private String baseUrl;

  @Value("${waypoint.dashboard.s3.database-dump-prefix}")
  private String baseKey;

  public SnapshotDatabaseEndpoint(S3FileUpload s3FileUpload, SqsUtility sqsUtility,
      AccountDataService accountDataService) {
    this.s3FileUpload = s3FileUpload;
    this.sqsUtility = sqsUtility;
    this.accountDataService = accountDataService;
  }

  @PostMapping(PATH)
  @PreAuthorize("hasAuthority('SCOPE_admin.full')")
  @Operation(
      summary = "Allows a user to take a snapshot of the dashboard database",
      security = @SecurityRequirement(name = "oAuth2JwtBearer"))
  public ResponseEntity<?> resetDatabase(Principal principal) {
    try {
      File tempFile = File.createTempFile(new Date().getTime() + "-dump", ".sql");

      Path sqlFile = tempFile.toPath();

      // stdErr
      ByteArrayOutputStream stdErr = new ByteArrayOutputStream();

      // stdOut
      OutputStream stdOut = new BufferedOutputStream(
          Files.newOutputStream(sqlFile, StandardOpenOption.CREATE,
              StandardOpenOption.TRUNCATE_EXISTING));

      ExecuteWatchdog watchdog = ExecuteWatchdog.builder().setTimeout(Duration.ofMinutes(5L)).get();

      DefaultExecutor defaultExecutor = DefaultExecutor.builder().get();
      defaultExecutor.setWatchdog(watchdog);
      defaultExecutor.setStreamHandler(new PumpStreamHandler(stdOut, stdErr));

      String database = dbUrl.split("/")[dbUrl.split("/").length - 1].split("\\?")[0];
      CommandLine commandLine = new CommandLine("mysqldump");
      commandLine.addArgument("-u" + dbUsername); // username
      commandLine.addArgument("-p" + dbPassword); // username
      commandLine.addArgument("-h" + dbUrl.split("//")[1].split(":")[0]);
      commandLine.addArgument("-P" + dbUrl.split("//")[1].split("/")[0].split(":")[1]); // password
      commandLine.addArgument(database); // database

      log.info("Exporting SQL data...");

      // Synchronous execution. Blocking until the execution of the child process is complete.
      int exitCode = defaultExecutor.execute(commandLine);

      if (defaultExecutor.isFailure(exitCode) && watchdog.killedProcess()) {
        log.error("timeout...");
      }

      log.info("SQL data export completed: exitCode=[{}], sqlFile=[{}]", exitCode,
          sqlFile);

      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYY-MM-dd hh:mm:ss");
      CustomMultipartFile multipartFile = new CustomMultipartFile(
          FileUtils.readFileToByteArray(tempFile), formatter.format(
          LocalDateTime.now()) + "-" + database + ".sql");

      String s3Key = s3FileUpload.uploadFile(multipartFile, principal.getName(), baseKey);

      AccountDetailsDto accountDetailsDto = accountDataService.getAccountDetails(
          principal.getName()).orElseThrow();

      sqsUtility.sendMessage(MailRequestQueueDto.builder()
          .requestType(MailRequestEnum.DB_BACKUP)
          .recipient(accountDetailsDto.getEmail())
          .links(LinksDto.builder()
              .downloadDbBackupLink(baseUrl + DownloadDBBackupFileEndpoint.PATH.
                  replace("{s3Key}", s3Key.replace(baseKey, "")))
              .build())
          .parameters(TemplateParametersDto.builder()
              .databaseName(database)
              .build())
          .build());

      return ResponseEntity.ok().build();
    } catch (IOException e) {
      log.warn("An error occurred while resetting the database", e);

      return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
    }
  }
}
