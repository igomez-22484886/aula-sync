🚀 Engineering Project - Contribution Guidelines

Welcome to the Engineering Project! To maintain code quality and collaboration, please follow these contribution guidelines.

📌 Git Commit Rules

Language: All commit messages must be in English.

Format: Use the conventional commit format:

<type>(<scope>): <message>

Example:

feat(auth): add JWT authentication
fix(database): resolve connection timeout issue

Commit types:

feat → New feature

fix → Bug fix

docs → Documentation update

test → Adding/updating tests

refactor → Code improvements (no new features)

chore → Maintenance tasks

🌱 Branching Strategy

Main branches:

main → Stable, production-ready

develop → Main development branch

Feature branches:

Always create a branch from develop.

Format: feature/<short-description>

git checkout develop
git checkout -b feature/login-system

After completion, submit a Pull Request (PR) to develop.

Bugfix branches:

Format: fix/<short-description>

git checkout develop
git checkout -b fix/database-timeout

Submit a PR to develop.

🔥 Pull Request (PR) Rules

Do NOT commit directly to develop or main.

Always open a PR with a clear description of changes.

At least one reviewer must approve the PR before merging.

Ensure all GitHub Actions checks pass before merging.

Format PR titles:

[Feature] Implement login system
[Fix] Resolve timeout issue in database connection

Add relevant issue numbers in the PR description:

Closes #15

✅ Code Review Guidelines

Reviews should be constructive and focused on improvement.

Use comments to suggest improvements rather than blocking unnecessarily.

Follow best practices for clean, maintainable code.

Check for performance, security, and readability issues.

🛠️ Code Quality Checks

Ensure the project compiles successfully before committing.

Run unit tests before pushing changes:

mvn test

Check for linting and formatting issues.

📜 Documentation

Update documentation (README.md or docs/) for any new features or changes.

API changes must be documented properly.

Following these guidelines ensures a smooth workflow and a high-quality codebase. 🚀Happy coding! 😃
