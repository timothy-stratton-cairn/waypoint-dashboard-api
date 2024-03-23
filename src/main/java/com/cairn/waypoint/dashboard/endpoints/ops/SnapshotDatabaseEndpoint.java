package com.cairn.waypoint.dashboard.endpoints.ops;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Tag(name = "Ops")
public class SnapshotDatabaseEndpoint {

  public static final String PATH = "/ops/snapshot-database";

  @PostMapping(PATH)
  @PreAuthorize("hasAuthority('SCOPE_admin.full')")
  @Operation(
      summary = "Allows a user to take a snapshot of the dashboard database",
      security = @SecurityRequirement(name = "oAuth2JwtBearer"))
  public ResponseEntity<?> resetDatabase() {
    try {
      Path sqlFile = Paths.get("C:\\Users\\tstra\\Desktop\\db.sql");

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

      CommandLine commandLine = new CommandLine("mysqldump");
      commandLine.addArgument("-uroot"); // username
      commandLine.addArgument("-p"); // password
      commandLine.addArgument("dashboard"); // database

      log.info("Exporting SQL data...");

      // Synchronous execution. Blocking until the execution of the child process is complete.
      int exitCode = defaultExecutor.execute(commandLine);

      if (defaultExecutor.isFailure(exitCode) && watchdog.killedProcess()) {
        log.error("timeout...");
      }

      log.info("SQL data export completed: exitCode=[{}], sqlFile=[{}]", exitCode,
          sqlFile);

      return ResponseEntity.ok().build();
    } catch (IOException e) {
      log.warn("An error occurred while resetting the database", e);

      return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
    }
  }
}
