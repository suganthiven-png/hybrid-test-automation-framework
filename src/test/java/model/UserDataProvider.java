package model;

import org.testng.annotations.DataProvider;

public class UserDataProvider {

    @DataProvider(name = "users")
    public static Object[][] users() {
        return new Object[][] {
            { new User("user1_test_" + System.currentTimeMillis(), "Pass@123") },
            { new User("user2_test_" + (System.currentTimeMillis()+1), "Pass@123") },
            { new User("user3_test_" + (System.currentTimeMillis()+2), "Pass@123") },
            { new User("user4_test_" + (System.currentTimeMillis()+3), "Pass@123") },
            { new User("user5_test_" + (System.currentTimeMillis()+4), "Pass@123") }
        };
    }
}
