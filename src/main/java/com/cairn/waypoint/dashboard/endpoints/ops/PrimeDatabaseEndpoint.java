package com.cairn.waypoint.dashboard.endpoints.ops;

import com.cairn.waypoint.dashboard.entity.ProtocolTemplate;
import com.cairn.waypoint.dashboard.entity.ProtocolTemplateLinkedStepTemplate;
import com.cairn.waypoint.dashboard.entity.StepTemplate;
import com.cairn.waypoint.dashboard.entity.TemplateCategory;
import com.cairn.waypoint.dashboard.service.ProtocolTemplateService;
import com.cairn.waypoint.dashboard.service.StepTemplateService;
import com.cairn.waypoint.dashboard.service.TemplateCategoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@SuppressWarnings("OptionalGetWithoutIsPresent")
@Slf4j
@RestController
@Tag(name = "Ops")
public class PrimeDatabaseEndpoint {

  public static final String PATH = "/ops/prime-database";

  private final StepTemplateService stepTemplateService;
  private final ProtocolTemplateService protocolTemplateService;
  private final TemplateCategory gatherDataCategory;
  private final TemplateCategory runAnalysisCategory;
  private final TemplateCategory craftRecommendationsCategory;
  private final TemplateCategory shareEducationCategory;
  private List<StepTemplate> stepTemplates;
  private List<ProtocolTemplate> protocolTemplates;

  public PrimeDatabaseEndpoint(StepTemplateService stepTemplateService,
      ProtocolTemplateService protocolTemplateService,
      TemplateCategoryService templateCategoryService) {
    this.stepTemplateService = stepTemplateService;
    this.protocolTemplateService = protocolTemplateService;

    gatherDataCategory = templateCategoryService.findByName("Gather Data").get();
    runAnalysisCategory = templateCategoryService.findByName("Run Analysis").get();
    craftRecommendationsCategory = templateCategoryService.findByName("Craft Recommendations")
        .get();
    shareEducationCategory = templateCategoryService.findByName("Share Education").get();
  }

