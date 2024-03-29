package com.cairn.waypoint.dashboard.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
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
@EqualsAndHashCode(callSuper = true, exclude = "comments")
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "protocol")
@SQLRestriction("active=1")
public class Protocol extends BaseEntity {

  private String name;
  private String description;

  private String goal;
  private String goalProgress;

  @OneToMany(fetch = FetchType.EAGER, mappedBy = "protocol", cascade = {CascadeType.MERGE,
      CascadeType.PERSIST})
  private Set<ProtocolCommentary> comments;

  @Column(name = "attention")
  private Boolean markedForAttention;
  private LocalDateTime lastStatusUpdateTimestamp;

  @JoinColumn(name = "protocol_template_id", nullable = false)
  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
  private ProtocolTemplate protocolTemplate;


  @OrderBy("ordinalIndex ASC")
  @JoinColumn(name = "parent_protocol_id")
  @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
  private Set<ProtocolStep> protocolSteps;

  @JoinColumn(name = "protocol_id")
  @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
  private Set<ProtocolUser> associatedUsers;
}
