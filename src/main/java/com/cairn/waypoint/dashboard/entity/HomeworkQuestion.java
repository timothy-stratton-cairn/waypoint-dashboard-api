package com.cairn.waypoint.dashboard.entity;

import com.cairn.waypoint.dashboard.entity.converter.QuestionTypeConverter;
import com.cairn.waypoint.dashboard.entity.enumeration.QuestionTypeEnum;
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
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLRestriction;

@Data
@Entity
@SuperBuilder
@EqualsAndHashCode(callSuper = true, exclude = "expectedHomeworkResponses")
@NoArgsConstructor
@AllArgsConstructor
@SQLRestriction("active=1")
@Table(name = "homework_question")
public class HomeworkQuestion extends BaseEntity {

  private String questionAbbreviation;
  private String question;

  @Column(name = "homework_question_type_id")
  @Convert(converter = QuestionTypeConverter.class)
  private QuestionTypeEnum questionType;

  private Boolean required;

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
}
