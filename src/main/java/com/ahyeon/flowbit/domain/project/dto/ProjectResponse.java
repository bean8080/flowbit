package com.ahyeon.flowbit.domain.project.dto;

import com.ahyeon.flowbit.domain.project.Project;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ProjectResponse {

    private Long id;
    private String name;
    private String description;
    private String status;
    private LocalDateTime createdAt;

    public ProjectResponse(Project project) {
        this.id = project.getId();
        this.name = project.getName();
        this.description = project.getDescription();
        this.status = project.getStatus().name();
        this.createdAt = project.getCreatedAt();
    }
}