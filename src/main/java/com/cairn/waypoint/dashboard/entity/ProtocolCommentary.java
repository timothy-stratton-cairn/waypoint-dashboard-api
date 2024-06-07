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
@EqualsAndHashCode(callSuper = true, exclude = "protocol")
@NoArgsConstructor
@AllArgsConstructor
@SQLRestriction("active=1")
@Table(name = "protocol_commentary")
public class ProtocolCommentary extends BaseEntity {

  private String originalCommenter;
  private String comment;

  @Column(name = "protocol_comment_type_id")
  @Convert(converter = ProtocolCommentTypeConverter.class)
  private ProtocolCommentTypeEnum commentType;

  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
  @JoinColumn(name = "protocol_id")
  private Protocol protocol;
}
