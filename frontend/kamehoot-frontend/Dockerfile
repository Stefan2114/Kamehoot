# Stage 1: Build the React app
FROM node:18-alpine AS build

WORKDIR /app

COPY package*.json ./
RUN npm install

COPY . .
RUN npm run build

# Stage 2: Serve the built files with a lightweight server
FROM nginx:alpine

# Copy built static files from build stage
COPY --from=build /app/dist /usr/share/nginx/html

# Copy custom nginx config if you want (optional)
# COPY nginx.conf /etc/nginx/nginx.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]

