package com.mbogo.sufeeds;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import javax.swing.JOptionPane;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

public class login extends JFrame implements ActionListener {
    
    private JLabel lblTitle, lblEmail, lblPassword;
    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JButton btnLogin, btnRegister;

    public login() {
        setTitle("SU Feeds - Login");
        setSize(450, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new GridLayout(5, 2, 10, 10));

        lblTitle = new JLabel("Login to Your Account");
        lblEmail = new JLabel("Email:");
        lblPassword = new JLabel("Password:");

        txtEmail = new JTextField();
        txtPassword = new JPasswordField();

        btnLogin = new JButton("Login");
        btnRegister = new JButton("Register");

        btnLogin.addActionListener(this);
        btnRegister.addActionListener(this);

        add(lblTitle);
        add(new JLabel(""));

        add(lblEmail);
        add(txtEmail);

        add(lblPassword);
        add(txtPassword);

        add(btnLogin);
        add(btnRegister);

        add(new JLabel(" © 2026 Morgan Mbogo. All rights reserved."));
        add(new JLabel(""));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnLogin) {
            String email = txtEmail.getText();
            String password = new String(txtPassword.getPassword());

            if (email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter your email and password.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            loginStudent(email, password);
        } else if (e.getSource() == btnRegister) {
            registration registrationForm = new registration();
            registrationForm.setVisible(true);
            this.dispose(); // Close the login window
        }
    }
    
private void loginStudent(String email, String password) {
        String sql = "SELECT * FROM tbl_students WHERE Email = ? AND password = ?";
        try (Connection conn = Connections.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                Dashboard dashboard = new Dashboard(rs.getInt("studentID"), rs.getString("full_name"));
                dashboard.setVisible(true);
                this.dispose(); // Close the login window
            } else {
                JOptionPane.showMessageDialog(this, "Invalid email or password.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error during login: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }}

