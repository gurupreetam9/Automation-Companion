# Getting Started — Automation Companion

Welcome to the Automation Companion project!  
This guide will help you set up the project, build it, and start contributing.

---

## 1. Requirements

| Tool | Version |
|------|---------|
| Android Studio | Ladybug or later |
| JDK | 17 |
| Gradle | Use the wrapper (`./gradlew`) |
| Android SDK | API 24–36 |

---

## 2. Clone the Repository

```bash
git clone https://github.com/gurupreetam9/Automation-Companion.git
cd AutomationCompanion
```
# 3.Branching Workflow

Never push directly to main

Work only on your feature branch

Merge → develop → then main after review

Create a feature branch:
```bash
git checkout develop
git checkout -b feature/<feature-name>-<your-name>

```

Examples:
```bash
feature/gesture-recording-alice
feature/semantic-bob
feature/dynamicui-charlie

```
---
# 4. Running the App

Open the project in Android Studio

1. Wait for Gradle sync

2. Select a device/emulator

3. Press Run

Or build via terminal:

```bash
./gradlew assembleDebug

```
---
# 5. Project Structure (Important)
```bash
app/
core/        # Interfaces & cross-feature contracts
data/        # Models & repository implementations
features/    # Each feature has its own folder
ui/          # Screens, navigation, components
docs/
.github/
```

For full details, see:
```bash
docs/PROJECT_OVERVIEW.md
```
---
# 6. Coding Standards

* Use Jetpack Compose for UI

* Follow MVVM where needed

* Keep feature modules isolated

* Never put concrete implementations in core/

* Use stub repositories in data/stub during early development

---

# 7. Submitting Your Work

Before opening a PR:

✔ Ensure the app builds

✔ Follow the PR template

✔ Link your PR to its issue

✔ Keep PRs small (1–3 files if possible)

---

# 8. Running CI (Automatic)

Each Pull Request triggers:

* Gradle build

* Unit tests (future)

* Lint checks (future)

Do not merge if CI fails.

---

# 9. Using AI for Code Generation

When using ChatGPT:

Always begin with:
```bash
Refer to docs/PROJECT_OVERVIEW.md for project structure.

```
This ensures correct folder paths & architecture.

---
