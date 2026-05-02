import { useEffect, useState, type CSSProperties } from "react";
import {
    getTasks,
    createTask,
    updateTask,
    startTask,
    completeTask,
    blockTask,
    deleteTask,
    type Task,
} from "../api/taskApi";
import {
    getProjects,
    createProject,
    updateProject,
    deleteProject,
    type Project,
} from "../api/projectApi";

const statusLabelMap: Record<string, string> = {
    TODO: "대기",
    IN_PROGRESS: "진행중",
    DONE: "완료",
    BLOCKED: "보류",
};

const projectStatusLabelMap: Record<string, string> = {
    READY: "준비중",
    IN_PROGRESS: "진행중",
    DONE: "완료",
    DELETED: "삭제됨",
};

const statusStyleMap: Record<string, CSSProperties> = {
    TODO: { background: "#eef2ff", color: "#4338ca" },
    IN_PROGRESS: { background: "#dbeafe", color: "#1d4ed8" },
    DONE: { background: "#dcfce7", color: "#15803d" },
    BLOCKED: { background: "#fef3c7", color: "#b45309" },
};

const styles: Record<string, CSSProperties> = {
    page: {
        minHeight: "100vh",
        background:
            "radial-gradient(circle at top left, #dbeafe 0, transparent 34%), linear-gradient(180deg, #f8fafc 0%, #eef2ff 100%)",
        color: "#0f172a",
        padding: "52px 24px",
    },
    container: { maxWidth: "1120px", margin: "0 auto" },
    hero: { marginBottom: "28px" },
    title: {
        fontSize: "64px",
        lineHeight: 1,
        margin: 0,
        color: "#0f172a",
        fontWeight: 900,
        letterSpacing: "-0.06em",
    },
    subtitle: {
        color: "#475569",
        marginTop: "12px",
        marginBottom: "28px",
        fontSize: "19px",
        fontWeight: 500,
    },
    loadingText: {
        marginTop: "10px",
        color: "#2563eb",
        fontWeight: 800,
        fontSize: "14px",
    },
    toolbar: {
        display: "grid",
        gridTemplateColumns: "1fr 1.4fr auto",
        gap: "12px",
        padding: "18px",
        border: "1px solid #dbe3f0",
        borderRadius: "24px",
        background: "rgba(255, 255, 255, 0.86)",
        boxShadow: "0 18px 45px rgba(15, 23, 42, 0.08)",
    },
    input: {
        padding: "13px 15px",
        borderRadius: "14px",
        border: "1px solid #cbd5e1",
        background: "#ffffff",
        color: "#0f172a",
        outline: "none",
        fontSize: "14px",
    },
    primaryButton: {
        padding: "11px 15px",
        borderRadius: "14px",
        border: "1px solid #2563eb",
        background: "#2563eb",
        color: "#ffffff",
        cursor: "pointer",
        fontWeight: 800,
    },
    ghostButton: {
        padding: "9px 12px",
        borderRadius: "13px",
        border: "1px solid #cbd5e1",
        background: "#f8fafc",
        color: "#334155",
        cursor: "pointer",
        fontWeight: 800,
    },
    dangerButton: {
        padding: "9px 12px",
        borderRadius: "13px",
        border: "1px solid #fecaca",
        background: "#fff1f2",
        color: "#be123c",
        cursor: "pointer",
        fontWeight: 800,
    },
    projectGrid: { display: "grid", gap: "28px" },
    projectSection: {
        padding: "26px",
        border: "1px solid #d7e0ee",
        borderRadius: "30px",
        background: "rgba(255, 255, 255, 0.92)",
        boxShadow: "0 22px 55px rgba(15, 23, 42, 0.09)",
    },
    projectHeader: {
        display: "flex",
        justifyContent: "space-between",
        alignItems: "flex-start",
        gap: "18px",
        marginBottom: "20px",
    },
    projectName: {
        fontSize: "32px",
        margin: 0,
        color: "#0f172a",
        fontWeight: 900,
        letterSpacing: "-0.04em",
    },
    projectDesc: { color: "#475569", margin: "8px 0 0", fontSize: "15px" },
    projectActions: { display: "flex", gap: "8px", flexWrap: "wrap" },
    projectDeleteButton: {
        padding: "9px 12px",
        borderRadius: "13px",
        border: "1px solid #ef4444",
        background: "#fee2e2",
        color: "#b91c1c",
        cursor: "pointer",
        fontWeight: 900,
    },
    editBox: { display: "grid", gap: "10px", flex: 1 },
    editActions: { display: "flex", gap: "8px", flexWrap: "wrap" },
    taskForm: {
        display: "grid",
        gridTemplateColumns: "1fr 1.4fr auto",
        gap: "10px",
        marginBottom: "18px",
    },
    taskList: {
        display: "grid",
        gridTemplateColumns: "repeat(auto-fit, minmax(280px, 1fr))",
        gap: "14px",
    },
    taskCard: {
        padding: "18px",
        border: "1px solid #e2e8f0",
        borderRadius: "22px",
        background: "linear-gradient(180deg, #ffffff 0%, #f8fafc 100%)",
        boxShadow: "0 10px 24px rgba(15, 23, 42, 0.05)",
    },
    taskTop: {
        display: "flex",
        justifyContent: "space-between",
        alignItems: "flex-start",
        gap: "12px",
    },
    taskTitle: {
        margin: 0,
        fontSize: "18px",
        color: "#0f172a",
        fontWeight: 900,
        letterSpacing: "-0.02em",
    },
    taskDescription: {
        margin: "10px 0 0",
        color: "#64748b",
        fontSize: "14px",
        lineHeight: 1.5,
    },
    badge: {
        padding: "6px 10px",
        borderRadius: "999px",
        fontSize: "13px",
        fontWeight: 900,
        whiteSpace: "nowrap",
    },
    actionRow: {
        display: "flex",
        gap: "8px",
        marginTop: "16px",
        flexWrap: "wrap",
    },
    startButton: {
        padding: "8px 11px",
        borderRadius: "12px",
        border: "1px solid #bfdbfe",
        background: "#eff6ff",
        color: "#1d4ed8",
        cursor: "pointer",
        fontWeight: 800,
    },
    doneButton: {
        padding: "8px 11px",
        borderRadius: "12px",
        border: "1px solid #bbf7d0",
        background: "#f0fdf4",
        color: "#15803d",
        cursor: "pointer",
        fontWeight: 800,
    },
    holdButton: {
        padding: "8px 11px",
        borderRadius: "12px",
        border: "1px solid #fde68a",
        background: "#fffbeb",
        color: "#b45309",
        cursor: "pointer",
        fontWeight: 800,
    },
    empty: {
        color: "#94a3b8",
        padding: "14px 0",
        fontWeight: 700,
    },
};

