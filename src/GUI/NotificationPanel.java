package GUI;

import DAO.NotificationDAO;
import model.Notification;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class NotificationPanel extends JPanel {
    private final NotificationDAO dao = new NotificationDAO();
    private final Integer lockedEmployeeId;
    private final DefaultTableModel model = new DefaultTableModel(
            new Object[][]{}, new String[]{"ID","Type","Title","Message","Created","Read"}) {
        public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(model);

    public NotificationPanel(Integer lockedEmployeeId) {
        this.lockedEmployeeId = lockedEmployeeId;
        setLayout(new BorderLayout(0, 16)); setBackground(Theme.BG);
        Theme.styleTable(table);
        setBorder(BorderFactory.createEmptyBorder(25, 28, 25, 28));
        JLabel title = new JLabel("Notifications"); title.setFont(new Font("Segoe UI", Font.BOLD, 27)); title.setForeground(Theme.TEXT);
        JLabel sub = new JLabel("Late attendance and pending leave alerts"); sub.setForeground(Theme.MUTED);
        JPanel heading = new JPanel(); heading.setOpaque(false); heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS)); heading.add(title); heading.add(sub);
        add(heading, BorderLayout.NORTH);

        JPanel card = Theme.card(); card.setLayout(new BorderLayout(10, 10));
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0)); buttons.setOpaque(false);
        JButton refresh = Theme.button("REFRESH", Theme.BLUE); refresh.addActionListener(e -> refreshData());
        JButton read = Theme.button("MARK AS READ", Theme.GREEN); read.addActionListener(e -> markRead());
        buttons.add(refresh); buttons.add(read); card.add(buttons, BorderLayout.NORTH);
        table.setRowHeight(30); table.getColumnModel().getColumn(3).setPreferredWidth(420);
        card.add(new JScrollPane(table), BorderLayout.CENTER); add(card, BorderLayout.CENTER);
    }

    private void markRead() {
        int row = table.getSelectedRow(); if (row < 0) { error("Select a notification first."); return; }
        int id = Integer.parseInt(model.getValueAt(table.convertRowIndexToModel(row), 0).toString());
        if (dao.markAsRead(id)) refreshData();
    }
    public void refreshData() {
        try {
            dao.generateAutomaticNotifications();
            List<Notification> list = dao.getNotifications(lockedEmployeeId, false);
            model.setRowCount(0);
            for (Notification n : list) model.addRow(new Object[]{n.getNotificationId(),n.getNotificationType(),n.getTitle(),n.getMessage(),n.getCreatedAt(),n.isRead()?"Yes":"No"});
        } catch (RuntimeException e) { error(e.getMessage()); }
    }
    private void error(String m) { JOptionPane.showMessageDialog(this, m, "Error", JOptionPane.ERROR_MESSAGE); }
}
