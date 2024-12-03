package com.cairn.waypoint.dashboard.mapper;
import com.cairn.waypoint.dashboard.endpoints.protocol.dto.AccountProtocolDto;
import com.cairn.waypoint.dashboard.endpoints.protocol.dto.AssociatedStepsListDto;
import com.cairn.waypoint.dashboard.endpoints.protocol.dto.ProtocolCommentDto;
import com.cairn.waypoint.dashboard.endpoints.protocol.dto.ProtocolCommentListDto;
import com.cairn.waypoint.dashboard.endpoints.protocol.dto.ProtocolStepDto;
import com.cairn.waypoint.dashboard.entity.Protocol;
import com.cairn.waypoint.dashboard.entity.ProtocolCommentary;
import com.cairn.waypoint.dashboard.entity.ProtocolStep;
import com.cairn.waypoint.dashboard.service.helper.ProtocolCalculationHelperService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;


@Mapper(componentModel = "spring")
public interface AccountProtocolMapper {

  @Mapping(target = "id", source = "id")
  @Mapping(target = "name", source = "name")
  @Mapping(target = "description", source = "description")
  @Mapping(target = "goal", source = "goal")
  @Mapping(target = "goalProgress", source = "goalProgress")
  @Mapping(target = "createdAt", source = "created")
  @Mapping(target = "dueBy", source = "dueDate")
  @Mapping(target = "completedOn", source = "completionDate")
  @Mapping(target = "needsAttention", source = "markedForAttention")
  @Mapping(target = "lastStatusUpdateTimestamp", source = "lastStatusUpdateTimestamp")
  @Mapping(target = "assignedUserId", source = "userId")
  @Mapping(target = "completionPercentage", expression = "java(calculateCompletionPercentage(protocol))")
  @Mapping(target = "protocolComments", source = "comments", qualifiedByName = "mapProtocolComments")
  @Mapping(target = "associatedSteps", source = "protocolSteps", qualifiedByName = "mapProtocolSteps")
  AccountProtocolDto toAccountProtocolDto(Protocol protocol);

  @Named("mapProtocolComments")
  default ProtocolCommentListDto mapProtocolComments(Set<ProtocolCommentary> comments) {
    if (comments == null || comments.isEmpty()) {
      return ProtocolCommentListDto.builder().comments(List.of()).build();
    }
    return ProtocolCommentListDto.builder()
        .comments(comments.stream()
            .map(comment -> ProtocolCommentDto.builder()
                .commentId(comment.getId())
                .takenAt(comment.getCreated())
                .takenBy(comment.getOriginalCommenter())
                .comment(comment.getComment())
                .commentType(comment.getCommentType().name())
                .build())
            .toList())
        .build();
  }

  @Named("mapProtocolSteps")
  default AssociatedStepsListDto mapProtocolSteps(Set<ProtocolStep> steps) {
    if (steps == null || steps.isEmpty()) {
      return AssociatedStepsListDto.builder().steps(List.of()).build();
    }
    return AssociatedStepsListDto.builder()
        .steps(steps.stream()
            .map(step -> ProtocolStepDto.builder()
                .id(step.getId())
                .name(step.getName())
                .description(step.getDescription())
                .status(step.getStatus().getInstance().getName())
                .category(step.getCategory().getStepTemplateCategory().getName())
                .stepTemplateId(step.getTemplate().getId())
                .build())
            .toList())
        .build();
  }

  default BigDecimal calculateCompletionPercentage(Protocol protocol) {
    return ProtocolCalculationHelperService.getProtocolCompletionPercentage(protocol);
  }
}
