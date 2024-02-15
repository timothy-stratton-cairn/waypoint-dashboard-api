package com.cairn.waypoint.dashboard.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Data
@Entity
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "homework")
public class Homework extends BaseEntity {
    private String name;
    private String description;

    @OneToMany(mappedBy = "homework")
    private Set<HomeworkResponse> homeworkQuestions;

    @JoinColumn(name = "homework_template_id", nullable = false)
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private HomeworkTemplate homeworkTemplate;

    @JoinColumn(name = "protocol_step_id")
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private ProtocolStep associatedProtocolStep;

    @OneToMany(mappedBy = "homework")
    private Set<HomeworkUser> associatedUsers;
}

