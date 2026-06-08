package com.ahyeon.flowbit.domain.snapshot.dto;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ProjectSnapshotResponse {

    private Long projectId;
    private String projectName;
    private LocalDateTime snapshotAt;

    private int totalTasks;
    private int todoCount;
    private int inProgressCount;
    private int blockedCount;
    private int doneCount;

    private List<TaskSnapshotResponse> tasks;

    public ProjectSnapshotResponse(
            Long projectId,
            String projectName,
            LocalDateTime snapshotAt,
            int totalTasks,
            int todoCount,
            int inProgressCount,
            int blockedCount,
            int doneCount,
            List<TaskSnapshotResponse> tasks
    ) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.snapshotAt = snapshotAt;
        this.totalTasks = totalTasks;
        this.todoCount = todoCount;
        this.inProgressCount = inProgressCount;
        this.blockedCount = blockedCount;
        this.doneCount = doneCount;
        this.tasks = tasks;
    }
}