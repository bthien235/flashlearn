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
