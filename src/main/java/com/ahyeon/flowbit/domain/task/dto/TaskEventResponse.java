package com.ahyeon.flowbit.domain.task.dto;

import com.ahyeon.flowbit.domain.task.TaskEvent;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class TaskEventResponse {

    private Long id;
    private Long taskId;
    private String eventType;
    private String fromStatus;
    private String toStatus;
    private String description;
    private LocalDateTime createdAt;

    private String label;
    private String summary;

    public TaskEventResponse(TaskEvent event) {
        this.id = event.getId();
        this.taskId = event.getTaskId();
        this.eventType = event.getEventType().name();
        this.fromStatus = event.getFromStatus() == null ? null : event.getFromStatus().name();
        this.toStatus = event.getToStatus().name();
        this.description = event.getDescription();
        this.createdAt = event.getCreatedAt();

        this.label = createLabel();
        this.summary = createSummary();
    }

    private String createLabel() {
        if (fromStatus == null) {
            return eventType + " → " + toStatus;
        }

        return eventType + " (" + fromStatus + " → " + toStatus + ")";
    }

    private String createSummary() {
        if (fromStatus == null) {
            return "작업이 " + toStatus + " 상태로 생성되었습니다.";
        }

        return "작업 상태가 " + fromStatus + "에서 " + toStatus + "로 변경되었습니다.";
    }
}