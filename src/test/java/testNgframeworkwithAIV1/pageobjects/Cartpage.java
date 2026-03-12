package testNgframeworkwithAIV1.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import method.Product;

import java.time.Duration;
import java.util.List;

public class Cartpage {
	private WebDriver driver;
	private By cartItems = By.cssSelector("div.cart_item");
	private By cartItemName = By.cssSelector("div.inventory_item_name");
	private By cartItemPrice = By.cssSelector("div.inventory_item_price");

	public Cartpage(WebDriver driver) {
		this.driver=driver;
	}

	private WebElement waitForVisible(By locator, int seconds) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
		return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
	}

	public Product getcardInformation() {
		return getCartProductDetails();
	}

	public Product getCartProductDetails() {

		// Wait longer for cart items to be present/visible (client-side rendering may be delayed)
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
		wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(cartItems));
		wait.until(ExpectedConditions.visibilityOfElementLocated(cartItems));

		List<WebElement> items = driver.findElements(cartItems);
		if (items.isEmpty()) throw new RuntimeException("No items found in cart");
		WebElement firstItem = items.get(0);

		String name = firstItem.findElement(cartItemName).getText();
		String price = firstItem.findElement(cartItemPrice).getText();

		return new Product(name, price);
	}

}