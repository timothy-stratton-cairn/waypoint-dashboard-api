package com.cairn.waypoint.dashboard.entity;

import com.cairn.waypoint.dashboard.entity.converter.ProtocolCommentTypeConverter;
import com.cairn.waypoint.dashboard.entity.enumeration.ProtocolCommentTypeEnum;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
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
@SQLRestriction("active=1")
@Table(name = "protocol_step_note")
public class ProtocolStepNote extends BaseEntity {

  private String originalCommenter;
  private String note;

  @Column(name = "protocol_step_note_type_id")
  @Convert(converter = ProtocolCommentTypeConverter.class)
  private ProtocolCommentTypeEnum noteType;

  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
  @JoinColumn(name = "protocol_step_id")
  private ProtocolStep protocolStep;
}
