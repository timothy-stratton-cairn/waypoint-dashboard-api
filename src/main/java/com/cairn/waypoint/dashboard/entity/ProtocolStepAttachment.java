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
import org.hibernate.annotations.SQLRestriction;

@Data
@Entity
@SuperBuilder
@EqualsAndHashCode(callSuper = true, exclude = "protocolStep")
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "protocol_step_attachment")
@SQLRestriction("active=1")
public class ProtocolStepAttachment extends BaseEntity {

  private String originalUploader;
  private String filename;
  private String fileGuid;

  @Column(name = "s3_key")
  private String s3Key;

  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
  @JoinColumn(name = "protocol_step_id")
  private ProtocolStep protocolStep;
}
