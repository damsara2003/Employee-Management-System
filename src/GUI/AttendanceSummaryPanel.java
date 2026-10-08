package GUI;

import DAO.AttendanceSummaryDAO;

import javax.swing.*;
import java.awt.*;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class AttendanceSummaryPanel extends JPanel {
    private final AttendanceSummaryDAO dao = new AttendanceSummaryDAO();
    private final JTextField employeeIdField = Theme.field();
    private final JTextField monthField = Theme.field();
    private final JLabel present = stat("0");
    private final JLabel absent = stat("0");
    private final JLabel late = stat("0");
    private final JLabel leave = stat("0");
    private final Integer lockedEmployeeId;

    public AttendanceSummaryPanel(Integer lockedEmployeeId) {
        this.lockedEmployeeId = lockedEmployeeId;
        setLayout(new BorderLayout(0, 16));
        setBackground(Theme.BG);
        setBorder(BorderFactory.createEmptyBorder(25, 28, 25, 28));
        JLabel title = new JLabel("Attendance Summary");
        title.setFont(new Font("Segoe UI", Font.BOLD, 27));
        title.setForeground(Theme.TEXT);
        JLabel sub = new JLabel("Monthly present, absent, late and leave summary");
        sub.setForeground(Theme.MUTED);
        JPanel heading = new JPanel(); heading.setOpaque(false);
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS)); heading.add(title); heading.add(sub);
        add(heading, BorderLayout.NORTH);

        JPanel card = Theme.card(); card.setLayout(new BorderLayout(20, 20));
        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0)); form.setOpaque(false);
        form.add(Theme.label("Employee ID"));
        if (lockedEmployeeId != null) employeeIdField.setText(String.valueOf(lockedEmployeeId));
        employeeIdField.setEnabled(lockedEmployeeId == null);
        form.add(employeeIdField);
        form.add(Theme.label("Month (YYYY-MM)"));
        monthField.setText(YearMonth.now().format(DateTimeFormatter.ofPattern("yyyy-MM")));
        form.add(monthField);
        JButton check = Theme.button("CHECK", Theme.BLUE); check.addActionListener(e -> loadSummary()); form.add(check);
        card.add(form, BorderLayout.NORTH);

        JPanel stats = new JPanel(new GridLayout(1, 4, 15, 0)); stats.setOpaque(false);
        stats.add(statCard("PRESENT", present, Theme.GREEN));
        stats.add(statCard("ABSENT", absent, Theme.RED));
        stats.add(statCard("LATE", late, Theme.BLUE));
        stats.add(statCard("LEAVE", leave, Theme.NAVY_2));
        card.add(stats, BorderLayout.CENTER);
        add(card, BorderLayout.CENTER);
        if (lockedEmployeeId != null) SwingUtilities.invokeLater(this::loadSummary);
    }

    private JLabel stat(String value) { JLabel l = new JLabel(value, SwingConstants.CENTER); l.setFont(new Font("Segoe UI", Font.BOLD, 28)); l.setForeground(Theme.TEXT); return l; }
    private JPanel statCard(String title, JLabel value, Color color) {
        JPanel p = Theme.card(); p.setLayout(new BorderLayout(5, 5));
        JLabel t = new JLabel(title, SwingConstants.CENTER); t.setFont(new Font("Segoe UI", Font.BOLD, 12)); t.setForeground(color);
        p.add(t, BorderLayout.NORTH); p.add(value, BorderLayout.CENTER); return p;
    }
    private void loadSummary() {
        try {
            int id = Integer.parseInt(employeeIdField.getText().trim());
            YearMonth ym;
            try { ym = YearMonth.parse(monthField.getText().trim(), DateTimeFormatter.ofPattern("yyyy-MM")); }
            catch (DateTimeParseException e) { throw new IllegalArgumentException("Use month format YYYY-MM."); }
            Object[] s = dao.getMonthlySummary(id, ym.getYear(), ym.getMonthValue());
            present.setText(String.valueOf(s[0])); absent.setText(String.valueOf(s[1]));
            late.setText(String.valueOf(s[2])); leave.setText(String.valueOf(s[3]));
        } catch (RuntimeException e) { JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); }
    }
    public void refreshData() { if (!employeeIdField.getText().trim().isEmpty()) loadSummary(); }
}
