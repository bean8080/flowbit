package com.ahyeon.flowbit.domain.project;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;                    // 프로젝트 이름

    private String description;             // 프로젝트 설명

    @Enumerated(EnumType.STRING)
    private ProjectStatus status;           // 현재 프로젝트 상태

    private LocalDateTime createdAt;        // 생성 시점

    private LocalDateTime startedAt;        // 시작 시점

    private LocalDateTime completedAt;      // 완료 시점

    private LocalDateTime deletedAt;        // 삭제 시점

    public Project(String name, String description, ProjectStatus status, LocalDateTime createdAt) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
    }

    public void update(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public void delete() {
        this.status = ProjectStatus.DELETED;
    }
}