  @Transactional
  @PostMapping(PATH)
  @PreAuthorize("hasAuthority('SCOPE_admin.full')")
  public ResponseEntity<?> primeDatabase() {
    try {
      log.info("Attempting to Prime the Database with some standard Protocol Templates");

      stepTemplates = getStepTemplates();
      protocolTemplates = getProtocolTemplates();

      this.stepTemplates = stepTemplateService.saveStepTemplateList(stepTemplates);
      this.protocolTemplates = protocolTemplateService.saveProtocolTemplateList(protocolTemplates);

      saveNewClientProtocolTemplate();
      saveJobChangeRetirementProtocolTemplate();
      saveInheritanceCapitalInfluxProtocolTemplate();
      savePrePurchaseHousePurchaseProtocolTemplate();
      savePostPurchaseHousePurchaseProtocolTemplate();

      log.info("Priming of Database complete");

      return ResponseEntity.ok().build();
    } catch (Exception e) {
      log.warn("An error occurred while priming the database", e);

      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  private void saveNewClientProtocolTemplate() {
    ProtocolTemplate newClientProtocolTemplate = this.protocolTemplates.stream()
        .filter(protocolTemplate -> protocolTemplate.getName().equals("New Client")).findFirst()
        .get();

    Set<ProtocolTemplateLinkedStepTemplate> protocolSteps = new LinkedHashSet<>();
    protocolSteps.add(ProtocolTemplateLinkedStepTemplate.builder()
        .protocolTemplate(newClientProtocolTemplate)
        .stepTemplate(this.stepTemplates.stream()
            .filter(stepTemplate -> stepTemplate.getName().equals("Send New Client Homework"))
            .findFirst().get())
        .ordinalIndex(0)
        .build());
    protocolSteps.add(ProtocolTemplateLinkedStepTemplate.builder()
        .protocolTemplate(newClientProtocolTemplate)
        .stepTemplate(this.stepTemplates.stream().filter(stepTemplate -> stepTemplate.getName()
            .equals("Assign New Client all pertinent Protocols")).findFirst().get())
        .ordinalIndex(1)
        .build());
    protocolSteps.add(ProtocolTemplateLinkedStepTemplate.builder()
        .protocolTemplate(newClientProtocolTemplate)
        .stepTemplate(this.stepTemplates.stream().filter(
                stepTemplate -> stepTemplate.getName().equals("Create New Client Financial Plan"))
            .findFirst().get())
        .ordinalIndex(2)
        .build());

    newClientProtocolTemplate.setProtocolTemplateSteps(protocolSteps);

    this.protocolTemplateService.saveProtocolTemplate(newClientProtocolTemplate);
  }

  private void saveJobChangeRetirementProtocolTemplate() {
    ProtocolTemplate jobChangeRetirementProtocolTemplate = this.protocolTemplates.stream()
        .filter(protocolTemplate -> protocolTemplate.getName().equals("Job Change + Retirement"))
        .findFirst().get();

    Set<ProtocolTemplateLinkedStepTemplate> protocolSteps = new LinkedHashSet<>();
    protocolSteps.add(ProtocolTemplateLinkedStepTemplate.builder()
        .protocolTemplate(jobChangeRetirementProtocolTemplate)
        .stepTemplate(this.stepTemplates.stream().filter(
                stepTemplate -> stepTemplate.getName().equals("Send Job Change/Retirement Homework"))
            .findFirst().get())
        .ordinalIndex(0)
        .build());
    protocolSteps.add(ProtocolTemplateLinkedStepTemplate.builder()
        .protocolTemplate(jobChangeRetirementProtocolTemplate)
        .stepTemplate(this.stepTemplates.stream().filter(stepTemplate -> stepTemplate.getName()
            .equals("Update Client Cash Flow/Retirement Plan in Right Capital")).findFirst().get())
        .ordinalIndex(1)
        .build());
    protocolSteps.add(ProtocolTemplateLinkedStepTemplate.builder()
        .protocolTemplate(jobChangeRetirementProtocolTemplate)
        .stepTemplate(this.stepTemplates.stream().filter(stepTemplate -> stepTemplate.getName()
            .equals("Analyze Life-/Disability-/Medical-Insurance options")).findFirst().get())
        .ordinalIndex(2)
        .build());
    protocolSteps.add(ProtocolTemplateLinkedStepTemplate.builder()
        .protocolTemplate(jobChangeRetirementProtocolTemplate)
        .stepTemplate(this.stepTemplates.stream().filter(stepTemplate -> stepTemplate.getName()
            .equals("Advise on or Enroll in 401(k)/HSA/FSA/DCFSA")).findFirst().get())
        .ordinalIndex(3)
        .build());
    protocolSteps.add(ProtocolTemplateLinkedStepTemplate.builder()
        .protocolTemplate(jobChangeRetirementProtocolTemplate)
        .stepTemplate(this.stepTemplates.stream().filter(stepTemplate -> stepTemplate.getName()
            .equals("Enroll New 401(k) Plan in Plan Confidence")).findFirst().get())
        .ordinalIndex(4)
        .build());
    protocolSteps.add(ProtocolTemplateLinkedStepTemplate.builder()
        .protocolTemplate(jobChangeRetirementProtocolTemplate)
        .stepTemplate(this.stepTemplates.stream().filter(
                stepTemplate -> stepTemplate.getName().equals("Update New 401(k) Plan Beneficiaries"))
            .findFirst().get())
        .ordinalIndex(5)
        .build());
    protocolSteps.add(ProtocolTemplateLinkedStepTemplate.builder()
        .protocolTemplate(jobChangeRetirementProtocolTemplate)
        .stepTemplate(this.stepTemplates.stream()
            .filter(stepTemplate -> stepTemplate.getName().equals("Review Client Protocols"))
            .findFirst().get())
        .ordinalIndex(6)
        .build());

    jobChangeRetirementProtocolTemplate.setProtocolTemplateSteps(protocolSteps);

    this.protocolTemplateService.saveProtocolTemplate(jobChangeRetirementProtocolTemplate);
  }

  private void saveInheritanceCapitalInfluxProtocolTemplate() {
    ProtocolTemplate newInheritanceCapitalInfluxProtocolTemplate = this.protocolTemplates.stream()
        .filter(
            protocolTemplate -> protocolTemplate.getName().equals("Inheritance + Capital Influx"))
        .findFirst().get();

    Set<ProtocolTemplateLinkedStepTemplate> protocolSteps = new LinkedHashSet<>();
    protocolSteps.add(ProtocolTemplateLinkedStepTemplate.builder()
        .protocolTemplate(newInheritanceCapitalInfluxProtocolTemplate)
        .stepTemplate(this.stepTemplates.stream().filter(stepTemplate -> stepTemplate.getName()
            .equals("Send Inheritance or Capital Influx Homework")).findFirst().get())
        .ordinalIndex(0)
        .build());
    protocolSteps.add(ProtocolTemplateLinkedStepTemplate.builder()
        .protocolTemplate(newInheritanceCapitalInfluxProtocolTemplate)
        .stepTemplate(this.stepTemplates.stream()
            .filter(stepTemplate -> stepTemplate.getName().equals("Recreate Retirement Analysis"))
            .findFirst().get())
        .ordinalIndex(1)
        .build());
    protocolSteps.add(ProtocolTemplateLinkedStepTemplate.builder()
        .protocolTemplate(newInheritanceCapitalInfluxProtocolTemplate)
        .stepTemplate(this.stepTemplates.stream().filter(
                stepTemplate -> stepTemplate.getName().equals("Update Cash Flow/Savings Rate Protocol"))
            .findFirst().get())
        .ordinalIndex(2)
        .build());
    protocolSteps.add(ProtocolTemplateLinkedStepTemplate.builder()
        .protocolTemplate(newInheritanceCapitalInfluxProtocolTemplate)
        .stepTemplate(this.stepTemplates.stream().filter(stepTemplate -> stepTemplate.getName()
                .equals("Review Client Protocols after Inheritance or Capital Influx")).findFirst()
            .get())
        .ordinalIndex(3)
        .build());
    protocolSteps.add(ProtocolTemplateLinkedStepTemplate.builder()
        .protocolTemplate(newInheritanceCapitalInfluxProtocolTemplate)
        .stepTemplate(this.stepTemplates.stream().filter(stepTemplate -> stepTemplate.getName()
                .equals("Propose Financial Plan to Client after Inheritance or Capital Influx"))
            .findFirst().get())
        .ordinalIndex(4)
        .build());

    newInheritanceCapitalInfluxProtocolTemplate.setProtocolTemplateSteps(protocolSteps);

    this.protocolTemplateService.saveProtocolTemplate(newInheritanceCapitalInfluxProtocolTemplate);
  }

  private void savePrePurchaseHousePurchaseProtocolTemplate() {
    ProtocolTemplate newPrePurchaseHousePurchaseProtocolTemplate = this.protocolTemplates.stream()
        .filter(
            protocolTemplate -> protocolTemplate.getName().equals("Pre-Purchase House Purchase"))
        .findFirst().get();

    Set<ProtocolTemplateLinkedStepTemplate> protocolSteps = new LinkedHashSet<>();
    protocolSteps.add(ProtocolTemplateLinkedStepTemplate.builder()
        .protocolTemplate(newPrePurchaseHousePurchaseProtocolTemplate)
        .stepTemplate(this.stepTemplates.stream().filter(stepTemplate -> stepTemplate.getName()
                .equals("Deliver Savings Plan for down payments and ancillary expenses")).findFirst()
            .get())
        .ordinalIndex(0)
        .build());
    protocolSteps.add(ProtocolTemplateLinkedStepTemplate.builder()
        .protocolTemplate(newPrePurchaseHousePurchaseProtocolTemplate)
        .stepTemplate(this.stepTemplates.stream().filter(stepTemplate -> stepTemplate.getName()
            .equals("Deliver Standard Education on the Home Purchase Process")).findFirst().get())
        .ordinalIndex(1)
        .build());
    protocolSteps.add(ProtocolTemplateLinkedStepTemplate.builder()
        .protocolTemplate(newPrePurchaseHousePurchaseProtocolTemplate)
        .stepTemplate(this.stepTemplates.stream().filter(stepTemplate -> stepTemplate.getName()
            .equals("Advise on How Much House a Client can Afford")).findFirst().get())
        .ordinalIndex(2)
        .build());
    protocolSteps.add(ProtocolTemplateLinkedStepTemplate.builder()
        .protocolTemplate(newPrePurchaseHousePurchaseProtocolTemplate)
        .stepTemplate(this.stepTemplates.stream().filter(stepTemplate -> stepTemplate.getName()
            .equals("Send Pre-Purchase House Purchase Homework")).findFirst().get())
        .ordinalIndex(3)
        .build());
    protocolSteps.add(ProtocolTemplateLinkedStepTemplate.builder()
        .protocolTemplate(newPrePurchaseHousePurchaseProtocolTemplate)
        .stepTemplate(this.stepTemplates.stream()
            .filter(stepTemplate -> stepTemplate.getName().equals("Deliver the Debt Spreadsheet"))
            .findFirst().get())
        .ordinalIndex(4)
        .build());

    newPrePurchaseHousePurchaseProtocolTemplate.setProtocolTemplateSteps(protocolSteps);

    this.protocolTemplateService.saveProtocolTemplate(newPrePurchaseHousePurchaseProtocolTemplate);
  }

  private void savePostPurchaseHousePurchaseProtocolTemplate() {
    ProtocolTemplate newPostPurchaseHousePurchaseProtocolTemplate = this.protocolTemplates.stream()
        .filter(
            protocolTemplate -> protocolTemplate.getName().equals("Post-Purchase House Purchase"))
        .findFirst().get();

    Set<ProtocolTemplateLinkedStepTemplate> protocolSteps = new LinkedHashSet<>();
    protocolSteps.add(ProtocolTemplateLinkedStepTemplate.builder()
        .protocolTemplate(newPostPurchaseHousePurchaseProtocolTemplate)
        .stepTemplate(this.stepTemplates.stream().filter(stepTemplate -> stepTemplate.getName()
            .equals("Send Post-Purchase House Purchase Homework")).findFirst().get())
        .ordinalIndex(0)
        .build());
    protocolSteps.add(ProtocolTemplateLinkedStepTemplate.builder()
        .protocolTemplate(newPostPurchaseHousePurchaseProtocolTemplate)
        .stepTemplate(this.stepTemplates.stream()
            .filter(stepTemplate -> stepTemplate.getName().equals("Rebuild Client Balance Sheet"))
            .findFirst().get())
        .ordinalIndex(1)
        .build());
    protocolSteps.add(ProtocolTemplateLinkedStepTemplate.builder()
        .protocolTemplate(newPostPurchaseHousePurchaseProtocolTemplate)
        .stepTemplate(this.stepTemplates.stream().filter(stepTemplate -> stepTemplate.getName()
            .equals("Review Client Protocols after House Purchase")).findFirst().get())
        .ordinalIndex(2)
        .build());
    protocolSteps.add(ProtocolTemplateLinkedStepTemplate.builder()
        .protocolTemplate(newPostPurchaseHousePurchaseProtocolTemplate)
        .stepTemplate(this.stepTemplates.stream().filter(stepTemplate -> stepTemplate.getName()
            .equals("Review Homeowners' Insurance after Home Purchase")).findFirst().get())
        .ordinalIndex(3)
        .build());
    protocolSteps.add(ProtocolTemplateLinkedStepTemplate.builder()
        .protocolTemplate(newPostPurchaseHousePurchaseProtocolTemplate)
        .stepTemplate(this.stepTemplates.stream().filter(
                stepTemplate -> stepTemplate.getName().equals("Update Cash Flow/Savings Rate Protocol"))
            .findFirst().get())
        .ordinalIndex(4)
        .build());

    newPostPurchaseHousePurchaseProtocolTemplate.setProtocolTemplateSteps(protocolSteps);

    this.protocolTemplateService.saveProtocolTemplate(newPostPurchaseHousePurchaseProtocolTemplate);
  }

  private List<StepTemplate> getStepTemplates() {
    return List.of(
        StepTemplate.builder()
            .name("Send New Client Homework")
            .description("Send a New Client the requisite homework to onboard")
            .category(gatherDataCategory)
            .build(),
        StepTemplate.builder()
            .name("Assign New Client all pertinent Protocols")
            .description("Assign a New Client the Protocols pertinent to their financial situation")
            .category(runAnalysisCategory)
            .build(),
        StepTemplate.builder()
            .name("Create New Client Financial Plan")
            .description(
                "Create New Client Financial Plan including creating and tracking the client balance sheet")
            .category(runAnalysisCategory)
            .build(),
        StepTemplate.builder()
            .name("Send Job Change/Retirement Homework")
            .description("Send the client the requisite Homework after a Job Change or Retirement")
            .category(gatherDataCategory)
            .build(),
        StepTemplate.builder()
            .name("Update Client Cash Flow/Retirement Plan in Right Capital")
            .description("Update the Client's Cash Flow and Retirement Plan within Right Capital")
            .category(craftRecommendationsCategory)
            .build(),
        StepTemplate.builder()
            .name("Analyze Life-/Disability-/Medical-Insurance options")
            .description(
                "Analyze the level of coverage for a Client's Life, Disability, Medical Insurances")
            .category(runAnalysisCategory)
            .build(),
        StepTemplate.builder()
            .name("Advise on or Enroll in 401(k)/HSA/FSA/DCFSA")
            .description(
                "Advise a client on their option with regard to 401(k)/HSA/FSA/DCFSA and enroll Client if decided upon")
            .category(shareEducationCategory)
            .build(),
        StepTemplate.builder()
            .name("Enroll New 401(k) Plan in Plan Confidence")
            .description("Enroll a Client's new 401(k) Plan in Plan Confidence")
            .category(craftRecommendationsCategory)
            .build(),
        StepTemplate.builder()
            .name("Update New 401(k) Plan Beneficiaries")
            .description("Update a Client's new 401(k) Plan Beneficiaries")
            .category(runAnalysisCategory)
            .build(),
        StepTemplate.builder()
            .name("Review Client Protocols")
            .description(
                "Variable Income? Equity-base Compensation? Rollover old 401(k)? Student Loans? Accredited Investor? Contract Income (1099)? Group Benefits? Risk Management?")
            .category(runAnalysisCategory)
            .build(),
        StepTemplate.builder()
            .name("Send Inheritance or Capital Influx Homework")
            .description("Send a Client the Inheritance or Capital Influx Homework")
            .category(shareEducationCategory)
            .build(),
        StepTemplate.builder()
            .name("Recreate Retirement Analysis")
            .description("Reanalyze and Recreate a Client Retirement Plan")
            .category(runAnalysisCategory)
            .build(),
        StepTemplate.builder()
            .name("Update Cash Flow/Savings Rate Protocol")
            .description(
                "Update Cash Flow and Savings Rate Protocol after an Inheritance or Capital Influx")
            .category(runAnalysisCategory)
            .build(),
        StepTemplate.builder()
            .name("Review Client Protocols after Inheritance or Capital Influx")
            .description(
                "Debt Protocol? Accredited Investor? Investment Proposal? Death Protocol? Tax Protocol?")
            .category(craftRecommendationsCategory)
            .build(),
        StepTemplate.builder()
            .name("Propose Financial Plan to Client after Inheritance or Capital Influx")
            .description(
                "Propose a Financial Plan to a Client after an Inheritance or Capital Influx")
            .category(shareEducationCategory)
            .build(),
        StepTemplate.builder()
            .name("Send Pre-Purchase House Purchase Homework")
            .description(
                "Collect Information around the Client's finances prior to purchasing a House")
            .category(gatherDataCategory)
            .build(),
        StepTemplate.builder()
            .name("Deliver the Debt Spreadsheet")
            .description(
                "Deliver the Debt Spreadsheet to the Client, comparing down payments and lengths of the mortgage")
            .category(shareEducationCategory)
            .build(),
        StepTemplate.builder()
            .name("Deliver Savings Plan for down payments and ancillary expenses")
            .description(
                "Deliver the Savings Plan for down payments and ancillary expenses to the Client")
            .category(shareEducationCategory)
            .build(),
        StepTemplate.builder()
            .name("Deliver Standard Education on the Home Purchase Process")
            .description(
                "Deliver the Standard Education on the Home Purchase Process to the Client")
            .category(shareEducationCategory)
            .build(),
        StepTemplate.builder()
            .name("Advise on How Much House a Client can Afford")
            .description("Advise the Client on Home Much Home the Client can Afford")
            .category(shareEducationCategory)
            .build(),
        StepTemplate.builder()
            .name("Send Post-Purchase House Purchase Homework")
            .description("Send a Client the Post-Purchase House Purchase Homework")
            .category(gatherDataCategory)
            .build(),
        StepTemplate.builder()
            .name("Rebuild Client Balance Sheet")
            .description("Rebuild a Client's Balance Sheet after Purchasing a Home")
            .category(runAnalysisCategory)
            .build(),
        StepTemplate.builder()
            .name("Review Client Protocols after House Purchase")
            .description("Reassess Education Plan? Reassess Estate Plan?")
            .category(craftRecommendationsCategory)
            .build(),
        StepTemplate.builder()
            .name("Review Homeowners' Insurance after Home Purchase")
            .description("Review a Client's Homeowners' Insurance after Home Purchase")
            .category(craftRecommendationsCategory)
            .build()
    );
  }

  private List<ProtocolTemplate> getProtocolTemplates() {
    return List.of(
        ProtocolTemplate.builder()
            .name("New Client")
            .description("Protocol to be Assigned to a New Client")
            .build(),
        ProtocolTemplate.builder()
            .name("Job Change + Retirement")
            .description("Protocol to be Assigned to a Client who has changed Jobs or Retired")
            .build(),
        ProtocolTemplate.builder()
            .name("Inheritance + Capital Influx")
            .description(
                "Protocol to be Assigned to a Client after an Inheritance or Capital Influx")
            .build(),
        ProtocolTemplate.builder()
            .name("Pre-Purchase House Purchase")
            .description("Protocol to be Assigned to Client who are about to Purchase a Home")
            .build(),
        ProtocolTemplate.builder()
            .name("Post-Purchase House Purchase")
            .description("Protocol to be Assigned to Client who have just Purchased a Home")
            .build()
    );
  }
}
