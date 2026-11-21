

package com.mbogo.sufeeds;
import javax.swing.JFrame;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Classes extends JFrame implements ActionListener {
    private int studentID;

    private JComboBox<ClassItem> cmbClasses;
    private JComboBox<String> cmbSemester;
    private JButton btnBack, btnView, btnSave;
    private JTextArea txtClasses;

    public Classes(int studentID) {
        this.studentID = studentID;

        setTitle("SU Feeds - My Classes");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Class dropdown
        cmbClasses = new JComboBox<>();

        // Semester dropdown
        cmbSemester = new JComboBox<>(new String[]{"Semester 1","Semester 2","Semester 3"});

        // Buttons
        btnSave = new JButton("Save Class");
        btnView = new JButton("View Classes");
        btnBack = new JButton("Back");

        // Form panel
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));

        panel.setBorder(
            BorderFactory.createEmptyBorder(15, 15, 15, 15));

        panel.add(new JLabel("Select Class:"));
        panel.add(cmbClasses);

        panel.add(new JLabel("Select Semester:"));
        panel.add(cmbSemester);

        panel.add(btnSave);
        panel.add(btnView);

        panel.add(btnBack);
        panel.add(new JLabel(""));

        // Display area
        txtClasses = new JTextArea();
        txtClasses.setEditable(false);

        add(panel, BorderLayout.NORTH);
        add(new JScrollPane(txtClasses), BorderLayout.CENTER);

        // Button listeners
        btnSave.addActionListener(this);
        btnView.addActionListener(this);
        btnBack.addActionListener(this);

        // Load available classes
        loadClasses();
    }
    // ClassItem stores the actual database class ID
    private static class ClassItem {
        int classID;
        String classCode;
        String className;

        public ClassItem(int id, String code, String name) {
            this.classID = id;
            this.classCode = code;
            this.className = name;}

        @Override
        public String toString() {
            return classCode + " - " + className;
        }
    }
    // Load all available classes from the database
    private void loadClasses() {
        String sql = "SELECT Class_id, class_code, class_name "+ "FROM tbl_classes ORDER BY class_code";

        try (Connection conn = Connections.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            cmbClasses.removeAllItems();

            while (rs.next()) {
                int id = rs.getInt("Class_id");
                String code = rs.getString("class_code");
                String name = rs.getString("class_name");

                cmbClasses.addItem(
                    new ClassItem(id, code, name));}

            if (cmbClasses.getItemCount() == 0) {
                JOptionPane.showMessageDialog(this,
                    "No classes are currently available.");
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Error loading classes: " + ex.getMessage());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Back button
        if (e.getSource() == btnBack) {
            dispose();
        }
        // Save Class button
        else if (e.getSource() == btnSave) {

            ClassItem selectedClass = (ClassItem) cmbClasses.getSelectedItem();

            if (selectedClass == null) {
                JOptionPane.showMessageDialog(this,
                    "Please select a class.");
                return;
            }

            String semester =(String) cmbSemester.getSelectedItem();

            if (semester == null) {
                JOptionPane.showMessageDialog(this,"Please select a semester.");
                return;
            }

            int classID = selectedClass.classID;
            // Check for duplicate registration
            String check = "SELECT * FROM tbl_student_classes "  + "WHERE StudentID = ? " + "AND Class_id = ? AND semester = ?";

            try (Connection conn = Connections.getConnection();
                 PreparedStatement pst = conn.prepareStatement(check)) {

                pst.setInt(1, studentID);
                pst.setInt(2, classID);
                pst.setString(3, semester);

                try (ResultSet rs = pst.executeQuery()) {

                    if (rs.next()) {
                        JOptionPane.showMessageDialog(this, "You have already registered this class " + "in " + semester + ".");
                        return;
                    }
                }
                // Save the student's class registration
                String insert = "INSERT INTO tbl_student_classes " + "(id, StudentID, Class_id, semester) "  + "SELECT COALESCE(MAX(id), 0) + 1, "+ "?, ?, ? FROM tbl_student_classes";

                try (PreparedStatement save =
                    conn.prepareStatement(insert)) {
                    save.setInt(1, studentID);
                    save.setInt(2, classID);
                    save.setString(3, semester);

                    save.executeUpdate();
                }
                JOptionPane.showMessageDialog(this,"Class registered successfully!");

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,"Unable to register class: " + ex.getMessage());
            }
        }
        // View Classes button
        else if (e.getSource() == btnView) {

            String sql = "SELECT c.class_code, c.class_name, " + "sc.semester " + "FROM tbl_student_classes sc "+ "JOIN tbl_classes c "
                       + "ON sc.Class_id = c.Class_id " + "WHERE sc.StudentID = ? "+ "ORDER BY sc.semester, c.class_code";

            try (Connection conn = Connections.getConnection();
                 PreparedStatement pst = conn.prepareStatement(sql)) {

                pst.setInt(1, studentID);
                try (ResultSet rs = pst.executeQuery()) {
                    txtClasses.setText("");

                    boolean found = false;

                    while (rs.next()) {
                        found = true;
                        txtClasses.append(
                            rs.getString("class_code") + " - "
                            + rs.getString("class_name") + "\n"+ "Semester: "
                            + rs.getString("semester")
                            + "\n\n");
                    }
                    if (!found) {
                        txtClasses.setText("You have not registered for any classes yet.");
                    }
                }

            } catch (SQLException ex) {

                JOptionPane.showMessageDialog(this,"Error viewing classes: " + ex.getMessage());}
        }
    }
}
