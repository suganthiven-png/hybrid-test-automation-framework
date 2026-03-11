package testNgframeworkwithAIV1.testcases;

import static org.testng.Assert.assertEquals;

import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import method.Product;
import testNgframeworkwithAIV1.pageobjects.Cartpage;
import testNgframeworkwithAIV1.pageobjects.InventoryPage;
import testNgframeworkwithAIV1.pageobjects.Loginpage;

public class TC_Cartvalidation_002 extends BaseTest{
	@Test
	public void cartvalidationchech() {
		// Perform login for this test explicitly
		Loginpage login = new Loginpage(driver);
		login.enterUsername("standard_user");
		login.enterPassword("secret_sauce");
		login.clickLogin();
	    InventoryPage inventory=new InventoryPage(driver);
		Product product=inventory.getfirstproductname();
		// Use no-wait variant to avoid flaky cart badge timing
		inventory.addFirstProductToCartNoWait();
		inventory.cartclick();
		Cartpage cart=new Cartpage(driver);
		Product productcart=cart.getcardInformation();
		Assert.assertEquals(productcart.getName(),product.getName());
		Assert.assertEquals(productcart.getPrice(),product.getPrice());
	}

}