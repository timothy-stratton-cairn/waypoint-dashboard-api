package com.cairn.waypoint.dashboard.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@Entity
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "protocol_user")
public class ProtocolUser extends BaseEntity {
    @Column(name = "protocol_id", nullable = false)
    private Long protocolId;

    @Column(name = "user_id", nullable = false)
    private Long userId;
}
