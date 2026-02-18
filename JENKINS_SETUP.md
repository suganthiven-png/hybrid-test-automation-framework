Jenkins integration and setup for testNgframeworkwithAIV1

Overview
- This project uses Maven to build and TestNG + ExtentReports for test reporting.
- The provided `Jenkinsfile` is a cross-platform (Windows/Unix) Declarative Pipeline that:
  - Checks out source
  - Runs `mvn clean test` to execute tests
  - Packages the artifact
  - Archives artifacts and test-output
  - Publishes JUnit/Surefire XMLs, TestNG artifacts and Extent HTML reports when available

Required Jenkins plugins
- Pipeline (workflow-aggregator)
- Git (for checkout)
- Pipeline: GitHub (optional, if using multibranch or GitHub hooks)
- HTML Publisher Plugin (for publishing Extent/TestNG HTML reports)
- JUnit Plugin (often preinstalled)
- TestNG Results Plugin (optional; used if you prefer TestNG native view)
- AnsiColor Plugin (for colored console output)

Optional (recommended)
- Workspace Cleanup Plugin
- Pipeline Utility Steps (for helpful pipeline steps)

How the `Jenkinsfile` works (high level)
1. Uses `agent any` so it can run on Linux or Windows agents.
2. For shell commands uses `isUnix()` to choose between `sh` and `bat`.
3. Runs `mvn -B -DskipTests=false clean test` during the Build & Test stage.
4. Publishes Surefire/JUnit XML results using the `junit` step.
5. Attempts to find and publish ExtentReports and TestNG emailable report using the HTML Publisher plugin.
6. Archives artifacts under `target/` and the `test-output/` folder.

Setting up a Pipeline job (frequently used flows)

A) Multibranch Pipeline (recommended for Git repos)
- Install `Multibranch Pipeline` and `Git` plugins.
- Create a new Multibranch Pipeline job in Jenkins.
- Set the repository URL and credentials (if private).
- Jenkins will scan branches and use the `Jenkinsfile` contained in the repository.

B) Single Pipeline job
- Create a new Pipeline job.
- In Pipeline -> Definition select `Pipeline script from SCM`.
- Select Git, provide repository URL, credentials and the branch to build.
- Set Script Path to `Jenkinsfile` (default)

Credentials and environment
- If tests need credentials/secrets, create Jenkins Credentials (Username/Password, Secret Text, or Certificates) and reference them via withCredentials in the pipeline.
- For browser-based Selenium tests using real browsers on an agent, make sure:
  - The agent has a GUI session or use headless browsers (Chrome Headless/Firefox Headless) or a Selenium Grid/remote driver.
  - WebDriverManager will download drivers on the agent by default; on locked-down systems you may want to preinstall drivers.

Configuring report publishing
- HTML Publisher plugin: The Jenkinsfile uses `publishHTML` to publish reports located at:
  - `test-output/extent-report/index.html`
  - `target/extent-report/index.html`
  - `test-output/ExtentReports/index.html`
  - and `test-output/emailable-report.html` for TestNG's emailable report.

- JUnit results: The Jenkinsfile calls `junit 'target/surefire-reports/*.xml'` (Surefire produces JUnit-style XMLs). The JUnit plugin will show test trends and failed tests.

- TestNG plugin: If installed, the pipeline attempts `publishTestNGResults testNGResultsPattern: 'test-output/testng-results.xml'` (this step is optional and will be skipped if the plugin is not available).

Windows agent specifics
- The Jenkinsfile uses `bat` for Windows commands.
- Ensure `mvn` is available on the PATH of the Windows agent, or use the Maven Tool Installation in Jenkins (`tool 'M3'`) and call: `bat "%%MAVEN_HOME%%\bin\mvn -B -DskipTests=false clean test"` or use `withMaven` step.

Troubleshooting
- If HTML reports do not appear, verify the `index.html` file exists in the expected directory after the build (check workspace files or archived artifacts).
- Ensure the HTML Publisher plugin is installed if `publishHTML` step fails.
- If `publishTestNGResults` fails, either install the TestNG plugin or rely on the JUnit conversion that Surefire already produces.

Local verification (run on your machine)
- Run tests locally to ensure Maven and reporting are working:

```cmd
mvn -B -DskipTests=false clean test
```

- Confirm that reports are produced under `test-output\` and `target\surefire-reports`.

Quality gates and CI checklist mapping
- Build and Test: `mvn clean test` -> covered by `Build & Test` stage in `Jenkinsfile` (Done)
- Cross-platform: uses `isUnix()` to select `sh` or `bat` -> covered (Done)
- Archive artifacts: `archiveArtifacts` steps included -> covered (Done)
- Publish Extent/TestNG reports: uses `publishHTML` and `publishTestNGResults` (if available) -> covered (Done; depends on plugin availability)

Next steps / Improvements
- Use `withMaven` pipeline step (from Maven Integration plugin) to get better Maven lifecycle integration and automatic test reporting.
- Add credentials handling (`withCredentials`) if tests need secure access.
- If running browser tests, consider using Docker images or a Selenium Grid to run browsers in CI reliably.

If you want, I can also:
- Create a `Jenkinsfile` variant that uses `withMaven` and Jenkins-managed Maven installation.
- Add an automated job definition (Job DSL) or a GitHub Actions workflow as an alternative CI.

