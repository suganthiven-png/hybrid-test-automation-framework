package testNgframeworkwithAIV1.pageobjects;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import testNgframeworkwithAIV1.util.ScreenshotUtil;

public class SignupPage {
    private WebDriver driver;
    private By signupLink = By.id("signin2");
    private By username = By.id("sign-username");
    private By password = By.id("sign-password");
    private By signupButton = By.xpath("//button[text()='Sign up']");
    private By signupModal = By.id("signInModal");
    private By homeProducts = By.cssSelector("#tbodyid");

    public SignupPage(WebDriver driver) {
        this.driver = driver;
    }

    private WebElement waitForElementVisible(By locator, int timeoutSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    private void waitForElementInvisible(By locator, int timeoutSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    public void openSignupModal() {
        int attempts = 0;
        while (attempts < 3) {
            attempts++;
            try {
                WebElement link = driver.findElement(signupLink);
                try { link.click(); } catch (Exception e) {
                    // fallback to JS click
                    try { ((JavascriptExecutor) driver).executeScript("arguments[0].click();", link); } catch (Exception ignored) {}
                }
                // wait briefly for username input to appear
                try { waitForElementVisible(username, 5); return; } catch (Exception ignored) {}
                Thread.sleep(300);
            } catch (Exception e) {
                try { Thread.sleep(300); } catch (InterruptedException e1) { Thread.currentThread().interrupt(); }
            }
        }
    }

    public void enterUsername(String u) {
        waitForElementVisible(username, 15).clear();
        waitForElementVisible(username, 15).sendKeys(u);
    }

    public void enterPassword(String p) {
        waitForElementVisible(password, 15).clear();
        waitForElementVisible(password, 15).sendKeys(p);
    }

    private String clickSignupAndGetAlertMessage() {
        try {
            waitForElementVisible(signupButton, 15).click();
        } catch (UnhandledAlertException uae) {
            // Alert may have appeared immediately; try to read and accept it
            try {
                Alert a = driver.switchTo().alert();
                String msg = a.getText();
                a.accept();
                // wait for modal to disappear
                try { waitForElementInvisible(signupModal, 5); } catch (Exception ignored) {}
                // refresh to ensure page returns to stable state
                try { driver.navigate().refresh(); Thread.sleep(500); } catch (Exception ignored) {}
                return msg != null ? msg.toLowerCase() : "";
            } catch (Exception e) {
                return "";
            }
        } catch (Exception e) {
            // click failed for another reason; continue to try to read any alert
        }

        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            String msg = alert.getText();
            alert.accept();
            try { waitForElementInvisible(signupModal, 5); } catch (Exception ignored) {}
            try { driver.navigate().refresh(); Thread.sleep(500); } catch (Exception ignored) {}
            return msg != null ? msg.toLowerCase() : "";
        } catch (Exception e) {
            // even if no alert was present, try waiting briefly for modal to close
            try { waitForElementInvisible(signupModal, 3); } catch (Exception ignored) {}
            try { driver.navigate().refresh(); Thread.sleep(500); } catch (Exception ignored) {}
            return "";
        }
    }

    private void captureDebugArtifacts(String prefix) {
        try { ScreenshotUtil.takeScreenshot(prefix); } catch (Exception ignored) {}
        try { ScreenshotUtil.takePageSource(prefix); } catch (Exception ignored) {}
    }

    /**
     * Signup user; returns the username to use for subsequent login.
     * If the site reports that the user already exists, do NOT attempt to create a new username;
     * instead return the original username so the caller will proceed to login.
     */
    public String signup(String u, String p) {
        openSignupModal();
        enterUsername(u);
        enterPassword(p);
        String alertMsg = clickSignupAndGetAlertMessage();
        // if alert indicates user already exists, do not retry; proceed to login with same username
        if (alertMsg.contains("exist") || alertMsg.contains("already")) {
            try { driver.navigate().refresh(); } catch (Exception ignored) {}
            // wait for homepage product list to stabilize
            try { waitForElementVisible(homeProducts, 10); } catch (Exception e) { try { Thread.sleep(500); } catch (Exception ignored) {} }
            return u;
        }
        // if alert message is present and doesn't indicate success, capture artifacts
        if (!alertMsg.isEmpty() && !alertMsg.contains("success")) {
            captureDebugArtifacts("signup_failure_" + u);
        }
        // wait for home to be stable after success
        try { waitForElementVisible(homeProducts, 10); } catch (Exception ignored) {}
        return u;
    }
}