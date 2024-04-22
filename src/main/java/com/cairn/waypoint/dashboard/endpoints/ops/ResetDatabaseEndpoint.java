package com.cairn.waypoint.dashboard.endpoints.ops;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import java.sql.SQLException;
import javax.sql.DataSource;
import liquibase.Liquibase;
import liquibase.command.CommandScope;
import liquibase.command.core.UpdateCommandStep;
import liquibase.command.core.helpers.DbUrlConnectionArgumentsCommandStep;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.CommandExecutionException;
import liquibase.exception.DatabaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Tag(name = "Ops")
public class ResetDatabaseEndpoint {

  public static final String PATH = "/api/ops/reset-database";

  private final DataSource dataSource;

  public ResetDatabaseEndpoint(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  @Transactional
  @PostMapping(PATH)
  @PreAuthorize("hasAuthority('SCOPE_admin.full')")
  @Operation(
      summary = "Allows a user to reset the dashboard database, clearing all data",
      security = @SecurityRequirement(name = "oAuth2JwtBearer"))
  public ResponseEntity<?> resetDatabase() {
    try {
      log.info("Attempting to reset the database");

      Database database = DatabaseFactory.getInstance()
          .findCorrectDatabaseImplementation(new JdbcConnection(dataSource.getConnection()));
      Liquibase liquibase = new liquibase.Liquibase("db/changelog/db.changelog-master.yaml",
          new ClassLoaderResourceAccessor(), database);

      liquibase.dropAll();

      CommandScope updateCommand = new CommandScope(UpdateCommandStep.COMMAND_NAME);
      updateCommand.addArgumentValue(DbUrlConnectionArgumentsCommandStep.DATABASE_ARG,
          liquibase.getDatabase());
      updateCommand.addArgumentValue(UpdateCommandStep.CHANGELOG_FILE_ARG,
          liquibase.getChangeLogFile());
      updateCommand.execute();

      log.info("Resetting of the Database complete");

      return ResponseEntity.ok().build();
    } catch (SQLException | DatabaseException | CommandExecutionException e) {
      log.info("An error occurred while resetting the database");

      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
    }
  }
}
