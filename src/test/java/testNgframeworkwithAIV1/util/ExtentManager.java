package testNgframeworkwithAIV1.util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class ExtentManager {
	private static ExtentReports extent;
	private static final String OUTPUT_FOLDER = "test-output/extent-report";
	private static final String FILE_NAME = "ExtentReport.html";

	public synchronized static ExtentReports getExtentReports() {
		if (extent == null) {
			try {
				Path out = Paths.get(OUTPUT_FOLDER);
				Files.createDirectories(out);
			} catch (Exception ignored) {
			}

			ExtentSparkReporter spark = new ExtentSparkReporter(OUTPUT_FOLDER + "/" + FILE_NAME);
			spark.config().setDocumentTitle("testNgframeworkwithAIV1 Test Report");
			spark.config().setReportName("testNgframeworkwithAIV Test Results");
			spark.config().setTheme(Theme.STANDARD);

			extent = new ExtentReports();
			extent.attachReporter(spark);
			extent.setSystemInfo("OS", System.getProperty("os.name"));
			extent.setSystemInfo("Java Version", System.getProperty("java.version"));

		}
		return extent;
	}
}
