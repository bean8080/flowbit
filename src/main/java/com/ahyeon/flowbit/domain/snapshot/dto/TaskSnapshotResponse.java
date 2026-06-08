package com.ahyeon.flowbit.domain.snapshot.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class TaskSnapshotResponse {

    private Long taskId;
    private String title;
    private String status;
    private LocalDateTime lastEventAt;

    public TaskSnapshotResponse(
            Long taskId,
            String title,
            String status,
            LocalDateTime lastEventAt
    ) {
        this.taskId = taskId;
        this.title = title;
        this.status = status;
        this.lastEventAt = lastEventAt;
    }
}