package com.bloodDonation.view;

import com.bloodDonation.enity.Appointment;
import com.bloodDonation.enity.AppointmentResponse;
import com.bloodDonation.enity.Worker;
import com.bloodDonation.enity.WorkerResponse;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.border.LineBorder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class WorkerMainAppPage extends JPanel {

  public WorkerMainAppPage(JPanel cardPanel, CardLayout cardLayout, Worker worker) {
    ArrayList<String> values = new ArrayList<>();

    this.setLayout(new BorderLayout());
    // JPanel registeredContestPanel = new JPanel();
    JPanel workerDetails = new JPanel(new GridLayout(4, 1));
    JLabel name = new JLabel("Name: " + worker.getFirst_name() + " " + worker.getLast_name());
    JLabel campId = new JLabel("Camp id:" + worker.getCampId());
    JLabel doj = new JLabel("Date of joining: " + worker.getDateOfJoining());
    JButton changePassword = new JButton("change password");
    changePassword.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            JPanel panel = new JPanel();
            JLabel password = new JLabel("New Password");
            JPasswordField passwordField = new JPasswordField();
            JButton submit = new JButton("Submit");
            panel.add(password);
            panel.add(passwordField);
            panel.add(submit);
            submit.addActionListener(
                new ActionListener() {
                  @Override
                  public void actionPerformed(ActionEvent e) {
                    RestTemplate restTemplate = new RestTemplate();
                    Worker workerChanged = worker;
                    workerChanged.setPassword(new String(passwordField.getPassword()));
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    HttpEntity<Worker> entity = new HttpEntity<>(workerChanged, headers);
                    ResponseEntity<WorkerResponse> response =
                        restTemplate.exchange(
                            "http://localhost:8080/worker/registration",
                            HttpMethod.PUT,
                            entity,
                            WorkerResponse.class);
                    WorkerResponse workerResponse = response.getBody();
                    cardPanel.add(
                        new WorkerMainAppPage(cardPanel, cardLayout, workerResponse.getWorker()),
                        "worker-main-page");
                    cardLayout.show(cardPanel, "worker-main-page");
                  }
                });
            cardPanel.add(panel, "change-password");
            cardLayout.show(cardPanel, "change-password");
          }
        });

    workerDetails.add(name);
    workerDetails.add(campId);
    workerDetails.add(doj);
    workerDetails.add(changePassword);
    workerDetails.setPreferredSize(new Dimension(50, 60));

    // Add a LineBorder to the panel with a red color and thickness of 2 pixels
    workerDetails.setBorder(new LineBorder(Color.RED, 2));
    add(workerDetails, BorderLayout.PAGE_START);
    RestTemplate restTemplate = new RestTemplate();

    // create request headers with Content-Type header
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    // create HTTP entity with headers
    HttpEntity<String> requestEntity = new HttpEntity<>(headers);

    // make the HTTP request and deserialize the response body to ContestResponse object
    String url = "http://localhost:8080/worker/appointment/" + worker.getUsername();
    ResponseEntity<AppointmentResponse> response =
        restTemplate.exchange(url, HttpMethod.GET, requestEntity, AppointmentResponse.class);
    AppointmentResponse appointmentResponse = response.getBody();
    JPanel appointments = new JPanel();

    for (Appointment appointment : appointmentResponse.getUpcomingAppointments()) {
      JPanel panel = new JPanel();
      JLabel contest_id = new JLabel("Contest Id: " + appointment.getContest_id());
      JLabel appointmentFor = new JLabel("Appointment For: " + appointment.getAppointment_for());
      JLabel appointmentTime = new JLabel("Time: " + appointment.getAppointment_time());
      panel.add(contest_id);
      panel.add(appointmentFor);
      panel.add(appointmentTime);
      JButton acceptButton = new JButton("Accept ");
      acceptButton.addActionListener(
          new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              cardPanel.add(
                  new DonationPanel(cardPanel, cardLayout, appointment, worker), "donation-form");
              cardLayout.show(cardPanel, "donation-form");
            }
          });
      panel.add(acceptButton);
      appointments.add(panel);
    }
    add(appointments);
  }
}
