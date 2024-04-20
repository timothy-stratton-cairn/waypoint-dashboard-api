package com.cairn.waypoint.dashboard.endpoints.ops;

import com.cairn.waypoint.dashboard.dto.BatchAddAccountDetailsDto;
import com.cairn.waypoint.dashboard.dto.BatchAddAccountDetailsListDto;
import com.cairn.waypoint.dashboard.endpoints.ops.dto.ClientCreationResponseListDto;
import com.cairn.waypoint.dashboard.service.data.AccountDataService;
import com.cairn.waypoint.dashboard.utility.fileupload.S3FileUpload;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;

@Slf4j
@RestController
@Tag(name = "Ops")
public class ImportDataEndpoint {

    public static final String PATH = "/api/ops/import-data";

    private final ResetDatabaseEndpoint resetDatabaseEndpoint;
    private final AccountDataService accountDataService;
    private final S3FileUpload s3FileUpload;

    public ImportDataEndpoint(ResetDatabaseEndpoint resetDatabaseEndpoint,
        AccountDataService accountDataService, S3FileUpload s3FileUpload) {
        this.resetDatabaseEndpoint = resetDatabaseEndpoint;
      this.accountDataService = accountDataService;
      this.s3FileUpload = s3FileUpload;
    }

    @Transactional
    @PostMapping(PATH)
    @PreAuthorize("hasAuthority('SCOPE_admin.full')")
    @Operation(
            summary = "Allows a user to import data into the System, priming the backend for use",
            security = @SecurityRequirement(name = "oAuth2JwtBearer"))
    public ResponseEntity<String> importData(@RequestParam("file") MultipartFile file, Principal principal) throws IOException {
        log.info("User [{}] is importing data", principal.getName());

        this.s3FileUpload.uploadFile(file, principal.getName());

        resetDatabaseEndpoint.resetDatabase();

        Workbook offices;
        String lowerCaseFileName = file.getOriginalFilename().toLowerCase();
        if (lowerCaseFileName.endsWith(".xlsx")) {
            offices = new XSSFWorkbook(file.getInputStream());
        } else {
            offices = new HSSFWorkbook(file.getInputStream());
        }

        ClientCreationResponseListDto response = importClients(offices.getSheet("Client Accounts"));



        return ResponseEntity.ok("Successfully uploaded the file");
    }

    private ClientCreationResponseListDto importClients(Sheet cllientAccountsSheet) {
        List<BatchAddAccountDetailsDto> accountsToAdd = new ArrayList<>();
        for (int i = 0; i < cllientAccountsSheet.getLastRowNum(); i++) {
            accountsToAdd.add(BatchAddAccountDetailsDto.builder()
                .firstName(cllientAccountsSheet.getRow(i).getCell(0).getStringCellValue())
                .lastName(cllientAccountsSheet.getRow(i).getCell(1).getStringCellValue())
                .email(cllientAccountsSheet.getRow(i).getCell(2).getStringCellValue())
                .build());
        }

        return accountDataService.createBatchAccounts(BatchAddAccountDetailsListDto.builder()
            .accountBatch(accountsToAdd)
            .build());
    }
}
