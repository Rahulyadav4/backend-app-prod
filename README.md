Scalable Spring Boot Microservice | Kubernetes + Monitoring
A production style backend project demonstrating Spring Boot microservices deployed on Kubernetes, with autoscaling, monitoring (Prometheus), and observability using Grafana.

Overview
This project simulates a real world backend system where:
 REST APIs handle user requests
 Application runs inside Docker containers
 Deployed on Kubernetes cluster (Minikube)
 Scaled dynamically using HPA
 Monitored using Prometheus & Grafana

 Tech Stack
 Backend: Spring Boot (Java)
 Database: MongoDB
 Containerization: Docker
 Orchestration: Kubernetes (Minikube)
 Monitoring: Prometheus, Grafana
 Metrics: Micrometer + Actuator
 CI: GitHub Actions
 Testing: Postman

 Features
  JWT based authentication & authorization  
  REST APIs with layered architecture (Controller → Service → Repository)  
  Dockerized application  
  Kubernetes deployment with pods, services  
  Horizontal Pod Autoscaler (HPA) for load based scaling  
  Health checks using readiness & liveness probes  
  Metrics exposed via prometheus 
  Monitoring dashboards in Grafana  

System Architecture
User → Ingress → Kubernetes Service → Pod (Spring Boot)
     → MongoDB
     → Metrics → Prometheus → Grafana

End to End Flow
User sends request (Postman / API client)
Request enters cluster via Service/Ingress
Kubernetes routes request to one of the pods
Spring Boot app processes request
Data stored/retrieved from MongoDB
Metrics generated via Micrometer
Prometheus scrapes metrics
Grafana visualizes metrics

