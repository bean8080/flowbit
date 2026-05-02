package com.ahyeon.flowbit.domain.task;

import com.ahyeon.flowbit.domain.project.Project;
import com.ahyeon.flowbit.domain.project.ProjectRepository;
import com.ahyeon.flowbit.domain.task.dto.*;
import com.ahyeon.flowbit.domain.project.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskEventRepository taskEventRepository;
    private final ProjectRepository projectRepository;
    private final ProjectService projectService;

    public TaskResponse createTask(CreateTaskRequest request) {

        LocalDateTime now = LocalDateTime.now();

        Project project;

        if (request.getProjectId() == null) {
            project = projectService.getOrCreateDefaultProject();
        } else {
            project = projectRepository.findById(request.getProjectId())
                    .orElseThrow(() -> new IllegalArgumentException("프로젝트를 찾을 수 없습니다."));
        }

        Task task = new Task(
                project,
                request.getTitle(),
                request.getDescription(),
                TaskStatus.TODO,
                request.getAssigneeId(),
                request.getPriority(),
                now
        );

        Task savedTask = taskRepository.save(task);

        TaskEvent event = new TaskEvent(
                savedTask.getId(),
                TaskEventType.CREATED,
                null,
                TaskStatus.TODO,
                "Task created",
                now,
                null
        );

        taskEventRepository.save(event);

        return new TaskResponse(savedTask);
    }

    @CacheEvict(value = "projectAnalysis", key = "#result.projectId")
    public TaskResponse updateTask(Long id, UpdateTaskRequest request) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("작업을 찾을 수 없습니다."));

        task.update(request.getTitle(), request.getDescription());

        return new TaskResponse(task);
    }

    public List<TaskResponse> getTasks(TaskStatus status, Long projectId) {
        List<Task> tasks;

        if (projectId != null && status != null) {
            tasks = taskRepository.findByProject_IdAndStatus(projectId, status);
        } else if (projectId != null) {
            tasks = taskRepository.findByProject_IdAndStatusNot(projectId, TaskStatus.DELETED);
        } else if (status != null) {
            tasks = taskRepository.findByStatus(status);
        } else {
            tasks = taskRepository.findByStatusNot(TaskStatus.DELETED);
        }

        return tasks.stream()
                .map(TaskResponse::new)
                .toList();
    }

    public TaskResponse getTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("작업을 찾을 수 없습니다."));

        return new TaskResponse(task);
    }

    @CacheEvict(value = "projectAnalysis", key = "#result.projectId")
    public TaskResponse startTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("작업을 찾을 수 없습니다."));

        TaskStatus fromStatus = task.getStatus();

        LocalDateTime now = LocalDateTime.now();

        task.start(now);

        TaskEvent event = new TaskEvent(
                task.getId(),
                TaskEventType.STARTED,
                fromStatus,
                TaskStatus.IN_PROGRESS,
                "Task started",
                now,
                null
        );

        taskEventRepository.save(event);

        return new TaskResponse(task);
    }

    public List<TaskEventResponse> getTaskEvents(Long taskId) {
        taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("작업을 찾을 수 없습니다."));

        return taskEventRepository.findByTaskIdOrderByCreatedAtAsc(taskId)
                .stream()
                .map(TaskEventResponse::new)
                .toList();
    }

    @CacheEvict(value = "projectAnalysis", key = "#result.projectId")
    public TaskResponse completeTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("작업을 찾을 수 없습니다."));

        TaskStatus fromStatus = task.getStatus();

        LocalDateTime now = LocalDateTime.now();

        task.complete(now);

        TaskEvent event = new TaskEvent(
                task.getId(),
                TaskEventType.COMPLETED,
                fromStatus,
                TaskStatus.DONE,
                "Task completed",
                now,
                null
        );

        taskEventRepository.save(event);

        return new TaskResponse(task);
    }

    @CacheEvict(value = "projectAnalysis", key = "#result.projectId")
    public TaskResponse blockTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("작업을 찾을 수 없습니다."));

        TaskStatus fromStatus = task.getStatus();

        LocalDateTime now = LocalDateTime.now();

        task.block();

        TaskEvent event = new TaskEvent(
                task.getId(),
                TaskEventType.BLOCKED,
                fromStatus,
                TaskStatus.BLOCKED,
                "Task blocked",
                now,
                null
        );

        taskEventRepository.save(event);

        return new TaskResponse(task);
    }

    @CacheEvict(value = "projectAnalysis", key = "#result.projectId")
    public TaskResponse deleteTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("작업을 찾을 수 없습니다."));

        TaskStatus fromStatus = task.getStatus();

        LocalDateTime now = LocalDateTime.now();

        task.delete(now);

        TaskEvent event = new TaskEvent(
                task.getId(),
                TaskEventType.DELETED,
                fromStatus,
                TaskStatus.DELETED,
                "Task deleted",
                now,
                null
        );

        taskEventRepository.save(event);

        return new TaskResponse(task);
    }

    public String getLatestStatusFromEvents(Long taskId) {
        taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("작업을 찾을 수 없습니다."));

        TaskEvent latestEvent = taskEventRepository.findTopByTaskIdOrderByCreatedAtDesc(taskId)
                .orElseThrow(() -> new IllegalStateException("이벤트가 존재하지 않습니다."));

        return latestEvent.getToStatus().name();
    }

    public List<TaskTimelineResponse> getTimeline(Long taskId) {
        taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("작업을 찾을 수 없습니다."));

        List<TaskEvent> events = taskEventRepository.findByTaskIdOrderByCreatedAtAsc(taskId);

        List<TaskTimelineResponse> timeline = new ArrayList<>();

        for (int i = 0; i < events.size(); i++) {
            LocalDateTime previousOccurredAt = i == 0 ? null : events.get(i - 1).getCreatedAt();

            timeline.add(new TaskTimelineResponse(events.get(i), previousOccurredAt));
        }

        return timeline;
    }
}