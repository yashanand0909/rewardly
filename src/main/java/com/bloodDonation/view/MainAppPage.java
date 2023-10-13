package com.bloodDonation.view;

import com.bloodDonation.enity.Appointment;
import com.bloodDonation.enity.AppointmentResponse;
import com.bloodDonation.enity.Contest;
import com.bloodDonation.enity.ContestDonation;
import com.bloodDonation.enity.ContestResponse;
import com.bloodDonation.enity.DeleteAppointmentRequest;
import com.bloodDonation.enity.Donor;
import com.bloodDonation.enity.DonorResponse;
import com.bloodDonation.enity.JoinContestRequest;
import com.bloodDonation.enity.LeaderBoard;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

public class MainAppPage extends JPanel {

  private JButton logoutButton, activeContest;

  public MainAppPage(JPanel cardPanel, CardLayout cardLayout, Donor donor) {
    this.setLayout(new BorderLayout());
    // JPanel registeredContestPanel = new JPanel();
    JPanel userDetails = new JPanel(new GridLayout(3, 1));
    JLabel name = new JLabel("Name: " + donor.getFirst_name() + " " + donor.getLast_name());
    JLabel dob = new JLabel("D.O.B:" + donor.getDate_of_birth());
    JLabel bloodType = new JLabel("Blood Type: " + donor.getBlood_group());
    userDetails.add(name);
    userDetails.add(dob);
    userDetails.add(bloodType);
    userDetails.setPreferredSize(new Dimension(50, 50));

    // Add a LineBorder to the panel with a red color and thickness of 2 pixels
    userDetails.setBorder(new LineBorder(Color.RED, 2));
    /** Set logout button. */
    this.logoutButton = new JButton("Logout");
    JButton changePassword = new JButton("change password");
    userDetails.add(changePassword);
    changePassword.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            JPanel panel = new JPanel();
            JLabel password = new JLabel("New Password");
            JPasswordField passwordField = new JPasswordField();
            passwordField.setPreferredSize(new Dimension(200, 20));
            JButton submit = new JButton("Submit");
            panel.add(password);
            panel.add(passwordField);
            panel.add(submit);
            submit.addActionListener(
                new ActionListener() {
                  @Override
                  public void actionPerformed(ActionEvent e) {
                    try {
                      RestTemplate restTemplate = new RestTemplate();
                      Donor donorChanged = donor;
                      donorChanged.setPassword(new String(passwordField.getPassword()));
                      HttpHeaders headers = new HttpHeaders();
                      headers.setContentType(MediaType.APPLICATION_JSON);
                      HttpEntity<Donor> entity = new HttpEntity<>(donorChanged, headers);
                      ResponseEntity<DonorResponse> response =
                          restTemplate.exchange(
                              "http://localhost:8080/donor/registration",
                              HttpMethod.PUT,
                              entity,
                              DonorResponse.class);
                      DonorResponse donorResponse = response.getBody();
                      cardPanel.add(
                          new MainAppPage(cardPanel, cardLayout, donorResponse.getDonor()),
                          "worker-main-page");
                      cardLayout.show(cardPanel, "worker-main-page");
                    } catch (HttpClientErrorException ex) {
                      JOptionPane.showMessageDialog(
                          new JOptionPane(),
                          "Error in connecting with server please try after some time",
                          "Error",
                          JOptionPane.ERROR_MESSAGE);
                      cardPanel.add(new MainAppPage(cardPanel, cardLayout, donor), "main-app");
                      cardLayout.show(cardPanel, "main-app");
                    }
                  }
                });
            cardPanel.add(panel, "change-password");
            cardLayout.show(cardPanel, "change-password");
          }
        });
    add(userDetails, BorderLayout.PAGE_START);
    Border activeContestBorder = BorderFactory.createTitledBorder("Active Contests");
    Border registeredContestBorder = BorderFactory.createTitledBorder("Registered Contests");
    // set the activeContestBorder to the panel

    JPanel registeredContestsPanel = new JPanel();
    JPanel activeContestPanel = new JPanel();
    registeredContestsPanel.setBorder(registeredContestBorder);
    activeContestPanel.setBorder(activeContestBorder);

    registeredContestsPanel.setLayout(new BoxLayout(registeredContestsPanel, BoxLayout.PAGE_AXIS));
    activeContestPanel.setLayout(new BoxLayout(activeContestPanel, BoxLayout.PAGE_AXIS));
    // get Active Contest
    try {

      RestTemplate restTemplate = new RestTemplate();

      // create request headers with Content-Type header
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);

      // create HTTP entity with headers
      HttpEntity<String> requestEntity = new HttpEntity<>(headers);

      // make the HTTP request and deserialize the response body to ContestResponse object
      String url = "http://localhost:8080/donor/contests/" + donor.getUsername();
      ResponseEntity<ContestResponse> response =
          restTemplate.exchange(url, HttpMethod.GET, requestEntity, ContestResponse.class);
      ContestResponse contestResponse = response.getBody();

      for (Contest contest : contestResponse.getActiveContest()) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout());

        JLabel contestId = new JLabel("Contest Id: " + contest.getContestId());
        JLabel contestName = new JLabel("Contest Name: " + contest.getName());
        JLabel description = new JLabel("Description: " + contest.getDescription());
        panel.add(contestId);
        panel.add(contestName);
        panel.add(description);
        activeContest = new JButton("Register");
        activeContest.addActionListener(
            new ActionListener() {
              @Override
              public void actionPerformed(ActionEvent e) {
                /** Registering contest. */
                try {

                  RestTemplate restTemplate = new RestTemplate();

                  // create request headers with Content-Type header
                  HttpHeaders headers = new HttpHeaders();
                  headers.setContentType(MediaType.APPLICATION_JSON);
                  JoinContestRequest joinContestRequest = new JoinContestRequest();
                  joinContestRequest.setContestId(contest.getContestId());
                  joinContestRequest.setUserName(donor.getUsername());
                  // create HTTP entity with headers
                  HttpEntity<JoinContestRequest> requestEntity =
                      new HttpEntity<>(joinContestRequest, headers);

                  // make the HTTP request and deserialize the response body to ContestResponse
                  // object
                  String url = "http://localhost:8080/donor/contest";
                  ResponseEntity<ContestResponse> response =
                      restTemplate.postForEntity(url, requestEntity, ContestResponse.class);

                  cardPanel.add(new MainAppPage(cardPanel, cardLayout, donor), "appointment-form");
                  cardLayout.show(cardPanel, "appointment-form");
                } catch (HttpClientErrorException ex) {
                  JOptionPane.showMessageDialog(
                      new JOptionPane(),
                      "Error in connecting with server please try after some time",
                      "Error",
                      JOptionPane.ERROR_MESSAGE);
                  cardPanel.add(new MainAppPage(cardPanel, cardLayout, donor), "main-app-page");
                  cardLayout.show(cardPanel, "main-app-page");
                }
              }
            });
        panel.add(activeContest);
        activeContestPanel.add(panel); // Add panel to the parent container
      }
      for (Contest contest : contestResponse.getJoinedContest()) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout());

        JLabel contestId = new JLabel("Contest Id: " + contest.getContestId());
        JLabel contestName = new JLabel("Contest Name: " + contest.getName());
        JLabel description = new JLabel("Description: " + contest.getDescription());
        panel.add(contestId);
        panel.add(contestName);
        panel.add(description);
        registeredContestsPanel.add(panel); // Add panel to the parent container
      }
      if (!contestResponse.getJoinedContest().isEmpty()) {
        JButton bookAppointment = new JButton("Book Appointment");
        registeredContestsPanel.add(bookAppointment);
        bookAppointment.addActionListener(
            new ActionListener() {
              @Override
              public void actionPerformed(ActionEvent e) {
                cardPanel.add(
                    new AppointmentPanel(
                        cardPanel, cardLayout, donor, contestResponse.getJoinedContest()),
                    "appointment-form");
                cardLayout.show(cardPanel, "appointment-form");
              }
            });
      }
    } catch (HttpClientErrorException ex) {
      JOptionPane.showMessageDialog(
          new JOptionPane(),
          "Error in connecting with server please try after some time",
          "Error",
          JOptionPane.ERROR_MESSAGE);
      cardPanel.add(new DonorLoginPanel(cardLayout, cardPanel), "login-page");
      cardLayout.show(cardPanel, "login-page");
    }

    /** Appointment panel */
    JPanel upcomingAppointments = new JPanel();
    RestTemplate restTemplate = new RestTemplate();
    // create request headers with Content-Type header
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    // create HTTP entity with headers
    HttpEntity<String> requestEntity = new HttpEntity<>(headers);
    // make the HTTP request and deserialize the response body to ContestResponse object
    try {
      String url = "http://localhost:8080/donor/appointment/" + donor.getUsername();
      ResponseEntity<AppointmentResponse> response =
          restTemplate.exchange(url, HttpMethod.GET, requestEntity, AppointmentResponse.class);

      AppointmentResponse appointmentResponse = response.getBody();

      for (Appointment appointment : appointmentResponse.getUpcomingAppointments()) {
        JPanel panel = new JPanel();
        JLabel contestId = new JLabel("Contest Id: " + appointment.getContest_id());
        JLabel appointmentWith = new JLabel("Collector: " + appointment.getAppointment_with());
        JLabel time = new JLabel("Time: " + appointment.getAppointment_time());
        panel.add(appointmentWith);
        panel.add(time);
        panel.add(contestId);
        Border border = BorderFactory.createEtchedBorder();
        panel.setBorder(border);
        upcomingAppointments.add(panel);
        JButton deleteAppointmentButton = new JButton("Delete");
        panel.add(deleteAppointmentButton);
        deleteAppointmentButton.addActionListener(
            new ActionListener() {
              @Override
              public void actionPerformed(ActionEvent e) {
                RestTemplate restTemplate = new RestTemplate();

                DeleteAppointmentRequest request = new DeleteAppointmentRequest();
                request.setAppointmentId(appointment.getAppointment_id());
                request.setDonorUserName(appointment.getAppointment_for());

                try {
                  // set properties of the request object as needed

                  HttpHeaders headers = new HttpHeaders();
                  headers.setContentType(MediaType.APPLICATION_JSON);

                  HttpEntity<DeleteAppointmentRequest> entity = new HttpEntity<>(request, headers);

                  ResponseEntity<AppointmentResponse> response =
                      restTemplate.exchange(
                          "http://localhost:8080/donor/appointment/",
                          HttpMethod.DELETE,
                          entity,
                          AppointmentResponse.class);

                  AppointmentResponse appointmentResponse = response.getBody();
                  if (response.getStatusCode() == HttpStatus.OK) {
                    cardPanel.add(new MainAppPage(cardPanel, cardLayout, donor), "main-app-page");
                    cardLayout.show(cardPanel, "main-app-page");
                  }
                } catch (HttpClientErrorException ex) {
                  JOptionPane.showMessageDialog(
                      new JOptionPane(),
                      "Error in connecting with server please try after some time",
                      "Error",
                      JOptionPane.ERROR_MESSAGE);
                  cardPanel.add(new MainAppPage(cardPanel, cardLayout, donor), "main-app-page");
                  cardLayout.show(cardPanel, "main-app-page");
                }
              }
            });
      }
      Border upcomingAppointmentsBorder = BorderFactory.createTitledBorder("Upcoming Appointments");
      upcomingAppointments.setBorder(upcomingAppointmentsBorder);
      JPanel centerPanel = new JPanel();

      centerPanel.add(activeContestPanel); // Add activeContestPanel to centerPanel
      centerPanel.add(registeredContestsPanel);
      centerPanel.add(upcomingAppointments);

      /** Leader Board. */
      RestTemplate leaderboardRestTemplate = new RestTemplate();

      HttpHeaders leaderBoardheaders = new HttpHeaders();
      leaderBoardheaders.setContentType(MediaType.APPLICATION_JSON);
      HttpEntity<String> demoEntity = new HttpEntity<>("{}", leaderBoardheaders);

      ResponseEntity<LeaderBoard> leaderBoardResponse =
          leaderboardRestTemplate.exchange(
              "http://localhost:8080/donor/leaderBoard",
              HttpMethod.GET,
              demoEntity,
              LeaderBoard.class);

      LeaderBoard leaderBoardBody = leaderBoardResponse.getBody();

      List<ContestDonation> list = leaderBoardBody.getDonations();
      if (!list.isEmpty()) {
        JPanel leaderBoard = new JPanel();
        Border leaderBoardBorder = BorderFactory.createTitledBorder("Leader Board");
        leaderBoard.setBorder(leaderBoardBorder);
        leaderBoard.setLayout(new GridBagLayout());
        addComponent(leaderBoard, new JLabel("Contest"), 0, 0);
        addComponent(leaderBoard, new JLabel("Donor"), 1, 0);
        addComponent(leaderBoard, new JLabel("Donated"), 2, 0);
        for (int i = 0; i < list.size(); i++) {
          addComponent(leaderBoard, new JLabel(list.get(i).getContest() + ""), 0, i + 1);
          addComponent(leaderBoard, new JLabel(list.get(i).getDonor() + ""), 1, i + 1);
          addComponent(leaderBoard, new JLabel(list.get(i).getDonated() + ""), 2, i + 1);
        }
        centerPanel.add(leaderBoard);
      }

      this.add(centerPanel, BorderLayout.CENTER);
      //    JPanel topPanel = new JPanel();
      //
      //    topPanel.add(this.logoutButton);
      //    this.add(topPanel, BorderLayout.NORTH);
    } catch (HttpClientErrorException ex) {
      JOptionPane.showMessageDialog(
          new JOptionPane(),
          "Error in connecting with server please try after some time",
          "Error",
          JOptionPane.ERROR_MESSAGE);
      cardPanel.add(new Homepage(), "home-page");
      cardLayout.show(cardPanel, "home-page");
    }
  }

  private void addComponent(JPanel panel, JComponent component, int gridx, int gridy) {
    GridBagConstraints constraints = new GridBagConstraints();
    constraints.fill = GridBagConstraints.HORIZONTAL;
    constraints.gridx = gridx;
    constraints.gridy = gridy;
    constraints.insets = new Insets(10, 10, 10, 10);
    panel.add(component, constraints);
  }
}
