package com.ahyeon.flowbit.domain.task.dto;

import com.ahyeon.flowbit.domain.task.Task;
import lombok.Getter;

@Getter
public class TaskResponse {

    private Long id;
    private String title;
    private String description;
    private String status;

    public TaskResponse(Task task) {
        this.id = task.getId();
        this.title = task.getTitle();
        this.description = task.getDescription();
        this.status = task.getStatus().name();
    }
}