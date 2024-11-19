package com.cairn.waypoint.dashboard.endpoints.ops;

import com.cairn.waypoint.dashboard.dto.authorization.BatchAddAccountDetailsDto;
import com.cairn.waypoint.dashboard.dto.authorization.BatchAddAccountDetailsListDto;
import com.cairn.waypoint.dashboard.endpoints.homeworkquestion.dto.importHomeworkQuestionDto;
import com.cairn.waypoint.dashboard.endpoints.homeworktemplate.dto.AddHomeworkQuestionDetailsDto;
import com.cairn.waypoint.dashboard.endpoints.homeworktemplate.dto.AddHomeworkTemplateDetailsDto;
import com.cairn.waypoint.dashboard.endpoints.homeworktemplate.dto.ExpectedResponseDto;
import com.cairn.waypoint.dashboard.endpoints.ops.dto.ClientCreationResponseDto;
import com.cairn.waypoint.dashboard.endpoints.ops.dto.ClientCreationResponseListDto;
import com.cairn.waypoint.dashboard.endpoints.ops.dto.HomeworkSheetEntryDto;
import com.cairn.waypoint.dashboard.endpoints.protocol.AddProtocolEndpoint;
import com.cairn.waypoint.dashboard.endpoints.protocol.dto.AddProtocolDetailsDto;
import com.cairn.waypoint.dashboard.endpoints.protocoltemplate.AddProtocolTemplateEndpoint;
import com.cairn.waypoint.dashboard.endpoints.protocoltemplate.dto.AddProtocolTemplateDetailsDto;
import com.cairn.waypoint.dashboard.endpoints.steptemplate.AddStepTemplateEndpoint;
import com.cairn.waypoint.dashboard.endpoints.steptemplate.UpdateStepTemplateEndpoint;
import com.cairn.waypoint.dashboard.endpoints.steptemplate.dto.AddStepTemplateDetailsDto;
import com.cairn.waypoint.dashboard.endpoints.steptemplate.dto.SuccessfulStepTemplateCreationResponseDto;
import com.cairn.waypoint.dashboard.endpoints.steptemplate.dto.UpdateStepTemplateDetailsDto;
import com.cairn.waypoint.dashboard.entity.HomeworkQuestion;
import com.cairn.waypoint.dashboard.entity.StepTemplate;
import com.cairn.waypoint.dashboard.entity.StepTemplateCategory;
import com.cairn.waypoint.dashboard.entity.enumeration.QuestionTypeEnum;
import com.cairn.waypoint.dashboard.service.data.AccountDataService;
import com.cairn.waypoint.dashboard.service.data.ExpectedResponseDataService;
import com.cairn.waypoint.dashboard.service.data.HomeworkCategoryDataService;
import com.cairn.waypoint.dashboard.service.data.HomeworkQuestionDataService;
import com.cairn.waypoint.dashboard.service.data.ProtocolDataService;
import com.cairn.waypoint.dashboard.service.data.ProtocolTemplateDataService;
import com.cairn.waypoint.dashboard.service.data.StepTemplateCategoryDataService;
import com.cairn.waypoint.dashboard.service.data.StepTemplateDataService;
import com.cairn.waypoint.dashboard.service.helper.QuestionMapper;
import com.cairn.waypoint.dashboard.utility.fileupload.S3FileUpload;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
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
  //private final AddStepTemplateEndpoint addStepTemplateEndpoint; These will be added back in eventually
  //private final AddProtocolTemplateEndpoint addProtocolTemplateEndpoint; I just don't want to deal with them right now
  private final HomeworkQuestionDataService homeworkQuestionDataService;
  private final AddProtocolEndpoint addProtocolEndpoint;
  private final ProtocolTemplateDataService protocolTemplateDataService;
  //private final StepTemplateCategoryDataService stepTemplateCategoryDataService;
  private final AccountDataService accountDataService;
  private final S3FileUpload s3FileUpload;
  private final ExpectedResponseDataService expectedResponseDataService;
  private final HomeworkCategoryDataService homeworkCategoryDataService;
  private final ProtocolDataService protocolDataService;
  private final QuestionMapper questionMapper;
  //private final StepTemplateDataService stepTemplateDataService;
  //private final UpdateStepTemplateEndpoint updateStepTemplateEndpoint;
  @Value("${waypoint.dashboard.s3.import-data-key-prefix}")
  private String baseKey;

  public ImportDataEndpoint(AddStepTemplateEndpoint addStepTemplateEndpoint,
      AddProtocolTemplateEndpoint addProtocolTemplateEndpoint,
      HomeworkQuestionDataService homeworkQuestionDataService,
      AddProtocolEndpoint addProtocolEndpoint,
      ProtocolTemplateDataService protocolTemplateDataService,
      StepTemplateCategoryDataService stepTemplateCategoryDataService,
      AccountDataService accountDataService,
      S3FileUpload s3FileUpload,
      ExpectedResponseDataService expectedResponseDataService,
      StepTemplateDataService stepTemplateDataService,
      UpdateStepTemplateEndpoint updateStepTemplateEndpoint,
      QuestionMapper questionMapper,
      HomeworkCategoryDataService homeworkCategoryDataService,
      ProtocolDataService protocolDataService)
  {
    //this.addStepTemplateEndpoint = addStepTemplateEndpoint;
    this.addProtocolEndpoint = addProtocolEndpoint;
    //this.addProtocolTemplateEndpoint = addProtocolTemplateEndpoint;
    this.protocolTemplateDataService = protocolTemplateDataService;
    //this.stepTemplateCategoryDataService = stepTemplateCategoryDataService;
    this.accountDataService = accountDataService;
    this.homeworkQuestionDataService = homeworkQuestionDataService;
    this.expectedResponseDataService = expectedResponseDataService;
    this.s3FileUpload = s3FileUpload;
    this.homeworkCategoryDataService = homeworkCategoryDataService;
    this.questionMapper = questionMapper;
    this.protocolDataService = protocolDataService;
  }

  @Transactional
  @PostMapping(PATH)
  @PreAuthorize("hasAuthority('SCOPE_admin.full')")
  @Operation(
      summary = "Allows a user to import data into the System, priming the backend for use",
      security = @SecurityRequirement(name = "oAuth2JwtBearer"),
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "Upload an Excel file (.xls or .xlsx) to import data",
          required = true,
          content = @Content(
              mediaType = "multipart/form-data",
              schema = @Schema(type = "string", format = "binary", description = "Excel file to be uploaded")
          )
      )
  )
  public ResponseEntity<String> importData(
      @Parameter(description = "Excel file to be uploaded", required = true)
      @RequestParam("file") MultipartFile file,
      Principal principal) throws IOException {
    log.info("User [{}] is importing data", principal.getName());

    Workbook waypointsDataImportSpreadsheet;
    String lowerCaseFileName = Objects.requireNonNull(file.getOriginalFilename()).toLowerCase();
    if (lowerCaseFileName.endsWith(".xlsx")) {
      waypointsDataImportSpreadsheet = new XSSFWorkbook(file.getInputStream());
    } else {
      waypointsDataImportSpreadsheet = new HSSFWorkbook(file.getInputStream());
    }
     //importEmployeeAccounts(waypointsDataImportSpreadsheet.getSheet("Employee Accounts"));
     // these will come back just
     //just not working on them right now so I'm commenting them out so I can focus on HW
     //ClientCreationResponseListDto response1 = importClients(
     //    waypointsDataImportSpreadsheet.getSheet("Client Accounts"));
     // See Above.
     //importProtocolTemplateDetails(waypointsDataImportSpreadsheet.getSheet("Protocols"),principal);
     importHomework(waypointsDataImportSpreadsheet.getSheet("Homework"), principal);
     //importProtocolAssignments(waypointsDataImportSpreadsheet.getSheet("Protocol Assignments"),
     //    response1,
     //    principal);

    //this.s3FileUpload.uploadFile(file, principal.getName(), baseKey);

    return ResponseEntity.ok("Successfully uploaded the file");
  }

  private void importHomework(Sheet homeworkSheet, Principal principal) {
    List<HomeworkQuestion> homeworkQuestions = new ArrayList<>();

    for (Row row : homeworkSheet) {
      try{
      importHomeworkQuestionDto homeworkQuestionDto = importHomeworkQuestionDto.builder()
          .category(row.getCell(1)!=null?homeworkCategoryDataService.getHomeworkCategoryByName(row.getCell(1).getStringCellValue()):null)
          //.description(row.getCell(1).getStringCellValue())
          .multipleResponses(row.getCell(3)!=null?row.getCell(3).getBooleanCellValue():null)
          .abbreviation(row.getCell(4)!=null?row.getCell(4).getStringCellValue():null)
          .question(row.getCell(5)!=null?row.getCell(5).getStringCellValue():null)
          .questionType(row.getCell(6) != null && !"AUTO".equalsIgnoreCase(row.getCell(6).getStringCellValue())
              ? QuestionTypeEnum.valueOf(row.getCell(6).getStringCellValue().toUpperCase().replace("-", "_"))
              : QuestionTypeEnum.valueOf("STRING"))

          .isRequired(row.getCell(7)!=null?row.getCell(7).getBooleanCellValue():false)
          .expectedResponse(row.getCell(8)!=null?row.getCell(8).getStringCellValue():null)
          //.triggeringResponse(expectedResponseDataService.getExpectedResponseByResponse(row.getCell(8).getStringCellValue()).orElse(null))
          .triggersProtocolCreation(false)
          .active(true)
          //.protocolTemplate(protocolTemplateDataService.findProtocolTemplateByName(row.getCell(10).getStringCellValue()).orElse(null))
          .build();



      HomeworkQuestion homeworkQuestion = questionMapper.INSTANCE.toEntity(homeworkQuestionDto);
      homeworkQuestions.add(homeworkQuestion);

      }catch(Exception e) {
        System.out.println(e.getMessage());
      }
    }

    // Batch save all HomeworkQuestions
    homeworkQuestionDataService.batchSaveHomeworkQuestions(homeworkQuestions);
  }
