package com.cairn.waypoint.dashboard.endpoints.ops;

import com.cairn.waypoint.dashboard.dto.BatchAddAccountDetailsDto;
import com.cairn.waypoint.dashboard.dto.BatchAddAccountDetailsListDto;
import com.cairn.waypoint.dashboard.endpoints.homeworktemplate.AddHomeworkTemplateEndpoint;
import com.cairn.waypoint.dashboard.endpoints.homeworktemplate.dto.AddHomeworkQuestionDetailsDto;
import com.cairn.waypoint.dashboard.endpoints.homeworktemplate.dto.AddHomeworkTemplateDetailsDto;
import com.cairn.waypoint.dashboard.endpoints.homeworktemplate.dto.ExpectedResponseDto;
import com.cairn.waypoint.dashboard.endpoints.ops.dto.ClientCreationResponseDto;
import com.cairn.waypoint.dashboard.endpoints.ops.dto.ClientCreationResponseListDto;
import com.cairn.waypoint.dashboard.endpoints.ops.dto.HomeworkSheetEntryDto;
import com.cairn.waypoint.dashboard.endpoints.protocol.AddProtocolEndpoint;
import com.cairn.waypoint.dashboard.endpoints.protocol.dto.AddProtocolDetailsDto;
import com.cairn.waypoint.dashboard.entity.HomeworkTemplate;
import com.cairn.waypoint.dashboard.entity.ProtocolTemplate;
import com.cairn.waypoint.dashboard.entity.ProtocolTemplateLinkedStepTemplate;
import com.cairn.waypoint.dashboard.entity.StepTemplate;
import com.cairn.waypoint.dashboard.entity.StepTemplateLinkedHomeworkTemplate;
import com.cairn.waypoint.dashboard.entity.TemplateCategory;
import com.cairn.waypoint.dashboard.entity.enumeration.QuestionTypeEnum;
import com.cairn.waypoint.dashboard.service.data.AccountDataService;
import com.cairn.waypoint.dashboard.service.data.HomeworkTemplateDataService;
import com.cairn.waypoint.dashboard.service.data.ProtocolTemplateDataService;
import com.cairn.waypoint.dashboard.service.data.StepTemplateDataService;
import com.cairn.waypoint.dashboard.service.data.TemplateCategoryDataService;
import com.cairn.waypoint.dashboard.utility.fileupload.S3FileUpload;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@Tag(name = "Ops")
public class ImportDataEndpoint {

  public static final String PATH = "/api/ops/import-data";

  private final AccountDataService accountDataService;
  private final ProtocolTemplateDataService protocolTemplateDataService;
  private final TemplateCategoryDataService templateCategoryDataService;
  private final StepTemplateDataService stepTemplateDataService;
  private final HomeworkTemplateDataService homeworkTemplateDataService;
  private final AddProtocolEndpoint addProtocolEndpoint;
  private final AddHomeworkTemplateEndpoint addHomeworkTemplateEndpoint;
  private final S3FileUpload s3FileUpload;

  public ImportDataEndpoint(AccountDataService accountDataService,
      ProtocolTemplateDataService protocolTemplateDataService,
      TemplateCategoryDataService templateCategoryDataService,
      StepTemplateDataService stepTemplateDataService,
      HomeworkTemplateDataService homeworkTemplateDataService,
      AddProtocolEndpoint addProtocolEndpoint,
      AddHomeworkTemplateEndpoint addHomeworkTemplateEndpoint,
      ResetDatabaseEndpoint resetDatabaseEndpoint,
      S3FileUpload s3FileUpload) {
    this.accountDataService = accountDataService;
    this.protocolTemplateDataService = protocolTemplateDataService;
    this.templateCategoryDataService = templateCategoryDataService;
    this.stepTemplateDataService = stepTemplateDataService;
    this.homeworkTemplateDataService = homeworkTemplateDataService;
    this.addProtocolEndpoint = addProtocolEndpoint;
    this.addHomeworkTemplateEndpoint = addHomeworkTemplateEndpoint;
    this.s3FileUpload = s3FileUpload;
  }

