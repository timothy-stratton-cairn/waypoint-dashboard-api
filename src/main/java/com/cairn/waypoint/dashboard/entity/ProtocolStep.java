package com.cairn.waypoint.dashboard.entity;

import jakarta.persistence.*;
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
@Table(name = "protocol_step")
public class ProtocolStep extends BaseEntity {
    private String name;
    private String description;
    private String notes;

    @JoinColumn(name = "step_status_id")
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private StepStatus status;

    @JoinColumn(name = "step_template_id", nullable = false)
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private StepTemplate template;

    @JoinColumn(name = "linked_step_task_id")
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private StepTask linkedTask;

    @JoinColumn(name = "linked_homework_id")
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private Homework linkedHomework;

    @JoinColumn(name = "parent_protocol_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private Protocol parentProtocol;

    @JoinColumn(name = "next_protocol_step_id")
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private ProtocolStep nextProtocolStep;
}
