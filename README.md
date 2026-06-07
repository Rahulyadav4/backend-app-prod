# Task Manager Microservice

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