  @Transactional
  @PostMapping(PATH)
  @PreAuthorize("hasAuthority('SCOPE_admin.full')")
  @Operation(
      summary = "Allows a user to import data into the System, priming the backend for use",
      security = @SecurityRequirement(name = "oAuth2JwtBearer"))
  public ResponseEntity<String> importData(@RequestParam("file") MultipartFile file,
      Principal principal) throws IOException {
    log.info("User [{}] is importing data", principal.getName());

    Workbook waypointsDataImportSpreadsheet;
    String lowerCaseFileName = Objects.requireNonNull(file.getOriginalFilename()).toLowerCase();
    if (lowerCaseFileName.endsWith(".xlsx")) {
      waypointsDataImportSpreadsheet = new XSSFWorkbook(file.getInputStream());
    } else {
      waypointsDataImportSpreadsheet = new HSSFWorkbook(file.getInputStream());
    }

    importEmployeeAccounts(waypointsDataImportSpreadsheet.getSheet("Employee Accounts"));
    ClientCreationResponseListDto response1 = importClients(
        waypointsDataImportSpreadsheet.getSheet("Client Accounts"));
    importProtocolTemplateDetails(waypointsDataImportSpreadsheet.getSheet("Protocols"),
        principal.getName());
    importHomework(waypointsDataImportSpreadsheet.getSheet("Homework"), principal);
    importProtocolTemplateDetails(waypointsDataImportSpreadsheet.getSheet("Protocols"),
        principal.getName());
    importProtocolAssignments(waypointsDataImportSpreadsheet.getSheet("Protocol Assignments"),
        response1, principal);

    this.s3FileUpload.uploadFile(file, principal.getName());

    return ResponseEntity.ok("Successfully uploaded the file");
  }

  private void importHomework(Sheet homeworkSheet, Principal principal) {
    List<HomeworkSheetEntryDto> sheetEntries = new ArrayList<>();

    for (Row row : homeworkSheet) {
      if (row.getRowNum() == 0 || getCellValue(row.getCell(0)) == null) {
        continue;
      }
      sheetEntries.add(HomeworkSheetEntryDto.builder()
          .homeworkName(getCellValue(row.getCell(0)))
          .homeworkDescription(getCellValue(row.getCell(1)))
          .canHaveMultipleResponses(Boolean.valueOf(getCellValue(row.getCell(2))))
          .homeworkQuestionAbbreviation(getCellValue(row.getCell(3)))
          .homeworkQuestion(getCellValue(row.getCell(4)))
          .homeworkQuestionType(QuestionTypeEnum.valueOf(
              Objects.requireNonNull(getCellValue(row.getCell(5))).toUpperCase().replace("-", "_")))
          .isRequired(Boolean.valueOf(getCellValue(row.getCell(6))))
          .expectedResponses(Objects.isNull(getCellValue(row.getCell(7))) ? null
              : Arrays.asList(Objects.requireNonNull(getCellValue(row.getCell(7))).split(",")))
          .triggeringResponse(
              Objects.isNull(getCellValue(row.getCell(8))) ? null : getCellValue(row.getCell(8)))
          .triggersProtocolCreation(Objects.isNull(getCellValue(row.getCell(9))) ? null
              : Boolean.valueOf(getCellValue(row.getCell(9))))
          .triggeredProtocol(
              Objects.isNull(getCellValue(row.getCell(10))) ? null : getCellValue(row.getCell(10)))
          .build());
    }

    Map<String, List<HomeworkSheetEntryDto>> groupedHomeworkQuestions = sheetEntries.stream()
        .collect(Collectors.groupingBy(HomeworkSheetEntryDto::getHomeworkName));

    groupedHomeworkQuestions.values().stream()
        .map(homeworkSheetEntryDtos -> AddHomeworkTemplateDetailsDto.builder()
            .name(homeworkSheetEntryDtos.get(0).getHomeworkName())
            .description(homeworkSheetEntryDtos.get(0).getHomeworkDescription())
            .isMultiResponse(homeworkSheetEntryDtos.get(0).getCanHaveMultipleResponses())
            .homeworkQuestions(homeworkSheetEntryDtos.stream()
                .map(homeworkQuestion -> AddHomeworkQuestionDetailsDto.builder()
                    .questionAbbr(homeworkQuestion.getHomeworkQuestionAbbreviation())
                    .question(homeworkQuestion.getHomeworkQuestion())
                    .questionType(homeworkQuestion.getHomeworkQuestionType())
                    .isRequired(homeworkQuestion.getIsRequired())
                    .triggerProtocolCreation(homeworkQuestion.getTriggersProtocolCreation())
                    .responseOptions(
                        homeworkQuestion.getTriggersProtocolCreation() &&
                            homeworkQuestion.getExpectedResponses() != null &&
                            !homeworkQuestion.getExpectedResponses().isEmpty() ?
                            Objects.requireNonNull(homeworkQuestion.getExpectedResponses())
                                .stream()
                                .map(response -> ExpectedResponseDto.builder()
                                    .response(response)
                                    .build())
                                .toList() :
                            null
                    )
                    .triggeringResponse(homeworkQuestion.getTriggersProtocolCreation() &&
                        homeworkQuestion.getTriggeringResponse() != null &&
                        !homeworkQuestion.getTriggeringResponse().isEmpty() ?
                        ExpectedResponseDto.builder()
                            .response(homeworkQuestion.getTriggeringResponse())
                            .build() :
                        null
                    )
                    .triggeredProtocolId(homeworkQuestion.getTriggersProtocolCreation() &&
                        homeworkQuestion.getTriggeringResponse() != null &&
                        !homeworkQuestion.getTriggeringResponse().isEmpty() ?
                        protocolTemplateDataService.
                            findProtocolTemplateByName(homeworkQuestion.getTriggeredProtocol())
                            .orElseThrow().getId() :
                        null
                    )
                    .build())
                .toList())
            .build())
        .forEach(
            addHomeworkTemplateDetailsDto -> this.addHomeworkTemplateEndpoint.addHomeworkTemplate(
                addHomeworkTemplateDetailsDto, principal));
  }

