package com.cairn.waypoint.dashboard.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.LinkedHashSet;
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
@EqualsAndHashCode(callSuper = false, exclude = "protocolTemplateSteps")
@NoArgsConstructor
@AllArgsConstructor
@SQLRestriction("active=1")
@Table(name = "protocol_template")
public class ProtocolTemplate extends BaseEntity {

  private String name;
  private String description;

  @OneToMany(mappedBy = "protocolTemplate",
      cascade = {CascadeType.MERGE, CascadeType.PERSIST})
  private Set<ProtocolTemplateLinkedStepTemplate> protocolTemplateSteps;

  public Set<ProtocolTemplateLinkedStepTemplate> getProtocolTemplateSteps() {
    if (protocolTemplateSteps == null) {
      protocolTemplateSteps = new LinkedHashSet<>();
    }
    return protocolTemplateSteps;
  }
}
