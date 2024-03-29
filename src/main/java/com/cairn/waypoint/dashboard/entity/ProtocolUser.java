package com.cairn.waypoint.dashboard.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@Entity
@SuperBuilder
@EqualsAndHashCode(callSuper = false, exclude = "protocol")
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "protocol_user")
public class ProtocolUser extends BaseEntity {

  @JoinColumn(name = "protocol_id", nullable = false)
  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
  private Protocol protocol;

  @Column(name = "user_id", nullable = false)
  private Long userId;
}
