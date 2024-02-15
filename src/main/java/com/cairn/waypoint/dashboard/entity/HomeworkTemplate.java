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
@Table(name = "homework_template")
public class HomeworkTemplate extends BaseEntity {
    private String name;
    private String description;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinTable(name = "homework_template_homework_question",
            joinColumns = @JoinColumn(name = "homework_template_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "homework_question_id", referencedColumnName = "id"))
    private Set<HomeworkQuestion> homeworkQuestions;

    @JoinColumn(name = "step_template_id", nullable = false)
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private StepTemplate associatedStepTemplate;
}
