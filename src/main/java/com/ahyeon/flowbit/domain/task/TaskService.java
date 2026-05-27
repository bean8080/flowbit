package com.ahyeon.flowbit.domain.task;

import com.ahyeon.flowbit.domain.auth.CurrentUserProvider;
import com.ahyeon.flowbit.domain.project.Project;
import com.ahyeon.flowbit.domain.project.ProjectRepository;
import com.ahyeon.flowbit.domain.task.dto.*;
import com.ahyeon.flowbit.domain.project.ProjectService;
import com.ahyeon.flowbit.domain.user.User;
import com.ahyeon.flowbit.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskEventRepository taskEventRepository;
    private final ProjectRepository projectRepository;
    private final ProjectService projectService;
    private final CurrentUserProvider currentUserProvider;
    private final UserRepository userRepository;

    @Transactional
    public TaskResponse createTask(CreateTaskRequest request) {

        LocalDateTime now = LocalDateTime.now();

        Long currentUserId = currentUserProvider.getCurrentUserId();

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
                currentUserId,
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
                currentUserId
        );

        taskEventRepository.save(event);

        return new TaskResponse(savedTask);
    }

    @Transactional
    @CacheEvict(value = "projectAnalysis", key = "#result.projectId")
    public TaskResponse updateTask(Long id, UpdateTaskRequest request) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("작업을 찾을 수 없습니다."));

        task.update(request.getTitle(), request.getDescription());

        return new TaskResponse(task);
    }

    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
    public TaskResponse getTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("작업을 찾을 수 없습니다."));

        return new TaskResponse(task);
    }

    @Transactional
    @CacheEvict(value = "projectAnalysis", key = "#result.projectId")
    public TaskResponse startTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("작업을 찾을 수 없습니다."));

        Long currentUserId = currentUserProvider.getCurrentUserId();

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
                currentUserId
        );

        taskEventRepository.save(event);

        return new TaskResponse(task);
    }

    @Transactional(readOnly = true)
    public List<TaskEventResponse> getTaskEvents(Long taskId) {
        taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("작업을 찾을 수 없습니다."));

        List<TaskEvent> events = taskEventRepository.findByTaskIdOrderByCreatedAtAsc(taskId);

        Map<Long, User> userMap = getUserMapByEvents(events);

        return events.stream()
                .map(event -> new TaskEventResponse(
                        event,
                        userMap.get(event.getCreatedBy())
                ))
                .toList();
    }

    @Transactional
    @CacheEvict(value = "projectAnalysis", key = "#result.projectId")
    public TaskResponse completeTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("작업을 찾을 수 없습니다."));

        Long currentUserId = currentUserProvider.getCurrentUserId();

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
                currentUserId
        );

        taskEventRepository.save(event);

        return new TaskResponse(task);
    }

    @Transactional
    @CacheEvict(value = "projectAnalysis", key = "#result.projectId")
    public TaskResponse blockTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("작업을 찾을 수 없습니다."));

        Long currentUserId = currentUserProvider.getCurrentUserId();

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
                currentUserId
        );

        taskEventRepository.save(event);

        return new TaskResponse(task);
    }

    @Transactional
    @CacheEvict(value = "projectAnalysis", key = "#result.projectId")
    public TaskResponse deleteTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("작업을 찾을 수 없습니다."));

        Long currentUserId = currentUserProvider.getCurrentUserId();

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
                currentUserId
        );

        taskEventRepository.save(event);

        return new TaskResponse(task);
    }

    @Transactional(readOnly = true)
    public String getLatestStatusFromEvents(Long taskId) {
        taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("작업을 찾을 수 없습니다."));

        TaskEvent latestEvent = taskEventRepository.findTopByTaskIdOrderByCreatedAtDesc(taskId)
                .orElseThrow(() -> new IllegalStateException("이벤트가 존재하지 않습니다."));

        return latestEvent.getToStatus().name();
    }

    @Transactional(readOnly = true)
    public List<TaskTimelineResponse> getTimeline(Long taskId) {
        taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("작업을 찾을 수 없습니다."));

        List<TaskEvent> events = taskEventRepository.findByTaskIdOrderByCreatedAtAsc(taskId);

        Map<Long, User> userMap = getUserMapByEvents(events);

        List<TaskTimelineResponse> timeline = new ArrayList<>();

        for (int i = 0; i < events.size(); i++) {
            LocalDateTime previousOccurredAt = i == 0 ? null : events.get(i - 1).getCreatedAt();

            TaskEvent event = events.get(i);

            timeline.add(new TaskTimelineResponse(
                    event,
                    previousOccurredAt,
                    userMap.get(event.getCreatedBy())
            ));
        }

        return timeline;
    }

    private Map<Long, User> getUserMapByEvents(List<TaskEvent> events) {
        List<Long> userIds = events.stream()
                .map(TaskEvent::getCreatedBy)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        return userRepository.findAllById(userIds)
                .stream()
                .collect(Collectors.toMap(User::getId, user -> user));
    }
}