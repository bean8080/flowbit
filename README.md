# 🐰 Flowbit

> **작업의 현재 상태뿐 아니라, 상태 변화 이벤트를 기록하여  
> 일이 어떤 흐름으로 진행되었는지 추적하는 작업 관리 시스템**

🔗 **Live Demo**: [https://flowbit.kr](https://flowbit.kr)  
📝 **Development Log**: [Flowbit Velog Series](https://velog.io/@bean8080/series/flowbit)

Flowbit은 단순한 Task 관리 도구가 아니라,  
`Task`의 현재 상태와 `TaskEvent`의 변경 이력을 분리하여  
작업이 어떤 과정을 거쳐 현재 상태가 되었는지 확인할 수 있도록 만든 프로젝트입니다.

---

## Why Flowbit?

일반적인 작업 관리 시스템은 현재 상태를 보여주는 데 집중합니다.

예를 들어 어떤 작업이 `DONE` 상태라면  
완료되었다는 사실은 알 수 있지만,  
그 작업이 언제 시작되었고, 중간에 막혔는지, 어떤 과정을 거쳐 완료되었는지는 알기 어렵습니다.

Flowbit은 이 문제를 해결하기 위해  
상태 변화 순간을 이벤트로 기록하고,  
그 이벤트들을 시간순으로 조회할 수 있도록 설계했습니다.

```text
Task       = 현재 상태
TaskEvent  = 상태 변화 이력
```

---

## Core Concept

```text
TODO → IN_PROGRESS → BLOCKED → IN_PROGRESS → DONE
```

Flowbit은 최종 상태인 `DONE`만 저장하는 것이 아니라,  
위와 같은 상태 변화 과정을 `TaskEvent`로 기록합니다.

이를 통해 다음 질문에 답할 수 있습니다.

- 이 작업은 언제 시작되었는가?
- 언제 완료되었는가?
- 중간에 막힌 적이 있었는가?
- 프로젝트 전체 작업은 어떤 흐름으로 진행되었는가?

---

## Features

### Project

- 프로젝트 생성 / 조회 / 수정 / 삭제
- 프로젝트별 Task 관리
- 프로젝트 단위 Timeline 조회
- 프로젝트 단위 Analysis 조회

### Task

- Task 생성 / 조회 / 수정 / 삭제
- 상태 변경
    - `TODO`
    - `IN_PROGRESS`
    - `DONE`
    - `BLOCKED`
    - `DELETED`
- Task 단위 이벤트 이력 조회
- Task 단위 Timeline 조회

### Timeline

- Task 상태 변화 이벤트를 시간순으로 조회
- Project에 속한 모든 TaskEvent를 통합 Timeline으로 조회

![타임라인 화면](img/timeline.png)

### Analysis

- 상태별 Task 개수 집계
- 전체 이벤트 수 집계
- 마지막 활동 시점 조회
- Spring Cache 기반 분석 결과 캐싱 구조 적용

---

## Architecture

![아키텍처 구조](img/architecture.png)

```text
Project
 └── Task
      └── TaskEvent
```

### 주요 설계 방향

- 현재 상태와 이벤트 이력 분리
- 상태 변경 시 TaskEvent 자동 생성
- Soft Delete로 이력 보존
- 트랜잭션으로 상태 변경과 이벤트 기록의 정합성 보장
- Spring Cache 기반 Analysis API 캐싱 구조 적용
- Docker Compose 기반 개발/배포 환경 구성
- AWS EC2 환경에 실도메인 배포

---

## Key Design Decisions

### 1. 현재 상태와 이벤트 이력 분리

`Task`는 현재 상태를 가지고,  
`TaskEvent`는 상태 변화 이력을 기록합니다.

이를 통해 현재 상태 조회는 단순하게 유지하면서도,  
상태가 변화해 온 흐름을 추적할 수 있습니다.

### 2. 순수 Event Sourcing이 아닌 이벤트 기반 이력 추적 구조

Flowbit은 모든 상태를 이벤트 재생만으로 복원하는 순수 Event Sourcing 구조는 아닙니다.

현재 구현에서는 `Task`가 현재 상태를 직접 가지고,  
`TaskEvent`가 변경 이력을 보존합니다.

MVP 단계에서는 조회 단순성과 구현 안정성을 우선했고,  
이후 Snapshot, Replay, Audit Log 구조로 확장할 수 있도록 설계했습니다.

### 3. 트랜잭션 정합성 보장

상태 변경과 이벤트 기록은 의미적으로 하나의 작업입니다.

따라서 다음 작업들은 하나의 트랜잭션 안에서 처리합니다.

- Task 생성과 TaskEvent 생성
- Task 상태 변경과 TaskEvent 기록
- Project 삭제와 하위 Task 삭제

```text
상태 변경과 이벤트 기록은 함께 성공하거나 함께 실패해야 한다.
```

### 4. Soft Delete

삭제도 작업 흐름의 일부라고 보고,  
데이터를 즉시 삭제하지 않고 상태로 관리합니다.

- `TaskStatus.DELETED`
- `ProjectStatus.DELETED`

### 5. Cache Strategy

Project Analysis API는 이벤트 데이터를 기반으로 집계 결과를 계산합니다.

반복 조회 비용을 줄이기 위해 Spring Cache를 적용했고,  
향후 Redis 기반 분산 캐싱으로 확장할 예정입니다.

---

## API

### Project API

```http
POST   /api/projects
GET    /api/projects
GET    /api/projects/{id}
PATCH  /api/projects/{id}
PATCH  /api/projects/{id}/delete

GET    /api/projects/{id}/timeline
GET    /api/projects/{id}/analysis
```

### Task API

```http
POST   /api/tasks
GET    /api/tasks
GET    /api/tasks/{id}
PATCH  /api/tasks/{id}

PATCH  /api/tasks/{id}/start
PATCH  /api/tasks/{id}/complete
PATCH  /api/tasks/{id}/block
PATCH  /api/tasks/{id}/delete

GET    /api/tasks/{id}/events
GET    /api/tasks/{id}/timeline
GET    /api/tasks/{id}/latest-status
```

---

## Tech Stack

### Backend

- Java 21
- Spring Boot
- Spring Data JPA
- Spring Cache
- Spring Security
- PostgreSQL
- Redis

### Frontend

- React
- TypeScript
- Vite
- Axios

### Infra

- Docker
- Docker Compose
- AWS EC2
- AWS Elastic IP
- Gabia DNS
- Nginx

### Test

- JUnit
- Spring Boot Test
- Spring Security Test
- HTTP Client 테스트 파일

---

## Deployment

Flowbit은 AWS EC2 환경에 배포되어 실제 도메인에서 접근할 수 있습니다.

- Live Demo: [https://flowbit.kr](https://flowbit.kr)

```text
flowbit.kr
 ↓
Gabia DNS
 ↓
AWS Elastic IP
 ↓
EC2
 ↓
Nginx
 ↓
React
 ↓
Spring Boot
 ↓
PostgreSQL / Redis
```

초기 MVP 단계에서는 비용과 운영 복잡도를 줄이기 위해  
하나의 EC2 인스턴스 안에서 Docker Compose 기반으로 구성했습니다.

향후에는 PostgreSQL을 RDS로, Redis를 ElastiCache로 분리하고  
GitHub Actions 기반 CI/CD와 HTTPS를 적용할 예정입니다.

---

## Getting Started

### 1. Infrastructure

```bash
docker-compose up -d
```

### 2. Backend

```bash
./gradlew bootRun
```

### 3. Frontend

```bash
cd flowbit-frontend
npm install
npm run dev
```

---

## Development Log

개발 과정과 설계 고민은 Velog에 기록하고 있습니다.

- [Flowbit 개발기 전체 시리즈](https://velog.io/@bean8080/series/flowbit)
- [#11. AWS EC2와 Docker Compose로 실도메인 배포하기](https://velog.io/@bean8080/flowbit-11.-AWS-EC2%EC%99%80-Docker-Compose%EB%A1%9C-%EC%8B%A4%EB%8F%84%EB%A9%94%EC%9D%B8-%EB%B0%B0%ED%8F%AC%ED%95%98%EA%B8%B0)
- [#12. 상태 변경과 이벤트 기록을 하나의 트랜잭션으로 묶기](https://velog.io/@bean8080/flowbit-12.-%EC%83%81%ED%83%9C-%EB%B3%80%EA%B2%BD%EA%B3%BC-%EC%9D%B4%EB%B2%A4%ED%8A%B8-%EA%B8%B0%EB%A1%9D%EC%9D%84-%ED%95%98%EB%82%98%EC%9D%98-%ED%8A%B8%EB%9E%9C%EC%9E%AD%EC%85%98%EC%9C%BC%EB%A1%9C-%EB%AC%B6%EA%B8%B0)

---

## Future Improvements

- User 도메인 추가
- Spring Security / JWT 기반 인증 구조 정리
- TaskEvent에 actorId 추가
- Workspace / WorkspaceMember 기반 협업 구조
- Workspace 단위 권한 관리
- Timeline UI 고도화
- 시간 기반 리포트 기능
- Redis 기반 분산 캐싱 검증
- GitHub Actions 기반 CI/CD 구축
- HTTPS 적용
- 운영/개발 환경 분리

---

## Summary

Flowbit은 단순한 Task 관리 시스템이 아니라,  
작업의 상태가 아닌 흐름을 관리하는 시스템입니다.

현재 상태는 결과일 뿐이고,  
Flowbit은 그 결과가 만들어진 과정을 기록합니다.
