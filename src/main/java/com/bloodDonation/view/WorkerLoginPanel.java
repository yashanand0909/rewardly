package com.bloodDonation.view;

import com.bloodDonation.enity.Login;
import com.bloodDonation.enity.WorkerResponse;
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
import org.springframework.web.client.RestTemplate;

public class WorkerLoginPanel extends JPanel {
  public WorkerLoginPanel(CardLayout cardLayout, JPanel cardPanel) {
    JPanel loginPanel = new JPanel();
    loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.PAGE_AXIS));
    JTextField usernameField = new JTextField(20);
    JPasswordField passwordField = new JPasswordField(20);
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
            if (usernameField.getText().isEmpty()) {
              flag = false;
              JOptionPane.showMessageDialog(
                  new JOptionPane(),
                  "User name can not be empty",
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
              HttpEntity<Login> requestEntity = new HttpEntity<>(login, headers);

              // make the HTTP request and deserialize the response body to DonorResponse object
              ResponseEntity<WorkerResponse> response =
                  restTemplate.postForEntity(
                      "http://localhost:8080/worker/login", requestEntity, WorkerResponse.class);
              WorkerResponse workerResponse = response.getBody();

              // Check the response code to see if the login was successful

              if (response.getStatusCode() == HttpStatus.OK
                  && !workerResponse.getStatus().equals("FAILURE")) {
                //            // Switch to the appropriate panel
                cardPanel.add(
                    new WorkerMainAppPage(cardPanel, cardLayout, workerResponse.getWorker()),
                    "worker-main-app");
                cardLayout.show(cardPanel, "worker-main-app");
              } else {
                JOptionPane.showMessageDialog(
                    new JOptionPane(),
                    workerResponse.getMessage(),
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
}
