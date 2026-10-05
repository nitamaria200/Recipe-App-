package test;

import model.Review;
import java.sql.Timestamp;

public class ReviewTest {

    public static void main(String[] args) {
        System.out.println("Review test");

        testReviewData();
        testStarFormat();

        System.out.println("\nTest finished");
    }

    public static void testReviewData() {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        Review r = new Review(1, 1, "user1", 5, "Great!", now);

        if (r.getComment().equals("Great!") && r.getRating() == 5) {
            System.out.println("[PASS] Review data saved correctly.");
        } else {
            System.out.println("[FAIL] Review data incorrect.");
        }
    }

    public static void testStarFormat() {
        Review r = new Review(1, 1, "user1", 3, "Ok", null);

        if (r.getStarRating().equals("3/5")) {
            System.out.println("[PASS] Star rating formatted correctly.");
        } else {
            System.out.println("[FAIL] Star rating format incorrect.");
            System.out.println("   Expected: 3/5");
            System.out.println("   Actual:   " + r.getStarRating());
        }
    }
}