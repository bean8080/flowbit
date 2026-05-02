package com.ahyeon.flowbit.domain.task;

import com.ahyeon.flowbit.domain.project.Project;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project; // 어떤 프로젝트에 속한 작업인지

    private String title; // 작업 제목

    private String description; // 작업 상세 설명

    @Enumerated(EnumType.STRING)
    private TaskStatus status; // 현재 작업 상태 (TODO, IN_PROGRESS 등)

    private Long assigneeId; // 담당자 ID (나중에 User 엔티티로 확장 가능)

    private Long createdBy; // 생성자 ID

    private Integer priority; // 우선순위 (예: 1=높음, 2=중간, 3=낮음)

    private LocalDateTime createdAt; // 작업 생성 시점

    private LocalDateTime startedAt; // 작업 시작 시점 (TODO → IN_PROGRESS 전환 시 기록)

    private LocalDateTime completedAt; // 작업 완료 시점 (DONE 상태일 때 기록)

    private LocalDateTime deletedAt; // 삭제 시점 (soft delete용, 실제 DB 삭제 안 함)

    public Task(Project project, String title, String description,
                TaskStatus status, Long assigneeId, Integer priority,
                LocalDateTime createdAt) {

        this.project = project;
        this.title = title;
        this.description = description;
        this.status = status;
        this.assigneeId = assigneeId;
        this.priority = priority;
        this.createdAt = createdAt;
    }

    public void start(LocalDateTime startedAt) {
        if (this.status == TaskStatus.DELETED) {
            throw new IllegalStateException("삭제된 작업은 변경할 수 없습니다.");
        }

        this.status = TaskStatus.IN_PROGRESS;
        this.startedAt = startedAt;
    }

    public void complete(LocalDateTime now) {
        if (this.status == TaskStatus.DELETED) {
            throw new IllegalStateException("삭제된 작업은 변경할 수 없습니다.");
        }

        this.status = TaskStatus.DONE;
        this.completedAt = now;
    }

    public void block() {
        if (this.status == TaskStatus.DELETED) {
            throw new IllegalStateException("삭제된 작업은 변경할 수 없습니다.");
        }

        this.status = TaskStatus.BLOCKED;
    }

    public void update(String title, String description) {
        if (this.status == TaskStatus.DELETED) {
            throw new IllegalStateException("삭제된 작업은 수정할 수 없습니다.");
        }

        this.title = title;
        this.description = description;
    }

    public void delete(LocalDateTime now) {
        this.status = TaskStatus.DELETED;
        this.deletedAt = now;
    }
}