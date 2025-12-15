# Build stage
FROM node:18-alpine AS builder
WORKDIR /app
COPY package.json package-lock.json* ./
COPY . .
RUN npm ci --silent && npm run build

# Production stage
FROM nginx:stable-alpine
COPY --from=builder /app/dist /usr/share/nginx/html
# Copy custom nginx config to proxy API calls to host microservices
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
