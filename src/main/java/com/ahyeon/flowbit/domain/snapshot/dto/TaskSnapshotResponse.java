package com.ahyeon.flowbit.domain.snapshot.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class TaskSnapshotResponse {

    private Long taskId;
    private String title;
    private String status;
    private LocalDateTime lastEventAt;

    private Long lastActorId;
    private String lastActorName;
    private String lastActorEmail;

    public TaskSnapshotResponse(
            Long taskId,
            String title,
            String status,
            LocalDateTime lastEventAt,
            Long lastActorId,
            String lastActorName,
            String lastActorEmail
    ) {
        this.taskId = taskId;
        this.title = title;
        this.status = status;
        this.lastEventAt = lastEventAt;
        this.lastActorId = lastActorId;
        this.lastActorName = lastActorName;
        this.lastActorEmail = lastActorEmail;
    }
}