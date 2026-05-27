package com.ahyeon.flowbit.domain.project.dto;

import com.ahyeon.flowbit.domain.task.TaskEvent;
import com.ahyeon.flowbit.domain.user.User;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ProjectTimelineResponse {

    private Long eventId;
    private Long taskId;
    private String taskTitle;
    private String eventType;
    private String fromStatus;
    private String toStatus;
    private String description;
    private LocalDateTime createdAt;
    private Long createdBy;
    private String actorName;
    private String actorEmail;

    public ProjectTimelineResponse(TaskEvent event, String taskTitle, User actor) {
        this.eventId = event.getId();
        this.taskId = event.getTaskId();
        this.taskTitle = taskTitle;
        this.eventType = event.getEventType().name();
        this.fromStatus = event.getFromStatus() == null ? null : event.getFromStatus().name();
        this.toStatus = event.getToStatus() == null ? null : event.getToStatus().name();
        this.description = event.getDescription();
        this.createdAt = event.getCreatedAt();
        this.createdBy = event.getCreatedBy();
        this.actorName = actor == null ? null : actor.getName();
        this.actorEmail = actor == null ? null : actor.getEmail();
    }
}