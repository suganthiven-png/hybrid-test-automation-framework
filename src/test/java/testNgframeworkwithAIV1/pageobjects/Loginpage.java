package testNgframeworkwithAIV1.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class Loginpage {
private WebDriver driver;
private By username=By.xpath("//input[@name='user-name']");
private By password=By.xpath("//input[@name='password']");
private By Login=By.xpath("//input[@name='login-button']");

// constructor
public Loginpage(WebDriver driver) {
    this.driver = driver;
}

private WebElement waitForElementVisible(By locator, int timeoutSeconds) {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
    return wait.until(d -> {
        WebElement e = d.findElement(locator);
        return e.isDisplayed() ? e : null;
    });
}

public void enterUsername(String U_name) {
    waitForElementVisible(username, 10).sendKeys(U_name);
}
public void enterPassword(String P_word) {
    waitForElementVisible(password, 10).sendKeys(P_word);
}
public void clickLogin() {
    waitForElementVisible(Login, 10).click();
}


}