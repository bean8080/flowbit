package com.ahyeon.flowbit.domain.task.dto;

import com.ahyeon.flowbit.domain.task.TaskEvent;
import lombok.Getter;

import java.time.Duration;
import java.time.LocalDateTime;

@Getter
public class TaskTimelineResponse {

    private Long eventId;
    private Long taskId;
    private String eventType;
    private String fromStatus;
    private String toStatus;
    private String label;
    private String summary;
    private LocalDateTime occurredAt;
    private Long minutesFromPreviousEvent;

    public TaskTimelineResponse(TaskEvent event, LocalDateTime previousOccurredAt) {
        this.eventId = event.getId();
        this.taskId = event.getTaskId();
        this.eventType = event.getEventType().name();
        this.fromStatus = event.getFromStatus() == null ? null : event.getFromStatus().name();
        this.toStatus = event.getToStatus().name();
        this.label = createLabel();
        this.summary = createSummary();
        this.occurredAt = event.getCreatedAt();
        this.minutesFromPreviousEvent = calculateMinutesFromPreviousEvent(previousOccurredAt);
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

    private Long calculateMinutesFromPreviousEvent(LocalDateTime previousOccurredAt) {
        if (previousOccurredAt == null) {
            return null;
        }

        return Duration.between(previousOccurredAt, this.occurredAt).toMinutes();
    }
}