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
@Table(name = "protocol_step_template")
public class StepTemplate extends BaseEntity {
    private String name;
    private String description;

    @JoinColumn(name = "linked_step_task_id")
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private StepTask linkedTask;

    @JoinColumn(name = "linked_homework_template_id")
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private HomeworkTemplate linkedHomeworkTemplate;

    @JoinColumn(name = "parent_protocol_id", nullable = false)
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private Protocol parentProtocol;

    @JoinColumn(name = "next_protocol_step_id")
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private ProtocolStep nextProtocolStep;
}
