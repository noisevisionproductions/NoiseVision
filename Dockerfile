# Stage 1: Build frontend
FROM node:18-alpine AS frontend-builder
WORKDIR /app/frontend
COPY frontend/package*.json ./
RUN npm install
COPY frontend/ ./
RUN npm run build

# Stage 2: Build backend
FROM eclipse-temurin:21-jdk-alpine AS backend-builder
WORKDIR /app/backend
COPY backend/pom.xml ./
COPY backend/src ./src
RUN apk add --no-cache maven && \
    mvn package -DskipTests

# Stage 3: Final image
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Kopiowanie zbudowanego frontendu
COPY --from=frontend-builder /app/frontend/dist /app/static

# Kopiowanie zbudowanego backendu
COPY --from=backend-builder /app/backend/target/*.jar app.jar

# Port dla App Engine
ENV PORT=8080
EXPOSE ${PORT}

# Uruchomienie aplikacji
CMD ["sh", "-c", "java -Dserver.port=${PORT} -jar app.jar"]