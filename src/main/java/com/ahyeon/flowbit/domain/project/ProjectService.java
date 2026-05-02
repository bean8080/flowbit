package com.ahyeon.flowbit.domain.project;

import com.ahyeon.flowbit.domain.project.dto.*;
import com.ahyeon.flowbit.domain.task.*;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final TaskEventRepository taskEventRepository;

    public ProjectResponse createProject(CreateProjectRequest request) {

        Project project = new Project(
                request.getName(),
                request.getDescription(),
                ProjectStatus.READY,
                LocalDateTime.now()
        );

        Project savedProject = projectRepository.save(project);

        return new ProjectResponse(savedProject, List.of());
    }

    public List<ProjectResponse> getProjects() {

        getOrCreateDefaultProject();

        return projectRepository.findByStatusNot(ProjectStatus.DELETED)
                .stream()
                .sorted((p1, p2) -> {
                    if (p1.getName().equals("DEFAULT")) return -1;
                    if (p2.getName().equals("DEFAULT")) return 1;
                    return p1.getId().compareTo(p2.getId());
                })
                .map(project -> {
                    List<Task> tasks = taskRepository.findByProject_IdAndStatusNot(
                            project.getId(),
                            TaskStatus.DELETED
                    );

                    return new ProjectResponse(project, tasks);
                })
                .toList();
    }

    public ProjectResponse getProject(Long id) {

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("프로젝트를 찾을 수 없습니다."));

        List<Task> tasks = taskRepository.findByProject_IdAndStatusNot(
                project.getId(),
                TaskStatus.DELETED
        );

        return new ProjectResponse(project, tasks);
    }

    public Project getOrCreateDefaultProject() {

        return projectRepository.findByName("DEFAULT")
                .orElseGet(() -> {
                    Project defaultProject = new Project(
                            "DEFAULT",
                            "기본 프로젝트",
                            ProjectStatus.READY,
                            LocalDateTime.now()
                    );
                    return projectRepository.save(defaultProject);
                });
    }

    public List<ProjectTimelineResponse> getProjectTimeline(Long projectId) {

        projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("프로젝트를 찾을 수 없습니다."));

        List<Task> tasks = taskRepository.findByProject_IdAndStatusNot(
                projectId,
                TaskStatus.DELETED
        );

        List<Long> taskIds = tasks.stream()
                .map(Task::getId)
                .toList();

        Map<Long, String> taskMap = tasks.stream()
                .collect(Collectors.toMap(Task::getId, Task::getTitle));

        List<TaskEvent> events = taskIds.isEmpty()
                ? List.of()
                : taskEventRepository.findByTaskIdInOrderByCreatedAtAsc(taskIds);

        return events.stream()
                .map(event -> new ProjectTimelineResponse(
                        event,
                        taskMap.get(event.getTaskId())
                ))
                .toList();
    }

    @Cacheable(value = "projectAnalysis", key = "#projectId")
    public ProjectAnalysisResponse getProjectAnalysis(Long projectId) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("프로젝트를 찾을 수 없습니다."));

        List<Task> tasks = taskRepository.findByProject_IdAndStatusNot(
                projectId,
                TaskStatus.DELETED
        );

        List<Long> taskIds = tasks.stream()
                .map(Task::getId)
                .toList();

        List<TaskEvent> events = taskIds.isEmpty()
                ? List.of()
                : taskEventRepository.findByTaskIdInOrderByCreatedAtAsc(taskIds);

        int totalTaskCount = tasks.size();

        int todoCount = countByStatus(tasks, TaskStatus.TODO);
        int inProgressCount = countByStatus(tasks, TaskStatus.IN_PROGRESS);
        int blockedCount = countByStatus(tasks, TaskStatus.BLOCKED);
        int doneCount = countByStatus(tasks, TaskStatus.DONE);

        LocalDateTime lastEventAt = events.isEmpty()
                ? null
                : events.get(events.size() - 1).getCreatedAt();

        return new ProjectAnalysisResponse(
                project.getId(),
                project.getName(),
                totalTaskCount,
                todoCount,
                inProgressCount,
                blockedCount,
                doneCount,
                events.size(),
                lastEventAt
        );
    }

    @CacheEvict(value = "projectAnalysis", key = "#id")
    public ProjectResponse updateProject(Long id, UpdateProjectRequest request) {

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("프로젝트를 찾을 수 없습니다."));

        if ("DEFAULT".equals(project.getName())) {
            throw new IllegalStateException("기본 프로젝트는 수정/삭제할 수 없습니다.");
        }

        project.update(request.getName(), request.getDescription());

        List<Task> tasks = taskRepository.findByProject_IdAndStatusNot(
                project.getId(),
                TaskStatus.DELETED
        );

        return new ProjectResponse(project, tasks);
    }

    @CacheEvict(value = "projectAnalysis", key = "#id")
    public ProjectResponse deleteProject(Long id) {

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("프로젝트를 찾을 수 없습니다."));

        if ("DEFAULT".equals(project.getName())) {
            throw new IllegalStateException("기본 프로젝트는 수정/삭제할 수 없습니다.");
        }

        LocalDateTime now = LocalDateTime.now();

        List<Task> tasks = taskRepository.findByProject_IdAndStatusNot(
                id,
                TaskStatus.DELETED
        );

        for (Task task : tasks) {
            task.delete(now);
        }

        project.delete(now);

        return new ProjectResponse(project, List.of());
    }

    private int countByStatus(List<Task> tasks, TaskStatus status) {
        return (int) tasks.stream()
                .filter(task -> task.getStatus() == status)
                .count();
    }
}