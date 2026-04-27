package com.ahyeon.flowbit.domain.task;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TaskEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long taskId;

    @Enumerated(EnumType.STRING)
    private TaskEventType eventType;

    @Enumerated(EnumType.STRING)
    private TaskStatus fromStatus;

    @Enumerated(EnumType.STRING)
    private TaskStatus toStatus;

    private String description;

    private LocalDateTime createdAt;

    private Long createdBy;

    public TaskEvent(Long taskId, TaskEventType eventType,
                     TaskStatus fromStatus, TaskStatus toStatus,
                     String description, LocalDateTime createdAt, Long createdBy) {
        this.taskId = taskId;
        this.eventType = eventType;
        this.fromStatus = fromStatus;
        this.toStatus = toStatus;
        this.description = description;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
    }
}