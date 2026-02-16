# hybrid-test-automation-framework

Project: hybrid-test-automation-framework

A small TestNG + Selenium framework used to demonstrate automated UI tests. The project includes page objects, utilities (driver management, waiting helpers), and TestNG testcases. The tests are written in Java and use Maven for dependency management.

Key features
- Page Object Model (POM) pattern for page interactions (e.g. `Homepage`).
- Utilities for driver management (`DriverFactory`) and waiting (`WaitUtils`).
- Uses WebDriverManager to manage browser drivers automatically.
- Tests managed by TestNG; sample suite is `testng1.xml`.

Technology stack
- Java (JDK 11+ recommended)
- Maven
- Selenium WebDriver
- TestNG
- WebDriverManager (io.github.bonigarcia)
- Eclipse (recommended IDE for this repo)

Repository layout (important paths)
- src/test/java/testNgframeworkwithAIV1/pageobjects - page object classes (e.g. `Homepage`)
- src/test/java/testNgframeworkwithAIV1/testcases - TestNG test classes (e.g. `TC_Homepage_001`)
- src/test/java/testNgframeworkwithAIV1/util - utilities (DriverFactory, WaitUtils, ConfigReader, etc.)
- src/test/resources - resource files (e.g. `config.properties`) if present
- testng1.xml - TestNG suite used by CI/IDE runs

Prerequisites
- JDK 11 or higher installed and JAVA_HOME set
- Maven installed and available on PATH (optional if using Eclipse built-in Maven)
- Internet access for WebDriverManager to download browser drivers (or provide local driver paths)
- Chrome or Firefox browser installed if running non-headless tests
- Eclipse (recommended) with TestNG plugin for convenient IDE runs

Quick start (Eclipse)
1. Import the project into Eclipse: File > Import > Maven > Existing Maven Projects > select the project folder.
2. Right click project > Maven > Update Project... to resolve dependencies.
3. Ensure TestNG plugin is installed (Eclipse Marketplace > TestNG).
4. Run a single test class: Right click `TC_Homepage_001.java` > Run As > TestNG Test.
5. Run the suite: Right click `testng1.xml` > Run As > TestNG Suite.

Running from the command line (Windows cmd.exe)
- Compile tests only (no test execution):
```cmd
cd C:\Users\raman\eclipse-workspace\testNgframeworkwithAIV1
mvn -DskipTests test-compile
```
- Run all tests:
```cmd
mvn test
```
- Run a single TestNG class (replace fully-qualified name):
```cmd
mvn -Dtest=testNgframeworkwithAIV1.testcases.TC_Homepage_001 test
```
- Override configuration properties via system properties (picked up by `ConfigReader`):
```cmd
mvn test -DbaseUrl=https://www.guru99.com -Dbrowser=chrome -Dheadless=false -DimplicitWait=10
```

Suggested VM arguments for IDE run configurations
- Visible Chrome run (non-headless):
```
-Dbrowser=chrome -Dheadless=false -DbaseUrl=https://www.guru99.com -DimplicitWait=10
```
- Headless run (CI friendly):
```
-Dbrowser=chrome -Dheadless=true -DbaseUrl=https://www.guru99.com -DimplicitWait=10
```

Config file
- `ConfigReader` loads `config.properties` from the classpath. You can override values at runtime using System properties as shown above.

Common troubleshooting
- "'mvn' is not recognized": either install Maven or use Eclipse's Maven (m2e) integration.
- WebDriverManager cannot download drivers due to firewall/proxy: either configure proxy or point to a local driver using `-Dwebdriver.chrome.driver=C:\path\to\chromedriver.exe`.
- Tests failing due to timing: adjust waits or increase timeouts in `WaitUtils` or `config.properties` implicitWait.
- Browser not launching (headless in config): set `-Dheadless=false`.

How to extend tests
- Create a new page object in `pageobjects/` for a new page and provide actions returning page objects where appropriate.
- Add a new test under `testcases/` that uses the page objects and TestNG assertions.
- Keep utility methods generic and reusable.

Contributing
- Follow a branching workflow: create a feature branch off `main` (or `master`), implement changes, run tests locally, push branch to remote, and create a Pull Request.
- Keep methods small and focused; add tests for new behaviors.

License
- Add a LICENSE file to the repo if you plan to publish this project publicly and want to specify a license.

If you'd like I can also add an `README-ECLIPSE.md` containing a step-by-step Eclipse run configuration export or produce an importable Run Configuration file for `TC_Homepage_001`.
