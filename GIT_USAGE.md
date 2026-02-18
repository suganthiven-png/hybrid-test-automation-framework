# Git & GitHub Usage Guide for hybrid-test-automation-framework

This document explains common Git commands and workflows to move the project between local and GitHub, how to create branches, push/pull changes, and handle merges.

Initial setup (one-time)
1. Create a GitHub repository (on github.com) for this project.
2. On your local machine, inside the project folder:
```cmd
cd C:\Users\raman\eclipse-workspace\testNgframeworkwithAIV1
git init
git add .
git commit -m "Initial commit"
```
3. Add remote and push:
```cmd
git remote add origin https://github.com/<your-username>/<repo-name>.git
git branch -M main
git push -u origin main
```

Common daily workflows
- Create a new feature branch:
```cmd
git checkout -b feature/your-feature-name
```
- Work locally, then stage and commit:
```cmd
git add path/to/changedfile
git commit -m "Short but descriptive message"
```
- Push branch to remote:
```cmd
git push -u origin feature/your-feature-name
```
- Create a Pull Request on GitHub from the pushed branch into `main`.

Pulling updates from remote
- Switch to main and pull remote changes:
```cmd
git checkout main
git pull origin main
```
- Rebase your feature branch onto updated main (optional workflow):
```cmd
git checkout feature/your-feature-name
git fetch origin
git rebase origin/main
# resolve conflicts if any, then
git rebase --continue
# push (force if rebased)
git push --force-with-lease origin feature/your-feature-name
```

Merging a PR locally (merge commit)
```cmd
git checkout main
git pull origin main
git merge --no-ff feature/your-feature-name
git push origin main
```

Resolving merge conflicts (basic)
1. When conflict occurs during merge or rebase, open conflicting files, look for conflict markers (<<<<<<, ======, >>>>>>), resolve manually.
2. Stage resolved files: `git add <file>`
3. Continue merge/rebase:
- For merge: `git commit` (if necessary)
- For rebase: `git rebase --continue`

Undoing changes (safely)
- Unstage a file:
```cmd
git reset HEAD <file>
```
- Discard local changes to a file:
```cmd
git checkout -- <file>
```
- Reset branch to remote state (dangerous - use with caution):
```cmd
git fetch origin
git reset --hard origin/main
```

Tagging a release
```cmd
git tag -a v1.0.0 -m "Release 1.0.0"
git push origin v1.0.0
```

Working with remotes
- List remotes:
```cmd
git remote -v
```
- Add a remote:
```cmd
git remote add upstream https://github.com/some/org/repo.git
```

Common GitHub actions
- Create a PR via GitHub web UI or gh CLI
- Request reviews and address comments
- Squash and merge or merge commits depending on repo policy

Best practices and notes
- Keep commits small and focused.
- Provide descriptive commit messages and PR descriptions.
- Run tests locally before pushing or opening a PR.
- Protect the main branch via branch protection rules on GitHub (require PR reviews, status checks).

If you'd like, I can:
- Add a .gitignore tailored for Java/Maven/Eclipse (if missing).
- Create a small CONTRIBUTING.md template for PRs and code style.
- Generate an Eclipse Run Configuration XML file ready to import for `TC_Homepage_001`.
