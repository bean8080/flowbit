package com.ahyeon.flowbit.domain.task.dto;

import lombok.Getter;

@Getter
public class CreateTaskRequest {

    private Long projectId;
    private String title;
    private String description;
    private Long assigneeId;
    private Integer priority;
}