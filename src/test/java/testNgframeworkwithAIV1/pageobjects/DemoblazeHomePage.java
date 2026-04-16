package testNgframeworkwithAIV1.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import model.Product;
import testNgframeworkwithAIV1.util.ScreenshotUtil;

public class DemoblazeHomePage {
    private WebDriver driver;
    private By signupLink = By.id("signin2");
    private By loginLink = By.id("login2");
    // use explicit IDs present in the page source
    private By signupUsername = By.id("sign-username");
    private By signupPassword = By.id("sign-password");
    private By signupButtonInModal = By.xpath("//div[@id='signInModal']//button[contains(text(),'Sign up')]|//button[text()='Sign up']");
    private By loginUsername = By.id("loginusername");
    private By loginPassword = By.id("loginpassword");
    private By loginButtonInModal = By.xpath("//div[@id='logInModal']//button[contains(text(),'Log in')]|//button[text()='Log in']");
    // product listing selectors based on saved page source
    private By firstProductContainer = By.cssSelector("#tbodyid .col-lg-4.col-md-6.mb-4:first-of-type");
    private By productNameWithin = By.cssSelector("h4.card-title a.hrefch");
    private By productPriceWithin = By.cssSelector("h5");

    // tuning parameters
    private final int SIGNUP_ATTEMPTS = 3;
    private final int LOGIN_ATTEMPTS = 3;
    private final int MODAL_TIMEOUT_SECONDS = 6;

    public DemoblazeHomePage(WebDriver driver) {
        this.driver = driver;
    }

    private WebElement waitForVisible(By locator, int seconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public void openSignupModal() {
        // If already visible, nothing to do
        try {
            if (isElementPresent(signupUsername, 1)) {
                return;
            }
        } catch (Exception ignored) {}

        try {
{