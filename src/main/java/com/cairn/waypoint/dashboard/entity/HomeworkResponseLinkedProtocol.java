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
@NoArgsConstructor
@AllArgsConstructor
@SQLRestriction("active=1")
@Table(name = "protocol_linked_homework_response")
public class HomeworkResponseLinkedProtocol {

	@ManyToOne
	@JoinColumn(name = "protocol_id", nullable = false)
	private Protocol protocol;
	
	
	@JoinColumn(name = "homework_response_id", nullable = false)
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
	private HomeworkResponse response;
}