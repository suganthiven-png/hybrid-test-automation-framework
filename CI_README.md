# CI configuration for testNgframeworkwithAIV1

This project includes example CI configurations for Jenkins and GitHub Actions.

Files added:
- `Jenkinsfile` - Declarative pipeline at the project root. Checks out the repository, runs `mvn clean test`, packages, publishes JUnit test results and archives artifacts.
- `.jenkins/pipeline.groovy` - Reusable Groovy pipeline that teams can adapt or turn into a shared library.
- `.github/workflows/maven.yml` - GitHub Actions workflow that runs on push and pull requests to `main`/`master`, runs `mvn clean test`, and uploads test results as an artifact.

How to use

Jenkinsfile
1. Create a Jenkins pipeline job pointing to this repository.
2. Use the `Jenkinsfile` in the root as the pipeline definition.
3. Configure credentials and Jenkins environment as needed.

Reusable pipeline
- Move `.jenkins/pipeline.groovy` into a shared library or reference it from the `Jenkinsfile` using `load` in a scripted pipeline.

GitHub Actions
- The workflow runs Maven with JDK 11. Modify the `java-version` if your project uses a different JDK.
- Test results are uploaded to the workflow run as `test-results` artifact.

Running locally (Windows)
- Ensure you have Maven installed and available on PATH.
- From the project root run:

```cmd
mvn -B -DskipTests=false clean test
```

Notes and next steps
- Update the `Jenkinsfile` to match your Jenkins agents (Windows vs Linux). The `sh` steps in the Jenkinsfile assume a Unix agent; change to `bat` for Windows nodes.
- Replace the placeholder action in the GitHub Actions workflow with a proper JUnit reporter step if you want to annotate PRs or create checks.
- If your builds produce different artifact types, adjust the `archiveArtifacts` pattern in the `Jenkinsfile`.
