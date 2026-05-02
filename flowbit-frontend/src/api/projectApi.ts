import axios from "axios";

const api = axios.create({
    baseURL: "http://localhost:8080/api",
});

export type Project = {
    id: number;
    name: string;
    description: string;
    status: string;
    createdAt: string;
};

export type ProjectTimelineEvent = {
    eventId: number;
    taskId: number;
    taskTitle: string;
    eventType: string;
    fromStatus: string | null;
    toStatus: string | null;
    description: string;
    createdAt: string;
};

export type ProjectAnalysis = {
    projectId: number;
    projectName: string;
    totalTaskCount: number;
    todoCount: number;
    inProgressCount: number;
    blockedCount: number;
    doneCount: number;
    totalEventCount: number;
    lastEventAt: string | null;
};

export const getProjects = () => {
    return api.get<Project[]>("/projects");
};

export const createProject = (data: {
    name: string;
    description: string;
}) => {
    return api.post<Project>("/projects", data);
};

export const updateProject = (
    id: number,
    data: {
        name: string;
        description: string;
    }
) => {
    return api.patch<Project>(`/projects/${id}`, data);
};

export const deleteProject = (id: number) => {
    return api.patch<Project>(`/projects/${id}/delete`);
};

export const getProjectTimeline = (id: number) => {
    return api.get<ProjectTimelineEvent[]>(`/projects/${id}/timeline`);
};

export const getProjectAnalysis = (id: number) => {
    return api.get<ProjectAnalysis>(`/projects/${id}/analysis`);
};