/*
  private void importProtocolAssignments(Sheet protocolAssignmentsSheet,
      ClientCreationResponseListDto createdClients, Principal principal) {
    for (Row row : protocolAssignmentsSheet) {
      if (row.getRowNum() == 0 || getCellValue(row.getCell(0)) == null) {
        continue;
      }

      addProtocolEndpoint.assignProtocol(AddProtocolDetailsDto.builder()
          .assignedHouseholdId(createdClients.getAccountCreationResponses().stream()
              .filter(Predicate.not(ClientCreationResponseDto::getError))
              .filter(clientCreationResponseDto -> clientCreationResponseDto.getUsername()
                  .equals(getCellValue(row.getCell(1))))
              .findFirst()
              .orElseThrow()
              .getHouseholdId())
          .protocolTemplateId(this.protocolTemplateDataService.findProtocolTemplateByName(
              getCellValue(row.getCell(0))).orElseThrow().getId())
          .goal(getCellValue(row.getCell(2)))
          .comment(getCellValue(row.getCell(2)))
          .build(), principal);
    }
  }
*/
/*
  @SuppressWarnings({"OptionalGetWithoutIsPresent", "StatementWithEmptyBody"})
  private void importProtocolTemplateDetails(Sheet protocolsSheet, Principal principal) {
    Map<String, AddProtocolTemplateDetailsDto> protocolTemplates = new HashMap<>();
    for (Row row : protocolsSheet) {
      if (row.getRowNum() == 0 || getCellValue(row.getCell(2)) == null) {
        continue;
      }

      Optional<StepTemplateCategory> stepCategoryOptional;
      if ((stepCategoryOptional = this.stepTemplateCategoryDataService.findByName(
          getCellValue(row.getCell(5)))).isEmpty()) {
        stepCategoryOptional = Optional.of(
            this.stepTemplateCategoryDataService.saveTemplateCategory(StepTemplateCategory.builder()
                .modifiedBy(principal.getName())
                .name(getCellValue(row.getCell(5)))
                .description(getCellValue(row.getCell(5)))
                .build()));
      }

      AddStepTemplateDetailsDto addStepTemplateDetailsRequest = AddStepTemplateDetailsDto.builder()
          .name(getCellValue(row.getCell(2)))
          .description(getCellValue(row.getCell(3)))
          .stepTemplateCategoryId(stepCategoryOptional.get().getId())
          .build();

      ResponseEntity<?> creationResponse = addStepTemplateEndpoint.addStepTemplate(
          addStepTemplateDetailsRequest, principal);

      if (creationResponse.getStatusCode().is2xxSuccessful()) {
        associateProtocolTemplateWithProtocolStepTemplate(protocolTemplates, row,
            ((SuccessfulStepTemplateCreationResponseDto) Objects.requireNonNull(
                creationResponse.getBody())).getStepTemplateId());
      } else if (creationResponse.getStatusCode().isSameCodeAs(
          HttpStatusCode.valueOf(409))) {
          Optional<StepTemplate> stepTemplateOptional = stepTemplateDataService.findStepTemplateByName(
            getCellValue(row.getCell(2)));
        associateProtocolTemplateWithProtocolStepTemplate(protocolTemplates, row,
            stepTemplateOptional.get().getId());
      } else {
        throw new RuntimeException("Failed to add step template details");
      }
    }

    for (AddProtocolTemplateDetailsDto addProtocolTemplateDetailsDto : protocolTemplates.values()) {
      ResponseEntity<?> creationResponse = addProtocolTemplateEndpoint.addProtocolTemplate(
          addProtocolTemplateDetailsDto, principal);

      if (creationResponse.getStatusCode().isSameCodeAs(
          HttpStatusCode.valueOf(409))) {
        //Do nothing
      } else if (!creationResponse.getStatusCode().is2xxSuccessful()) {
        throw new RuntimeException("Failed to add protocol template details");
      }
    }
  }

  @SuppressWarnings("ArraysAsListWithZeroOrOneArgument")
  private void associateProtocolTemplateWithProtocolStepTemplate(
      Map<String, AddProtocolTemplateDetailsDto> protocolTemplates, Row row, Long stepTemplateId) {
    if (protocolTemplates.containsKey(getCellValue(row.getCell(0)))) {
      protocolTemplates.get(getCellValue(row.getCell(0))).getAssociatedStepTemplateIds()
          .add(stepTemplateId);
    } else {
      protocolTemplates.put(getCellValue(row.getCell(0)),
          AddProtocolTemplateDetailsDto.builder()
              .name(getCellValue(row.getCell(0)))
              .description(getCellValue(row.getCell(1)))
              .associatedStepTemplateIds(
                  new LinkedHashSet<>(Arrays.asList(stepTemplateId)))
              .build());
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
*/
  /*
  private ClientCreationResponseListDto importClients(Sheet cllientAccountsSheet) {
    List<BatchAddAccountDetailsDto> accountsToAdd = new ArrayList<>();
    for (int i = 1; i < cllientAccountsSheet.getLastRowNum(); i++) {
      if (cllientAccountsSheet.getRow(i) != null &&
          !Objects.requireNonNull(getCellValue(cllientAccountsSheet.getRow(i).getCell(11)))
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
*/

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