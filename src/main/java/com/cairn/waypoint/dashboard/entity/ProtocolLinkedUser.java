package com.cairn.waypoint.dashboard.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
//@EqualsAndHashCode(callSuper = true, exclude = "protocol_id")
@NoArgsConstructor
@AllArgsConstructor
@SQLRestriction("active=1")
@Table(name = "protocol_user")
public class ProtocolLinkedUser {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@JoinColumn(name="protocol_id",nullable = false)
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE) 
	private Protocol protocol;
	
	private Long userId;
	
	
	
}