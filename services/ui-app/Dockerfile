FROM node:20-alpine AS build
WORKDIR /app
COPY services/ui-app/package*.json ./
RUN npm install --no-audit --no-fund
COPY services/ui-app/ ./
RUN npm run build:prod

FROM nginx:1.27-alpine
COPY services/ui-app/nginx/default.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist/eventflowx-ui-app/browser /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
