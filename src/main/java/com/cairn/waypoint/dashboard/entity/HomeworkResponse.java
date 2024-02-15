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
@Table(name = "homework_response")
public class HomeworkResponse extends BaseEntity {
    private String response;

    @JoinColumn(name = "homework_question_id", nullable = false)
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private HomeworkQuestion homeworkQuestion;

    @JoinColumn(name = "homework_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private Homework homework;
}
