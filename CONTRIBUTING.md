# Contributing to ShieldHer

## Workflow
1. Pick an issue (e.g. SH-03) and say in the issue that you are taking it.
2. Branch from an up-to-date main:

        git switch main
        git pull
        git switch -c feature/SH-03-sos-api

   Branch names: feature/SH-<n>-<short-name>, fix/SH-<n>-<short-name>, docs/<short-name>.
3. Commit small and often with a prefix: feat:, fix:, docs:, chore:, test:, ci:
   Example: feat(SH-03): add POST /api/alerts
4. Push and open a Pull Request into main. Fill in the PR template.
5. CI (GitHub Actions) must be green.
6. One approval from your pair partner is required before merging. Never approve your own PR.
7. Merge with "Squash and merge", then delete the branch.

## Rules
- No direct pushes to main.
- Never commit .env or any secret. Use .env.example for sample values.
- Change docs/API_CONTRACT.md in a PR BEFORE changing an endpoint or JSON shape.
- Keep PRs small: one issue per PR.

## Pair split and swap review (full-stack features)
One person owns the API (controller, service, repository, tests). The other owns the UI (HTML, JS, CSS).
Agree the JSON in docs/API_CONTRACT.md first, then code in parallel.
When both parts are done, swap: the API owner reviews the UI PR and the UI owner reviews the API PR,
and each runs the other's branch locally before approving.

## Review checklist
- Matches docs/API_CONTRACT.md
- Errors and results are shown honestly in the UI (no fake success)
- No sensitive data in logs (phone numbers, location, passwords, tokens)
- It runs locally and CI is green