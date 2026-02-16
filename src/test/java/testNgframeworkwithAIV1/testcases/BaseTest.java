package testNgframeworkwithAIV1.testcases;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import testNgframeworkwithAIV1.util.ConfigReader;
import testNgframeworkwithAIV1.util.DriverFactory;

public class BaseTest {
	protected WebDriver driver;
	protected ConfigReader config;

	@BeforeMethod(alwaysRun = true)
	public void setUp() {
		config = new ConfigReader();
		DriverFactory.initDriver(config);
		driver = DriverFactory.getDriver();
		driver.get(config.getBaseUrl());
	}

	@AfterMethod(alwaysRun = true)
	public void tearDown() {
		DriverFactory.quitDriver();
	}
}
