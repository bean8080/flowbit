import axios from "axios";

const api = axios.create({
    baseURL: "http://localhost:8080/api",
});

export const getTasks = (projectId?: number) => {
    const params = projectId ? { projectId } : {};
    return api.get("/tasks", { params });
};

export const startTask = (id: number) => api.patch(`/tasks/${id}/start`);
export const completeTask = (id: number) => api.patch(`/tasks/${id}/complete`);
export const blockTask = (id: number) => api.patch(`/tasks/${id}/block`);
export const getTimeline = (id: number) => api.get(`/tasks/${id}/timeline`);