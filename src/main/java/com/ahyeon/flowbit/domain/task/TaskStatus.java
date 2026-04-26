package com.ahyeon.flowbit.domain.task;

public enum TaskStatus {
    TODO,           // 생성만 된 상태 (아직 시작 안함)
    IN_PROGRESS,    // 작업 시작됨 (진행 중)
    BLOCKED,        // 작업 일시 중단 (막힘 / 대기)
    DONE,           // 완료됨
    DELETED         // 삭제됨 (soft delete)
}