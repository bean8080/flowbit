package com.ahyeon.flowbit.domain.project.dto;

import com.ahyeon.flowbit.domain.task.TaskEvent;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ProjectTimelineResponse {

    private Long eventId;
    private Long taskId;
    private String eventType;
    private String fromStatus;
    private String toStatus;
    private String description;
    private LocalDateTime occurredAt;

    public ProjectTimelineResponse(TaskEvent event) {
        this.eventId = event.getId();
        this.taskId = event.getTaskId();
        this.eventType = event.getEventType().name();
        this.fromStatus = event.getFromStatus() == null ? null : event.getFromStatus().name();
        this.toStatus = event.getToStatus().name();
        this.description = event.getDescription();
        this.occurredAt = event.getCreatedAt();
    }
}