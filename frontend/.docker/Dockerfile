# frontend/.docker/Dockerfile
FROM node:18-alpine AS builder

WORKDIR /app

COPY . .

RUN npm ci --prefer-offline && \
    npm run build

FROM nginx:alpine

COPY .docker/nginx/nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=builder /app/dist /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]