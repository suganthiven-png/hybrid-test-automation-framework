package testNgframeworkwithAIV1.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import testNgframeworkwithAIV1.util.WaitUtils;

public class Homepage {
private WebDriver driver;
private By title = By.xpath("//img[@alt='Guru99']");

public Homepage(WebDriver driver) {
	this.driver=driver;
}
public boolean clicktitle(){
	// Use WaitUtils to wait until the title is clickable and click it
	return WaitUtils.waitAndClick(title, 10);
}


}