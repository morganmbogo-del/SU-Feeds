package com.mbogo.sufeeds;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.BorderFactory;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Font;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Dashboard extends JFrame implements ActionListener {
    private final int studentID;
    private  final String fullName;

    private JLabel lblWelcome, lblCopyright;
    private JButton btnLogout,btnMyClasses, btnTopics, btnFeedback;

    public Dashboard(int studentID, String fullName) {
        this.studentID = studentID;
        this.fullName = fullName;

        setTitle("SU Feeds - Dashboard");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        lblCopyright = new JLabel("© 2026 Morgan Mbogo. All rights reserved.");
        lblWelcome = new JLabel("Welcome, " + fullName + "!");
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 16));
        lblWelcome.setHorizontalAlignment(SwingConstants.CENTER);
        lblCopyright.setHorizontalAlignment(SwingConstants.CENTER);

      btnMyClasses = new JButton("My Classes");
      btnTopics = new JButton("Topics");
      btnFeedback = new JButton("Feedback");

    btnMyClasses.addActionListener(this);
    btnTopics.addActionListener(this);
    btnFeedback.addActionListener(this);

    btnLogout= new JButton("Logout");
    btnLogout.addActionListener(this);

    JPanel topPanel = new JPanel(new BorderLayout());
    topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    topPanel.add(lblWelcome, BorderLayout.CENTER);
    topPanel.add(btnLogout, BorderLayout.EAST);

    JPanel navigationPanel = new JPanel(new GridLayout(1, 3, 15, 15));
    navigationPanel.add(btnMyClasses);
    navigationPanel.add(btnTopics);
    navigationPanel.add(btnFeedback);  
     
    add(topPanel, BorderLayout.NORTH);
    add(navigationPanel, BorderLayout.CENTER);  
    add(lblCopyright, BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnLogout) {
            dispose();
            new login().setVisible(true);
        }
        else if (e.getSource() == btnMyClasses) {
            new Classes(studentID).setVisible(true);
        } else if (e.getSource() == btnTopics) {
            new Topics(studentID).setVisible(true);
        } else if (e.getSource() == btnFeedback) {
            new Feedback(studentID).setVisible(true);
        }
    }}