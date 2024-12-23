package com.cairn.waypoint.dashboard.entity;

import com.cairn.waypoint.dashboard.entity.converter.QuestionTypeConverter;
import com.cairn.waypoint.dashboard.entity.converter.TemplateStatusConverter;
import com.cairn.waypoint.dashboard.entity.enumeration.QuestionTypeEnum;
import com.cairn.waypoint.dashboard.entity.enumeration.TemplateStatusEnum;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@Entity
@SuperBuilder
@EqualsAndHashCode(callSuper = true, exclude = "expectedHomeworkResponses")
@NoArgsConstructor
@AllArgsConstructor
//@SQLRestriction("active=1") I don't want to do this, but it's causing Entity not found issues. I'll come up with something better.
@Table(name = "homework_question")
public class HomeworkQuestion extends BaseEntity {

  private String questionAbbreviation;
  private String question;
  private String label;

  @Column(name = "homework_question_type_id")
  @Convert(converter = QuestionTypeConverter.class)
  private QuestionTypeEnum questionType;

  private Boolean required;
  private Boolean active;

  @Builder.Default
  @Column(name = "status_id")
  @Convert(converter = TemplateStatusConverter.class)
  private TemplateStatusEnum status = TemplateStatusEnum.INACTIVE;

  @OrderBy("ordinalIndex ASC")
  @JoinColumn(name = "parent_homework_question_id")
  @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
  private Set<ExpectedResponse> expectedHomeworkResponses;

  private Boolean triggersProtocolCreation;

  @JoinColumn(name = "triggering_response_id", nullable = false)
  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
  private ExpectedResponse triggeringResponse;

  @JoinColumn(name = "triggered_protocol_template_id", nullable = false)
  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
  private ProtocolTemplate triggeredProtocol;

  @JoinColumn(name = "category_id", nullable = false)
  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
  private HomeworkCategory category;
}
