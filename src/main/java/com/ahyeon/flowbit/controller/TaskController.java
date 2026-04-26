package com.ahyeon.flowbit.controller;

import com.ahyeon.flowbit.domain.task.Task;
import com.ahyeon.flowbit.domain.task.TaskService;
import com.ahyeon.flowbit.domain.task.dto.CreateTaskRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public void createTask(@RequestBody CreateTaskRequest request) {
        taskService.createTask(request);
    }

    @GetMapping
    public List<Task> getAllTasks() {
        return taskService.getAllTasks();
    }

    @GetMapping("/{id}")
    public Task getTask(@PathVariable Long id) {
        return taskService.getTask(id);
    }
}