const getProjectDisplayName = (project: Project) => {
    return project.name === "DEFAULT" ? "기본 프로젝트" : project.name;
};

const getErrorMessage = (error: unknown) => {
    if (typeof error === "object" && error !== null && "response" in error) {
        const axiosError = error as {
            response?: {
                data?: {
                    message?: string;
                };
            };
        };

        return axiosError.response?.data?.message ?? "요청 처리 중 오류가 발생했습니다.";
    }

    return "요청 처리 중 오류가 발생했습니다.";
};

export default function TaskList() {
    const [projects, setProjects] = useState<Project[]>([]);
    const [tasks, setTasks] = useState<Task[]>([]);
    const [loading, setLoading] = useState(false);

    const [newProjectName, setNewProjectName] = useState("");
    const [newProjectDescription, setNewProjectDescription] = useState("");
    const [taskTitles, setTaskTitles] = useState<Record<number, string>>({});
    const [taskDescriptions, setTaskDescriptions] = useState<Record<number, string>>({});

    const [editingProjectId, setEditingProjectId] = useState<number | null>(null);
    const [editProjectName, setEditProjectName] = useState("");
    const [editProjectDescription, setEditProjectDescription] = useState("");

    const [editingTaskId, setEditingTaskId] = useState<number | null>(null);
    const [editTaskTitle, setEditTaskTitle] = useState("");
    const [editTaskDescription, setEditTaskDescription] = useState("");

    const refreshWorkspace = async () => {
        const [projectResponse, taskResponse] = await Promise.all([
            getProjects(),
            getTasks(),
        ]);

        setProjects(projectResponse.data);
        setTasks(taskResponse.data);
    };

    const runRequest = async (request: () => Promise<unknown>) => {
        if (loading) return;

        try {
            setLoading(true);
            await request();
            await refreshWorkspace();
        } catch (error) {
            alert(getErrorMessage(error));
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        runRequest(refreshWorkspace);
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    const handleCreateProject = () => {
        if (!newProjectName.trim()) {
            alert("프로젝트 이름을 입력해줘");
            return;
        }

        runRequest(async () => {
            await createProject({
                name: newProjectName,
                description: newProjectDescription,
            });

            setNewProjectName("");
            setNewProjectDescription("");
        });
    };

    const handleEditProject = (project: Project) => {
        if (project.name === "DEFAULT" || loading) return;

        setEditingProjectId(project.id);
        setEditProjectName(project.name);
        setEditProjectDescription(project.description ?? "");
    };

    const handleCancelEditProject = () => {
        if (loading) return;

        setEditingProjectId(null);
        setEditProjectName("");
        setEditProjectDescription("");
    };

    const handleSaveProject = (projectId: number) => {
        if (!editProjectName.trim()) {
            alert("프로젝트 이름을 입력해줘");
            return;
        }

        runRequest(async () => {
            await updateProject(projectId, {
                name: editProjectName,
                description: editProjectDescription,
            });

            handleCancelEditProject();
        });
    };

    const handleDeleteProject = (project: Project) => {
        if (project.name === "DEFAULT" || loading) return;
        if (!confirm("프로젝트를 삭제할까요?")) return;

        runRequest(() => deleteProject(project.id));
    };

    const handleChangeTaskTitle = (projectId: number, value: string) => {
        setTaskTitles((prev) => ({ ...prev, [projectId]: value }));
    };

    const handleChangeTaskDescription = (projectId: number, value: string) => {
        setTaskDescriptions((prev) => ({ ...prev, [projectId]: value }));
    };

    const handleCreateTask = (projectId: number) => {
        const title = taskTitles[projectId];
        const description = taskDescriptions[projectId] ?? "";

        if (!title || !title.trim()) {
            alert("작업 제목을 입력해줘");
            return;
        }

        runRequest(async () => {
            await createTask({
                projectId,
                title,
                description,
                assigneeId: null,
                priority: 1,
            });

            setTaskTitles((prev) => ({ ...prev, [projectId]: "" }));
            setTaskDescriptions((prev) => ({ ...prev, [projectId]: "" }));
        });
    };

    const handleEditTask = (task: Task) => {
        if (loading) return;

        setEditingTaskId(task.id);
        setEditTaskTitle(task.title);
        setEditTaskDescription(task.description ?? "");
    };

    const handleCancelEditTask = () => {
        if (loading) return;

        setEditingTaskId(null);
        setEditTaskTitle("");
        setEditTaskDescription("");
    };

    const handleSaveTask = (taskId: number) => {
        if (!editTaskTitle.trim()) {
            alert("작업 제목을 입력해줘");
            return;
        }

        runRequest(async () => {
            await updateTask(taskId, {
                title: editTaskTitle,
                description: editTaskDescription,
            });

            handleCancelEditTask();
        });
    };

    const handleStart = (id: number) => runRequest(() => startTask(id));
    const handleComplete = (id: number) => runRequest(() => completeTask(id));
    const handleBlock = (id: number) => runRequest(() => blockTask(id));

    const handleDeleteTask = (id: number) => {
        if (loading) return;
        if (!confirm("작업을 삭제할까요?")) return;

        runRequest(() => deleteTask(id));
    };

    return (
        <main style={styles.page}>
            <div style={styles.container}>
                <header style={styles.hero}>
                    <h1 style={styles.title}>Flowbit</h1>
                    <p style={styles.subtitle}>
                        작업의 상태 변화 흐름을 기록하고 보여주는 프로젝트 작업공간
                    </p>

                    {loading && <p style={styles.loadingText}>처리 중입니다...</p>}

                    <div style={styles.toolbar}>
                        <input
                            style={styles.input}
                            value={newProjectName}
                            onChange={(e) => setNewProjectName(e.target.value)}
                            placeholder="새 프로젝트 이름"
                            disabled={loading}
                        />
                        <input
                            style={styles.input}
                            value={newProjectDescription}
                            onChange={(e) => setNewProjectDescription(e.target.value)}
                            placeholder="프로젝트 설명"
                            disabled={loading}
                        />
                        <button
                            style={styles.primaryButton}
                            onClick={handleCreateProject}
                            disabled={loading}
                        >
                            {loading ? "처리 중..." : "+ 프로젝트 생성"}
                        </button>
                    </div>
                </header>

                <div style={styles.projectGrid}>
                    {projects.map((project) => {
                        const projectTasks = tasks.filter(
                            (task) => task.projectId === project.id
                        );

                        const isEditing = editingProjectId === project.id;
                        const isDefaultProject = project.name === "DEFAULT";
                        const projectDisplayName = getProjectDisplayName(project);

                        return (
                            <section key={project.id} style={styles.projectSection}>
                                <div style={styles.projectHeader}>
                                    {isEditing ? (
                                        <div style={styles.editBox}>
                                            <input
                                                style={styles.input}
                                                value={editProjectName}
                                                onChange={(e) => setEditProjectName(e.target.value)}
                                                placeholder="프로젝트 이름"
                                                disabled={loading}
                                            />
                                            <input
                                                style={styles.input}
                                                value={editProjectDescription}
                                                onChange={(e) =>
                                                    setEditProjectDescription(e.target.value)
                                                }
                                                placeholder="프로젝트 설명"
                                                disabled={loading}
                                            />
                                            <div style={styles.editActions}>
                                                <button
                                                    style={styles.primaryButton}
                                                    onClick={() => handleSaveProject(project.id)}
                                                    disabled={loading}
                                                >
                                                    {loading ? "저장 중..." : "저장"}
                                                </button>
                                                <button
                                                    style={styles.ghostButton}
                                                    onClick={handleCancelEditProject}
                                                    disabled={loading}
                                                >
                                                    취소
                                                </button>
                                            </div>
                                        </div>
                                    ) : (
                                        <div>
                                            <h2 style={styles.projectName}>{projectDisplayName}</h2>
                                            <p style={styles.projectDesc}>
                                                {project.description || "설명 없음"} ·{" "}
                                                {projectStatusLabelMap[project.status] ??
                                                    project.status}
                                            </p>
                                        </div>
                                    )}

                                    {!isEditing && !isDefaultProject && (
                                        <div style={styles.projectActions}>
                                            <button
                                                style={styles.ghostButton}
                                                onClick={() => handleEditProject(project)}
                                                disabled={loading}
                                            >
                                                수정
                                            </button>
                                            <button
                                                style={styles.projectDeleteButton}
                                                onClick={() => handleDeleteProject(project)}
                                                disabled={loading}
                                            >
                                                프로젝트 삭제
                                            </button>
                                        </div>
                                    )}
                                </div>

                                <div style={styles.taskForm}>
                                    <input
                                        style={styles.input}
                                        value={taskTitles[project.id] ?? ""}
                                        onChange={(e) =>
                                            handleChangeTaskTitle(project.id, e.target.value)
                                        }
                                        placeholder={`${projectDisplayName}에 작업 추가`}
                                        disabled={loading}
                                    />
                                    <input
                                        style={styles.input}
                                        value={taskDescriptions[project.id] ?? ""}
                                        onChange={(e) =>
                                            handleChangeTaskDescription(project.id, e.target.value)
                                        }
                                        placeholder="작업 설명"
                                        disabled={loading}
                                    />
                                    <button
                                        style={styles.primaryButton}
                                        onClick={() => handleCreateTask(project.id)}
                                        disabled={loading}
                                    >
                                        {loading ? "추가 중..." : "+ 작업 추가"}
                                    </button>
                                </div>

                                {projectTasks.length === 0 ? (
                                    <p style={styles.empty}>첫 작업을 추가해보세요 🚀</p>
                                ) : (
                                    <div style={styles.taskList}>
                                        {projectTasks.map((task) => {
                                            const isEditingTask = editingTaskId === task.id;

                                            return (
                                                <article key={task.id} style={styles.taskCard}>
                                                    {isEditingTask ? (
                                                        <>
                                                            <input
                                                                style={styles.input}
                                                                value={editTaskTitle}
                                                                onChange={(e) =>
                                                                    setEditTaskTitle(e.target.value)
                                                                }
                                                                placeholder="작업 제목"
                                                                disabled={loading}
                                                            />
                                                            <input
                                                                style={{
                                                                    ...styles.input,
                                                                    marginTop: "10px",
                                                                }}
                                                                value={editTaskDescription}
                                                                onChange={(e) =>
                                                                    setEditTaskDescription(
                                                                        e.target.value
                                                                    )
                                                                }
                                                                placeholder="작업 설명"
                                                                disabled={loading}
                                                            />
                                                            <div style={styles.actionRow}>
                                                                <button
                                                                    style={styles.primaryButton}
                                                                    onClick={() =>
                                                                        handleSaveTask(task.id)
                                                                    }
                                                                    disabled={loading}
                                                                >
                                                                    {loading ? "저장 중..." : "저장"}
                                                                </button>
                                                                <button
                                                                    style={styles.ghostButton}
                                                                    onClick={handleCancelEditTask}
                                                                    disabled={loading}
                                                                >
                                                                    취소
                                                                </button>
                                                            </div>
                                                        </>
                                                    ) : (
                                                        <>
                                                            <div style={styles.taskTop}>
                                                                <h3 style={styles.taskTitle}>
                                                                    {task.title}
                                                                </h3>
                                                                <span
                                                                    style={{
                                                                        ...styles.badge,
                                                                        ...(statusStyleMap[
                                                                            task.status
                                                                            ] ?? {
                                                                            background: "#f1f5f9",
                                                                            color: "#334155",
                                                                        }),
                                                                    }}
                                                                >
                                                                    {statusLabelMap[task.status] ??
                                                                        task.status}
                                                                </span>
                                                            </div>

                                                            <p style={styles.taskDescription}>
                                                                {task.description || "설명 없음"}
                                                            </p>

                                                            <div style={styles.actionRow}>
                                                                <button
                                                                    style={styles.startButton}
                                                                    onClick={() =>
                                                                        handleStart(task.id)
                                                                    }
                                                                    disabled={loading}
                                                                >
                                                                    시작
                                                                </button>
                                                                <button
                                                                    style={styles.doneButton}
                                                                    onClick={() =>
                                                                        handleComplete(task.id)
                                                                    }
                                                                    disabled={loading}
                                                                >
                                                                    완료
                                                                </button>
                                                                <button
                                                                    style={styles.holdButton}
                                                                    onClick={() =>
                                                                        handleBlock(task.id)
                                                                    }
                                                                    disabled={loading}
                                                                >
                                                                    보류
                                                                </button>
                                                                <button
                                                                    style={styles.ghostButton}
                                                                    onClick={() =>
                                                                        handleEditTask(task)
                                                                    }
                                                                    disabled={loading}
                                                                >
                                                                    수정
                                                                </button>
                                                                <button
                                                                    style={styles.dangerButton}
                                                                    onClick={() =>
                                                                        handleDeleteTask(task.id)
                                                                    }
                                                                    disabled={loading}
                                                                >
                                                                    삭제
                                                                </button>
                                                            </div>
                                                        </>
                                                    )}
                                                </article>
                                            );
                                        })}
                                    </div>
                                )}
                            </section>
                        );
                    })}
                </div>
            </div>
        </main>
    );
}