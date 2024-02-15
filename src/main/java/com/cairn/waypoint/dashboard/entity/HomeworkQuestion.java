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
@Table(name = "homework_question")
public class HomeworkQuestion extends BaseEntity {
    private String questionAbbreviation;
    private String question;

    @JoinColumn(name = "next_homework_question_id")
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private HomeworkQuestion nextQuestion;

    @JoinColumn(name = "parent_homework_template_id", nullable = false)
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private HomeworkTemplate parentHomeworkTemplate;
}