  private void importProtocolAssignments(Sheet protocolAssignmentsSheet,
      ClientCreationResponseListDto createdClients, Principal principal) {
    for (Row row : protocolAssignmentsSheet) {
      if (row.getRowNum() == 0 || getCellValue(row.getCell(0)) == null) {
        continue;
      }

      addProtocolEndpoint.addProtocolTemplate(AddProtocolDetailsDto.builder()
          .associatedAccountId(createdClients.getAccountCreationResponses().stream()
              .filter(Predicate.not(ClientCreationResponseDto::getError))
              .filter(clientCreationResponseDto -> clientCreationResponseDto.getUsername()
                  .equals(getCellValue(row.getCell(1))))
              .findFirst()
              .orElseThrow()
              .getAccountId())
          .protocolTemplateId(this.protocolTemplateDataService.findProtocolTemplateByName(
              getCellValue(row.getCell(0))).orElseThrow().getId())
          .goal(getCellValue(row.getCell(2)))
          .comment(getCellValue(row.getCell(2)))
          .build(), principal);
    }
  }

  private void importProtocolTemplateDetails(Sheet protocolsSheet, String modifiedBy) {
    for (Row row : protocolsSheet) {
      if (row.getRowNum() == 0 || getCellValue(row.getCell(2)) == null) {
        continue;
      }

      Optional<ProtocolTemplate> protocolTemplateOptional;
      if ((protocolTemplateOptional = this.protocolTemplateDataService.findProtocolTemplateByName(
          getCellValue(row.getCell(0)))).isEmpty()) {
        protocolTemplateOptional = Optional.of(
            this.protocolTemplateDataService.saveProtocolTemplate(ProtocolTemplate.builder()
                .modifiedBy(modifiedBy)
                .name(getCellValue(row.getCell(0)))
                .description(getCellValue(row.getCell(1)))
                .build()));
      }

      Optional<TemplateCategory> stepCategoryOptional;
      if ((stepCategoryOptional = this.templateCategoryDataService.findByName(
          getCellValue(row.getCell(4)))).isEmpty()) {
        stepCategoryOptional = Optional.of(
            this.templateCategoryDataService.saveTemplateCategory(TemplateCategory.builder()
                .modifiedBy(modifiedBy)
                .name(getCellValue(row.getCell(4)))
                .description(getCellValue(row.getCell(4)))
                .build()));
      }

      Optional<HomeworkTemplate> homeworkTemplateOptional = Optional.empty();
      if (getCellValue(row.getCell(4)) != null) {
        homeworkTemplateOptional = this.homeworkTemplateDataService.findHomeworkTemplateByName(
            getCellValue(row.getCell(4)));
      }

      Optional<StepTemplate> stepTemplateOptional;
      if ((stepTemplateOptional = this.stepTemplateDataService.findStepTemplateByName(
          getCellValue(row.getCell(2)))).isEmpty()) {
        stepTemplateOptional = Optional.of(
            this.stepTemplateDataService.saveStepTemplate(StepTemplate.builder()
                .modifiedBy(modifiedBy)
                .name(getCellValue(row.getCell(2)))
                .description(getCellValue(row.getCell(3)))
                .category(stepCategoryOptional.get())
                .build()));
      }

      if (homeworkTemplateOptional.isPresent()) {
        stepTemplateOptional.get().getStepTemplateLinkedHomeworks().add(
            StepTemplateLinkedHomeworkTemplate.builder()
                .modifiedBy(modifiedBy)
                .stepTemplate(stepTemplateOptional.get())
                .homeworkTemplate(homeworkTemplateOptional.get())
                .build());
        this.stepTemplateDataService.saveStepTemplate(stepTemplateOptional.get());
      }

      protocolTemplateOptional.get().getProtocolTemplateSteps()
          .add(ProtocolTemplateLinkedStepTemplate.builder()
              .modifiedBy(modifiedBy)
              .protocolTemplate(protocolTemplateOptional.get())
              .stepTemplate(stepTemplateOptional.get())
              .ordinalIndex(row.getRowNum())
              .build());

      protocolTemplateDataService.saveProtocolTemplate(protocolTemplateOptional.get());
    }
  }

