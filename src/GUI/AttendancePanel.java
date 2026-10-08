package GUI;

import DAO.AttendanceDAO;
import DAO.EmployeeDAO;
import model.Attendance;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AttendancePanel extends JPanel {

    private final EmployeeDAO employeeDAO;
    private final AttendanceDAO attendanceDAO = new AttendanceDAO();

    private final JTextField attendanceIdField = Theme.field();
    private final JTextField employeeIdField = Theme.field();
    private final JTextField dateField = Theme.field();
    private final JTextField checkInField = Theme.field();
    private final JTextField checkOutField = Theme.field();

    private final JComboBox<String> statusBox =
            new JComboBox<>(
                    new String[]{
                            "Present",
                            "Absent",
                            "Late",
                            "Leave"
                    }
            );

    private final DefaultTableModel model =
            new DefaultTableModel(
                    new Object[][]{},
                    new String[]{
                            "Attendance ID",
                            "Employee ID",
                            "Date",
                            "Check In",
                            "Check Out",
                            "Status"
                    }
            ) {

                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

    private final JTable table = new JTable(model);


    // =========================
    // CONSTRUCTOR
    // =========================

    public AttendancePanel(EmployeeDAO employeeDAO) {

        this.employeeDAO = employeeDAO;

        setLayout(new BorderLayout(0, 16));
        setBackground(Theme.BG);
        Theme.styleTable(table);
        Theme.styleComboBox(statusBox);

        setBorder(
                BorderFactory.createEmptyBorder(
                        25,
                        28,
                        25,
                        28
                )
        );


        // =========================
        // TITLE
        // =========================

        JLabel title =
                new JLabel("Attendance");

        title.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        27
                )
        );

        title.setForeground(Theme.TEXT);


        JLabel sub =
                new JLabel(
                        "Record and view employee attendance"
                );

        sub.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        13
                )
        );

        sub.setForeground(Theme.MUTED);


        JPanel heading = new JPanel();

        heading.setOpaque(false);

        heading.setLayout(
                new BoxLayout(
                        heading,
                        BoxLayout.Y_AXIS
                )
        );

        heading.add(title);
        heading.add(sub);

        add(
                heading,
                BorderLayout.NORTH
        );


        // =========================
        // FORM CARD
        // =========================

        JPanel form = Theme.card();

        form.setPreferredSize(
                new Dimension(
                        330,
                        0
                )
        );

        form.setLayout(
                new BorderLayout(
                        0,
                        15
                )
        );


        JPanel fields =
                new JPanel(
                        new GridBagLayout()
                );

        fields.setOpaque(false);


        addField(
                fields,
                0,
                "Attendance ID",
                attendanceIdField
        );

        addField(
                fields,
                1,
                "Employee ID",
                employeeIdField
        );

        addField(
                fields,
                2,
                "Date",
                dateField
        );

        addField(
                fields,
                3,
                "Check In",
                checkInField
        );

        addField(
                fields,
                4,
                "Check Out",
                checkOutField
        );

        addField(
                fields,
                5,
                "Status",
                statusBox
        );


        // Default Date

        dateField.setText(
                LocalDate.now().toString()
        );


        // Default Check In Time

        checkInField.setText(
                LocalTime.now().format(
                        DateTimeFormatter.ofPattern(
                                "HH:mm"
                        )
                )
        );


        // =========================
        // BUTTONS
        // =========================

        JButton save =
                Theme.button(
                        "SAVE ATTENDANCE",
                        Theme.BLUE
                );

        JButton clear =
                Theme.button(
                        "CLEAR",
                        Theme.NAVY_2
                );


        save.addActionListener(
                e -> saveAttendance()
        );

        clear.addActionListener(
                e -> clearForm()
        );


        JPanel buttons =
                new JPanel(
                        new GridLayout(
                                1,
                                2,
                                8,
                                8
                        )
                );

        buttons.setOpaque(false);

        buttons.add(save);
        buttons.add(clear);


        form.add(
                fields,
                BorderLayout.CENTER
        );

        form.add(
                buttons,
                BorderLayout.SOUTH
        );


        // =========================
        // RECORDS TABLE
        // =========================

        JPanel records = Theme.card();

        records.setLayout(
                new BorderLayout(
                        0,
                        10
                )
        );


        JLabel tableTitle =
                Theme.label(
                        "Attendance Records"
                );

        tableTitle.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        18
                )
        );


        table.setRowHeight(30);

        table.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        12
                )
        );

        table.getTableHeader().setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        12
                )
        );

        table.setAutoCreateRowSorter(true);


        records.add(
                tableTitle,
                BorderLayout.NORTH
        );

        records.add(
                new JScrollPane(table),
                BorderLayout.CENTER
        );


        // =========================
        // SPLIT PANEL
        // =========================

        JSplitPane split =
                new JSplitPane(
                        JSplitPane.HORIZONTAL_SPLIT,
                        form,
                        records
                );

        split.setResizeWeight(0.28);

        split.setDividerSize(8);

        split.setBorder(null);


        add(
                split,
                BorderLayout.CENTER
        );
    }


    // =========================
    // ADD FIELD
    // =========================

    private void addField(
            JPanel panel,
            int row,
            String label,
            JComponent field
    ) {

        GridBagConstraints g =
                new GridBagConstraints();

        g.insets =
                new Insets(
                        5,
                        3,
                        5,
                        8
                );

        g.fill =
                GridBagConstraints.HORIZONTAL;

        g.gridx = 0;
        g.gridy = row;

        panel.add(
                Theme.label(label),
                g
        );


        g.gridx = 1;

        g.weightx = 1;


        if (field instanceof JTextField) {

            JTextField textField =
                    (JTextField) field;

            textField.setPreferredSize(
                    new Dimension(
                            160,
                            35
                    )
            );
        }


        panel.add(
                field,
                g
        );
    }


    // =========================
    // REFRESH DATA
    // =========================

    public void refreshData() {

        try {

            List<Object[]> rows =
                    attendanceDAO.getAllAttendance();

            model.setRowCount(0);


            for (Object[] row : rows) {

                model.addRow(row);
            }


        } catch (RuntimeException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Attendance table may not exist yet.\n\n"
                            + ex.getMessage(),
                    "Attendance Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }


    // =========================
    // SAVE ATTENDANCE
    // =========================

    private void saveAttendance() {

        try {

            // Check empty fields

            if (
                    attendanceIdField
                            .getText()
                            .trim()
                            .isEmpty()
                            ||
                            employeeIdField
                                    .getText()
                                    .trim()
                                    .isEmpty()
                            ||
                            dateField
                                    .getText()
                                    .trim()
                                    .isEmpty()
            ) {

                throw new IllegalArgumentException(
                        "Attendance ID, Employee ID and Date are required."
                );
            }


            // Convert IDs to integers

            int attendanceId =
                    Integer.parseInt(
                            attendanceIdField
                                    .getText()
                                    .trim()
                    );


            int employeeId =
                    Integer.parseInt(
                            employeeIdField
                                    .getText()
                                    .trim()
                    );


            // Check employee exists

            if (
                    employeeDAO
                            .getEmployeeById(employeeId)
                            == null
            ) {

                throw new IllegalArgumentException(
                        "Employee ID does not exist."
                );
            }


            // Create Attendance object

            Attendance attendance =
                    new Attendance(
                            attendanceId,
                            employeeId,
                            dateField
                                    .getText()
                                    .trim(),

                            checkInField
                                    .getText()
                                    .trim(),

                            checkOutField
                                    .getText()
                                    .trim(),

                            statusBox
                                    .getSelectedItem()
                                    .toString()
                    );


            // Save to database

            if (
                    attendanceDAO
                            .addAttendance(attendance)
            ) {

                JOptionPane.showMessageDialog(
                        this,
                        "Attendance saved successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );


                clearForm();

                refreshData();
            }


        } catch (NumberFormatException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Attendance ID and Employee ID must be whole numbers.",
                    "Invalid Input",
                    JOptionPane.ERROR_MESSAGE
            );


        } catch (IllegalArgumentException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Invalid Input",
                    JOptionPane.ERROR_MESSAGE
            );


        } catch (RuntimeException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }


    // =========================
    // CLEAR FORM
    // =========================

    private void clearForm() {

        attendanceIdField.setText("");

        employeeIdField.setText("");


        dateField.setText(
                LocalDate.now().toString()
        );


        checkInField.setText(
                LocalTime.now().format(
                        DateTimeFormatter.ofPattern(
                                "HH:mm"
                        )
                )
        );


        checkOutField.setText("");


        statusBox.setSelectedIndex(0);
    }
}