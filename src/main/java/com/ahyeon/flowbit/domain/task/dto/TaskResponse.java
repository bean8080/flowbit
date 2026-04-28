package com.ahyeon.flowbit.domain.task.dto;

import com.ahyeon.flowbit.domain.task.Task;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class TaskResponse {

    private Long id;
    private Long projectId;
    private String title;
    private String description;
    private String status;
    private Long assigneeId;
    private Integer priority;
    private LocalDateTime createdAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private LocalDateTime deletedAt;

    public TaskResponse(Task task) {
        this.id = task.getId();
        this.projectId = task.getProjectId();
        this.title = task.getTitle();
        this.description = task.getDescription();
        this.status = task.getStatus().name();
        this.assigneeId = task.getAssigneeId();
        this.priority = task.getPriority();
        this.createdAt = task.getCreatedAt();
        this.startedAt = task.getStartedAt();
        this.completedAt = task.getCompletedAt();
        this.deletedAt = task.getDeletedAt();
    }
}