  private void importEmployeeAccounts(Sheet employeeAccountsSheet) {
    List<BatchAddAccountDetailsDto> accountsToAdd = new ArrayList<>();
    for (int i = 1; i < employeeAccountsSheet.getLastRowNum(); i++) {
      if (!Objects.requireNonNull(getCellValue(employeeAccountsSheet.getRow(i).getCell(3)))
          .isEmpty()) {
        accountsToAdd.add(BatchAddAccountDetailsDto.builder()
            .firstName(getCellValue(employeeAccountsSheet.getRow(i).getCell(0)))
            .lastName(getCellValue(employeeAccountsSheet.getRow(i).getCell(1)))
            .email(getCellValue(employeeAccountsSheet.getRow(i).getCell(2)))
            .username(getCellValue(employeeAccountsSheet.getRow(i).getCell(3)))
            .password(getCellValue(employeeAccountsSheet.getRow(i).getCell(4)))
            .roleNames(Set.of(Objects.requireNonNull(
                getCellValue(employeeAccountsSheet.getRow(i).getCell(5)))))
            .build());
      }
    }

    accountDataService.createBatchAccounts(BatchAddAccountDetailsListDto.builder()
        .accountBatch(accountsToAdd)
        .build());
  }

  private ClientCreationResponseListDto importClients(Sheet cllientAccountsSheet) {
    List<BatchAddAccountDetailsDto> accountsToAdd = new ArrayList<>();
    for (int i = 1; i < cllientAccountsSheet.getLastRowNum(); i++) {
      if (!Objects.requireNonNull(getCellValue(cllientAccountsSheet.getRow(i).getCell(11)))
          .isEmpty()) {
        accountsToAdd.add(BatchAddAccountDetailsDto.builder()
            .firstName(getCellValue(cllientAccountsSheet.getRow(i).getCell(0)))
            .lastName(getCellValue(cllientAccountsSheet.getRow(i).getCell(1)))
            .email(getCellValue(cllientAccountsSheet.getRow(i).getCell(2)))
            .phoneNumber(getCellValue(cllientAccountsSheet.getRow(i).getCell(3)))
            .address1(getCellValue(cllientAccountsSheet.getRow(i).getCell(4)))
            .address2(getCellValue(cllientAccountsSheet.getRow(i).getCell(5)))
            .city(getCellValue(cllientAccountsSheet.getRow(i).getCell(6)))
            .state(getCellValue(cllientAccountsSheet.getRow(i).getCell(7)))
            .zip(getCellValue(cllientAccountsSheet.getRow(i).getCell(8)))
            .coClientUsername(getCellValue(cllientAccountsSheet.getRow(i).getCell(9)))
            .parentAccountUsername(getCellValue(cllientAccountsSheet.getRow(i).getCell(10)))
            .username(getCellValue(cllientAccountsSheet.getRow(i).getCell(11)))
            .roleNames(Set.of("CLIENT"))
            .build());
      }
    }

    return accountDataService.createBatchAccounts(BatchAddAccountDetailsListDto.builder()
        .accountBatch(accountsToAdd)
        .build());
  }

  private String getCellValue(Cell cell) {
    try {
      return cell.getStringCellValue();
    } catch (IllegalStateException e) {
      try {
        return String.valueOf(cell.getNumericCellValue()).split("\\.")[0];
      } catch (IllegalStateException e1) {
        return String.valueOf(cell.getBooleanCellValue());
      }
    } catch (NullPointerException e) {
      return null;
    }
  }
}
