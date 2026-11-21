
package com.mbogo.sufeeds;
import javax.swing.JFrame;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.BorderFactory;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
public class Topics extends JFrame implements ActionListener {

    private int studentID;

    private JComboBox<ClassItem> cmbClasses;
    private JButton btnBack, btnView, btnSave;
    private JTextArea txtTopics;
    private JTextField txtTopic, txtTopicID;

    // Stores class details in the dropdown
    private static class ClassItem {
        int classID;
        String classCode;
        String className;

        public ClassItem(int classID, String classCode, String className) {
            this.classID = classID;
            this.classCode = classCode;
            this.className = className;
        }
        @Override
        public String toString() {
            return classCode + " - " + className;}
    }
    public Topics(int studentID) {
        this.studentID = studentID;

        setTitle("SU Feeds - Topics");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        cmbClasses = new JComboBox<>();

        btnSave = new JButton("Save Topic");
        btnView = new JButton("View Topics");
        btnBack = new JButton("Back");

        txtTopic = new JTextField(15);
        txtTopicID = new JTextField(15);
        // Form panel
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(
            BorderFactory.createEmptyBorder(15, 15, 15, 15));

        panel.add(new JLabel("Select Class:"));
        panel.add(cmbClasses);

        panel.add(new JLabel("Topic:"));
        panel.add(txtTopic);

        panel.add(new JLabel("Topic ID:"));
        panel.add(txtTopicID);

        panel.add(btnSave);
        panel.add(btnView);
        panel.add(btnBack);
        // Display area
        txtTopics = new JTextArea();
        txtTopics.setEditable(false);

        add(panel, BorderLayout.NORTH);
        add(new JScrollPane(txtTopics), BorderLayout.CENTER);

        btnSave.addActionListener(this);
        btnView.addActionListener(this);
        btnBack.addActionListener(this);

        // Load registered classes
        loadClasses();
    }
    // Retrieve classes belonging to logged-in student
    private void loadClasses() {

        String sql ="SELECT DISTINCT c.Class_id, c.class_code, c.class_name " +"FROM tbl_classes c " +
            "JOIN tbl_student_classes sc " +"ON c.Class_id = sc.Class_id " +"WHERE sc.StudentID = ?";

        try (Connection conn = Connections.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, studentID);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {

                    ClassItem item = new ClassItem(
                        rs.getInt("Class_id"),
                        rs.getString("class_code"),
                        rs.getString("class_name"));

                    cmbClasses.addItem(item);
                }
            }
        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(this,"Error fetching classes: " + ex.getMessage());
        }
    }
    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == btnBack) {
            dispose();

        } else if (e.getSource() == btnSave) {
            saveTopic();
        } else if (e.getSource() == btnView) {
            viewTopics();
        }
    }
    // Save topic
    private void saveTopic() {
        String id = txtTopicID.getText().trim();
        String topic = txtTopic.getText().trim();

        ClassItem selectedClass =
            (ClassItem) cmbClasses.getSelectedItem();

        if (selectedClass == null || id.isEmpty() || topic.isEmpty()) {

            JOptionPane.showMessageDialog(this,
                "Please fill in all fields.");

            return;
        }
        try {
            int topicID = Integer.parseInt(id);
            String sql ="INSERT INTO tbl_topics " +"(topicID, Class_id, StudentID, topic_title) " +
             "VALUES (?, ?, ?, ?)";

            try (Connection conn = Connections.getConnection();
                 PreparedStatement pst = conn.prepareStatement(sql)) {

                pst.setInt(1, topicID);
                pst.setInt(2, selectedClass.classID);
                pst.setInt(3, studentID);
                pst.setString(4, topic);

                pst.executeUpdate();
            }
            JOptionPane.showMessageDialog(this,"Topic saved successfully!");
            txtTopicID.setText("");
            txtTopic.setText("");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                "Topic ID must be a number.");}
                 catch (SQLException ex) {

            JOptionPane.showMessageDialog(this,
                "Error saving topic: " + ex.getMessage());
        }
    }
    // View topics belonging to logged-in student
    private void viewTopics() {
        txtTopics.setText("");

        String sql ="SELECT t.topicID, t.topic_title, " +"c.class_code, c.class_name " + "FROM tbl_topics t " +
            "JOIN tbl_classes c " +
            "ON t.Class_id = c.Class_id " +"WHERE t.StudentID = ?";

        try (Connection conn = Connections.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, studentID);

            try (ResultSet rs = pst.executeQuery()) {

                while (rs.next()) {

                    txtTopics.append( "Topic ID: " + rs.getInt("topicID") + "\n" +
                        "topic: " + rs.getString("topic_title") + "\n" +
                        "Class: " + rs.getString("class_code") +
                        " - " + rs.getString("class_name") + "\n" +
                        "-----------------------------\n");
                }
                if (txtTopics.getText().isEmpty()) {
                    txtTopics.setText("No topics found.");
                }
            }

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(this,
                "Error viewing topics: " + ex.getMessage());
        }
    }
}
