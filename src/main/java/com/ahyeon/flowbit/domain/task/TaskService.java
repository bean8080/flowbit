package com.ahyeon.flowbit.domain.task;

import com.ahyeon.flowbit.domain.task.dto.CreateTaskRequest;
import com.ahyeon.flowbit.domain.task.dto.TaskResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    public void createTask(CreateTaskRequest request) {

        Task task = new Task(
                request.getProjectId(),
                request.getTitle(),
                request.getDescription(),
                TaskStatus.TODO,
                request.getAssigneeId(),
                request.getPriority(),
                LocalDateTime.now()
        );

        taskRepository.save(task);
    }

    public List<TaskResponse> getAllTasks() {
        return taskRepository.findAll()
                .stream()
                .map(TaskResponse::new)
                .toList();
    }

    public TaskResponse getTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        return new TaskResponse(task);
    }
}