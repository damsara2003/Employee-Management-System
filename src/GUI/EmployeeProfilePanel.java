package GUI;

import DAO.EmployeeProfileDAO;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;

public class EmployeeProfilePanel extends JPanel {
    private final EmployeeProfileDAO dao = new EmployeeProfileDAO();
    private final JTextField employeeIdField = Theme.field();
    private final JLabel photoLabel = new JLabel("No Photo", SwingConstants.CENTER);
    private final JLabel[] valueLabels = new JLabel[8];
    private byte[] selectedPhoto;
    private final Integer lockedEmployeeId;

    public EmployeeProfilePanel(Integer lockedEmployeeId) {
        this.lockedEmployeeId = lockedEmployeeId;
        setLayout(new BorderLayout(0, 16));
        setBackground(Theme.BG);
        setBorder(BorderFactory.createEmptyBorder(25, 28, 25, 28));

        JLabel title = new JLabel("Employee Profile");
        title.setFont(new Font("Segoe UI", Font.BOLD, 27));
        title.setForeground(Theme.TEXT);
        JLabel sub = new JLabel("View complete employee details and manage profile photo");
        sub.setForeground(Theme.MUTED);
        JPanel heading = new JPanel();
        heading.setOpaque(false);
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
        heading.add(title); heading.add(sub);
        add(heading, BorderLayout.NORTH);

        JPanel main = new JPanel(new BorderLayout(18, 0));
        main.setOpaque(false);

        JPanel left = Theme.card();
        left.setPreferredSize(new Dimension(260, 0));
        left.setLayout(new BorderLayout(10, 15));
        photoLabel.setPreferredSize(new Dimension(200, 210));
        photoLabel.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        photoLabel.setForeground(Theme.MUTED);
        left.add(photoLabel, BorderLayout.CENTER);

        JPanel photoButtons = new JPanel(new GridLayout(2, 1, 0, 8));
        photoButtons.setOpaque(false);
        JButton choose = Theme.button("SELECT PHOTO", Theme.BLUE);
        JButton save = Theme.button("SAVE PHOTO", Theme.GREEN);
        choose.addActionListener(e -> choosePhoto());
        save.addActionListener(e -> savePhoto());
        photoButtons.add(choose); photoButtons.add(save);
        left.add(photoButtons, BorderLayout.SOUTH);

        JPanel right = Theme.card();
        right.setLayout(new BorderLayout(12, 12));
        JPanel search = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        search.setOpaque(false);
        search.add(Theme.label("Employee ID"));
        if (lockedEmployeeId != null) employeeIdField.setText(String.valueOf(lockedEmployeeId));
        employeeIdField.setEnabled(lockedEmployeeId == null);
        search.add(employeeIdField);
        JButton searchButton = Theme.button("SEARCH", Theme.BLUE);
        searchButton.addActionListener(e -> loadProfile());
        search.add(searchButton);
        right.add(search, BorderLayout.NORTH);

        JPanel details = new JPanel(new GridBagLayout());
        details.setOpaque(false);
        String[] labels = {"Name", "Address", "Phone", "Email", "Department", "Position", "Employee Type", "Salary"};
        for (int i = 0; i < labels.length; i++) {
            valueLabels[i] = new JLabel("-");
            valueLabels[i].setFont(new Font("Segoe UI", Font.PLAIN, 14));
            addRow(details, i, labels[i], valueLabels[i]);
        }
        right.add(new JScrollPane(details), BorderLayout.CENTER);
        main.add(left, BorderLayout.WEST);
        main.add(right, BorderLayout.CENTER);
        add(main, BorderLayout.CENTER);

        if (lockedEmployeeId != null) SwingUtilities.invokeLater(this::loadProfile);
    }

    private void addRow(JPanel panel, int row, String label, JLabel value) {
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.gridy = row; g.weightx = 0; g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(7, 7, 7, 15);
        panel.add(Theme.label(label), g);
        g.gridx = 1; g.weightx = 1; g.insets = new Insets(7, 7, 7, 7);
        panel.add(value, g);
    }

    private int employeeId() {
        try { return Integer.parseInt(employeeIdField.getText().trim()); }
        catch (NumberFormatException e) { throw new IllegalArgumentException("Employee ID must be a whole number."); }
    }

    private void loadProfile() {
        try {
            int id = employeeId();
            Object[] p = dao.getProfile(id);
            if (p == null) { error("Employee ID does not exist."); return; }
            valueLabels[0].setText(String.valueOf(p[1]));
            valueLabels[1].setText(String.valueOf(p[2]));
            valueLabels[2].setText(String.valueOf(p[3]));
            valueLabels[3].setText(String.valueOf(p[4]));
            valueLabels[4].setText(String.valueOf(p[5]));
            valueLabels[5].setText(String.valueOf(p[6]));
            valueLabels[6].setText(String.valueOf(p[7]));
            valueLabels[7].setText(String.format("Rs. %.2f + Rs. %.2f allowance", ((Number)p[8]).doubleValue(), ((Number)p[9]).doubleValue()));
            selectedPhoto = (byte[]) p[10];
            displayPhoto(selectedPhoto);
        } catch (RuntimeException e) { error(e.getMessage()); }
    }

    private void choosePhoto() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File file = chooser.getSelectedFile();
                selectedPhoto = Files.readAllBytes(file.toPath());
                displayPhoto(selectedPhoto);
            } catch (Exception e) { error("Could not read image: " + e.getMessage()); }
        }
    }

    private void displayPhoto(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            photoLabel.setIcon(null); photoLabel.setText("No Photo"); return;
        }
        ImageIcon original = new ImageIcon(bytes);
        Image image = original.getImage().getScaledInstance(200, 210, Image.SCALE_SMOOTH);
        photoLabel.setText(""); photoLabel.setIcon(new ImageIcon(image));
    }

    private void savePhoto() {
        try {
            if (selectedPhoto == null) { error("Select a photo first."); return; }
            if (dao.savePhoto(employeeId(), selectedPhoto)) {
                JOptionPane.showMessageDialog(this, "Employee photo saved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (RuntimeException e) { error(e.getMessage()); }
    }

    public void refreshData() { if (!employeeIdField.getText().trim().isEmpty()) loadProfile(); }
    private void error(String message) { JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE); }
}
