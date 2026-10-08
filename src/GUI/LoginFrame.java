package GUI;

import DAO.UserDAO;
import model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class LoginFrame extends JFrame {

    private final JTextField usernameField = new JTextField();
    private final JPasswordField passwordField = new JPasswordField();

    private final UserDAO userDAO = new UserDAO();

    // Modern colors
    private static final Color DARK = new Color(15, 23, 42);
    private static final Color DARK2 = new Color(30, 41, 59);
    private static final Color BLUE = new Color(37, 99, 235);
    private static final Color BLUE_HOVER = new Color(29, 78, 216);
    private static final Color WHITE = Color.WHITE;
    private static final Color MUTED = new Color(100, 116, 139);
    private static final Color BORDER = new Color(226, 232, 240);
    private static final Color FIELD_BG = new Color(248, 250, 252);

    public LoginFrame() {

        setTitle("Employee Management System - Login");
        setSize(900, 560);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // Main background
        GradientPanel background = new GradientPanel();
        background.setLayout(null);

        // ==============================
        // LEFT SIDE - BRANDING
        // ==============================

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(null);
        leftPanel.setOpaque(false);
        leftPanel.setBounds(0, 0, 430, 560);

        // Logo circle
        JLabel logo = new JLabel("EM", SwingConstants.CENTER);
        logo.setFont(new Font("Segoe UI", Font.BOLD, 25));
        logo.setForeground(WHITE);
        logo.setBackground(BLUE);
        logo.setOpaque(true);
        logo.setBounds(65, 75, 88, 88);

        // Use the supplied employee-management image as the brand logo.
        ImageIcon brandIcon = Theme.brandingIcon(88, 88);
        if (brandIcon != null) {
            logo.setText("");
            logo.setIcon(brandIcon);
            logo.setBackground(new Color(30, 41, 59));
        }

        // Main title
        JLabel systemTitle = new JLabel(
                "<html><div style='text-align:center;'>EMPLOYEE<br>MANAGEMENT</div></html>",
                SwingConstants.CENTER
        );

        systemTitle.setFont(new Font("Segoe UI", Font.BOLD, 30));
        systemTitle.setForeground(WHITE);
        systemTitle.setBounds(35, 185, 360, 90);

        JLabel systemText = new JLabel(
                "<html><div style='text-align:center;'>Smart Employee Management<br>System</div></html>",
                SwingConstants.CENTER
        );

        systemText.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        systemText.setForeground(new Color(203, 213, 225));
        systemText.setBounds(35, 285, 360, 55);

        JLabel features = new JLabel(
                "<html><div style='text-align:center;'>"
                        + "Employees  •  Attendance  •  Leave  •  Payroll"
                        + "</div></html>",
                SwingConstants.CENTER
        );

        features.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        features.setForeground(new Color(148, 163, 184));
        features.setBounds(25, 355, 380, 30);

        leftPanel.add(logo);
        leftPanel.add(systemTitle);
        leftPanel.add(systemText);
        leftPanel.add(features);

        // ==============================
        // RIGHT SIDE - LOGIN CARD
        // ==============================

        RoundedPanel loginCard = new RoundedPanel(28);
        loginCard.setBackground(WHITE);
        loginCard.setLayout(null);
        loginCard.setBounds(475, 55, 365, 450);

        JLabel loginTitle = new JLabel("Welcome Back!");
        loginTitle.setFont(new Font("Segoe UI", Font.BOLD, 27));
        loginTitle.setForeground(DARK);
        loginTitle.setBounds(40, 35, 285, 40);

        JLabel loginSubtitle = new JLabel(
                "Sign in to continue to your dashboard"
        );

        loginSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        loginSubtitle.setForeground(MUTED);
        loginSubtitle.setBounds(40, 75, 285, 25);

        // Username
        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        usernameLabel.setForeground(DARK2);
        usernameLabel.setBounds(40, 120, 280, 25);

        usernameField.setBounds(40, 148, 285, 42);
        styleField(usernameField);

        // Password
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        passwordLabel.setForeground(DARK2);
        passwordLabel.setBounds(40, 202, 280, 25);

        passwordField.setBounds(40, 230, 285, 42);
        styleField(passwordField);

        // Show password checkbox
        JCheckBox showPassword = new JCheckBox("Show password");
        showPassword.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        showPassword.setForeground(MUTED);
        showPassword.setBackground(WHITE);
        showPassword.setFocusPainted(false);
        showPassword.setBounds(40, 278, 150, 25);

        showPassword.addActionListener(e -> {

            if (showPassword.isSelected()) {
                passwordField.setEchoChar((char) 0);
            } else {
                passwordField.setEchoChar('•');
            }
        });

        // Login button
        JButton loginButton = new RoundedButton(
                "LOGIN",
                BLUE,
                BLUE_HOVER
        );

        loginButton.setBounds(40, 320, 285, 45);

        loginButton.addActionListener(e -> login());

        passwordField.addActionListener(e -> login());

        // Add components
        loginCard.add(loginTitle);
        loginCard.add(loginSubtitle);

        loginCard.add(usernameLabel);
        loginCard.add(usernameField);

        loginCard.add(passwordLabel);
        loginCard.add(passwordField);

        loginCard.add(showPassword);
        loginCard.add(loginButton);

        background.add(leftPanel);
        background.add(loginCard);

        add(background);

        getRootPane().setDefaultButton(loginButton);

        SwingUtilities.invokeLater(() -> usernameField.requestFocus());
    }

    // =========================================================
    // LOGIN METHOD
    // =========================================================

    private void login() {

        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        // Empty validation
        if (username.isEmpty() || password.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter username and password.",
                    "Login Required",
                    JOptionPane.WARNING_MESSAGE
            );

            if (username.isEmpty()) {
                usernameField.requestFocus();
            } else {
                passwordField.requestFocus();
            }

            return;
        }

        try {

            User user = userDAO.login(username, password);

            // Invalid login
            if (user == null) {

                JOptionPane.showMessageDialog(
                        this,
                        "Invalid username or password.",
                        "Login Failed",
                        JOptionPane.ERROR_MESSAGE
                );

                passwordField.setText("");
                passwordField.requestFocus();

                return;
            }

            // Successful login
            dispose();

            SwingUtilities.invokeLater(() -> {

                MainFrame mainFrame = new MainFrame(user);
                mainFrame.setVisible(true);

            });

        } catch (RuntimeException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================================================
    // FIELD STYLE
    // =========================================================

    private void styleField(JTextField field) {

        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setForeground(DARK);
        field.setBackground(FIELD_BG);

        field.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER, 1),
                        new EmptyBorder(5, 12, 5, 12)
                )
        );

        field.setCaretColor(Color.BLACK);
    }

    // =========================================================
    // GRADIENT BACKGROUND
    // =========================================================

    private static class GradientPanel extends JPanel {

        @Override
        protected void paintComponent(Graphics g) {

            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();

            GradientPaint gradient = new GradientPaint(
                    0,
                    0,
                    DARK,
                    getWidth(),
                    getHeight(),
                    new Color(30, 64, 175)
            );

            g2.setPaint(gradient);
            g2.fillRect(0, 0, getWidth(), getHeight());

            // Decorative circles
            g2.setColor(new Color(255, 255, 255, 20));
            g2.fillOval(-100, -100, 300, 300);

            g2.setColor(new Color(255, 255, 255, 15));
            g2.fillOval(250, 390, 300, 300);

            g2.dispose();
        }
    }

    // =========================================================
    // ROUNDED PANEL
    // =========================================================

    private static class RoundedPanel extends JPanel {

        private final int radius;

        public RoundedPanel(int radius) {
            this.radius = radius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {

            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setColor(getBackground());

            g2.fillRoundRect(
                    0,
                    0,
                    getWidth(),
                    getHeight(),
                    radius,
                    radius
            );

            g2.dispose();

            super.paintComponent(g);
        }
    }

    // =========================================================
    // ROUNDED BUTTON
    // =========================================================

    private static class RoundedButton extends JButton {

        private final Color normalColor;
        private final Color hoverColor;

        public RoundedButton(
                String text,
                Color normalColor,
                Color hoverColor
        ) {

            super(text);

            this.normalColor = normalColor;
            this.hoverColor = hoverColor;

            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setForeground(WHITE);
            setBackground(normalColor);

            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {

            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            if (getModel().isPressed()) {
                g2.setColor(hoverColor.darker());
            } else if (getModel().isRollover()) {
                g2.setColor(hoverColor);
            } else {
                g2.setColor(normalColor);
            }

            g2.fillRoundRect(
                    0,
                    0,
                    getWidth(),
                    getHeight(),
                    15,
                    15
            );

            g2.dispose();

            super.paintComponent(g);
        }
    }

    // =========================================================
    // MAIN
    // =========================================================

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            try {
                UIManager.setLookAndFeel(
                        UIManager.getSystemLookAndFeelClassName()
                );
            } catch (Exception ignored) {
            }

            new LoginFrame().setVisible(true);
        });
    }
}