package com.ahyeon.flowbit.domain.project.dto;

import com.ahyeon.flowbit.domain.project.Project;
import com.ahyeon.flowbit.domain.task.Task;
import com.ahyeon.flowbit.domain.task.TaskStatus;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ProjectResponse {

    private Long id;
    private String name;
    private String description;
    private String status;
    private LocalDateTime createdAt;

    public ProjectResponse(Project project, List<Task> tasks) {
        this.id = project.getId();
        this.name = project.getName();
        this.description = project.getDescription();
        this.status = calculateStatus(tasks);
        this.createdAt = project.getCreatedAt();
    }

    private String calculateStatus(List<Task> tasks) {
        if (tasks == null || tasks.isEmpty()) {
            return "READY";
        }

        boolean hasBlocked = tasks.stream()
                .anyMatch(task -> task.getStatus() == TaskStatus.BLOCKED);

        boolean hasInProgress = tasks.stream()
                .anyMatch(task -> task.getStatus() == TaskStatus.IN_PROGRESS);

        boolean allDone = tasks.stream()
                .allMatch(task -> task.getStatus() == TaskStatus.DONE);

        if (hasBlocked) {
            return "BLOCKED";
        }

        if (hasInProgress) {
            return "IN_PROGRESS";
        }

        if (allDone) {
            return "DONE";
        }

        return "READY";
    }
}