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
    actorId: number | null;
    actorName: string | null;
    actorEmail: string | null;
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

export type ProjectSnapshot = {
    projectId: number;
    projectName: string;
    snapshotAt: string;
    totalTasks: number;
    todoCount: number;
    inProgressCount: number;
    blockedCount: number;
    doneCount: number;
    deletedCount: number;
    tasks: TaskSnapshot[];
};

export type TaskSnapshot = {
    taskId: number;
    title: string;
    status: string;
    lastEventAt: string;
    lastActorId: number | null;
    lastActorName: string | null;
    lastActorEmail: string | null;
};