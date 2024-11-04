package com.cairn.waypoint.dashboard.mapper;

import com.cairn.waypoint.dashboard.endpoints.protocol.dto.AccountProtocolDto;
import com.cairn.waypoint.dashboard.endpoints.protocol.dto.AssociatedStepsListDto;
//import com.cairn.waypoint.dashboard.endpoints.protocol.dto.LinkedHomeworksDto;
import com.cairn.waypoint.dashboard.endpoints.protocol.dto.ProtocolCommentDto;
import com.cairn.waypoint.dashboard.endpoints.protocol.dto.ProtocolCommentListDto;
import com.cairn.waypoint.dashboard.endpoints.protocol.dto.ProtocolDetailsDto;
import com.cairn.waypoint.dashboard.endpoints.protocol.dto.ProtocolDto;
import com.cairn.waypoint.dashboard.endpoints.protocol.dto.ProtocolStepDto;
import com.cairn.waypoint.dashboard.endpoints.protocol.dto.ProtocolStepNoteDto;
import com.cairn.waypoint.dashboard.endpoints.protocol.dto.ProtocolStepNoteListDto;
import com.cairn.waypoint.dashboard.endpoints.protocol.dto.RecurrenceDetailsDto;
//import com.cairn.waypoint.dashboard.entity.Homework;
import com.cairn.waypoint.dashboard.entity.Protocol;
import com.cairn.waypoint.dashboard.entity.ProtocolCommentary;
import com.cairn.waypoint.dashboard.entity.ProtocolStep;

import com.cairn.waypoint.dashboard.entity.ProtocolTemplate;
import com.cairn.waypoint.dashboard.entity.StepTemplate;
import com.cairn.waypoint.dashboard.service.helper.ProtocolCalculationHelperService;
import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ProtocolMapper {

  ProtocolMapper INSTANCE = Mappers.getMapper(ProtocolMapper.class);

  ProtocolDto toDto(Protocol protocol);

  AccountProtocolDto toAccountProtocolDto(Protocol protocol);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "status", ignore = true)
  Protocol protocolTemplateToProtocol(ProtocolTemplate protocolTemplate);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "status", ignore = true)
  ProtocolStep protocolStepTemplateToProtocolStep(StepTemplate stepTemplate);

  @Mapping(target = "createdAt", source = "created")
  @Mapping(target = "dueBy", source = "dueDate")
  @Mapping(target = "completedOn", source = "completionDate")
  @Mapping(target = "needsAttention", source = "markedForAttention")
  @Mapping(target = "completionPercentage", source = "protocol", qualifiedByName = "completionPercentage")
  @Mapping(target = "protocolComments", source = "comments", qualifiedByName = "protocolCommentary")
  @Mapping(target = "nextInstance", source = "protocol", qualifiedByName = "recurrenceType")
  @Mapping(target = "associatedSteps", source = "protocolSteps", qualifiedByName = "protocolSteps")
  ProtocolDetailsDto toDetailsDto(Protocol protocol);

  @Named("completionPercentage")
  default BigDecimal calculateCompletionPercentage(Protocol protocol) {
    return ProtocolCalculationHelperService.getProtocolCompletionPercentage(protocol);
  }

  @Named("protocolCommentary")
  default ProtocolCommentListDto protocolCommentListToDtoList(
      Set<ProtocolCommentary> protocolComments) {
    return ProtocolCommentListDto.builder()
        .comments(protocolComments == null || protocolComments.isEmpty() ?
            null :
            protocolComments.stream()
                .map(protocolComment -> ProtocolCommentDto.builder()
                    .commentId(protocolComment.getId())
                    .takenAt(protocolComment.getCreated())
                    .takenBy(protocolComment.getOriginalCommenter())
                    .comment(protocolComment.getComment())
                    .commentType(protocolComment.getCommentType().name())
                    .build())
                .toList())
        .build();
  }

  @Named("recurrenceType")
  default RecurrenceDetailsDto protocolRecurrence(Protocol protocol) {
    return RecurrenceDetailsDto.builder()
        .recurrenceType(protocol.getRecurrenceType().name())
        .triggeringStatus(protocol.getTriggeringStatus() == null ?
            null :
            protocol.getTriggeringStatus().name())
        .willReoccurInYears(protocol.getReoccurInYears())
        .willReoccurInMonths(protocol.getReoccurInMonths())
        .willReoccurInDays(protocol.getReoccurInDays())
        .build();
  }

  @Named("protocolSteps")
  default AssociatedStepsListDto protocolSteps(Set<ProtocolStep> protocolSteps) {
    return AssociatedStepsListDto.builder()
        .steps(protocolSteps.stream()
            .map(protocolStep -> ProtocolStepDto.builder()
                .id(protocolStep.getId())
                .name(protocolStep.getName())
                .description(protocolStep.getDescription())
                .stepNotes(ProtocolStepNoteListDto.builder()
                    .notes(protocolStep.getNotes() == null || protocolStep.getNotes()
                        .isEmpty() ?
                        null : protocolStep.getNotes().stream()
                        .map(protocolStepNote -> ProtocolStepNoteDto.builder()
                            .noteId(protocolStepNote.getId())
                            .takenAt(protocolStepNote.getCreated())
                            .takenBy(protocolStepNote.getOriginalCommenter())
                            .note(protocolStepNote.getNote())
                            .build())
                        .toList())
                    .build())
                //TODO here we can add the attachments but it's not straightforward as the baseURL comes from properties
                .status(protocolStep.getStatus().getInstance().getName())
                /*.linkedHomeworks(protocolStep.getLinkedHomework() != null ?
                    LinkedHomeworksDto.builder()
                        .homeworkIds(protocolStep.getLinkedHomework().stream().map(
                                ProtocolStepLinkedHomework::getHomework)
                            .map(Homework::getId)
                            .collect(Collectors.toSet()))
                        .build() : null)*/
                .category(
                    protocolStep.getCategory().getStepTemplateCategory().getName())
                .stepTemplateId(protocolStep.getTemplate().getId())
                .build())
            .collect(Collectors.toList()))
        .build();
  }
}
