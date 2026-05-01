package com.ahyeon.flowbit.domain.project.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ProjectAnalysisResponse {

    private Long projectId;
    private String projectName;

    private int totalTaskCount;
    private int todoCount;
    private int inProgressCount;
    private int blockedCount;
    private int doneCount;

    private int totalEventCount;
    private LocalDateTime lastEventAt;

    public ProjectAnalysisResponse(
            Long projectId,
            String projectName,
            int totalTaskCount,
            int todoCount,
            int inProgressCount,
            int blockedCount,
            int doneCount,
            int totalEventCount,
            LocalDateTime lastEventAt
    ) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.totalTaskCount = totalTaskCount;
        this.todoCount = todoCount;
        this.inProgressCount = inProgressCount;
        this.blockedCount = blockedCount;
        this.doneCount = doneCount;
        this.totalEventCount = totalEventCount;
        this.lastEventAt = lastEventAt;
    }
}