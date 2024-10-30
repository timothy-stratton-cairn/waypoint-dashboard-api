package com.cairn.waypoint.dashboard.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLRestriction;

@Data
@Entity
@SuperBuilder
@EqualsAndHashCode(callSuper = true, exclude = {"protocolTemplate", "question"})
@NoArgsConstructor
@AllArgsConstructor
@SQLRestriction("active=1")
@Table(name = "homework_question_linked_protocol_template")
public class HomeworkQuestionLinkedProtocolTemplate extends BaseEntity{
	@ManyToOne
	@JoinColumn(name = "template_id", nullable = false)
	private ProtocolTemplate protocolTemplate;
	
	
	@JoinColumn(name = "question_id", nullable = false)
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
	private HomeworkQuestion question;
}