# README - Eclipse Run Instructions (hybrid-test-automation-framework)

This document contains an exportable Eclipse Run Configuration for running `TC_Homepage_001` and step-by-step guidance to import and use it.

Import project into Eclipse
1. File > Import > Maven > Existing Maven Projects
2. Select workspace folder: `C:\Users\raman\eclipse-workspace\testNgframeworkwithAIV1`
3. Finish and update project dependencies (Right-click > Maven > Update Project)

Run configuration (how to use the provided launch configuration)
1. In Eclipse: Run > Run Configurations...
2. In the list, choose TestNG and either import the provided launch file or create a new TestNG configuration.

Provided launch file
- `TC_Homepage_001.launch` shipped in the project root is an XML export of an Eclipse run configuration for `TC_Homepage_001`.
- To import: in Run Configurations dialog, select the TestNG node, click the 'Import...' button, select `TC_Homepage_001.launch` from the project root, and import.

Recommended VM arguments for visible tests
-Dbrowser=chrome -Dheadless=false -DbaseUrl=https://www.guru99.com -DimplicitWait=10

Recommended VM arguments for CI/headless runs
-Dbrowser=chrome -Dheadless=true -DbaseUrl=https://www.guru99.com -DimplicitWait=10

Notes
- Ensure TestNG Eclipse plugin and m2e (Maven integration) are installed.
- If the run fails due to missing drivers, WebDriverManager will attempt to download them. Ensure network access, or use `-Dwebdriver.chrome.driver=` to point a local driver.

The `TC_Homepage_001.launch` file is in the project root; import it into Eclipse if you want a ready-to-run config.
