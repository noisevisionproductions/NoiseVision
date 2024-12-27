FROM node:18-alpine as build

WORKDIR /app

COPY ../package.json .
COPY ../package-lock.json .
COPY ../tsconfig.json .
COPY ../vite.config.ts .
COPY ../index.html .
COPY ../postcss.config.js .
COPY ../tailwind.config.js .

RUN npm ci

COPY ../public ./public/
COPY ../src ./src/

RUN npm run build

FROM nginx:alpine

COPY --from=build /app/dist /usr/share/nginx/html
COPY ../../nginx/conf.d/default.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]