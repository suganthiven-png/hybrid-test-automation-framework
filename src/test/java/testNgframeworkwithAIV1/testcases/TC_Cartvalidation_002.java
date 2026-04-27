package testNgframeworkwithAIV1.testcases;

import org.testng.Assert;
import org.testng.annotations.Test;

import model.Product;
import testNgframeworkwithAIV1.pageobjects.CartPage;
import testNgframeworkwithAIV1.pageobjects.InventoryPage;
import testNgframeworkwithAIV1.pageobjects.Loginpage;

public class TC_Cartvalidation_002 extends BaseTest {

    @Test
    public void cartvalidationchech() {
        Loginpage login = new Loginpage(driver);
        login.enterUsername("standard_user");
        login.enterPassword("secret_sauce");
        login.clickLogin();

        InventoryPage inventory = new InventoryPage(driver);
        Product product = inventory.getfirstproductname();

        inventory.addFirstProductToCartNoWait();
        inventory.cartclick();

        CartPage cart = new CartPage(driver);
        Product productcart = cart.getFirstCartProduct();

        Assert.assertEquals(productcart.getName(), product.getName());
        Assert.assertEquals(productcart.getPrice(), product.getPrice());
    }
}
