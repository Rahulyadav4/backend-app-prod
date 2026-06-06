# Scalable Spring Boot Microservice | Kubernetes + Monitoring

![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=flat&logo=springboot&logoColor=white)
![Kubernetes](https://img.shields.io/badge/Kubernetes-326CE5?style=flat&logo=kubernetes&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=flat&logo=docker&logoColor=white)
![Prometheus](https://img.shields.io/badge/Prometheus-E6522C?style=flat&logo=prometheus&logoColor=white)
![Grafana](https://img.shields.io/badge/Grafana-F46800?style=flat&logo=grafana&logoColor=white)

A production-style backend project demonstrating Spring Boot microservices deployed on Kubernetes, with autoscaling, monitoring via Prometheus, and observability using Grafana.

---

## 📖 Overview

This project simulates a real-world backend system where:

- REST APIs handle user requests
- Application runs inside Docker containers
- Deployed on a Kubernetes cluster
- Scaled dynamically using HPA (Horizontal Pod Autoscaler)
- Monitored using Prometheus & Grafana

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Backend | Spring Boot (Java) |
| Database | MongoDB |
| Caching | Redis |
| Containerization | Docker |
| Orchestration | Kubernetes |
| Monitoring | Prometheus, Grafana |
| Metrics | Micrometer + Actuator |
| CI | GitHub Actions |
| Testing | Postman |

---

## Features

- 🔐 JWT-based authentication & authorization
- 🌐 REST APIs with layered architecture (`Controller → Service → Repository`)
- ⚡ Redis for optimized read performance
- 🐳 Dockerized application
- ☸️ Kubernetes deployment with pods and services
- 📈 Horizontal Pod Autoscaler (HPA) for load-based scaling
- 🩺 Health checks via readiness & liveness probes
- 📊 Metrics exposed via Prometheus
- 📉 Monitoring dashboards in Grafana

---

## System Architecture

---

## End-to-End Flow

1. **Request** — User sends a request via Postman or API client
2. **Entry** — Request enters the cluster via Service / Ingress
3. **Routing** — Kubernetes routes the request to an available pod
4. **Processing** — Spring Boot app processes the incoming request
5. **Cache Check** — Request checked against Redis cache:
   - **Cache Hit** → Response served directly from Redis
   - **Cache Miss** → Request forwarded to MongoDB
6. **Persistence** — Data stored/retrieved from MongoDB
7. **Metrics** — Micrometer generates application metrics
8. **Scraping** — Prometheus scrapes metrics from the app
9. **Visualization** — Grafana visualizes metrics on dashboards



