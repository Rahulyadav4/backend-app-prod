<div align="center">

# Task Manager Microservice

### Spring Boot service — containerized, orchestrated, cached, circuit-broken, and monitored

[![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=openjdk)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-6DB33F?style=flat-square&logo=springboot)](https://spring.io/projects/spring-boot)
[![MongoDB](https://img.shields.io/badge/MongoDB-7.x-47A248?style=flat-square&logo=mongodb)](https://www.mongodb.com/)
[![Redis](https://img.shields.io/badge/Redis-7.x-DC382D?style=flat-square&logo=redis)](https://redis.io/)
[![Kubernetes](https://img.shields.io/badge/Kubernetes-Minikube-326CE5?style=flat-square&logo=kubernetes)](https://minikube.sigs.k8s.io/)
[![Prometheus](https://img.shields.io/badge/Prometheus-Scraping-E6522C?style=flat-square&logo=prometheus)](https://prometheus.io/)
[![Grafana](https://img.shields.io/badge/Grafana-Dashboards-F46800?style=flat-square&logo=grafana)](https://grafana.com/)

</div>

---

## What This Is

A backend project built to simulate a real-world production environment on a local machine.
It goes beyond a basic CRUD API — every layer has an impacting engineering decision behind it.

The service manages tasks via a secured REST API. Under the hood it demonstrates the patterns
backend engineers apply at scale: distributed caching, fault isolation, horizontal scaling,
container-aware resource management, and a full metrics pipeline.
(**ongoing kafka addon to this)

---

## System Architecture

                      ┌─────────────────────────────────────────┐
                      │            Kubernetes Cluster            │
                      │                                          │
Client / Postman │ ┌──────────────────────────────────┐ │ │ │ │ springboot-service │ │ │ HTTP Request │ │ (NodePort) │ │ └──────────────────┼──►│ │ │ │ └────────────┬─────────────────────┘ │ │ │ routes to │ │ ┌────────────▼─────────────────────┐ │ │ │ HPA (2–5 Pods) │ │ │ │ │ │ │ │ ┌─────────┐ ┌─────────┐ │ │ │ │ │ Pod 1 │ │ Pod 2 │ ... │ │ │ │ │Spring │ │Spring │ │ │ │ │ │Boot App │ │Boot App │ │ │ │ │ └────┬────┘ └────┬────┘ │ │ │ └───────┼─────────────┼───────────┘ │ │ │ shared │ │ │ ┌───────▼──────────────▼───────────┐ │ │ │ Redis Cache │ │ │ │ (shared across pods) │ │ │ └───────────────────┬──────────────┘ │ │ │ cache miss │ │ ┌───────────────────▼──────────────┐ │ │ │ MongoDB │ │ │ └──────────────────────────────────┘ │ │ │ │ ┌──────────────────────────────────┐ │ │ │ /actuator/prometheus → │ │ │ │ Prometheus → Grafana │ │ │ └──────────────────────────────────┘ │ └─────────────────────────────────────────┘


---

## Request Lifecycle

| Step | What Happens |
|---|---|
| 1 | Client sends HTTP request to the NodePort service |
| 2 | Kubernetes routes request to one of 2–5 running pods |
| 3 | `JwtAuthFilter` intercepts — validates Bearer token or rejects with 401 |
| 4 | Controller delegates to `TaskService` (no business logic in controller) |
| 5 | `TaskService` checks shared Redis cache — returns in ~2–5ms on hit |
| 6 | On cache miss, Resilience4j circuit breaker checks MongoDB health |
| 7 | If circuit is closed, `TaskRepository` queries MongoDB (~8–28ms total) |
| 8 | Result written to Redis (10-min TTL) — next read is a cache hit |
| 9 | Micrometer records latency, hit/miss counts, circuit breaker state |
| 10 | Prometheus scrapes `/actuator/prometheus` every 10s — Grafana visualizes |

---

## Tech Stack

| Layer | Technology | Purpose |
|---|---|---|
| Language | Java 21 | LTS release, virtual thread ready |
| Framework | Spring Boot 3.x | REST, DI, AOP, Actuator |
| Database | MongoDB | Document store, Spring Data repository |
| Cache | Redis + Lettuce | Shared distributed cache, JSON serialized |
| Auth | JWT (HMAC-SHA) | Stateless authentication, 1hr expiry |
| Rate Limiting | Bucket4j | Per-IP token bucket, 10 req/min |
| Resilience | Resilience4j | Circuit breaker on MongoDB read path |
| Containerization | Docker | Reproducible image, container-aware JVM |
| Orchestration | Kubernetes (Minikube) | Pod lifecycle, services, config |
| Autoscaling | HPA | 2–5 replicas at 70% CPU threshold |
| Health Checks | Spring Actuator Probes | Liveness (JVM) + Readiness (dependencies) |
| Metrics | Micrometer + Actuator | JVM, HTTP, circuit breaker metrics |
| Monitoring | Prometheus + Grafana | Scrape pipeline + visualization |
| CI | GitHub Actions | Build and image pipeline |
| Testing | Postman | API contract verification |

---

## Core Features

**Security**
- JWT authentication on all `/tasks/*` endpoints
- HMAC-SHA signing — key loaded once at JVM startup
- Per-IP rate limiting on login — 10 requests/min via Bucket4j

**Caching**
- Redis cache shared across all pods — a cache hit on Pod 1 is a hit on Pod 2
- `@CacheEvict` on every update and delete — no stale data served
- 10-minute TTL with JSON serialization via `GenericJackson2JsonRedisSerializer`

**Resilience**
- Resilience4j `mongoBreaker` circuit breaker on the read path
- MongoDB connection timeout: 3s connect / 5s socket — threads released on failure
- 2-pod minimum ensures zero downtime on single pod failure

**Scaling**
- HPA scales 2 → 5 pods at 70% average CPU
- Stateless pods — no sticky sessions, any pod handles any request
- New pods join the load balancer only after passing the readiness probe

**Observability**
- Liveness probe: JVM health only — avoids restarts on transient dependency failure
- Readiness probe: checks MongoDB + Redis — removes unhealthy pod from load balancer
- Full Prometheus scrape pipeline via `ServiceMonitor`
- Grafana dashboards for JVM, HTTP latency, throughput, circuit breaker state

# Task Manager Microservice - Detailed metrics

A production-grade REST API built with Spring Boot, deployed on Kubernetes (Minikube), demonstrating
distributed caching, circuit breaking, horizontal pod autoscaling, and observability.

---

## What This Project Demonstrates

| Concern | Implementation |
|---|---|
| API Design | RESTful CRUD with JWT authentication |
| Caching | Shared Redis cache across all pods — 70–80% cache hit rate |
| Resilience | Resilience4j circuit breaker on MongoDB read path |
| Scalability | Kubernetes HPA — 2 to 5 replicas at 70% CPU |
| Observability | Prometheus + Grafana via ServiceMonitor scraping |
| Security | HMAC-SHA JWT, per-IP rate limiting (Bucket4j) |
| Container Awareness | JVM tuned with `-XX:+UseContainerSupport` to prevent OOMKill |

---

## Architecture

Client │ ▼ NodePort Service (Kubernetes) │ ├── Pod 1 ──┐ ├── Pod 2 ──┤──► Shared Redis Cache ──► MongoDB ├── Pod 3 ──┤ ├── Pod 4 ──┤ └── Pod 5 ──┘ │ ▼ Prometheus Scrape (/actuator/prometheus) │ ▼ Grafana


**Classification:**

| Layer | Choice |
|---|---|
| Pattern | Monolithic REST service with Kubernetes orchestration |
| Caching | Distributed (Redis shared across pods) |
| Resilience | Circuit breaker + multi-pod redundancy |
| Data Layer | Single node (MongoDB, Redis — acceptable for local/POC) |
| Observability | Metrics (Prometheus + Grafana) |
| Overall Tier | Mid-tier production / strong proof of concept |

---

## Performance Characteristics

### Request Latency (p50 estimates)

| Operation | Latency |
|---|---|
| GET — cache hit | ~2–5ms |
| GET — cache miss | ~8–28ms |
| POST create | ~6–24ms |
| PUT update | ~8–27ms (includes cache eviction) |
| DELETE | ~8–27ms (includes cache eviction) |
| POST /auth/login | ~2–5ms |

### Concurrency

| Scenario | Per Pod | 5 Replicas |
|---|---|---|
| Cache hit path | ~200–400 | ~1,000–2,000 |
| Cache miss path | ~50–100 | ~250–500 |
| Tomcat thread pool | 200 threads | 1,000 threads total |

### Throughput

| Scenario | Estimated |
|---|---|
| Read-heavy, cache-warm, 5 replicas | ~3,000–5,000 req/s |
| Mixed read/write with cache misses | ~500–1,500 req/s |
| Write-heavy | ~200–500 req/s |

---

## Scalability — Before vs After

| Metric | Before | After |
|---|---|---|
| Max concurrent requests | 200 (1 pod) | 1,000 (5 pods × 200 threads) |
| Throughput | ~500 req/s | ~2,000–2,500 req/s |
| Cross-pod cache hit rate | ~0% | ~70–80% |
| MongoDB read load | 100% of requests | ~20–30% (cache absorbs rest) |
| Pod crash recovery | ~30s full downtime | 0s — other pod takes over |

---

## Availability

| Component | Replicas | Pod Failure Impact |
|---|---|---|
| Spring Boot pods | 2 minimum (HPA) | One pod fails — no downtime |
| MongoDB | 1 | Full write downtime |
| Redis | 1 | Cache cold start, MongoDB overload risk |
| Circuit breaker (read) | — | Returns 503 fast, protects thread pool |
| Circuit breaker (writes) | Not covered | Hangs until MongoDB connection timeout |

**Probe design:**
- Liveness → checks JVM only — avoids restarting pods on transient dependency failures
- Readiness → checks MongoDB + Redis — removes pod from load balancer until dependencies recover

---

## Request Flow

### Authentication
POST /auth/login → Rate limit check (10 req/min per IP, per pod) → Credential validation (env-configured) → JWT generation (HMAC-SHA, 1hr expiry) → 200 OK with token


### Read Task
GET /tasks/{id} → JwtAuthFilter validates Bearer token → Redis cache lookup (key: task::<id>) ├── HIT → return immediately (~2–5ms) └── MISS → circuit breaker check ├── OPEN → 503 (fast fail) └── CLOSED → MongoDB findById → cache populate (TTL 10min) → return task (~8–28ms)


### Write Task
POST /tasks → JWT → MongoDB save (no cache interaction) PUT /tasks/{id} → JWT → cache evict → MongoDB upsert DELETE /tasks/{id} → JWT → cache evict → MongoDB deleteById

---

## Key Design Decisions

| Decision | Rationale |
|---|---|
| Shared Redis cache | Eliminates cross-pod cache miss problem — a hit on Pod 1 is a hit on Pod 2 |
| `@CacheEvict` on update/delete | Prevents stale data — cache is always consistent with MongoDB |
| Circuit breaker on read path only | Write paths are less frequent; read path protects the thread pool from MongoDB slowdowns |
| Liveness ≠ Readiness probe | Decouples JVM health from dependency health — avoids cascading restarts |
| Per-IP rate limiting (Bucket4j) | Protects login endpoint; acknowledged gap — not distributed across pods |
| `-XX:+UseContainerSupport` | JVM respects container memory limits — prevents OOMKill in Kubernetes |
| NodePort over Ingress | Correct for Minikube — Ingress requires extra addon and adds complexity without local benefit |

---

## Tech Stack

| Component | Technology |
|---|---|
| Runtime | Java 21 (eclipse-temurin:21-jdk) |
| Framework | Spring Boot |
| Database | MongoDB |
| Cache | Redis (Lettuce client, JSON serialization) |
| Auth | JWT (HMAC-SHA via jjwt) |
| Rate Limiting | Bucket4j (in-memory) |
| Resilience | Resilience4j circuit breaker |
| Orchestration | Kubernetes (Minikube) |
| Autoscaling | HorizontalPodAutoscaler |
| Metrics | Micrometer + Prometheus + Grafana |



