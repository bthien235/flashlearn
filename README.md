# flashlearn

## Branch strategy

- `main`: production branch, no direct push.
- `develop`: integration branch for team.
- `feature/*`: feature branches created from `develop`.

Frontend members should create one branch per screen/feature:

- `feature/fe/login-screen`
- `feature/fe/dashboard-screen`
- `feature/fe/profile-screen`

## Git flow for members

```bash
git checkout develop
git pull origin develop
git checkout -b feature/fe/<screen-name>
# code frontend
git add .
git commit -m "feat(fe): <short-description>"
git push -u origin feature/fe/<screen-name>
```

Then create Pull Request:

- `feature/fe/*` -> `develop`
- `develop` -> `main` when ready for release

## CI/CD overview

- Backend CI/CD: `.github/workflows/ci.yml`
  - Runs on backend changes.
  - Ignores `frontend/**`.
  - Builds and tests backend for `develop` and `main`.
  - Builds and pushes backend Docker image on push to `main`.
  - Deploys to VPS over SSH on push to `main`.

- Frontend CI: `.github/workflows/frontend-ci.yml`
  - Runs only when files in `frontend/**` change.
  - Installs dependencies, lint, test, and build frontend.

## GitHub settings (manual)

Configure branch protection rules in GitHub:

1. Protect `main`:
   - Require a pull request before merging.
   - Require approvals (at least 1).
   - Require status checks to pass.
   - Disable direct pushes.
2. Protect `develop`:
   - Require a pull request before merging.
   - Require approvals (at least 1).
   - Require status checks to pass.
   - Disable direct pushes.

## VPS auto deploy setup

Pipeline deploy file: `docker-compose.prod.yml`.

1. On VPS, create app folder and copy deploy files:
   - `/opt/flashlearn/docker-compose.prod.yml`
   - `/opt/flashlearn/.env` (production values)
2. In VPS `.env`, set:
   - `APP_IMAGE=ghcr.io/<your-org-or-user>/flashlearn-backend:latest`
   - `DB_*`, `JWT_*`, `SPRING_PROFILES_ACTIVE=prod`, `APP_PORT`
3. Add GitHub repository secrets:
   - `VPS_HOST`: VPS IP/domain
   - `VPS_PORT`: SSH port (use `22` if default SSH)
   - `VPS_USER`: SSH username
   - `VPS_SSH_KEY`: private SSH key (PEM)
   - `VPS_APP_DIR`: deploy directory on VPS (example `/opt/flashlearn`)
   - `GHCR_USERNAME`: GitHub username/org that owns image
   - `GHCR_TOKEN`: GitHub token with `read:packages`

After that, every push to `main` will:
- test backend
- build + push image to GHCR
- SSH to VPS and run `docker compose -f docker-compose.prod.yml pull app && docker compose -f docker-compose.prod.yml up -d`
