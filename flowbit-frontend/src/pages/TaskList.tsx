import { useEffect, useState } from "react";
import { getTasks } from "../api/taskApi";

type Task = {
    id: number;
    title: string;
    status: string;
    projectName: string;
};

export default function TaskList() {
    const [tasks, setTasks] = useState<Task[]>([]);

    useEffect(() => {
        getTasks().then((res) => {
            setTasks(res.data);
        });
    }, []);

    return (
        <main style={{ maxWidth: "900px", margin: "0 auto", padding: "48px 24px" }}>
            <h1 style={{ fontSize: "48px", marginBottom: "8px" }}>Flowbit</h1>
            <p style={{ color: "#9ca3af", marginBottom: "32px" }}>
                작업의 상태 변화 흐름을 기록하고 보여주는 시스템
            </p>

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

                        <p style={{ color: "#9ca3af", marginBottom: 0 }}>
                            Project: {task.projectName}
                        </p>
                    </section>
                ))}
            </div>
        </main>
    );
}