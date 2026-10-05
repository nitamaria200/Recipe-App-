package test;

import model.User;

public class UserTest {

    public static void main(String[] args) {
        System.out.println("UserTest");

        testAdminRole();
        testHomeCookRole();
        testPassword();

        System.out.println("\nTests finished");
    }

    public static void testAdminRole() {
        User admin = new User("admin", "1234", "Admin User");

        if (admin.getRole().equals("Administrator")) {
            System.out.println("[PASS] Admin detected correctly.");
        } else {
            System.out.println("[FAIL] Admin NOT detected.");
            System.out.println("   Expected: Administrator");
            System.out.println("   Actual:   " + admin.getRole());
        }
    }

    public static void testHomeCookRole() {
        User cook = new User("chef_john", "1234", "John");

        if (cook.getRole().equals("Home Cook")) {
            System.out.println("[PASS] Home Cook detected correctly.");
        } else {
            System.out.println("[FAIL] Home Cook NOT detected.");
            System.out.println("   Expected: Home Cook");
            System.out.println("   Actual:   " + cook.getRole());
        }
    }

    public static void testPassword() {
        User user = new User("test", "secret", "Test User");

        if (user.checkPassword("secret") && !user.checkPassword("wrong")) {
            System.out.println("[PASS] Password check works.");
        } else {
            System.out.println("[FAIL] Password check failed.");
        }
    }
}