import axios from "axios";

const api = axios.create({
    baseURL: "http://localhost:8080/api",
});

export type Task = {
    id: number;
    projectId: number;
    projectName: string;
    title: string;
    description: string;
    status: string;
    assigneeId: number | null;
    priority: number | null;
    createdAt: string;
    startedAt: string | null;
    completedAt: string | null;
    deletedAt: string | null;
};

export const getTasks = (projectId?: number) => {
    const params = projectId ? { projectId } : {};
    return api.get<Task[]>("/tasks", { params });
};

export const createTask = (data: {
    projectId: number;
    title: string;
    description: string;
    assigneeId?: number | null;
    priority?: number | null;
}) => {
    return api.post<Task>("/tasks", data);
};

export const startTask = (id: number) => api.patch<Task>(`/tasks/${id}/start`);

export const completeTask = (id: number) =>
    api.patch<Task>(`/tasks/${id}/complete`);

export const blockTask = (id: number) => api.patch<Task>(`/tasks/${id}/block`);

export interface UpdateTaskRequest {
    title: string;
    description: string;
}

export const updateTask = (id: number, data: UpdateTaskRequest) => {
    return api.patch<Task>(`/tasks/${id}`, data);
};

export const deleteTask = (id: number) =>
    api.patch<Task>(`/tasks/${id}/delete`);

export const getTimeline = (id: number) => api.get(`/tasks/${id}/timeline`);