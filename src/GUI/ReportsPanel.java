package GUI;

import DAO.ReportDAO;
import model.ReportData;
import util.ReportExporter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;

public class ReportsPanel extends JPanel {
    private final ReportDAO dao = new ReportDAO();
    private final JComboBox<String> typeBox = new JComboBox<>(new String[]{"Employees","Attendance","Leave","Payroll"});
    private final DefaultTableModel model = new DefaultTableModel() { public boolean isCellEditable(int r,int c){return false;} };
    private final JTable table = new JTable(model);
    private ReportData current;

    public ReportsPanel() {
        setLayout(new BorderLayout(0, 16)); setBackground(Theme.BG);
        Theme.styleTable(table);
        Theme.styleComboBox(typeBox);
        setBorder(BorderFactory.createEmptyBorder(25, 28, 25, 28));
        JLabel title = new JLabel("Reports"); title.setFont(new Font("Segoe UI", Font.BOLD, 27)); title.setForeground(Theme.TEXT);
        JLabel sub = new JLabel("View and export employee, attendance, leave and payroll reports"); sub.setForeground(Theme.MUTED);
        JPanel heading = new JPanel(); heading.setOpaque(false); heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS)); heading.add(title); heading.add(sub); add(heading, BorderLayout.NORTH);

        JPanel card = Theme.card(); card.setLayout(new BorderLayout(10, 10));
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0)); top.setOpaque(false);
        top.add(Theme.label("Report")); top.add(typeBox);
        JButton load = Theme.button("LOAD REPORT", Theme.BLUE); load.addActionListener(e -> loadReport());
        JButton csv = Theme.button("EXPORT CSV", Theme.GREEN); csv.addActionListener(e -> exportCsv());
        JButton pdf = Theme.button("EXPORT PDF", Theme.NAVY_2); pdf.addActionListener(e -> exportPdf());
        top.add(load); top.add(csv); top.add(pdf); card.add(top, BorderLayout.NORTH);
        card.add(new JScrollPane(table), BorderLayout.CENTER); add(card, BorderLayout.CENTER);
        loadReport();
    }

    private void loadReport() {
        try {
            current = dao.getReport(typeBox.getSelectedItem().toString());
            model.setRowCount(0); model.setColumnCount(0);
            for (String h : current.getHeaders()) model.addColumn(h);
            for (Object[] row : current.getRows()) model.addRow(row);
        } catch (RuntimeException e) { error(e.getMessage()); }
    }
    private void exportCsv() {
        if (current == null) loadReport(); if (current == null) return;
        JFileChooser chooser = new JFileChooser(); chooser.setSelectedFile(new File(current.getTitle().replace(' ','_') + "_" + ReportExporter.timestamp() + ".csv"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) try { ReportExporter.exportCsv(current, chooser.getSelectedFile()); success("CSV exported successfully."); } catch (Exception e) { error(e.getMessage()); }
    }
    private void exportPdf() {
        if (current == null) loadReport(); if (current == null) return;
        JFileChooser chooser = new JFileChooser(); chooser.setSelectedFile(new File(current.getTitle().replace(' ','_') + "_" + ReportExporter.timestamp() + ".pdf"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) try { ReportExporter.exportPdf(current, chooser.getSelectedFile()); success("PDF exported successfully."); } catch (Exception e) { error(e.getMessage()); }
    }
    private void success(String m) { JOptionPane.showMessageDialog(this, m, "Success", JOptionPane.INFORMATION_MESSAGE); }
    private void error(String m) { JOptionPane.showMessageDialog(this, m, "Error", JOptionPane.ERROR_MESSAGE); }
    public void refreshData() { loadReport(); }
}
