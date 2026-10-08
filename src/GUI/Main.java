package GUI;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            try {
                UIManager.setLookAndFeel(
                        UIManager.getSystemLookAndFeelClassName()
                );
            } catch (Exception ignored) {
            }

            // Keep typed text readable on every input component across the application.
            UIManager.put("TextField.foreground", java.awt.Color.BLACK);
            UIManager.put("TextField.caretForeground", java.awt.Color.BLACK);
            UIManager.put("PasswordField.foreground", java.awt.Color.BLACK);
            UIManager.put("PasswordField.caretForeground", java.awt.Color.BLACK);

            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
