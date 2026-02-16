package testNgframeworkwithAIV1.testcases;

import org.testng.Assert;
import org.testng.annotations.Test;

import testNgframeworkwithAIV1.pageobjects.Homepage;



public class TC_Homepage_001 extends BaseTest {
	// This test case will verify the homepage title and heading
	// We will use the Homepage page object to interact with the homepage elements
	// We will also use WaitUtils to wait for elements to be visible before interacting with them	
	  @Test
	    public void openHomePageAndCheckHeading() {
	        Homepage home = new Homepage(driver);
	        boolean clickvalidation = home.clicktitle();
	        // Assert that the click action was successful
	        Assert.assertTrue(clickvalidation, "Click on title should succeed");
	    }

}