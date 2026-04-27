package com.ahyeon.flowbit.domain.task.dto;

import com.ahyeon.flowbit.domain.task.TaskEvent;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class TaskEventResponse {

    private Long id;
    private Long taskId;
    private String eventType;
    private String fromStatus;
    private String toStatus;
    private String description;
    private LocalDateTime createdAt;

    public TaskEventResponse(TaskEvent event) {
        this.id = event.getId();
        this.taskId = event.getTaskId();
        this.eventType = event.getEventType().name();
        this.fromStatus = event.getFromStatus() == null ? null : event.getFromStatus().name();
        this.toStatus = event.getToStatus().name();
        this.description = event.getDescription();
        this.createdAt = event.getCreatedAt();
    }
}