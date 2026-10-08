package GUI;

import DAO.EmployeeDAO;
import DAO.LeaveDAO;
import DAO.PayrollDAO;
import DAO.PartTimeEmployeeDAO;
import model.User;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MainFrame extends JFrame {
    private final EmployeeDAO employeeDAO = new EmployeeDAO();
    private final PartTimeEmployeeDAO partTimeEmployeeDAO = new PartTimeEmployeeDAO();
    private final LeaveDAO leaveDAO = new LeaveDAO();
    private final PayrollDAO payrollDAO = new PayrollDAO();
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel content = new JPanel(cardLayout);
    private final User loggedInUser;
    private final List<JButton> menuButtons = new ArrayList<>();
    private String activePage;

    private DashboardPanel dashboardPanel;
    private EmployeePanel employeePanel;
    private AttendancePanel attendancePanel;
    private PartTimeEmployeePanel partTimeEmployeePanel;
    private LeavePanel leavePanel;
    private PayrollPanel payrollPanel;
    private EmployeeProfilePanel profilePanel;
    private AttendanceSummaryPanel summaryPanel;
    private NotificationPanel notificationPanel;
    private ReportsPanel reportsPanel;

    public MainFrame(User loggedInUser) {
        this.loggedInUser = loggedInUser;
        setTitle("Employee Management System");
        setSize(1240, 760);
        setMinimumSize(new Dimension(1080, 680));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        buildUI();
        showPage(isEmployee() ? "Employee Profile" : "Dashboard");
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Theme.BG);
        root.add(createSidebar(), BorderLayout.WEST);

        boolean employeeRole = isEmployee();
        Integer employeeId = employeeRole ? loggedInUser.getEmployeeId() : null;

        dashboardPanel = new DashboardPanel(employeeDAO);
        employeePanel = new EmployeePanel(employeeDAO);
        attendancePanel = new AttendancePanel(employeeDAO);
        partTimeEmployeePanel = new PartTimeEmployeePanel(partTimeEmployeeDAO);
        leavePanel = new LeavePanel(leaveDAO);
        payrollPanel = new PayrollPanel(payrollDAO);
        profilePanel = new EmployeeProfilePanel(employeeId);
        summaryPanel = new AttendanceSummaryPanel(employeeId);
        notificationPanel = new NotificationPanel(employeeId);
        reportsPanel = new ReportsPanel();

        content.setBackground(Theme.BG);
        content.add(dashboardPanel, "Dashboard");
        content.add(employeePanel, "Employees");
        content.add(partTimeEmployeePanel, "Part-Time Employees");
        content.add(attendancePanel, "Attendance");
        content.add(leavePanel, "Leave Management");
        content.add(payrollPanel, "Payroll Management");
        content.add(profilePanel, "Employee Profile");
        content.add(summaryPanel, "Attendance Summary");
        content.add(notificationPanel, "Notifications");
        content.add(reportsPanel, "Reports");

        root.add(content, BorderLayout.CENTER);
        setContentPane(root);
    }

    private boolean isAdmin() { return "ADMIN".equalsIgnoreCase(loggedInUser.getRole()); }
    private boolean isHR() { return "HR".equalsIgnoreCase(loggedInUser.getRole()); }
    private boolean isEmployee() { return "EMPLOYEE".equalsIgnoreCase(loggedInUser.getRole()); }

    private JPanel createSidebar() {
        JPanel side = new JPanel(new BorderLayout());
        side.setPreferredSize(new Dimension(245, 760));
        side.setBackground(Theme.NAVY);
        side.setBorder(BorderFactory.createEmptyBorder(18, 15, 16, 15));

        // Brand area
        JPanel brand = new JPanel(new BorderLayout(0, 5));
        brand.setOpaque(false);
        brand.setBorder(BorderFactory.createEmptyBorder(5, 4, 14, 4));

        JLabel mark = new JLabel("EM", SwingConstants.CENTER);
        mark.setOpaque(true);
        mark.setBackground(Theme.BLUE);
        mark.setForeground(Color.WHITE);
        mark.setFont(new Font("Segoe UI", Font.BOLD, 19));
        mark.setPreferredSize(new Dimension(58, 58));

        // Use the same supplied image on the dashboard/sidebar brand mark.
        ImageIcon brandIcon = Theme.brandingIcon(58, 58);
        if (brandIcon != null) {
            mark.setText("");
            mark.setIcon(brandIcon);
            mark.setBackground(Theme.NAVY_2);
        }

        brand.add(mark, BorderLayout.WEST);

        JPanel brandText = new JPanel();
        brandText.setOpaque(false);
        brandText.setLayout(new BoxLayout(brandText, BoxLayout.Y_AXIS));
        JLabel name = new JLabel("EMPLOYEE");
        name.setForeground(Color.WHITE);
        name.setFont(new Font("Segoe UI", Font.BOLD, 17));
        JLabel name2 = new JLabel("MANAGEMENT");
        name2.setForeground(Color.WHITE);
        name2.setFont(new Font("Segoe UI", Font.BOLD, 17));
        JLabel system = new JLabel("SMART HR SYSTEM");
        system.setForeground(new Color(148, 163, 184));
        system.setFont(new Font("Segoe UI", Font.PLAIN, 9));
        brandText.add(name); brandText.add(name2); brandText.add(Box.createVerticalStrut(2)); brandText.add(system);
        brand.add(brandText, BorderLayout.CENTER);

        JPanel line = new JPanel();
        line.setBackground(new Color(51, 65, 85));
        line.setPreferredSize(new Dimension(1, 1));
        brand.add(line, BorderLayout.SOUTH);

        JPanel menu = new JPanel();
        menu.setOpaque(false);
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.setBorder(BorderFactory.createEmptyBorder(18, 0, 0, 0));

        if (!isEmployee()) addMenuButton(menu, "Dashboard", "Dashboard","⌂" );
        if (!isEmployee()) {
            addMenuButton(menu, "Employees", "Employees", "♙");
            addMenuButton(menu, "Part-Time Employees", "Part-Time Employees", "◉");
            addMenuButton(menu, "Attendance", "Attendance", "◷");
            addMenuButton(menu, "Leave Management", "Leave Management", "◇");
            addMenuButton(menu, "Payroll Management", "Payroll Management", "▤");
        }
        addMenuButton(menu, "Employee Profile", "Employee Profile", "◎");
        addMenuButton(menu, "Attendance Summary", "Attendance Summary", "▥");
        addMenuButton(menu, "Notifications", "Notifications", "●");
        if (!isEmployee()) addMenuButton(menu, "Reports", "Reports", "▦");

        // User section at the bottom
        JPanel userCard = new JPanel(new BorderLayout(10, 0));
        userCard.setOpaque(true);
        userCard.setBackground(new Color(20, 31, 52));
        userCard.setBorder(BorderFactory.createEmptyBorder(11, 12, 11, 12));

        JLabel avatar = new JLabel(initials(loggedInUser.getUsername()), SwingConstants.CENTER);
        avatar.setOpaque(true);
        avatar.setBackground(Theme.BLUE);
        avatar.setForeground(Color.WHITE);
        avatar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        avatar.setPreferredSize(new Dimension(38, 38));

        JPanel userInfo = new JPanel();
        userInfo.setOpaque(false);
        userInfo.setLayout(new BoxLayout(userInfo, BoxLayout.Y_AXIS));
        JLabel username = new JLabel(escapeHtml(loggedInUser.getUsername()));
        username.setForeground(Color.WHITE);
        username.setFont(new Font("Segoe UI", Font.BOLD, 12));
        JLabel role = new JLabel(escapeHtml(loggedInUser.getRole()));
        role.setForeground(new Color(148, 163, 184));
        role.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        userInfo.add(username); userInfo.add(role);
        userCard.add(avatar, BorderLayout.WEST);
        userCard.add(userInfo, BorderLayout.CENTER);

        JButton logout = Theme.button("LOGOUT", Theme.NAVY_2);
        logout.addActionListener(e -> logout());
        JButton exit = Theme.button("EXIT", Theme.RED);
        exit.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(this, "Exit the application?", "Confirm Exit",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) System.exit(0);
        });

        JPanel bottom = new JPanel();
        bottom.setOpaque(false);
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
        bottom.add(userCard);
        bottom.add(Box.createVerticalStrut(10));
        JPanel actions = new JPanel(new GridLayout(1, 2, 7, 0));
        actions.setOpaque(false);
        actions.add(logout); actions.add(exit);
        bottom.add(actions);

        side.add(brand, BorderLayout.NORTH);
        side.add(new JScrollPane(menu) {{
            setBorder(null);
            setOpaque(false);
            getViewport().setOpaque(false);
            getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
        }}, BorderLayout.CENTER);
        side.add(bottom, BorderLayout.SOUTH);
        return side;
    }

    private String initials(String value) {
        if (value == null || value.trim().isEmpty()) return "U";
        String[] parts = value.trim().split("\\s+");
        if (parts.length == 1) return parts[0].substring(0, 1).toUpperCase();
        return (parts[0].substring(0, 1) + parts[parts.length - 1].substring(0, 1)).toUpperCase();
    }

    private String escapeHtml(String value) {
        if (value == null) return "";
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private void addMenuButton(JPanel menu, String text, String page, String number) {
        JButton button = new JButton();
        button.setText("<html><div style='width:190px'><span style='color:#94A3B8'>" + number +
                "</span>&nbsp;&nbsp;<b>" + escapeHtml(text) + "</b></div></html>");
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(215, 43));
        button.setPreferredSize(new Dimension(215, 43));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(BorderFactory.createEmptyBorder(0, 13, 0, 8));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setForeground(new Color(226, 232, 240));
        button.setBackground(Theme.NAVY);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.addActionListener(e -> showPage(page));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                if (!page.equals(activePage)) button.setOpaque(true);
                button.setBackground(Theme.NAVY_2);
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                if (!page.equals(activePage)) {
                    button.setOpaque(false);
                    button.setBackground(Theme.NAVY);
                }
            }
        });
        menuButtons.add(button);
        menu.add(button);
        menu.add(Box.createVerticalStrut(3));
    }

    private void updateActiveMenu(String page) {
        activePage = page;
        for (JButton button : menuButtons) {
            String html = button.getText();
            boolean active = html != null && html.contains(">" + escapeHtml(page) + "</b>");
            button.setOpaque(active);
            button.setBackground(active ? Theme.BLUE : Theme.NAVY);
            button.setForeground(Color.WHITE);
            button.setBorder(active
                    ? BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 3, 0, 0, Theme.SKY),
                        BorderFactory.createEmptyBorder(0, 10, 0, 8))
                    : BorderFactory.createEmptyBorder(0, 13, 0, 8));
        }
    }

    private void showPage(String page) {
        switch (page) {
            case "Dashboard" -> dashboardPanel.refreshData();
            case "Employees" -> employeePanel.refreshData();
            case "Part-Time Employees" -> partTimeEmployeePanel.refreshData();
            case "Attendance" -> attendancePanel.refreshData();
            case "Leave Management" -> leavePanel.refreshData();
            case "Payroll Management" -> payrollPanel.refreshData();
            case "Employee Profile" -> profilePanel.refreshData();
            case "Attendance Summary" -> summaryPanel.refreshData();
            case "Notifications" -> notificationPanel.refreshData();
            case "Reports" -> reportsPanel.refreshData();
        }
        cardLayout.show(content, page);
        updateActiveMenu(page);
    }

    private void logout() {
        if (JOptionPane.showConfirmDialog(this, "Do you want to logout?", "Confirm Logout",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
            dispose();
            SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
        }
    }

    public PartTimeEmployeeDAO getPartTimeEmployeeDAO() { return partTimeEmployeeDAO; }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception ignored) {}
            new LoginFrame().setVisible(true);
        });
    }
}
