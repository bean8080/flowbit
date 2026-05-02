import { useEffect, useState } from "react";
import {
    getTasks,
    startTask,
    completeTask,
    blockTask,
} from "../api/taskApi";
import { getProjects, type Project } from "../api/projectApi";

type Task = {
    id: number;
    title: string;
    status: string;
    projectName: string;
};

export default function TaskList() {
    const [tasks, setTasks] = useState<Task[]>([]);
    const [projects, setProjects] = useState<Project[]>([]);
    const [selectedProjectId, setSelectedProjectId] = useState<number | undefined>();

    const fetchTasks = () => {
        getTasks(selectedProjectId).then((res) => {
            setTasks(res.data);
        });
    };

    useEffect(() => {
        getProjects().then((res) => {
            setProjects(res.data);
        });
    }, []);

    useEffect(() => {
        fetchTasks();
    }, [selectedProjectId]);

    const handleStart = (id: number) => {
        startTask(id).then(fetchTasks);
    };

    const handleComplete = (id: number) => {
        completeTask(id).then(fetchTasks);
    };

    const handleBlock = (id: number) => {
        blockTask(id).then(fetchTasks);
    };

    return (
        <main style={{ maxWidth: "900px", margin: "0 auto", padding: "48px 24px" }}>
            <h1 style={{ fontSize: "48px", marginBottom: "8px" }}>Flowbit</h1>
            <p style={{ color: "#9ca3af", marginBottom: "32px" }}>
                작업의 상태 변화 흐름을 기록하고 보여주는 시스템
            </p>

            <select
                style={{ marginBottom: "24px", padding: "8px 12px" }}
                value={selectedProjectId ?? ""}
                onChange={(e) =>
                    setSelectedProjectId(e.target.value ? Number(e.target.value) : undefined)
                }
            >
                <option value="">전체 프로젝트</option>

                {projects.map((project) => (
                    <option key={project.id} value={project.id}>
                        {project.name}
                    </option>
                ))}
            </select>

            <div style={{ display: "grid", gap: "16px" }}>
                {tasks.map((task) => (
                    <section
                        key={task.id}
                        style={{
                            padding: "20px",
                            border: "1px solid #2f3545",
                            borderRadius: "16px",
                            background: "#1b1f2a",
                            textAlign: "left",
                        }}
                    >
                        <div style={{ display: "flex", justifyContent: "space-between" }}>
                            <h2 style={{ margin: 0, fontSize: "22px" }}>{task.title}</h2>
                            <span>{task.status}</span>
                        </div>

                        <p style={{ color: "#9ca3af" }}>Project: {task.projectName}</p>

                        <div style={{ display: "flex", gap: "8px", marginTop: "12px" }}>
                            <button onClick={() => handleStart(task.id)}>시작</button>
                            <button onClick={() => handleComplete(task.id)}>완료</button>
                            <button onClick={() => handleBlock(task.id)}>막힘</button>
                        </div>
                    </section>
                ))}
            </div>
        </main>
    );
}