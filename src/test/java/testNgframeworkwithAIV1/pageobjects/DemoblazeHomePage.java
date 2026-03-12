package testNgframeworkwithAIV1.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import method.Product;
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
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement el = wait.until(ExpectedConditions.elementToBeClickable(signupLink));
            try {
                el.click();
            } catch (Exception e) {
                try { ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);arguments[0].click();", el); } catch (Exception ignore) {}
            }
        } catch (Exception e) {
            // last resort: ensure visible and JS click
            WebElement el = waitForVisible(signupLink, 10);
            try { ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el); } catch (Exception ignore) {}
        }

        // Wait longer for the modal container to appear then username field
        By signupModal = By.id("signInModal");
        try {
            WebDriverWait modalWait = new WebDriverWait(driver, Duration.ofSeconds(20));
            modalWait.until(ExpectedConditions.visibilityOfElementLocated(signupModal));
            modalWait.until(ExpectedConditions.visibilityOfElementLocated(signupUsername));
        } catch (Exception e) {
            // Attempt alternative: programmatically show bootstrap modal via jQuery if present, else DOM class changes
            try {
                ((JavascriptExecutor) driver).executeScript(
                        "if(window.jQuery){$('#signInModal').modal('show');} else {var m=document.getElementById('signInModal'); if(m){m.classList.add('show'); m.style.display='block'; m.setAttribute('aria-hidden','false');}}"
                );
                WebDriverWait modalWait2 = new WebDriverWait(driver, Duration.ofSeconds(15));
                modalWait2.until(ExpectedConditions.visibilityOfElementLocated(signupUsername));
            } catch (Exception ignored) {
                // fallback: try a shorter visibility wait for the username field
                waitForVisible(signupUsername, 8);
            }
        }
    }

    // Helper: check element presence quickly
    private boolean isElementPresent(By locator, int timeoutSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
            wait.until(ExpectedConditions.presenceOfElementLocated(locator));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void signup(String username, String password) {
        Exception lastEx = null;
        for (int attempt = 1; attempt <= SIGNUP_ATTEMPTS; attempt++) {
            try {
                openSignupModal();
                WebElement userEl = null;
                try {
                    userEl = findFirstVisible(10, signupUsername);
                    userEl.clear();
                    userEl.sendKeys(username);
                    WebElement passEl = findFirstVisible(5, signupPassword);
                    passEl.clear();
                    passEl.sendKeys(password);
                    WebElement btn = driver.findElement(signupButtonInModal);
                    try {
                        btn.click();
                    } catch (Exception e) {
                        try { ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn); } catch (Exception ignore) {}
                    }
                } catch (Exception e) {
                    // Fallback: set fields and call page JS directly
                    try {
                        ((JavascriptExecutor) driver).executeScript(
                            "if(document.getElementById('sign-username')){document.getElementById('sign-username').value=arguments[0];} if(document.getElementById('sign-password')){document.getElementById('sign-password').value=arguments[1];} if(window.register){register();} ",
                            username, password
                        );
                    } catch (Exception jsEx) {
                        throw new RuntimeException("Failed to interact with signup modal", jsEx);
                    }
                }

                // Wait for either alert, modal error label, or modal to close (success)
                try {
                    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(MODAL_TIMEOUT_SECONDS));
                    wait.until(ExpectedConditions.or(
                            ExpectedConditions.alertIsPresent(),
                            ExpectedConditions.visibilityOfElementLocated(By.id("errors")),
                            ExpectedConditions.invisibilityOfElementLocated(signupUsername)
                    ));
                } catch (Exception ignored) {
                    // timed out waiting; we'll inspect and retry
                }

                // if an alert is present, read and act on it
                try {
                    WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(2));
                    shortWait.until(ExpectedConditions.alertIsPresent());
                    String alertText = driver.switchTo().alert().getText();
                    driver.switchTo().alert().accept();
                    if (alertText != null) {
                        String lower = alertText.toLowerCase();
                        if (lower.contains("502") || lower.contains("bad gateway") || lower.contains("server error")) {
                            lastEx = new RuntimeException("Signup alert indicates server error: " + alertText);
                            Thread.sleep(500L * attempt);
                            continue; // retry
                        }
                        // else alert likely indicates success or 'user exists' - treat as success
                        return;
                    }
                } catch (Exception noAlert) {
                    // No alert; check modal error label
                    try {
                        WebElement errors = driver.findElement(By.id("errors"));
                        String errText = errors.getText();
                        if (errText != null && !errText.trim().isEmpty()) {
                            String lower = errText.toLowerCase();
                            if (lower.contains("exist") || lower.contains("already")) {
                                // User already exists; treat as OK (we'll login later)
                                return;
                            }
                            if (lower.contains("502") || lower.contains("bad gateway") || lower.contains("server error")) {
                                lastEx = new RuntimeException("Signup modal reports server error: " + errText);
                                Thread.sleep(500L * attempt);
                                continue; // retry
                            }
                            // other validation error - surface it
                            throw new RuntimeException("Signup failed: " + errText);
                        } else {
                            // No explicit errors; if modal closed (username field not present) treat as success
                            if (!isElementPresent(signupUsername, 1)) {
                                return;
                            }
                            // otherwise unexpected state - retry
                            lastEx = new RuntimeException("Signup did not warn or succeed; retrying");
                            Thread.sleep(500L * attempt);
                            continue;
                        }
                    } catch (Exception e) {
                        // cannot locate errors label; if modal closed, success
                        if (!isElementPresent(signupUsername, 1)) return;
                        lastEx = e;
                        Thread.sleep(500L * attempt);
                        continue;
                    }
                }

            } catch (Exception e) {
                lastEx = e;
                try { Thread.sleep(500L * attempt); } catch (InterruptedException ignored) {}
            }
        }

        // capture artifacts and fail
        try { ScreenshotUtil.takeScreenshot("signup_failure_" + username); } catch (Exception ignored) {}
        try { ScreenshotUtil.takePageSource("signup_failure_" + username); } catch (Exception ignored) {}
        if (lastEx instanceof RuntimeException) throw (RuntimeException) lastEx;
        throw new RuntimeException("Signup failed after retries for user: " + username, lastEx);
    }

    public void openLoginModal() {
        // If already visible, nothing to do
        try { if (isElementPresent(loginUsername, 1)) return; } catch (Exception ignored) {}

        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement el = wait.until(ExpectedConditions.elementToBeClickable(loginLink));
            try { el.click(); } catch (Exception e) { try { ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);arguments[0].click();", el); } catch (Exception ignore) {} }
        } catch (Exception e) {
            WebElement el = waitForVisible(loginLink, 10);
            try { ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el); } catch (Exception ignore) {}
        }

        By loginModal = By.id("logInModal");
        try {
            WebDriverWait modalWait = new WebDriverWait(driver, Duration.ofSeconds(20));
            modalWait.until(ExpectedConditions.visibilityOfElementLocated(loginModal));
            modalWait.until(ExpectedConditions.visibilityOfElementLocated(loginUsername));
        } catch (Exception e) {
            try {
                ((JavascriptExecutor) driver).executeScript(
                        "if(window.jQuery){$('#logInModal').modal('show');} else {var m=document.getElementById('logInModal'); if(m){m.classList.add('show'); m.style.display='block'; m.setAttribute('aria-hidden','false');}}"
                );
                WebDriverWait modalWait2 = new WebDriverWait(driver, Duration.ofSeconds(15));
                modalWait2.until(ExpectedConditions.visibilityOfElementLocated(loginUsername));
            } catch (Exception ignored) {
                waitForVisible(loginUsername, 8);
            }
        }
    }

    public void login(String username, String password) {
        Exception lastEx = null;
        for (int attempt = 1; attempt <= LOGIN_ATTEMPTS; attempt++) {
            try {
                openLoginModal();
                try {
                    WebElement userEl = findFirstVisible(8, loginUsername);
                    userEl.clear();
                    userEl.sendKeys(username);
                    WebElement passEl = findFirstVisible(5, loginPassword);
                    passEl.clear();
                    passEl.sendKeys(password);
                    WebElement btn = driver.findElement(loginButtonInModal);
                    try {
                        btn.click();
                    } catch (Exception e) {
                        try { ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn); } catch (Exception ignore) {}
                    }
                } catch (Exception e) {
                    // Fallback: set fields and call page JS directly
                    try {
                        ((JavascriptExecutor) driver).executeScript(
                                "if(document.getElementById('loginusername')){document.getElementById('loginusername').value=arguments[0];} if(document.getElementById('loginpassword')){document.getElementById('loginpassword').value=arguments[1];} if(window.logIn){logIn();}",
                                username, password
                        );
                    } catch (Exception jsEx) {
                        throw new RuntimeException("Failed to interact with login modal", jsEx);
                    }
                }

                // wait for either nav username or error label or alert
                try {
                    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(MODAL_TIMEOUT_SECONDS));
                    wait.until(ExpectedConditions.or(
                            ExpectedConditions.visibilityOfElementLocated(By.id("nameofuser")),
                            ExpectedConditions.visibilityOfElementLocated(By.id("errorl")),
                            ExpectedConditions.alertIsPresent()
                    ));
                } catch (Exception ignored) {
                }

                // check alert
                try {
                    WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(2));
                    shortWait.until(ExpectedConditions.alertIsPresent());
                    String alertText = driver.switchTo().alert().getText();
                    driver.switchTo().alert().accept();
                    if (alertText != null) {
                        String lower = alertText.toLowerCase();
                        if (lower.contains("502") || lower.contains("bad gateway") || lower.contains("server error")) {
                            lastEx = new RuntimeException("Login alert indicates server error: " + alertText);
                            Thread.sleep(500L * attempt);
                            continue; // retry
                        }
                    }
                } catch (Exception noAlert) {
                    // no alert
                }

                // check nav username
                if (isElementPresent(By.id("nameofuser"), 2)) {
                    return; // logged in
                }

                // check error label
                try {
                    WebElement err = driver.findElement(By.id("errorl"));
                    String txt = err.getText();
                    if (txt != null && !txt.trim().isEmpty()) {
                        String lower = txt.toLowerCase();
                        if (lower.contains("502") || lower.contains("bad gateway") || lower.contains("server error")) {
                            lastEx = new RuntimeException("Login modal reports server error: " + txt);
                            Thread.sleep(500L * attempt);
                            continue; // retry
                        }
                        // other auth error
                        throw new RuntimeException("Login failed: " + txt);
                    }
                } catch (Exception e) {
                    // no explicit error label; retry once
                }

                // If reached here and not logged in, retry after a small backoff
                lastEx = new RuntimeException("Login did not complete (no nav username or explicit error)");
                Thread.sleep(500L * attempt);
            } catch (Exception e) {
                lastEx = e;
                try { Thread.sleep(500L * attempt); } catch (InterruptedException ignored) {}
            }
        }

        try { ScreenshotUtil.takeScreenshot("login_failure_" + username); } catch (Exception ignored) {}
        try { ScreenshotUtil.takePageSource("login_failure_" + username); } catch (Exception ignored) {}
        if (lastEx instanceof RuntimeException) throw (RuntimeException) lastEx;
        throw new RuntimeException("Login failed after retries for user: " + username, lastEx);
    }

    public Product captureFirstProductInfo() {
        WebElement first = waitForVisible(firstProductContainer, 10);
        String name = "";
        String price = "";
        try {
            name = first.findElement(productNameWithin).getText();
        } catch (Exception e) {
            // fallback: search within tbody
            try { name = driver.findElement(By.cssSelector("#tbodyid .hrefch")).getText(); } catch (Exception ignore) {}
        }
        try {
            price = first.findElement(productPriceWithin).getText();
        } catch (Exception e) {
            try { price = driver.findElement(By.cssSelector("#tbodyid h5")).getText(); } catch (Exception ignore) {}
        }
        return new Product(name, price);
    }

    public void clickFirstProduct() {
        WebElement first = waitForVisible(firstProductContainer, 10);
        WebElement link = first.findElement(productNameWithin);
        try {
            link.click();
        } catch (Exception e) {
            try { ((JavascriptExecutor) driver).executeScript("arguments[0].click();", link); } catch (Exception ignore) {}
        }
        // wait for product detail page to load - product name link in detail has class name 'name' or similar; wait for Add to cart button
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        try { wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[text()='Add to cart']"))); } catch (Exception ignored) {}
    }

    // On product detail page click Add to cart
    public void addToCartFromProductPage() {
        By addToCart = By.xpath("//a[text()='Add to cart']");
        WebElement btn = waitForVisible(addToCart, 8);
        try { btn.click(); } catch (Exception e) { try { ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn); } catch (Exception ignore) {} }
        // after clicking add to cart, demoblaze shows an alert; accept it
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            wait.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
        } catch (Exception ignored) {
        }
    }

    public void goToCart() {
        By cartLink = By.id("cartur");
        waitForVisible(cartLink, 5).click();
        // wait for cart table
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(8));
        try { wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#tbodyid tr"))); } catch (Exception ignored) {}
    }

    // Add helper to try multiple locators and return first visible element
    private WebElement findFirstVisible(int seconds, By... locators) {
        for (By loc : locators) {
            try {
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
                WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(loc));
                if (el != null && el.isDisplayed()) return el;
            } catch (Exception ignored) {}
        }
        // last resort: try findElement without wait for first locator
        try {
            WebElement el = driver.findElement(locators[0]);
            if (el != null && el.isDisplayed()) return el;
        } catch (Exception ignored) {}
        throw new org.openqa.selenium.TimeoutException("None of the provided locators became visible");
    }
}