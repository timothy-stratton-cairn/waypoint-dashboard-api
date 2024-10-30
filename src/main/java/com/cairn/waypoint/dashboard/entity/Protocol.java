package com.cairn.waypoint.dashboard.entity;

import com.cairn.waypoint.dashboard.entity.converter.ProtocolStatusConverter;
import com.cairn.waypoint.dashboard.entity.converter.RecurrenceTypeConverter;
import com.cairn.waypoint.dashboard.entity.enumeration.ProtocolStatusEnum;
import com.cairn.waypoint.dashboard.entity.enumeration.RecurrenceTypeEnum;
import com.cairn.waypoint.dashboard.utility.protocoltriggering.StatusManager;
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
import java.time.LocalDate;
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
@EqualsAndHashCode(callSuper = true, exclude = {"comments", "protocolSteps"})
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "protocol")
@SQLRestriction("active=1")
public class Protocol extends BaseEntity {

  private String name;
  private String description;

  private String goal;
  private String goalProgress;

  private LocalDate dueDate;
  private LocalDate completionDate;

  @Column(name = "protocol_status_id")
  @Convert(converter = ProtocolStatusConverter.class)
  private ProtocolStatusEnum status;
  private Long userId;
  private Long assignedHouseholdId;

  @OneToMany(fetch = FetchType.EAGER, mappedBy = "protocol", cascade = {CascadeType.MERGE,
      CascadeType.PERSIST})
  private Set<ProtocolCommentary> comments;

  @Column(name = "attention")
  private Boolean markedForAttention;
  private LocalDateTime lastStatusUpdateTimestamp;

  @JoinColumn(name = "protocol_template_id", nullable = false)
  @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
  private ProtocolTemplate protocolTemplate;

  @Column(name = "recurrence_type_id")
  @Convert(converter = RecurrenceTypeConverter.class)
  private RecurrenceTypeEnum recurrenceType;

  @Column(name = "triggering_protocol_status_id")
  @Convert(converter = ProtocolStatusConverter.class)
  private ProtocolStatusEnum triggeringStatus;

  private Integer reoccurInYears;
  private Integer reoccurInMonths;
  private Integer reoccurInDays;

  @OrderBy("ordinalIndex ASC")
  @JoinColumn(name = "parent_protocol_id")
  @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
  private Set<ProtocolStep> protocolSteps;

  public void setStatus(ProtocolStatusEnum status) {
    this.status = status;
    StatusManager.notify(this);
  }
}
