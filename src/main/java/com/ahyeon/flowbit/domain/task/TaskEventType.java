package com.ahyeon.flowbit.domain.task;

public enum TaskEventType {
    CREATED,    // 작업 생성됨 (없음 → TODO)
    STARTED,    // 작업 시작됨 (TODO → IN_PROGRESS)
    COMPLETED,  // 작업 완료됨 (IN_PROGRESS → DONE)
    BLOCKED,    // 작업이 막힘 상태로 변경됨 (TODO/IN_PROGRESS → BLOCKED)
    DELETED     // 작업 삭제됨 (soft delete, 어떤 상태든 → DELETED)
}