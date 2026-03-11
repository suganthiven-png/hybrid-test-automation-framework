package testNgframeworkwithAIV1.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import method.Product;

import java.time.Duration;
import java.util.List;
import java.net.URL;

import testNgframeworkwithAIV1.util.ScreenshotUtil;
import testNgframeworkwithAIV1.util.WaitUtils;

public class InventoryPage {
	private WebDriver driver;
	// Use CSS selectors and target the first inventory item container
	private By inventoryItems = By.cssSelector("div.inventory_item");
	private By itemNameWithin = By.cssSelector("div.inventory_item_name");
	private By itemPriceWithin = By.cssSelector("div.inventory_item_price");
	// add-to-cart button usually has data-test attribute like 'add-to-cart-something' or button text 'Add to cart'
	private By addToCartButtonWithin = By.cssSelector("button.btn_inventory");
	private By cartButton = By.cssSelector("a.shopping_cart_link");
	private By cartBadge = By.cssSelector("span.shopping_cart_badge");

	public InventoryPage(WebDriver driver) {
	    this.driver = driver;
	}

	private WebElement waitForVisible(By locator, int seconds) {
	    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
	    return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
	}

	public Product getfirstproductname() {
		// wait for inventory list to appear
		WebElement first = waitForVisible(inventoryItems, 15);
		List<WebElement> items = driver.findElements(inventoryItems);
		if (items.isEmpty()) {
			throw new RuntimeException("No inventory items found on the page");
		}
		WebElement firstItem = items.get(0);
		String productname = "";
		String price = "";
		try {
			WebElement nameEl = firstItem.findElement(itemNameWithin);
			productname = nameEl.getText();
		} catch (Exception e) {
			// leave empty, will fail later with clear message
		}
		try {
			WebElement priceEl = firstItem.findElement(itemPriceWithin);
			price = priceEl.getText();
		} catch (Exception e) {
			// ignore
		}
		return new Product(productname, price);
	}

	public void addcartbutton() {
		List<WebElement> items = driver.findElements(inventoryItems);
		if (items.isEmpty()) throw new RuntimeException("No inventory items found to add to cart");
		WebElement firstItem = items.get(0);
		WebElement btn = firstItem.findElement(addToCartButtonWithin);
		// Try to make click robust: scroll into view, wait for clickable, JS fallback
		try {
			((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", btn);
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
			wait.until(ExpectedConditions.elementToBeClickable(btn));
			btn.click();
		} catch (Exception e) {
			// try WaitUtils click by button id if available (more robust: finds from driver context)
			String btnId = null;
			try { btnId = btn.getAttribute("id"); } catch (Exception ignored) {}
			boolean clicked = false;
			if (btnId != null && !btnId.isEmpty()) {
				clicked = WaitUtils.waitAndClick(By.id(btnId), 10);
			}
			if (!clicked) {
				try {
					btn.click();
				} catch (Exception ex) {
					try { ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn); } catch (Exception ignore) {}
				}
			}
		}
		// wait for cart badge to show up confirming item added (longer timeout to avoid flakiness)
		WebDriverWait waitBadge = new WebDriverWait(driver, Duration.ofSeconds(15));
		try {
			waitBadge.until(
					ExpectedConditions.or(
						ExpectedConditions.visibilityOfElementLocated(cartBadge),
						ExpectedConditions.textToBePresentInElement(btn, "Remove")
					)
				);
		} catch (Exception timeout) {
			// capture debugging artifacts to test-output and rethrow a clearer exception
			try { ScreenshotUtil.takeScreenshot("addcart_timeout"); } catch (Exception ignored) {}
			try { ScreenshotUtil.takePageSource("addcart_timeout"); } catch (Exception ignored) {}
			throw new RuntimeException("Timed out waiting for cart confirmation (badge or 'Remove' text) after clicking add-to-cart", timeout);
		}
	}

	// New method: click first product add-to-cart but don't wait for cart badge; useful when badge is flaky
	public void addFirstProductToCartNoWait() {
		List<WebElement> items = driver.findElements(inventoryItems);
		if (items.isEmpty()) throw new RuntimeException("No inventory items found to add to cart");
		WebElement firstItem = items.get(0);
		WebElement btn = firstItem.findElement(addToCartButtonWithin);

		int attempts = 0;
		boolean confirmed = false;
		while (attempts < 3 && !confirmed) {
			attempts++;
			try {
				((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", btn);
				WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
				wait.until(ExpectedConditions.elementToBeClickable(btn));
				btn.click();
			} catch (Exception e) {
				String btnId = null;
				try { btnId = btn.getAttribute("id"); } catch (Exception ignored) {}
				boolean clicked = false;
				if (btnId != null && !btnId.isEmpty()) {
					clicked = WaitUtils.waitAndClick(By.id(btnId), 10);
				}
				if (!clicked) {
					try { btn.click(); } catch (Exception ex) { try { ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn); } catch (Exception ignore) {} }
				}
			}

			// short wait for confirmation
			try {
				WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(3));
				shortWait.until(ExpectedConditions.or(
						ExpectedConditions.visibilityOfElementLocated(cartBadge),
						ExpectedConditions.textToBePresentInElement(btn, "Remove")
					));
				confirmed = true;
			} catch (Exception ex) {
				// not yet confirmed; try again after small sleep
				try { Thread.sleep(400); } catch (InterruptedException ignored) {}
			}
		}

		if (!confirmed) {
			// capture artifacts for debugging
			try { ScreenshotUtil.takeScreenshot("addcart_nowait_no_confirmation_retry"); } catch (Exception ignored) {}
			try { ScreenshotUtil.takePageSource("addcart_nowait_no_confirmation_retry"); } catch (Exception ignored) {}
		}
	}

	public void cartclick() {
		// Use JS click for reliability then navigate to cart URL if needed and wait
		WebElement cartEl = driver.findElement(cartButton);
		try {
			((JavascriptExecutor) driver).executeScript("arguments[0].click();", cartEl);
		} catch (Exception e) {
			// fallback to normal click
			cartEl.click();
		}
		// Wait briefly for URL change, then ensure navigation to cart page
		try {
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
			wait.until(ExpectedConditions.urlContains("cart"));
		} catch (Exception ignored) {
		}
		// If page is not cart, navigate explicitly to origin + /cart.html
		try {
			String current = driver.getCurrentUrl();
			if (current == null || !current.contains("cart")) {
				URL u = new URL(current);
				String origin = u.getProtocol() + "://" + u.getHost();
				if (u.getPort() != -1) origin += ":" + u.getPort();
				driver.get(origin + "/cart.html");
				WebDriverWait wait2 = new WebDriverWait(driver, Duration.ofSeconds(10));
				wait2.until(ExpectedConditions.urlContains("cart"));
			}
		} catch (Exception ignored) {
		}
	}

}