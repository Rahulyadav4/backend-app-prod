# TaskManager — Real-Time Kafka-Driven Task Pipeline on Kubernetes

A production-grade Spring Boot microservice that ingests real-time data via Kafka, persists tasks to MongoDB, and serves them through a REST API backed by Redis caching — all running on Kubernetes with circuit breaking, autoscaling, and Prometheus monitoring.

---

## Architecture Overview

Real-Time System │ ▼ CSV Text File ←── Raw data written every few milliseconds │ ▼ Kafka Producer ←── Reads CSV rows, publishes to topic │ ▼ Kafka Consumer ←── Parses fields, maps to Task document │ ▼ MongoDB ←── Persists / upserts task records │ ▼ Spring Boot API ←── REST layer with Redis cache + circuit breaker │ ▼ Kubernetes ←── Deployment, HPA, health probes, secrets


---

## Key Features

- **Real-Time Ingestion** — External system writes CSV data to a file every few milliseconds; a Kafka producer tails and publishes each record to a topic
- **Kafka Consumer → MongoDB** — Consumer parses incoming messages by configured field mapping and upserts task documents into MongoDB
- **Redis Caching** — `@Cacheable` on reads, `@CacheEvict` on writes; cache misses fall through to MongoDB
- **Circuit Breaker** — Resilience4j wraps MongoDB calls; returns `503 Service Unavailable` during outages instead of cascading failures
- **Kubernetes-Native** — Deployments, Services, HPA (CPU-based autoscaling), liveness/readiness probes, and secrets managed via K8s Secrets
- **Observability** — Spring Actuator + Micrometer + Prometheus + ServiceMonitor for metrics scraping
- **Security** — JWT authentication; all secrets injected from Kubernetes Secrets (never hardcoded)

---

## Tech Stack

| Layer | Technology |
|---|---|
| Runtime | Java 21, Spring Boot 3.2 |
| Messaging | Apache Kafka |
| Primary DB | MongoDB 6 |
| Cache | Redis 7 |
| Resilience | Resilience4j Circuit Breaker |
| Container | Docker (eclipse-temurin:21-jdk) |
| Orchestration | Kubernetes / Minikube |
| Autoscaling | HorizontalPodAutoscaler (2–5 replicas) |
| Monitoring | Prometheus, Micrometer, Spring Actuator |
| Auth | JWT (jjwt 0.11.5) |
| Build | Maven |

---

## Data Flow — Kafka Pipeline

1. A real-time source system appends rows to a `.csv` file at sub-second intervals
2. The **Kafka Producer** watches the file, reads new lines, and publishes each row to a configured Kafka topic
3. The **Kafka Consumer** subscribes to the topic, parses fields (id, title, description, status), and upserts records into MongoDB
4. Updates arrive continuously — stale Redis cache entries are evicted on every write

---

## REST API

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/tasks/control` | Health check |
| `POST` | `/tasks` | Create a task |
| `GET` | `/tasks/{id}` | Get task by ID (cached) |
| `PUT` | `/tasks/{id}` | Update task (evicts cache) |
| `DELETE` | `/tasks/{id}` | Delete task (evicts cache) |

---

## Running Locally (Docker Compose)

```bash
# Build the JAR first
mvn clean package -DskipTests

# Start MongoDB, Redis, and the API
docker-compose up --build
API available at http://localhost:8080

Running on Minikube
# Enable metrics server for HPA
minikube addons enable metrics-server

# Apply secrets first
kubectl apply -f k8s/secrets.yaml

# Deploy MongoDB, Redis, Spring Boot app
kubectl apply -f k8s/mongodb-deployment.yaml
kubectl apply -f k8s/redis-deployment.yaml
kubectl apply -f k8s/springboot-deployment.yaml

# Apply autoscaler
kubectl apply -f k8s/hpa.yaml

# Access the service
minikube service springboot-service
Configuration
All sensitive values are managed via Kubernetes Secrets — never committed to source:

Variable	Description
JWT_SECRET	JWT signing key (min 32 chars)
APP_USERNAME	Basic auth username
APP_PASSWORD	Basic auth password
REDIS_HOST	Redis hostname
Observability
Actuator endpoints: /actuator/health/liveness, /actuator/health/readiness
Prometheus metrics: /actuator/prometheus
ServiceMonitor configured for scrape interval of 10s
Liveness probe: starts after 180s, checks every 10s
Readiness probe: starts after 120s, checks Mongo + Redis
Project Structure
├── src/main/java/com/taskmanager/
│   ├── model/          # Task document model
│   ├── controller/     # REST endpoints
│   ├── service/        # Business logic, caching, circuit breaker
│   └── repository/     # MongoDB repository
├── k8s/                # Kubernetes manifests
├── docker-compose.yml
├── Dockerfile
└── pom.xml
Notes
JVM is configured with UseContainerSupport and MaxRAMPercentage=75.0 for container-aware memory management
HPA is kept to a max of 5 replicas given Minikube's local resource constraints
spring-boot-starter-data-jpa and H2 are scoped to test only — not included in the production image

A few things worth noting about the project itself:

**One security flag** — your `kubeconfig` file (the one with `client-certificate-data` and `client-key-data`) was included in the pasted content. Those are TLS credentials for your Minikube cluster. Since this is a local Minikube setup it's low risk, but avoid committing that file to your repo. Add it to `.gitignore`.

**Kafka setup** — the README describes the Kafka pipeline based on your description, but I didn't see Kafka-specific code (producer, consumer, topic config) in what you shared. If that code exists, let me know and I can update the README with the actual class names and topic configuration details.
