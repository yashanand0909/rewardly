package com.bloodDonation.view;

import com.bloodDonation.enity.DonorResponse;
import com.bloodDonation.enity.Login;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

public class DonorLoginPanel extends JPanel {
  private JTextField usernameField;
  private JPasswordField passwordField;

  public DonorLoginPanel(CardLayout cardLayout, JPanel cardPanel) {
    JPanel loginPanel = new JPanel();
    loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.PAGE_AXIS));
    usernameField = new JTextField(20);
    passwordField = new JPasswordField(20);
    loginPanel.add(new JLabel("Username:"));
    loginPanel.add(usernameField);
    loginPanel.add(new JLabel("Password:"));
    loginPanel.add(passwordField);
    JPanel loginButtonPanel = new JPanel();
    JButton loginButton = new JButton("Login");
    loginButton.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            String username = "";
            String password = "";
            Boolean flag = true;
            if (usernameField.getText().length() == 0) {
              flag = false;
              JOptionPane.showMessageDialog(
                  new JOptionPane(),
                  "Username can not be empty",
                  "Error",
                  JOptionPane.ERROR_MESSAGE);
            } else {
              username = usernameField.getText();
            }
            if (passwordField.getPassword().length == 0) {
              flag = false;
              JOptionPane.showMessageDialog(
                  new JOptionPane(),
                  "Password can not be empty",
                  "Error",
                  JOptionPane.ERROR_MESSAGE);
            } else {
              password = new String(passwordField.getPassword());
            }
            if (flag) {
              // Make an HTTP POST request with the user's login details
              RestTemplate restTemplate = new RestTemplate();

              // create request headers with Content-Type header
              HttpHeaders headers = new HttpHeaders();
              headers.setContentType(MediaType.APPLICATION_JSON);

              // create request body
              Login login = new Login();
              login.setUsername(username);
              login.setPassword(password);

              // create HTTP entity with request body and headers
              try {
                HttpEntity<Login> requestEntity = new HttpEntity<>(login, headers);

                // make the HTTP request and deserialize the response body to DonorResponse object
                ResponseEntity<DonorResponse> response =
                    restTemplate.postForEntity(
                        "http://localhost:8080/donor/login", requestEntity, DonorResponse.class);
                DonorResponse donorResponse = response.getBody();

                // Check the response code to see if the login was successful

                if (response.getStatusCode() == HttpStatus.OK
                    && !donorResponse.getStatus().equals("FAILURE")) {
                  //            // Switch to the appropriate panel
                  cardPanel.add(
                      new MainAppPage(cardPanel, cardLayout, donorResponse.getDonor()), "main-app");
                  cardLayout.show(cardPanel, "main-app");

                } else {
                  JOptionPane.showMessageDialog(
                      new JOptionPane(),
                      donorResponse.getMessage() + " or sign-up",
                      "Error",
                      JOptionPane.ERROR_MESSAGE);
                }
              } catch (HttpClientErrorException ex) {
                JOptionPane.showMessageDialog(
                    new JOptionPane(),
                    "Error in connecting with server please try after some time",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
              }
            }
          }
        });
    loginButtonPanel.add(loginButton);
    loginPanel.add(loginButtonPanel);
    add(loginPanel);
  }

  public String getUsername() {
    return usernameField.getText();
  }
}
