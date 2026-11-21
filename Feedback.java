

package com.mbogo.sufeeds;

import javax.swing.JFrame;
import javax.swing.JComboBox;
import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Feedback extends JFrame {

    private int studentID;

    private JComboBox<ClassItem> cmbClasses = new JComboBox<>();
    private JComboBox<TopicItem> cmbTopics = new JComboBox<>();
    private JTextArea txtFeedback = new JTextArea(5, 30);

    public Feedback(int studentID) {
        this.studentID = studentID;

        setTitle("Student Contributions");
        setSize(550, 400);
        setLayout(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel(new GridLayout(2, 2, 5, 5));

        topPanel.add(new JLabel("Select Class:"));
        topPanel.add(cmbClasses);

        topPanel.add(new JLabel("Select Topic:"));
        topPanel.add(cmbTopics);

        add(topPanel, BorderLayout.NORTH);

        add(new JScrollPane(txtFeedback), BorderLayout.CENTER);

        JButton btnSubmit = new JButton("Submit Contribution");
        JButton btnView = new JButton("View Feedbacks");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnSubmit);
        buttonPanel.add(btnView);

        JPanel bottomPanel = new JPanel(new BorderLayout());

        bottomPanel.add(buttonPanel, BorderLayout.CENTER);

        bottomPanel.add(
            new JLabel("© 2026 Morgan Mbogo. All rights reserved.",
                       SwingConstants.CENTER),
            BorderLayout.SOUTH
        );

        add(bottomPanel, BorderLayout.SOUTH);

        // Listen for class selection changes
        cmbClasses.addActionListener(e -> loadTopics());

        // Load classes and their topics
        loadClasses();
        loadTopics();

        btnSubmit.addActionListener(e -> submitFeedback());

        btnView.addActionListener(e -> viewFeedbacks());

        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/db_morgan_mbogo_227060",
            "root",
            "password"
        );
    }

    private void loadClasses() {
        cmbClasses.removeAllItems();

        String sql = "SELECT DISTINCT c.Class_id, c.class_name " + "FROM tbl_classes c " + "JOIN tbl_student_classes sc " + "ON c.Class_id = sc.Class_id " +
                     "WHERE sc.StudentID = ?";

        try (Connection con = connect();
             PreparedStatement ps = con.prepareStatement(sql)) {
                 ps.setInt(1, studentID);
                ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                cmbClasses.addItem(new ClassItem(
                    rs.getInt("Class_id"),
                    rs.getString("class_name")
                ));
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void loadTopics() {
        cmbTopics.removeAllItems();

        ClassItem selectedClass =
            (ClassItem) cmbClasses.getSelectedItem();

        if (selectedClass == null) {
            return;
        }

        String sql = "SELECT topicID, topic_title " +"FROM tbl_topics " + "WHERE Class_id = ? AND StudentID = ?";

        try (Connection con = connect();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, selectedClass.id);
            ps.setInt(2, studentID);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                cmbTopics.addItem(new TopicItem(
                    rs.getInt("topicID"),
                    rs.getString("topic_title")
                ));
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void submitFeedback() {
        TopicItem topic = (TopicItem) cmbTopics.getSelectedItem();
        String message = txtFeedback.getText().trim();

        if (topic == null || message.isEmpty()) {
            JOptionPane.showMessageDialog(
                this, "Select a topic and enter your contribution."
            );
            return;
        }

        String sql = "INSERT INTO tbl_feedback " +
                     "(feedback_id, topicID, StudentID, feedback_message) " + "VALUES (?, ?, ?, ?)";

        try (Connection con = connect()) {
            int feedbackID = 1;

            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(
                "SELECT COALESCE(MAX(feedback_id), 0) + 1 FROM tbl_feedback");

            if (rs.next()) {
                feedbackID = rs.getInt(1);
            }

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, feedbackID);
            ps.setInt(2, topic.id);
            ps.setInt(3, studentID);
            ps.setString(4, message);

            ps.executeUpdate();

            JOptionPane.showMessageDialog(
                this, "Contribution submitted successfully!" );

            txtFeedback.setText("");

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void viewFeedbacks() {
        JFrame window = new JFrame("View Student Feedbacks");
        JTextArea txtResults = new JTextArea();
        txtResults.setEditable(false);

        String sql = "SELECT c.class_name, t.topic_title, " +
                     "f.feedback_message " +"FROM tbl_feedback f " +
                     "JOIN tbl_topics t ON f.topicID = t.topicID " +
                     "JOIN tbl_classes c ON t.Class_id = c.Class_id " +"ORDER BY f.feedback_id DESC";

        try (Connection con = connect();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                txtResults.append(
                    "Class: " + rs.getString("class_name") +
                    "\nTopic: " + rs.getString("topic_title") +
                    "\nFeedback: " + rs.getString("feedback_message") +"\n..........................\n");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(window, e.getMessage());
        }
        window.add(new JScrollPane(txtResults), BorderLayout.CENTER);
        window.add(
            new JLabel("© 2026 SUFeeds. All rights reserved."+SwingConstants.CENTER),
            BorderLayout.SOUTH);

        window.setSize(550, 400);
        window.setLocationRelativeTo(this);
        window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        window.setVisible(true);
    }
    private class ClassItem {
        int id;
        String name;
        ClassItem(int id, String name) {
            this.id = id;
            this.name = name;
        }
        public String toString() {
            return name;}
    }
    private class TopicItem {
        int id;
        String title;
        TopicItem(int id, String title) {
            this.id = id;
            this.title = title;
        }
        public String toString() {
            return title;
        }
    }
}
