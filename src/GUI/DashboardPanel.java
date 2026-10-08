package GUI;

import DAO.EmployeeDAO;
import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class DashboardPanel extends JPanel {
    private final EmployeeDAO dao;
    private final JLabel employeeCount = new JLabel("0");
    private final JLabel salaryTotal = new JLabel("Rs. 0.00");
    private final JLabel status = new JLabel("Ready");

    public DashboardPanel(EmployeeDAO dao) {
        this.dao = dao;
        setLayout(new BorderLayout(0, 20));
        setBackground(Theme.BG);
        setBorder(BorderFactory.createEmptyBorder(28, 30, 28, 30));

        JLabel title = new JLabel("Dashboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(Theme.TEXT);

        JLabel subtitle = new JLabel("Overview of your employee management system");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(Theme.MUTED);

        JPanel heading = new JPanel(new BorderLayout());
        heading.setOpaque(false);
        heading.add(title, BorderLayout.NORTH);
        heading.add(subtitle, BorderLayout.SOUTH);

        JPanel stats = new JPanel(new GridLayout(1, 2, 18, 0));
        stats.setOpaque(false);
        stats.add(statCard("TOTAL EMPLOYEES", employeeCount, Theme.BLUE));
        stats.add(statCard("TOTAL PAYROLL", salaryTotal, Theme.GREEN));

        JPanel top = new JPanel(new BorderLayout(0, 18));
        top.setOpaque(false);
        top.add(heading, BorderLayout.NORTH);
        top.add(stats, BorderLayout.CENTER);

        JPanel recent = Theme.card();
        recent.setLayout(new BorderLayout(10, 10));

        JLabel recentTitle = Theme.label("Recent Employees");
        recentTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));

        JTable table = new JTable();
        table.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{}, new String[]{"ID", "Name", "Department", "Position", "Salary"}
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        });
        styleTable(table);

        recent.add(recentTitle, BorderLayout.NORTH);
        recent.add(new JScrollPane(table), BorderLayout.CENTER);
        recent.add(status, BorderLayout.SOUTH);

        add(top, BorderLayout.NORTH);
        add(recent, BorderLayout.CENTER);

        this.table = table;
    }

    private JTable table;

    private JPanel statCard(String title, JLabel value, Color accent) {
        JPanel card = Theme.card();
        card.setLayout(new BorderLayout(10, 5));

        JLabel heading = new JLabel(title);
        heading.setForeground(Theme.MUTED);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 12));

        value.setForeground(accent);
        value.setFont(new Font("Segoe UI", Font.BOLD, 28));

        card.add(heading, BorderLayout.NORTH);
        card.add(value, BorderLayout.CENTER);
        return card;
    }

    public void refreshData() {
        try {
            employeeCount.setText(String.valueOf(dao.getEmployeeCount()));
            salaryTotal.setText(formatMoney(dao.getTotalSalary()));

            List<Object[]> rows = dao.getAllEmployees();
            var model = (javax.swing.table.DefaultTableModel) table.getModel();
            model.setRowCount(0);

            int start = Math.max(0, rows.size() - 8);
            for (int i = rows.size() - 1; i >= start; i--) {
                Object[] r = rows.get(i);
                model.addRow(new Object[]{
                        r[0], r[1], r[2], r[3], formatMoney(((Number) r[7]).doubleValue())
                });
            }
            status.setText(rows.size() + " employee(s) in database");
        } catch (RuntimeException ex) {
            status.setText("Database not available");
        }
    }

    private String formatMoney(double value) {
        return "Rs. " + NumberFormat.getNumberInstance(Locale.US)
                .format(value);
    }

    private void styleTable(JTable table) {
        table.setRowHeight(32);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true);
    }
}
