package com.bloodDonation.view;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Homepage extends JFrame implements ActionListener {
  private Image backgroundImage;
  private JPanel cardPanel;
  private CardLayout cardLayout;
  private JButton loginButton;
  private JButton signupButton;
  private JButton workerLoginButton;

  public Homepage() {
    setTitle("Blood is the new Gold");
    try {
      backgroundImage =
          ImageIO.read(getClass().getClassLoader().getResource("homepage-background.jpg"));
    } catch (IOException e) {
      e.printStackTrace();
    }
    // Create card panel and set layout
    cardPanel = new JPanel();
    cardLayout = new CardLayout();
    cardPanel.setLayout(cardLayout);

    // Create buttons and add action listeners
    loginButton = new JButton("Login-page");
    loginButton.addActionListener(this);
    signupButton = new JButton("Signup-page");
    signupButton.addActionListener(this);
    workerLoginButton = new JButton("Worker-Login-page");
    workerLoginButton.addActionListener(this);

    // Add buttons to panel
    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new GridLayout());
    buttonPanel.add(loginButton);
    buttonPanel.add(signupButton);
    buttonPanel.add(workerLoginButton);

    // Add card panel and button panel to window
    getContentPane().add(cardPanel, BorderLayout.CENTER);
    getContentPane().add(buttonPanel, BorderLayout.PAGE_START);

    // Set window size and show it
    setSize(800, 800);
    setVisible(true);
  }

  public void actionPerformed(ActionEvent e) {
    // Get the name of the button that was clicked
    String buttonName = e.getActionCommand();
    switch (buttonName) {
      case "Login-page":
        cardPanel.add(new DonorLoginPanel(cardLayout, cardPanel), "donor-login-page");
        cardLayout.show(cardPanel, "donor-login-page");
        break;
      case "Signup-page":
        cardPanel.add(new RegistrationForm(cardPanel, cardLayout), "signup-page");
        cardLayout.show(cardPanel, "signup-page");
        break;
      case "Worker-Login-page":
        cardPanel.add(new WorkerLoginPanel(cardLayout, cardPanel), "worker-login");
        cardLayout.show(cardPanel, "worker-login");
        break;
    }
  }

  @Override
  public void paint(Graphics g) {
    super.paint(g);

    // Draw the background image with 50% opacity
    Graphics2D g2d = (Graphics2D) g;
    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f));
    g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
  }
}
