# syntax=docker/dockerfile:1

# the build output is platform independent, build once on the native platform for every target platform
FROM --platform=$BUILDPLATFORM node:22-alpine AS build
WORKDIR /app
COPY package.json package-lock.json .npmrc ./
RUN npm ci --no-audit --no-fund
COPY . .
RUN npm run build

# nginx running as the unprivileged 'nginx' user (uid 101) on port 8080
FROM nginxinc/nginx-unprivileged:1.29-alpine
ENV ENV_FILE=/etc/rhc/.env \
    OUTPUT_FILE=/usr/share/nginx/html/env-config.js
# keys and defaults of the runtime configuration; override them with environment variables (e.g. API_URL)
COPY .env /etc/rhc/.env
COPY nginx/default.conf /etc/nginx/conf.d/default.conf
# the nginx entrypoint runs /docker-entrypoint.d/*.sh before starting nginx, env.sh writes env-config.js
COPY --chmod=755 env.sh /docker-entrypoint.d/40-env-config.sh
# env.sh replaces env-config.js, so the nginx user needs to own the web root
COPY --from=build --chown=101:101 /app/dist /usr/share/nginx/html
USER 0
# tolerate CRLF checkouts on Windows
RUN sed -i 's/\r$//' /docker-entrypoint.d/40-env-config.sh /etc/rhc/.env \
    && chown 101:101 /usr/share/nginx/html
USER 101
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=3s CMD ["wget", "-q", "-O", "/dev/null", "http://127.0.0.1:8080/healthz"]
