<div align="center">

# ⚡ TaskManager — Real-Time Kafka Pipeline on Kubernetes

### A production-grade Spring Boot microservice that ingests live CSV data via Kafka,
### persists to MongoDB, and serves through a Redis-cached REST API — fully Kubernetes-native.

![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2-6DB33F?style=for-the-badge&logo=springboot)
![Kafka](https://img.shields.io/badge/Apache_Kafka-231F20?style=for-the-badge&logo=apachekafka)
![MongoDB](https://img.shields.io/badge/MongoDB-6-47A248?style=for-the-badge&logo=mongodb)
![Redis](https://img.shields.io/badge/Redis-7-DC382D?style=for-the-badge&logo=redis)
![Kubernetes](https://img.shields.io/badge/Kubernetes-326CE5?style=for-the-badge&logo=kubernetes)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker)
![Prometheus](https://img.shields.io/badge/Prometheus-E6522C?style=for-the-badge&logo=prometheus)

</div>

---

## 🏗️ Architecture

┌─────────────────────────────────────────────────────────────────┐ │ REAL-TIME DATA PIPELINE │ │ │ │ 🖥️ Real-Time System │ │ │ writes rows every few milliseconds │ │ ▼ │ │ 📄 CSV Text File │ │ │ tailed by producer │ │ ▼ │ │ 📨 Kafka Producer ──────► Kafka Topic │ │ │ │ │ ▼ │ │ 📥 Kafka Consumer │ │ │ field-mapped upserts │ │ ▼ │ │ 🍃 MongoDB ◄──── Spring Boot API │ │ │ │ │ 🔴 Redis Cache ──┘ │ │ │ │ ☸️ Kubernetes │ 📈 HPA Autoscaling │ 📊 Prometheus │ └─────────────────────────────────────────────────────────────────┘


---

## ✨ Features

| | Feature | Detail |
|---|---|---|
| ⚡ | **Real-Time Kafka Ingestion** | External system writes CSV every few ms → Kafka producer publishes → consumer upserts into MongoDB |
| 🔴 | **Redis Caching** | `@Cacheable` on reads, `@CacheEvict` on updates & deletes — cache always stays fresh |
| 🔌 | **Circuit Breaker** | Resilience4j wraps MongoDB calls — returns `503` during outages instead of cascading failure |
| ☸️ | **Kubernetes Native** | Deployments, Services, HPA (2–5 replicas), liveness & readiness probes |
| 🔐 | **Zero Hardcoded Secrets** | JWT + credentials injected exclusively via Kubernetes Secrets |
| 📊 | **Full Observability** | Actuator + Micrometer + Prometheus + ServiceMonitor (10s scrape interval) |
| 🐳 | **Container-Aware JVM** | `UseContainerSupport` + `MaxRAMPercentage=75.0` for optimal memory inside Docker |

---

## 🔄 Kafka Data Pipeline — How It Works

Real-time source system appends rows to a .csv file └─► format: id, title, description, status

Kafka Producer watches the file └─► reads new lines every few milliseconds └─► publishes each row to a Kafka topic

Kafka Consumer subscribes to the topic └─► parses fields by configured mapping └─► upserts Task documents into MongoDB (MongoDb is the target db)

Redis cache evicted on every write └─► GET requests always reflect latest data


---

## 🛠️ Tech Stack

<div align="center">

| Layer | Technology |
|---|---|
| 🖥️ Runtime | Java 21 · Spring Boot 3.2 |
| 📨 Messaging | Apache Kafka |
| 🍃 Primary DB | MongoDB 6 |
| 🔴 Cache | Redis 7 |
| 🔌 Resilience | Resilience4j Circuit Breaker |
| 🐳 Container | Docker · eclipse-temurin:21-jdk |
| ☸️ Orchestration | Kubernetes · Minikube |
| 📈 Autoscaling | HorizontalPodAutoscaler |
| 📊 Monitoring | Prometheus · Micrometer · Actuator |
| 🔐 Auth | JWT (jjwt 0.11.5) |
| 🔨 Build | Maven |

</div>

---

## 📡 REST API

Base URL: http://localhost:8080


| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/tasks/control` | 🟢 Health check |
| `POST` | `/tasks` | ➕ Create a task |
| `GET` | `/tasks/{id}` | 🔍 Get task by ID *(Redis cached)* |
| `PUT` | `/tasks/{id}` | ✏️ Update task *(evicts cache)* |
| `DELETE` | `/tasks/{id}` | 🗑️ Delete task *(evicts cache)* |

---

## 🚀 Quick Start

### ▶️ Local — Docker Compose

```bash
# 1. Build the JAR
mvn clean package -DskipTests

# 2. Spin up MongoDB + Redis + API
docker-compose up --build
API live at → http://localhost:8080

☸️ Kubernetes — Minikube
# Enable metrics server (required for HPA)
minikube addons enable metrics-server

# Apply secrets first — never skip this
kubectl apply -f k8s/secrets.yaml

# Deploy infrastructure
kubectl apply -f k8s/mongodb-deployment.yaml
kubectl apply -f k8s/redis-deployment.yaml
kubectl apply -f k8s/springboot-deployment.yaml

# Enable autoscaling
kubectl apply -f k8s/hpa.yaml

# Open in browser
minikube service springboot-service
🔐 Secrets Configuration
All sensitive values live in Kubernetes Secrets — never in source code.

Variable	Description
JWT_SECRET	JWT signing key (min 32 chars)
APP_USERNAME	Basic auth username
APP_PASSWORD	Basic auth password
REDIS_HOST	Redis service hostname
# Apply to cluster
kubectl apply -f k8s/secrets.yaml
📊 Observability
/actuator/health/liveness   ──►  Liveness probe  (starts @ 180s, every 10s)
/actuator/health/readiness  ──►  Readiness probe (starts @ 120s, every 5s)
/actuator/prometheus        ──►  Prometheus metrics scrape endpoint
ServiceMonitor scrapes every 10 seconds — plug straight into Grafana for dashboards.

📁 Project Structure
taskmanager/
├── 📂 src/main/java/com/taskmanager/
│   ├── 📂 model/           # Task document (MongoDB + Redis Serializable)
│   ├── 📂 controller/      # REST endpoints
│   ├── 📂 service/         # Caching · Circuit Breaker · Business logic
│   └── 📂 repository/      # MongoDB repository
├── 📂 k8s/
│   ├── springboot-deployment.yaml
│   ├── mongodb-deployment.yaml
│   ├── redis-deployment.yaml
│   ├── hpa.yaml
│   ├── secrets.yaml
│   └── service-monitor.yaml
├── 🐳 Dockerfile
├── 🐳 docker-compose.yml
└── 🔨 pom.xml
⚙️ Autoscaling Policy
minReplicas: 2
maxReplicas: 5
targetCPUUtilizationPercentage: 70
Kept at max 5 replicas — tuned for Minikube's local resource constraints.

```
---

<div align="center">

**Built with Java 21 · Spring Boot · Kafka · MongoDB · Redis · Kubernetes**

*Real-time ingestion · Resilient caching · Production-ready observability*

</div>
`
