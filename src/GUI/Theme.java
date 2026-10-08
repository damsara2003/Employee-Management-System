package GUI;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.JTableHeader;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.net.URL;

/** Central visual theme for the whole Employee Management System. */
public final class Theme {
    public static final Color NAVY = new Color(5, 16, 36);
    public static final Color NAVY_2 = new Color(12, 35, 68);
    public static final Color NAVY_3 = new Color(18, 55, 100);
    public static final Color BLUE = new Color(37, 99, 235);
    public static final Color BLUE_HOVER = new Color(29, 78, 216);
    public static final Color SKY = new Color(14, 165, 233);
    public static final Color GREEN = new Color(16, 185, 129);
    public static final Color GREEN_HOVER = new Color(5, 150, 105);
    public static final Color RED = new Color(239, 68, 68);
    public static final Color RED_HOVER = new Color(220, 38, 38);
    public static final Color ORANGE = new Color(245, 158, 11);
    public static final Color PURPLE = new Color(124, 58, 237);
    public static final Color BG = new Color(7, 25, 50);
    public static final Color CARD = new Color(13, 42, 78);
    public static final Color TEXT = new Color(241, 248, 255);
    public static final Color MUTED = new Color(164, 190, 218);
    public static final Color BORDER = new Color(43, 79, 120);
    public static final Color FIELD_BG = new Color(225, 239, 253);
    public static final Color TABLE_HEADER = new Color(178, 216, 249);
    public static final Color TABLE_ALT = new Color(235, 246, 255);


    /**
     * Loads the project branding image from the bundled assets folder.
     * The file is also resolved from the project root so it works directly
     * when the project is run from IntelliJ IDEA.
     */
    public static ImageIcon brandingIcon(int width, int height) {
        BufferedImage source = null;
        try {
            URL url = Theme.class.getResource("/assets/employee_network.jpg");
            if (url != null) {
                source = ImageIO.read(url);
            }
            if (source == null) {
                File file = new File("assets/employee_network.jpg");
                if (!file.exists()) {
                    file = new File("src/assets/employee_network.jpg");
                }
                if (file.exists()) {
                    source = ImageIO.read(file);
                }
            }
        } catch (IOException ignored) {
        }

        if (source == null) {
            return null;
        }

        int side = Math.min(source.getWidth(), source.getHeight());
        int x = (source.getWidth() - side) / 2;
        int y = (source.getHeight() - side) / 2;

        BufferedImage square = new BufferedImage(
                width, height, BufferedImage.TYPE_INT_ARGB
        );

        Graphics2D g2 = square.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        Shape clip = new java.awt.geom.RoundRectangle2D.Double(
                0, 0, width, height, 18, 18
        );
        g2.setClip(clip);
        g2.drawImage(
                source.getSubimage(x, y, side, side),
                0, 0, width, height, null
        );
        g2.dispose();

        return new ImageIcon(square);
    }

    private Theme() {}

    public static JTextField field() {
        JTextField field = new JTextField();
        styleField(field);
        return field;
    }

    public static void styleField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setForeground(Color.BLACK);
        field.setBackground(FIELD_BG);
        field.setCaretColor(Color.BLACK);
        field.setOpaque(true);
        field.setBorder(fieldBorder(new Color(73, 125, 180)));
        field.setPreferredSize(new Dimension(250, 40));

