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