export const statusLabelMap: Record<string, string> = {
    TODO: "대기",
    IN_PROGRESS: "진행중",
    DONE: "완료",
    BLOCKED: "보류",
    DELETED: "삭제됨",
};

export const eventTypeLabelMap: Record<string, string> = {
    CREATED: "생성",
    STARTED: "시작",
    COMPLETED: "완료",
    BLOCKED: "보류",
    DELETED: "삭제",
};

export const projectStatusLabelMap: Record<string, string> = {
    READY: "준비중",
    IN_PROGRESS: "진행중",
    DONE: "완료",
    DELETED: "삭제됨",
};

export const getStatusLabel = (status?: string | null) => {
    if (!status) return "-";
    return statusLabelMap[status] ?? status;
};

export const getEventTypeLabel = (eventType?: string | null) => {
    if (!eventType) return "-";
    return eventTypeLabelMap[eventType] ?? eventType;
};

export const getProjectStatusLabel = (status?: string | null) => {
    if (!status) return "-";
    return projectStatusLabelMap[status] ?? status;
};