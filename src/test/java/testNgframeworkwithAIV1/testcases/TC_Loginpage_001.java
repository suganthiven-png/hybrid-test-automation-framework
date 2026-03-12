package testNgframeworkwithAIV1.testcases;

import static org.testng.Assert.assertTrue;

import org.testng.Assert;
import org.testng.annotations.Test;

import testNgframeworkwithAIV1.pageobjects.Loginpage;

public class TC_Loginpage_001 extends BaseTest {

    @Test
    public void testValidLogin() {
        Loginpage loginpage = new Loginpage(driver);
        loginpage.enterUsername("standard_user");
        loginpage.enterPassword("secret_sauce");
        loginpage.clickLogin();
        String actualURL = driver.getCurrentUrl();
       // String expectedURL = "https://www.saucedemo.com/inventory.html";
        //Assert.assertEquals(actualURL, expectedURL, "Login failed: URL mismatch");
        assertTrue(actualURL.contains("/inventory"), "Login failed: Not navigated to inventory page");
    }

}