        field.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                field.setBorder(fieldBorder(new Color(45, 137, 239)));
                field.setBackground(new Color(240, 247, 255));
            }
            @Override public void focusLost(FocusEvent e) {
                field.setBorder(fieldBorder(new Color(73, 125, 180)));
                field.setBackground(FIELD_BG);
            }
        });
    }

    private static Border fieldBorder(Color color) {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 1),
                BorderFactory.createEmptyBorder(7, 11, 7, 11)
        );
    }

    public static JButton button(String text, Color color) {
        Color hover = color;
        if (color.equals(BLUE)) hover = BLUE_HOVER;
        else if (color.equals(GREEN)) hover = GREEN_HOVER;
        else if (color.equals(RED)) hover = RED_HOVER;
        else if (color.equals(NAVY_2)) hover = NAVY_3;
        return new RoundedButton(text, color, hover);
    }

    public static JButton outlineButton(String text, Color color) {
        JButton button = new OutlineButton(text, color);
        return button;
    }

    public static JLabel label(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(TEXT);
        return label;
    }

    public static JPanel card() {
        return new RoundedCard();
    }

    public static void styleComboBox(JComboBox<?> combo) {
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        combo.setForeground(new Color(8, 28, 55));
        combo.setBackground(new Color(225, 239, 253));
        combo.setBorder(BorderFactory.createLineBorder(new Color(91, 155, 210)));
        combo.setPreferredSize(new Dimension(170, 38));
    }

    public static void styleTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setForeground(new Color(8, 28, 55));
        table.setBackground(new Color(225, 239, 253));
        table.setSelectionBackground(new Color(96, 165, 250));
        table.setSelectionForeground(Color.WHITE);
        table.setGridColor(new Color(166, 202, 232));
        table.setRowHeight(32);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setAutoCreateRowSorter(true);

        // Light-blue professional row styling with subtle alternating rows.
        DefaultTableCellRenderer bodyRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean selected,
                                                           boolean focused, int row, int column) {
                Component c = super.getTableCellRendererComponent(t, value, selected, focused, row, column);
                if (selected) {
                    c.setBackground(new Color(96, 165, 250));
                    c.setForeground(Color.WHITE);
                } else {
                    c.setBackground(row % 2 == 0 ? new Color(225, 239, 253) : new Color(237, 247, 255));
                    c.setForeground(new Color(8, 28, 55));
                }
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return c;
            }
        };
        table.setDefaultRenderer(Object.class, bodyRenderer);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setForeground(new Color(6, 31, 61));
        header.setBackground(TABLE_HEADER);
        header.setOpaque(true);
        header.setPreferredSize(new Dimension(header.getWidth(), 40));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));
    }

    public static JScrollPane scrollPane(Component component) {
        JScrollPane scroll = new JScrollPane(component);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER));
        scroll.getViewport().setBackground(new Color(225, 239, 253));
        scroll.setBackground(new Color(225, 239, 253));
        return scroll;
    }

    public static JLabel pageTitle(String title) {
        JLabel label = new JLabel(title);
        label.setFont(new Font("Segoe UI", Font.BOLD, 28));
        label.setForeground(TEXT);
        return label;
    }

    public static JLabel pageSubtitle(String subtitle) {
        JLabel label = new JLabel(subtitle);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(MUTED);
        return label;
    }

    private static class RoundedCard extends JPanel {
        RoundedCard() {
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth() - 1;
            int h = getHeight() - 1;
            g2.setColor(new Color(0, 0, 0, 70));
            g2.fillRoundRect(3, 5, Math.max(0, w - 3), Math.max(0, h - 3), 22, 22);
            g2.setColor(CARD);
            g2.fillRoundRect(0, 0, w, h, 22, 22);
            g2.setColor(new Color(67, 110, 157));
            g2.drawRoundRect(0, 0, w, h, 20, 20);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class RoundedButton extends JButton {
        private final Color normal;
        private final Color hover;

        RoundedButton(String text, Color normal, Color hover) {
            super(text);
            this.normal = normal;
            this.hover = hover;
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setForeground(Color.WHITE);
            setBackground(normal);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setMargin(new Insets(0, 14, 0, 14));
            setPreferredSize(new Dimension(145, 40));
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color c = getModel().isPressed() ? hover.darker()
                    : getModel().isRollover() ? hover : normal;

            // Soft button shadow
            g2.setColor(new Color(15, 23, 42, 28));
            g2.fillRoundRect(1, 3, Math.max(0, getWidth() - 2),
                    Math.max(0, getHeight() - 2), 14, 14);

            g2.setColor(c);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class OutlineButton extends JButton {
        private final Color color;
        OutlineButton(String text, Color color) {
            super(text);
            this.color = color;
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setForeground(color);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getModel().isRollover() ? new Color(color.getRed(), color.getGreen(), color.getBlue(), 18) : new Color(255,255,255,0));
            g2.fillRoundRect(0,0,getWidth(),getHeight(),12,12);
            g2.setColor(color);
            g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,12,12);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
