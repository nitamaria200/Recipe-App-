import model.UserException;
import ui.Login;
import model.User;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> new Login().setVisible(true));

        try {
            User testUser = new User();
            testUser.setPassword("123");
        } catch (UserException e) {
            System.out.println(" [TEST] Caught Exception: " + e.getMessage());
        }
    }
}