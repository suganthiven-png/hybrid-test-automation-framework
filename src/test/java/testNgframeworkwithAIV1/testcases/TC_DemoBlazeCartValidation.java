package testNgframeworkwithAIV1.testcases;

import org.testng.Assert;
import org.testng.annotations.Test;

import method.Product;
//import model.User;
//import model.UserDataProvider;
import testNgframeworkwithAIV1.pageobjects.DemoblazeCartPage;
import testNgframeworkwithAIV1.pageobjects.DemoblazeHomePage;
import testNgframeworkwithAIV1.util.ScreenshotUtil;

public class TC_DemoBlazeCartValidation extends BaseTest {

    @Test(dataProvider = "users", dataProviderClass = UserDataProvider.class)
    public void testSignupLoginAddToCart(User user) {
        DemoblazeHomePage home = new DemoblazeHomePage(driver);

        // Create new user via signup modal
        home.signup(user.getUsername(), user.getPassword());
        ScreenshotUtil.takeScreenshot("after_signup_" + user.getUsername());

        // After signup, attempt to login
        home.login(user.getUsername(), user.getPassword());
        ScreenshotUtil.takeScreenshot("after_login_" + user.getUsername());

        // Capture first product details on home page
        Product productOnHome = home.captureFirstProductInfo();
        ScreenshotUtil.takeScreenshot("before_click_product_" + user.getUsername());

        // Click first product to go to product page
        home.clickFirstProduct();

        // Add to cart from product detail
        home.addToCartFromProductPage();
        ScreenshotUtil.takeScreenshot("after_add_to_cart_" + user.getUsername());

        // Go to cart and validate first item matches
        home.goToCart();
        ScreenshotUtil.takeScreenshot("after_navigate_cart_" + user.getUsername());
        DemoblazeCartPage cart = new DemoblazeCartPage(driver);
        Product productInCart = cart.getFirstCartProduct();

        Assert.assertEquals(productInCart.getName(), productOnHome.getName(), "Product name in cart does not match selected product");
        Assert.assertEquals(productInCart.getPrice(), productOnHome.getPrice(), "Product price in cart does not match selected product");
    }
}
