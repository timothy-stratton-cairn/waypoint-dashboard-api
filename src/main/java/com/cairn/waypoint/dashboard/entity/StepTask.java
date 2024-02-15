package com.cairn.waypoint.dashboard.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Collection;

@Data
@Entity
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "protocol_step_task")
public class StepTask extends BaseEntity {
    private String name;
    private String description;
    private String executableReference;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinTable(name = "protocol_step_task_protocol_step",
            joinColumns = @JoinColumn(name = "protocol_step_task_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "protocol_step_id", referencedColumnName = "id"))
    private Collection<ProtocolStep> associatedSteps;
}
