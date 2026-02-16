package testNgframeworkwithAIV1.util;

import java.time.Duration;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
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
			if (headless)
				options.addArguments("--headless=new");
			options.addArguments("--remote-allow-origins*");
			driver.set(new ChromeDriver(options));
			break;
		}

		// Set implicit wait and try to maximize
		try {
			getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(config.getImplicitWait()));
			getDriver().manage().window().maximize();
		} catch (Exception ignored) {
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
