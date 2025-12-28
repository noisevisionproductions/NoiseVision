# Noise Vision (Tech Playground)

**Noise Vision** is a full-stack experimental project designed as a Research & Development playground.

Its primary purpose is to explore and implement advanced libraries, architectural patterns, and integrations within the
**Java 21 / Spring Boot** ecosystem, paired with a modern **React** frontend. It serves as a proof-of-concept for
handling complex flows like event streaming, geolocation, and distributed caching.

## 🧪 Key Experiments & Features

This project implements various "technical toys" and concepts:

* **🌍 Geolocation Services:** Integration with **MaxMind GeoIP2** to resolve user locations based on IP addresses.
* **📨 Event-Driven Architecture:** Implementation of **Apache Kafka** producers and consumers for asynchronous
  communication (e.g., User Registration Events).
* **🚀 High-Performance Caching:** Usage of **Redis** for caching data and managing session state.
* **🔐 Modern Security:** Custom implementation of **JWT (JSON Web Tokens)** authentication and authorization with Spring
  Security.
* **📊 Interactive UI:** A dashboard built with **React & Recharts** to visualize data, styled with **Tailwind CSS** and
  **Shadcn/UI**.
* **🐳 Containerization:** Full Docker support with multi-stage builds for both Backend (Java 21) and Frontend.

## 🛠️ Tech Stack

The project utilizes a cutting-edge stack, focusing on performance and developer experience.

### Backend (API)

* **Core:** Java 21, Spring Boot 3.4.0
* **Database:** PostgreSQL (Production), H2 (Testing)
* **Messaging & Cache:** Apache Kafka, Redis
* **Validation & Docs:** Hibernate Validator, SpringDoc OpenAPI (Swagger)
* **Testing:** JUnit 5, Mockito, Testcontainers
* **Build Tool:** Maven

### Frontend (SPA)

* **Framework:** React 18, TypeScript
* **Build Tool:** Vite
* **Styling:** Tailwind CSS, Radix UI (Shadcn), Lucide Icons
* **State & Utils:** Lodash, Date-fns, Axios
* **Testing:** Vitest, React Testing Library

### DevOps & CI/CD

* **Docker:** Optimized multi-stage Dockerfiles
* **Cloud:** Google Cloud Build (`cloudbuild.yaml`)
* **Server:** Nginx

## 📂 Project Structure

The backend follows a modular structure based on feature domains:

* `src/main/java/org/noisevisionproductions/noisevision/`
  * `auth/` - Authentication logic & controllers.
  * `geolp/` - Geolocation services implementation.
  * `kafka/` - Kafka producers, consumers, and event DTOs.
  * `cache/` - Redis configuration and caching strategies.
  * `security/` - JWT filters and security config.

## 🚀 Getting Started

### Prerequisites

* Java 21 SDK
* Node.js 20+
* Docker & Docker Compose (Recommended)

### Running with Docker (Easiest)

Since the project relies on external services like Redis, Kafka, and PostgreSQL, using Docker Compose is recommended.
```bash
# Start all services (Database, Kafka, Redis, Backend, Frontend)
docker-compose up -d --build
```

### Running Locally (Manual)

1. **Backend:** Ensure you have a local PostgreSQL, Redis, and Kafka instance running, or update `application-dev.yml` to point to valid services.
```bash
# Run tests
./mvnw test

# Start application
./mvnw spring-boot:run
``` 

2. **Frontend:**

```bash
cd frontend

# Install dependencies
npm install

# Start development server
npm run dev
```

## 📄 License

This project is created for educational and experimental purposes. All rights reserved.