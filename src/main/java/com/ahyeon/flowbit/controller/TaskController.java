package com.ahyeon.flowbit.controller;

import com.ahyeon.flowbit.domain.task.TaskService;
import com.ahyeon.flowbit.domain.task.TaskStatus;
import com.ahyeon.flowbit.domain.task.dto.CreateTaskRequest;
import com.ahyeon.flowbit.domain.task.dto.TaskResponse;
import com.ahyeon.flowbit.domain.task.dto.TaskEventResponse;
import com.ahyeon.flowbit.domain.task.dto.TaskTimelineResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "http://localhost:5173")
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public TaskResponse createTask(@RequestBody CreateTaskRequest request) {
        return taskService.createTask(request);
    }

    @GetMapping
    public List<TaskResponse> getTasks(
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) Long projectId
    ) {
        return taskService.getTasks(status, projectId);
    }

    @GetMapping("/{id}")
    public TaskResponse getTask(@PathVariable Long id) {
        return taskService.getTask(id);
    }

    @PatchMapping("/{id}/start")
    public TaskResponse startTask(@PathVariable Long id) {
        return taskService.startTask(id);
    }

    @GetMapping("/{id}/events")
    public List<TaskEventResponse> getTaskEvents(@PathVariable Long id) {
        return taskService.getTaskEvents(id);
    }

    @PatchMapping("/{id}/complete")
    public TaskResponse completeTask(@PathVariable Long id) {
        return taskService.completeTask(id);
    }

    @PatchMapping("/{id}/block")
    public TaskResponse blockTask(@PathVariable Long id) {
        return taskService.blockTask(id);
    }

    @PatchMapping("/{id}/delete")
    public TaskResponse deleteTask(@PathVariable Long id) {
        return taskService.deleteTask(id);
    }

    @GetMapping("/{id}/latest-status")
    public String getLatestStatus(@PathVariable Long id) {
        return taskService.getLatestStatusFromEvents(id);
    }

    @GetMapping("/{id}/timeline")
    public List<TaskTimelineResponse> getTimeline(@PathVariable Long id) {
        return taskService.getTimeline(id);
    }
}