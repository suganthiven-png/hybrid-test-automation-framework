package testNgframeworkwithAIV1.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import testNgframeworkwithAIV1.util.ConfigReader;
import io.github.bonigarcia.wdm.WebDriverManager;

public class DriverFactory {
	private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();

	public static WebDriver getDriver() {
		return driver.get();
	}

	public static void initDriver(ConfigReader config) {
		String browser = config.getBrowser().toLowerCase();
		boolean headless = config.isHeadless();
		String remoteUrl = config.getRemoteUrl();

		try {
			if (remoteUrl != null && !remoteUrl.isEmpty()) {
				// Use RemoteWebDriver (Selenium Grid / standalone)
				URL url = new URL(remoteUrl);
				switch (browser) {
				case "firefox":
					FirefoxOptions ffOptions = new FirefoxOptions();
					if (headless)
						ffOptions.addArguments("-headless");
					driver.set(new RemoteWebDriver(url, ffOptions));
					break;
				case "chrome":
				default:
					ChromeOptions options = new ChromeOptions();
					Map<String, Object> prefs = new HashMap<>();

					// Disable password manager
					prefs.put("credentials_enable_service", false);
					prefs.put("profile.password_manager_enabled", false);

					// Disable password leak detection
					prefs.put("profile.password_manager_leak_detection", false);

					// Disable save password prompt
					prefs.put("profile.password_manager_enabled", false);

					options.setExperimentalOption("prefs", prefs);

					// Optional stability flags
					options.addArguments("--disable-notifications");
					options.addArguments("--incognito");
					options.addArguments("--disable-sync");
					if (headless) {
						options.addArguments("--headless=new");
						// ensure a default window size so elements are visible in headless mode
						options.addArguments("--window-size=1920,1080");
					}
					options.addArguments("--remote-allow-origins=*");
					driver.set(new RemoteWebDriver(url, options));
					break;
				}
			} else {
				// Local WebDriver using WebDriverManager
				switch (browser) {
				case "firefox":
					WebDriverManager.firefoxdriver().setup();
					FirefoxOptions ffOptions = new FirefoxOptions();
					if (headless)
						ffOptions.addArguments("-headless");
					driver.set(new FirefoxDriver(ffOptions));
					break;
				case "chrome":
				default:
					WebDriverManager.chromedriver().setup();
					ChromeOptions options = new ChromeOptions();
					if (headless) {
						options.addArguments("--headless=new");
						options.addArguments("--window-size=1920,1080");
					}
					options.addArguments("--remote-allow-origins=*");
					driver.set(new ChromeDriver(options));
					break;
				}
			}

			// Set implicit wait and try to maximize
			try {
				getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(config.getImplicitWait()));
				getDriver().manage().window().maximize();
			} catch (Exception ignored) {
			}

		} catch (MalformedURLException e) {
			throw new RuntimeException("Invalid selenium.remote.url: " + remoteUrl, e);
		}
	}

	public static void quitDriver() {
		WebDriver wd = driver.get();
		if (wd != null) {
			wd.quit();
			driver.remove();
		}
	}

}