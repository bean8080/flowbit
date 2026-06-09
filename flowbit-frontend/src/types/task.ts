export type Task = {
    id: number;
    projectId: number;
    projectName: string;
    title: string;
    description: string;
    status: string;
    assigneeId: number | null;
    createdBy: number | null;
    priority: number | null;
    createdAt: string;
    startedAt: string | null;
    completedAt: string | null;
    deletedAt: string | null;
};

export type CreateTaskRequest = {
    projectId: number;
    title: string;
    description: string;
    assigneeId?: number | null;
    priority?: number | null;
};

export type UpdateTaskRequest = {
    title: string;
    description: string;
};