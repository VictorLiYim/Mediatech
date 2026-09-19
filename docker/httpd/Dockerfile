# Contexte de build attendu : la racine du repo (pour accéder à frontend/).
# docker build -f docker/httpd/Dockerfile .

# --- Étape 1 : build du frontend (Vue + Vite) ---
FROM node:22-alpine AS frontend-build
WORKDIR /app
COPY frontend/package.json frontend/package-lock.json* ./
RUN npm install
COPY frontend .
RUN npm run build

# --- Étape 2 : httpd sert les fichiers statiques + reverse proxy vers les 3 services ---
FROM httpd:2.4-alpine
COPY --from=frontend-build /app/dist/ /usr/local/apache2/htdocs/
COPY docker/httpd/httpd.conf /usr/local/apache2/conf/httpd.conf
