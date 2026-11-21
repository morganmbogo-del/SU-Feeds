
package com.mbogo.sufeeds;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class registration extends JFrame implements ActionListener {

    private JLabel lblTitle, lblStudentID, lblName;
    private JLabel lblEmail, lblPassword, lblConfirmPassword;

    private JTextField txtStudentID, txtName, txtEmail;

    private JPasswordField txtPassword, txtConfirmPassword;

    private JButton btnCreateAccount, btnLogin;

    public registration() {

        setTitle("SU Feeds - Registration");
        setSize(450, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(7, 2, 10, 10));

        lblTitle = new JLabel("Create Your Account");
        lblStudentID = new JLabel("Student ID:");
        lblName = new JLabel("Full Name:");
        lblEmail = new JLabel("Email:");
        lblPassword = new JLabel("Password:");
        lblConfirmPassword = new JLabel("Confirm Password:");

        txtStudentID = new JTextField();
        txtName = new JTextField();
        txtEmail = new JTextField();

        txtPassword = new JPasswordField();
        txtConfirmPassword = new JPasswordField();

        btnCreateAccount = new JButton("Create Account");
        btnLogin = new JButton(" Login");

        btnCreateAccount.addActionListener(this);
        btnLogin.addActionListener(this);

        panel.add(lblTitle);
        panel.add(new JLabel(""));

        panel.add(lblStudentID);
        panel.add(txtStudentID);

        panel.add(lblName);
        panel.add(txtName);

        panel.add(lblEmail);
        panel.add(txtEmail);

        panel.add(lblPassword);
        panel.add(txtPassword);

        panel.add(lblConfirmPassword);
        panel.add(txtConfirmPassword);

    
        panel.add(btnCreateAccount);
        panel.add(btnLogin);
        add(panel,BorderLayout.CENTER);
        JLabel Copyright=new JLabel(" © 2026 Morgan Mbogo.All rights reserved.",SwingConstants.CENTER);
        add(Copyright,BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
         if (e.getSource() == btnLogin) {
                login loginForm = new login();
                loginForm.setVisible(true);
                this.dispose();} 
       else if(e.getSource() == btnCreateAccount) {
            // retrieve user input from the text fields
            String studentID = txtStudentID.getText();
            String name = txtName.getText();
            String email = txtEmail.getText();
            String password = new String(txtPassword.getPassword());
            String confirmPassword = new String(txtConfirmPassword.getPassword());

            // Perform validation and account creation logic
            if (studentID.isEmpty() || name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;}
                //check if student ID is a number
                try{ Integer.parseInt(studentID); } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Student ID must be a number.", "Error", JOptionPane.ERROR_MESSAGE);
                    return; }
                if (password.length() < 8) {
                    JOptionPane.showMessageDialog(this, "Password must be at least 8 characters long.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;}
            //password validation
            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match.", "Error", JOptionPane.ERROR_MESSAGE);
                return; }
            if (registerStudent(studentID, name, email, password)) {
                // Here you would typically save the user data to a database or perform other registration logic
                JOptionPane.showMessageDialog(this, "Account created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

                login loginForm = new login();
                loginForm.setVisible(true);
                this.dispose();} // Close the registration window
         } 
    }
    
        // Handle button click events
    private boolean registerStudent(String studentID, String name, String email, String password) {
        String sql="INSERT INTO tbl_students (studentID, full_name, Email, password) VALUES (?, ?, ?, ?)";
    
    try (Connection conn = Connections.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setInt(1, Integer.parseInt(studentID));
        pstmt.setString(2, name);
        pstmt.setString(3, email);
        pstmt.setString(4, password);
        pstmt.executeUpdate();
        return true;
    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error registering student: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        return false;
    }
    }}


