package com.bloodDonation.impl;

import com.bloodDonation.db.DataBaseConnector;
import com.bloodDonation.enity.Appointment;
import com.bloodDonation.enity.AppointmentResponse;
import com.bloodDonation.enity.Donation;
import com.bloodDonation.enity.Login;
import com.bloodDonation.enity.Worker;
import com.bloodDonation.exception.BadRequestException;
import com.bloodDonation.exception.GenericException;
import com.bloodDonation.utility.MapperObject;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WorkerProcessImpl {
  @Autowired MapperObject mapper;
  private final DataBaseConnector dbConnector;

  @Autowired
  public WorkerProcessImpl(DataBaseConnector dbConnector) {
    this.dbConnector = dbConnector;
  }

  public void createWorker(Worker worker) {
    try {
      Connection con =
          dbConnector.getConnection(
              mapper.getUrl(), mapper.getRoot(), mapper.getPassword(), new HashMap<>());
      if (worker.getUsername().contains(" ") || worker.getPassword().contains(" "))
        throw new BadRequestException("White space Not allowed in username and password");

      if (con != null) {
        String userNameSQL = "SELECT * FROM worker where username = ?";
        PreparedStatement stmt = con.prepareStatement(userNameSQL);
        stmt.setString(1, worker.getUsername());
        ResultSet rs = stmt.executeQuery();
        if (rs.next())
          throw new BadRequestException("username already exists please try with another username");
        String sql =
            "INSERT INTO worker (username, first_name, last_name, password, gender, date_of_joining, camp_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        // Set the parameter values for the query

        try {
          stmt = con.prepareStatement(sql);
          stmt.setString(1, worker.getUsername());
          stmt.setString(2, worker.getFirst_name());
          stmt.setString(3, worker.getLast_name());
          stmt.setString(4, worker.getPassword());
          stmt.setString(5, worker.getGender());
          stmt.setDate(6, worker.getDateOfJoining());
          stmt.setInt(7, worker.getCampId());
          // Execute the query to insert the record
          int rowsInserted = stmt.executeUpdate();
          if (rowsInserted > 0) {
            System.out.println("A new record has been inserted.");
          } else {
            System.out.println("Failed to insert a new record.");
          }

          stmt.close();
          dbConnector.closeConnection();
        } catch (SQLException e) {
          throw new RuntimeException(e);
        }

        con.close();
      } else {
        System.err.println("Failed to establish a database connection.");
      }
    } catch (SQLException ex) {
      System.err.println("An error occurred while connecting to the database: " + ex.getMessage());
    } catch (Exception ex) {
      System.err.println("An unexpected error occurred: " + ex.getMessage());
    }
  }

  public Worker loginWorker(Login login) {
    Connection con =
        dbConnector.getConnection(
            mapper.getUrl(), mapper.getRoot(), mapper.getPassword(), new HashMap<>());
    if (con != null) {
      String sql = "SELECT * FROM worker WHERE username=? AND password=?";
      PreparedStatement stmt = null;
      try {
        stmt = con.prepareStatement(sql);
        stmt.setString(1, login.getUsername());
        stmt.setString(2, login.getPassword());

        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
          Worker worker = new Worker();
          worker.setUsername(rs.getString("username"));
          worker.setFirst_name(rs.getString("first_name"));
          worker.setLast_name(rs.getString("last_name"));
          worker.setPassword(rs.getString("password"));
          worker.setDateOfJoining(rs.getDate("date_of_joining"));
          worker.setCampId(rs.getInt("camp_id"));
          worker.setGender(rs.getString("gender"));
          System.out.println("Login successful");
          return worker;
          // The user's login credentials match
        } else {
          System.out.println("Login failed");
          throw new GenericException(
              "User details not found please try to login with correct details");
          // The user's login credentials do not match
        }
      } catch (SQLException e) {

        throw new RuntimeException(e);
      }
    }
    return null;
  }

  public AppointmentResponse getAppointment(String workerUsername) {
    Connection con =
        dbConnector.getConnection(
            mapper.getUrl(), mapper.getRoot(), mapper.getPassword(), new HashMap<>());
    AppointmentResponse appointmentResponse = new AppointmentResponse();
    if (con != null) {
      String sql =
          "SELECT * FROM appointment WHERE appointment_with =? and appointment_time >= Now() and checked_in = false order by appointment_time";
      PreparedStatement stmt = null;
      List<Appointment> appointmentsList = new ArrayList<>();
      try {
        stmt = con.prepareStatement(sql);
        stmt.setString(1, workerUsername);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
          Appointment contest = new Appointment();
          contest.setContest_id(rs.getInt("contest_id"));
          contest.setAppointment_id(rs.getInt("appointment_id"));
          contest.setAppointment_time(rs.getDate("appointment_time"));
          contest.setAppointment_for(rs.getString("appointment_for"));
          contest.setAppointment_with(rs.getString("appointment_with"));
          appointmentsList.add(contest);
        }
        appointmentResponse.setUpcomingAppointments(appointmentsList);
        stmt.close();
        dbConnector.closeConnection();
        con.close();
      } catch (Exception e) {
        System.out.println("Get contest failed");
        // add error handling
      }
    }
    return appointmentResponse;
  }

  public List<Worker> getWorkers() {
    Connection con =
        dbConnector.getConnection(
            mapper.getUrl(), mapper.getRoot(), mapper.getPassword(), new HashMap<>());
    List<Worker> workerList = new ArrayList<>();
    if (con != null) {
      String sql = "SELECT * FROM worker";
      PreparedStatement stmt = null;
      try {
        stmt = con.prepareStatement(sql);

        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
          Worker worker = new Worker();
          worker.setUsername(rs.getString("username"));
          worker.setGender(rs.getString("gender"));
          worker.setLast_name(rs.getString("last_name"));
          worker.setFirst_name(rs.getString("first_name"));
          worker.setCampId(rs.getInt("camp_id"));
          worker.setPassword(rs.getString("password"));
          worker.setDateOfJoining(rs.getDate("date_of_joining"));
          workerList.add(worker);
        }

        stmt.close();
        dbConnector.closeConnection();
        con.close();
      } catch (Exception e) {
        System.out.println("Get contest failed");
        // add error handling
      }
    }
    return workerList;
  }

  public Worker updateWorker(Worker worker) {
    Connection con = null;
    CallableStatement stmt = null;
    try {
      con =
          dbConnector.getConnection(
              mapper.getUrl(), mapper.getRoot(), mapper.getPassword(), new HashMap<>());
      if (con != null) {
        String storedProc = "{CALL update_worker_password(?,?)}";
        stmt = con.prepareCall(storedProc);
        stmt.setString(1, worker.getUsername());
        stmt.setString(2, worker.getPassword());
        stmt.execute();
        int rowsUpdated = stmt.getUpdateCount();
        if (rowsUpdated == 0) {
          System.out.println("Failed to Update the record.");
          throw new BadRequestException("Update appointment failed");
        }
        stmt.close();
        dbConnector.closeConnection();
        return worker;
      } else {
        System.err.println("Failed to establish a database connection.");
        throw new GenericException("Failed to establish a database connection.");
      }
    } catch (SQLException ex) {
      System.err.println("An error occurred while connecting to the database: " + ex.getMessage());
      throw new GenericException("Error occurred: " + ex.getMessage());
    } catch (Exception ex) {
      System.err.println("An unexpected error occurred: " + ex.getMessage());
      throw new GenericException("An unexpected error occurred: " + ex.getMessage());
    }
  }

  public void addDonation(Donation donation) {
    Connection con = null;
    CallableStatement stmt = null;
    try {
      con =
          dbConnector.getConnection(
              mapper.getUrl(), mapper.getRoot(), mapper.getPassword(), new HashMap<>());
      if (con != null) {
        String storedProc = "{CALL add_donation_for_appointment(?,?,?,?)}";
        stmt = con.prepareCall(storedProc);
        stmt.setInt(1, donation.getAppointmentId());
        stmt.setInt(2, donation.getContestId());
        stmt.setDate(3, donation.getDateOfdonation());
        stmt.setInt(4, donation.getQuantity());
        stmt.execute();
        int rowsUpdated = stmt.getUpdateCount();
        if (rowsUpdated == 0) {
          System.out.println("Failed to Update the record.");
          throw new BadRequestException("Update appointment failed");
        }
        stmt.close();
        dbConnector.closeConnection();
      } else {
        System.err.println("Failed to establish a database connection.");
        throw new GenericException("Failed to establish a database connection.");
      }
    } catch (SQLException ex) {
      System.err.println("An error occurred while connecting to the database: " + ex.getMessage());
      throw new GenericException("Error occurred: " + ex.getMessage());
    } catch (Exception ex) {
      System.err.println("An unexpected error occurred: " + ex.getMessage());
      throw new GenericException("An unexpected error occurred: " + ex.getMessage());
    }
  }
}
