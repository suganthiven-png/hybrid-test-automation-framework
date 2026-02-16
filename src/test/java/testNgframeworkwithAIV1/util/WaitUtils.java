package testNgframeworkwithAIV1.util;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import testNgframeworkwithAIV1.util.DriverFactory;
import testNgframeworkwithAIV1.util.ScreenshotUtil;

public class WaitUtils {
	public static WebElement waitForElementVisible(By locator, int timeoutSeconds) {
		WebDriver driver = DriverFactory.getDriver();
		return new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds))
				.until(ExpectedConditions.visibilityOfElementLocated(locator));
	}

	public static boolean waitForTitleContains(String partialTitle, int timeoutSeconds) {
		WebDriver driver = DriverFactory.getDriver();
		return new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds))
				.until(ExpectedConditions.titleContains(partialTitle));
	}

	// New helper: wait until element is clickable then click it, return true on success
	public static boolean waitAndClick(By locator, int timeoutSeconds) {
		WebDriver driver = DriverFactory.getDriver();
		try {
			WebElement elem = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds))
				.until(ExpectedConditions.elementToBeClickable(locator));
			elem.click();
			return true;
		} catch (Exception e) {
			// take a screenshot and page source to help debugging
			try { ScreenshotUtil.takeScreenshot("waitAndClick_failure"); } catch (Exception ignored) {}
			try { ScreenshotUtil.takePageSource("waitAndClick_failure"); } catch (Exception ignored) {}
			return false;
		}
	}
}