package com.bloodDonation.view;

import com.bloodDonation.enity.Appointment;
import com.bloodDonation.enity.Donation;
import com.bloodDonation.enity.Worker;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class DonationPanel extends JPanel {

  public DonationPanel(
      JPanel cardPanel, CardLayout cardLayout, Appointment appointment, Worker worker) {

    // Create a new JPanel to hold the form components
    JPanel formPanel = new JPanel(new FlowLayout());

    // Add a label and text field for the amount
    JLabel amountLabel = new JLabel("Amount:");
    JTextField amountField = new JTextField(10);
    formPanel.add(amountLabel);
    formPanel.add(amountField);

    // Add a submit button with an ActionListener that prints the amount to the console
    JButton submitButton = new JButton("Submit");
    submitButton.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            RestTemplate restTemplate = new RestTemplate();

            Donation donation = new Donation();
            donation.setAppointmentId(appointment.getAppointment_id());
            donation.setContestId(appointment.getContest_id());
            donation.setDateOfdonation(new java.sql.Date(System.currentTimeMillis()));
            Boolean flag = true;
            try {

              if (Integer.parseInt(amountField.getText()) > 5
                  || Integer.parseInt(amountField.getText()) < 1) {
                flag = false;
                JOptionPane.showMessageDialog(
                    new JOptionPane(),
                    "Donation amount can range from 1 to 5",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
              } else {
                donation.setQuantity(Integer.parseInt(amountField.getText()));
              }
            } catch (Exception ex) {
              flag = false;
              JOptionPane.showMessageDialog(
                  new JOptionPane(),
                  "Only positive values allowed",
                  "Error",
                  JOptionPane.ERROR_MESSAGE);
            }

            // set properties of the donation object as needed
            if (flag) {

              HttpHeaders headers = new HttpHeaders();
              headers.setContentType(MediaType.APPLICATION_JSON);

              HttpEntity<Donation> entity = new HttpEntity<>(donation, headers);

              ResponseEntity<String> response =
                  restTemplate.exchange(
                      "http://localhost:8080/worker/donation",
                      HttpMethod.POST,
                      entity,
                      String.class);
              String message = response.getBody();
              cardPanel.add(
                  new WorkerMainAppPage(cardPanel, cardLayout, worker), "worker-main-app");
              cardLayout.show(cardPanel, "worker-main-app");
            }
          }
        });
    formPanel.add(submitButton);

    // Add the form panel to the DonationPanel
    add(formPanel);
  }
}
