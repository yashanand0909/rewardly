package com.bloodDonation.view;

import com.bloodDonation.enity.AppointmentResponse;
import com.bloodDonation.enity.Contest;
import com.bloodDonation.enity.CreateAppointment;
import com.bloodDonation.enity.Donor;
import com.bloodDonation.enity.Worker;
import com.bloodDonation.enity.WorkerListResponse;
import com.bloodDonation.utility.DateLabelFormatter;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class AppointmentPanel extends JPanel {

  private JLabel idLabel, collectorLabel, dateLabel;
  private JComboBox<String> collectorBox;
  private JComboBox<Integer> contestBox;
  private JTextField dateTextField;
  private JSpinner timeSpinner;

  public AppointmentPanel(
      JPanel cardPanel, CardLayout cardLayout, Donor donor, List<Contest> contests) {

    // Set layout to GridBagLayout
    this.setLayout(new GridBagLayout());

    collectorLabel = new JLabel("Appointment With:");
    JLabel contestLabel = new JLabel("Contest");
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> entity = new HttpEntity<>(headers);

    ResponseEntity<WorkerListResponse> response =
        restTemplate.exchange(
            "http://localhost:8080/worker/", HttpMethod.GET, entity, WorkerListResponse.class);

    List<Worker> workers = response.getBody().getWorkers();
    List<String> appointmentWith = new ArrayList<>();
    for (Worker worker : workers) {
      appointmentWith.add(worker.getUsername());
    }

    collectorBox = new JComboBox<>(appointmentWith.toArray(new String[0]));
    addComponent(collectorLabel, 0, 1);
    addComponent(collectorBox, 1, 1);
    List<Integer> contestIds = new ArrayList<>();
    for (Contest contest : contests) {
      contestIds.add(contest.getContestId());
    }
    contestBox = new JComboBox<>(contestIds.toArray(new Integer[0]));
    addComponent(contestLabel, 0, 3);
    addComponent(contestBox, 1, 3);
    UtilDateModel model = new UtilDateModel();
    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    Date latestDate = calendar.getTime();
    model.setValue(latestDate);

    Properties props = new Properties();
    props.put("text.day", "Day");
    props.put("text.month", "Month");
    props.put("text.year", "Year");

    JDatePanelImpl datePanel = new JDatePanelImpl(model, props);
    JDatePickerImpl datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());
    datePicker.setPreferredSize(new Dimension(150, 30));

    timeSpinner = new JSpinner(new SpinnerDateModel());
    JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeSpinner, "HH:mm:ss");
    timeSpinner.setEditor(timeEditor);
    //    addComponent(dateLabel, 0, 2);
    addComponent(datePicker, 1, 2);
    addComponent(timeSpinner, 2, 2);
    JButton addButton = new JButton("Add Appointment");
    addButton.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            CreateAppointment createAppointment =
                new CreateAppointment(); // replace with your object
            createAppointment.setAppointmentFor(donor.getUsername());
            UtilDateModel model = (UtilDateModel) datePicker.getModel();
            java.util.Date selectedDate = model.getValue();
            java.sql.Date sqlDate = new java.sql.Date(selectedDate.getTime());

            Boolean flag = true;

            java.util.Date today = new java.util.Date();
            if (sqlDate.compareTo(new java.sql.Date(today.getTime())) > 0) {
              createAppointment.setAppointmentTime(sqlDate);
            } else {
              flag = false;
              JOptionPane.showMessageDialog(
                  new JOptionPane(),
                  "Date should be of future",
                  "Error",
                  JOptionPane.ERROR_MESSAGE);
            }
            if (flag) {

              createAppointment.setAppointmentWith(collectorBox.getSelectedItem().toString());
              createAppointment.setContestId((Integer) contestBox.getSelectedItem());
              HttpEntity<CreateAppointment> request = new HttpEntity<>(createAppointment, headers);

              String url =
                  "http://localhost:8080/donor/appointment"; // replace with your endpoint URL
              ResponseEntity<AppointmentResponse> responseEntity =
                  restTemplate.postForEntity(url, request, AppointmentResponse.class);

              cardPanel.add(new MainAppPage(cardPanel, cardLayout, donor), "main-app");
              cardLayout.show(cardPanel, "main-app");
            }
          }
        });
    addComponent(addButton, 1, 4);
  }

  private void addComponent(JComponent component, int gridx, int gridy) {
    GridBagConstraints constraints = new GridBagConstraints();
    constraints.fill = GridBagConstraints.HORIZONTAL;
    constraints.gridx = gridx;
    constraints.gridy = gridy;
    constraints.insets = new Insets(10, 10, 10, 10);
    this.add(component, constraints);
  }
}
