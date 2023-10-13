package com.bloodDonation.view;

import com.bloodDonation.enity.Donor;
import com.bloodDonation.enity.DonorResponse;
import com.bloodDonation.utility.DateLabelFormatter;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.util.Properties;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

public class RegistrationForm extends JPanel {

  private JTextField usernameField;
  private JTextField firstNameField;
  private JTextField lastNameField;
  private JPasswordField passwordField;
  private JComboBox<String> genderField;
  private JDatePickerImpl datePicker;
  private JComboBox<String> bloodGroupField;

  public RegistrationForm(JPanel cardPanel, CardLayout cardLayout) {
    setLayout(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();

    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.anchor = GridBagConstraints.WEST;
    add(new JLabel("Username"), gbc);

    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    usernameField = new JTextField(20);
    add(usernameField, gbc);

    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.fill = GridBagConstraints.NONE;
    add(new JLabel("First Name"), gbc);

    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    firstNameField = new JTextField(20);
    add(firstNameField, gbc);

    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.fill = GridBagConstraints.NONE;
    add(new JLabel("Last Name"), gbc);

    gbc.gridx = 1;
    gbc.gridy = 2;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    lastNameField = new JTextField(20);
    add(lastNameField, gbc);

    gbc.gridx = 0;
    gbc.gridy = 3;
    gbc.fill = GridBagConstraints.NONE;
    add(new JLabel("Password"), gbc);

    gbc.gridx = 1;
    gbc.gridy = 3;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    passwordField = new JPasswordField(20);
    add(passwordField, gbc);

    gbc.gridx = 0;
    gbc.gridy = 4;
    gbc.fill = GridBagConstraints.NONE;
    add(new JLabel("Gender"), gbc);

    gbc.gridx = 1;
    gbc.gridy = 4;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    String[] genderOptions = {"Male", "Female", "Others"};
    genderField = new JComboBox<>(genderOptions);
    add(genderField, gbc);

    gbc.gridx = 0;
    gbc.gridy = 5;
    gbc.fill = GridBagConstraints.NONE;
    add(new JLabel("Date of Birth"), gbc);

    gbc.gridx = 1;
    gbc.gridy = 5;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    UtilDateModel model = new UtilDateModel();
    Properties props = new Properties();
    props.put("text.day", "Day");
    props.put("text.month", "Month");
    props.put("text.year", "Year");

    JDatePanelImpl datePanel = new JDatePanelImpl(model, props);
    datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());
    datePicker.setPreferredSize(new Dimension(150, 30));
    add(datePicker, gbc);

    gbc.gridx = 0;
    gbc.gridy = 6;
    gbc.fill = GridBagConstraints.NONE;
    add(new JLabel("Blood Group"), gbc);

    gbc.gridx = 1;
    gbc.gridy = 6;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    String[] bloodOptions = {"A+", "AB+", "A-", "B+", "B-", "O-", "O+"};
    bloodGroupField = new JComboBox<>(bloodOptions);
    add(bloodGroupField, gbc);
    JButton signup = new JButton("signup");
    signup.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Boolean flag = true;

            // Create a URL object with the REST API endpoint
            RestTemplate restTemplate = new RestTemplate();

            // create request headers with Content-Type header
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // create request body
            Donor user = new Donor();
            if (getUsername() != null && !getUsername().equals("")) {
              user.setUsername(getUsername());
            } else {
              flag = false;
              JOptionPane.showMessageDialog(
                  new JOptionPane(),
                  "User name can not be empty",
                  "Error",
                  JOptionPane.ERROR_MESSAGE);
            }
            if (getFirstName() != null && !getFirstName().equals("")) {
              user.setFirst_name(getFirstName());
            } else {
              flag = false;
              JOptionPane.showMessageDialog(
                  new JOptionPane(),
                  "First name can not be empty",
                  "Error",
                  JOptionPane.ERROR_MESSAGE);
            }

            user.setLast_name(getLastName());
            if (passwordField.getPassword().length != 0
                && passwordField != null
                && passwordField.getPassword() != null
                && !passwordField.getPassword().toString().equals("")) {
              char[] passwordChars = passwordField.getPassword();
              String password = new String(passwordChars);
              user.setPassword(password);
            } else {
              flag = false;
              JOptionPane.showMessageDialog(
                  new JOptionPane(),
                  "Password can not be empty",
                  "Error",
                  JOptionPane.ERROR_MESSAGE);
            }
            user.setGender(getGender());
            if (datePicker.getModel().getValue() != null) {
              user.setDate_of_birth(getDateOfBirth());
            } else {
              flag = false;
              JOptionPane.showMessageDialog(
                  new JOptionPane(), "D.O.B can not be empty", "Error", JOptionPane.ERROR_MESSAGE);
            }

            user.setBlood_group(getBloodGroup());
            if (flag) {
              try {

                // create HTTP entity with request body and headers
                HttpEntity<Donor> requestEntity = new HttpEntity<>(user, headers);

                // make the HTTP request and deserialize the response body to DonorResponse object
                //          try{
                ResponseEntity<DonorResponse> response =
                    restTemplate.postForEntity(
                        "http://localhost:8080/donor/registration",
                        requestEntity,
                        DonorResponse.class);

                DonorResponse donorResponse = response.getBody();
                System.out.println(response.getBody().getMessage());

                // Check the response code to see if the login was successful

                if (response.getStatusCode() == HttpStatus.OK
                    && !response.getBody().getStatus().equals("FAILURE")) {
                  // Switch to the appropriate panel
                  cardPanel.add(
                      new MainAppPage(cardPanel, cardLayout, donorResponse.getDonor()), "main-app");
                  cardLayout.show(cardPanel, "main-app");
                  // cardLayout.show(cardPanel, "workerLogin-page");
                } else {

                  JOptionPane.showMessageDialog(
                      new JOptionPane(),
                      response.getBody().getMessage(),
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
              //
            }
          }
        });
    add(signup);
  }

  public String getUsername() {
    return usernameField.getText();
  }

  public String getFirstName() {
    return firstNameField.getText();
  }

  public String getLastName() {
    return lastNameField.getText();
  }

  public String getGender() {
    return (String) genderField.getSelectedItem();
  }

  public String getBloodGroup() {
    return (String) bloodGroupField.getSelectedItem();
  }

  public Date getDateOfBirth() {
    UtilDateModel model = (UtilDateModel) datePicker.getModel();
    java.util.Date selectedDate = model.getValue();
    Date sqlDate = new Date(selectedDate.getTime());
    return sqlDate;
  }
}
