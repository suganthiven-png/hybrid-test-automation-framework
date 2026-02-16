package testNgframeworkwithAIV1.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import testNgframeworkwithAIV1.util.DriverFactory;

public class ScreenshotUtil {
	private static final ThreadLocal<String> lastScreenshot = new ThreadLocal<>();
	private static final ThreadLocal<String> lastPageSource = new ThreadLocal<>();

	public static String takeScreenshot(String name) {
		WebDriver driver = DriverFactory.getDriver();
		if (driver == null)
			return null;
		try {
			File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
			Path destDir = Paths.get("test-output", "screenshots");
			Files.createDirectories(destDir);
			Path dest = destDir.resolve(name + "_" + timestamp + ".png");
			Files.copy(src.toPath(), dest);
			String path = dest.toAbsolutePath().toString();
			lastScreenshot.set(path);
			return path;
		} catch (IOException | ClassCastException e) {
			System.err.println("Failed to take screenshot: " + e.getMessage());
			return null;
		}
	}

	/**
	 * Capture the current page source to a file and return the path.
	 */
	public static String takePageSource(String name) {
		WebDriver driver = DriverFactory.getDriver();
		if (driver == null)
			return null;
		try {
			String pageSource = driver.getPageSource();
			String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
			Path destDir = Paths.get("test-output", "pagesources");
			Files.createDirectories(destDir);
			Path dest = destDir.resolve(name + "_" + timestamp + ".html");
			Files.write(dest, pageSource.getBytes(StandardCharsets.UTF_8));
			String path = dest.toAbsolutePath().toString();
			lastPageSource.set(path);
			return path;
		} catch (IOException | UnsupportedOperationException e) {
			System.err.println("Failed to save page source: " + e.getMessage());
			return null;
		}
	}

	/**
	 * Returns the last screenshot path for the current thread without clearing it.
	 */
	public static String getLastScreenshot() {
		return lastScreenshot.get();
	}

	/**
	 * Returns and clears the last screenshot path for the current thread.
	 */
	public static String consumeLastScreenshot() {
		String p = lastScreenshot.get();
		lastScreenshot.remove();
		return p;
	}

	public static void clearLastScreenshot() {
		lastScreenshot.remove();
	}

	/**
	 * Returns the last page source path for the current thread without clearing it.
	 */
	public static String getLastPageSource() {
		return lastPageSource.get();
	}

	/**
	 * Returns and clears the last page source path for the current thread.
	 */
	public static String consumeLastPageSource() {
		String p = lastPageSource.get();
		lastPageSource.remove();
		return p;
	}

	public static void clearLastPageSource() {
		lastPageSource.remove();
	}

}