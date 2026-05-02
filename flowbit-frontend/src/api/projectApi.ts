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