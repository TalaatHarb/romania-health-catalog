# Kubernetes deployment

Manifests for running the Romania Health Catalog on a cluster with the
[ingress-nginx](https://kubernetes.github.io/ingress-nginx/) controller and
[cert-manager](https://cert-manager.io/) (a `letsencrypt-prod` ClusterIssuer).

| URL | Component |
| --- | --- |
| https://rhc.talaatharb.net | Frontend (React SPA served by nginx) |
| https://rhc-api.talaatharb.net/backend | Backend API (Spring Boot), e.g. `/backend/api/v1/versions` |

Everything runs in the `rhc` namespace:

| File | Resources |
| --- | --- |
| `00-namespace.yaml` | Namespace `rhc` |
| `secrets-example.yaml` | Example `rhc-db-secret` (database name/user/password) and `rhc-upload-secret` (`UPLOAD_SECRET`) |
| `postgres/storage.yaml` | StorageClass + hostPath PersistentVolume (`/data/rhc/postgres` on one node) |
| `postgres/statefulset.yaml` | PostgreSQL 17 StatefulSet + headless Service `rhc-postgres` |
| `backend/*` | ConfigMap, Deployment and Service `rhc-be` (port 8080) |
| `frontend/*` | ConfigMap (`API_URL`), Deployment and Service `rhc-fe` (port 80) |
| `ingress.yaml` | Ingresses for both hosts with Let's Encrypt certificates |
| `kustomization.yaml` | Everything except the secrets |

## Deploying

1. Point the DNS records `rhc.talaatharb.net` and `rhc-api.talaatharb.net` at the ingress controller.
2. Choose the node that keeps the database: put its name (`kubectl get nodes`) in `postgres/storage.yaml` instead of
   `CHANGE-ME-node-name`. The data is stored in `/data/rhc/postgres` on that node.
3. Create the secrets (`secrets.yaml` is git-ignored):
   ```shell
   kubectl apply -f infrastructure/k8s/00-namespace.yaml
   cp infrastructure/k8s/secrets-example.yaml infrastructure/k8s/secrets.yaml   # then replace every value
   kubectl apply -f infrastructure/k8s/secrets.yaml
   ```
   Or create them without a file:
   ```shell
   kubectl -n rhc create secret generic rhc-db-secret --from-literal=POSTGRES_DB=health_catalog \
     --from-literal=POSTGRES_USER=health_catalog --from-literal=POSTGRES_PASSWORD='<password>'
   kubectl -n rhc create secret generic rhc-upload-secret --from-literal=UPLOAD_SECRET='<upload secret>'
   ```
4. Apply the rest:
   ```shell
   kubectl apply -k infrastructure/k8s
   kubectl -n rhc get pods,ingress,certificate
   ```

If `POSTGRES_DB` isn't `health_catalog`, update `DB_URL` in `backend/configmap.yaml` too. The database user and
password are only applied when postgres initialises an empty data directory.

## Images

The deployments use `ghcr.io/talaatharb/romania-health-catalog-be:latest` and
`ghcr.io/talaatharb/romania-health-catalog-fe:latest`. The release workflow (`.github/workflows/release.yml`)
publishes them for every `v*` tag. For a fixed release, pin a version tag (e.g. `:1.2.0`) and set
`imagePullPolicy: IfNotPresent`.

- **Backend:** `romania-health-catalog-be/Dockerfile` builds the Spring Boot app, which listens on port 8080.
- **Frontend:** `romania-health-catalog-fe/Dockerfile` serves the Vite build with nginx on port 8080. Before nginx
  starts, `env.sh` writes `env-config.js` from the `API_URL` environment variable (set in `frontend/configmap.yaml`).

## CORS

The frontend and the API are on different hosts, so the browser sends CORS requests, including a preflight for
uploads because of the `uploadSecret` header.

- The backend answers them itself and only allows `https://rhc.talaatharb.net`. This comes from
  `CORS_ALLOWED_ORIGINS` in `backend/configmap.yaml`, which takes a comma-separated list and patterns.
- The ingress deliberately doesn't enable nginx's CORS annotations. Both would add `Access-Control-Allow-Origin`,
  and browsers reject duplicated headers.
- Responses produced by nginx itself (413, 502, 504) carry no CORS headers, so the browser reports them as CORS
  errors. For that reason the API ingress allows 50 MB request bodies (the backend's upload limit) and
  10-minute timeouts for big imports.

## Notes

- The backend reserves and is limited to 8 GiB of container memory, with a 6 GiB Java heap
  (`JAVA_TOOL_OPTIONS` in `backend/configmap.yaml`). The remaining 2 GiB is for native JVM memory and other
  container overhead. The node needs enough allocatable memory for this request alongside PostgreSQL and
  the other pods; otherwise the backend stays Pending. Rolling updates can temporarily need two backend pods.
  Imports still hold the uncompressed XML and parsed objects in memory, so larger or concurrent imports can
  exceed this budget. Heap exhaustion exits the JVM so Kubernetes can restart it.
  To apply the memory change without rebuilding the image:
  ```shell
  kubectl apply -k infrastructure/k8s
  kubectl -n rhc rollout restart deployment rhc-be
  kubectl -n rhc rollout status deployment rhc-be
  ```
- The backend starts with the `postgres` profile, and Hibernate creates or updates the schema. Keep one backend
  replica, so only one instance changes the schema at a time.
- Only the `health` and `info` actuator endpoints are exposed. The probes use
  `/backend/actuator/health/liveness` and `/backend/actuator/health/readiness`.
- The FE upload dialog is pre-filled with the development default `UPLOAD_SECREET`. Type the real upload
  secret from `rhc-upload-secret` there.
- The dialog also accepts a catalog URL. The backend downloads it and returns the imported version in the
  same request. `CATALOG_IMPORT_ALLOWED_HOSTS` in the backend ConfigMap restricts downloads and redirects to
  `www.casmb.ro,www.cnas.ro`; add any other trusted source hosts explicitly. Downloads require outbound HTTPS.
- ConfigMaps are read only when a container starts. After changing `frontend/configmap.yaml`, run
  `kubectl -n rhc rollout restart deployment rhc-fe`; for `backend/configmap.yaml`, restart `rhc-be`.
