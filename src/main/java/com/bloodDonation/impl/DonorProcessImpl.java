package com.bloodDonation.impl;

import com.bloodDonation.db.DataBaseConnector;
import com.bloodDonation.enity.Appointment;
import com.bloodDonation.enity.AppointmentResponse;
import com.bloodDonation.enity.Contest;
import com.bloodDonation.enity.ContestDonation;
import com.bloodDonation.enity.ContestResponse;
import com.bloodDonation.enity.CreateAppointment;
import com.bloodDonation.enity.DeleteAppointmentRequest;
import com.bloodDonation.enity.Donation;
import com.bloodDonation.enity.DonationResponse;
import com.bloodDonation.enity.Donor;
import com.bloodDonation.enity.JoinContestRequest;
import com.bloodDonation.enity.LeaderBoard;
import com.bloodDonation.enity.Login;
import com.bloodDonation.exception.BadRequestException;
import com.bloodDonation.exception.GenericException;
import com.bloodDonation.utility.MapperObject;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DonorProcessImpl {

  private final DataBaseConnector dbConnector;

  @Autowired MapperObject mapper;

  @Autowired
  public DonorProcessImpl(DataBaseConnector dbConnector) {
    this.dbConnector = dbConnector;
  }

  public void createDonor(Donor donor) {
    try {
      Connection con =
          dbConnector.getConnection(
              mapper.getUrl(), mapper.getRoot(), mapper.getPassword(), new HashMap<>());
      if (donor.getUsername().contains(" ") || donor.getPassword().contains(" "))
        throw new BadRequestException("White space Not allowed in username and password");

      if (con != null) {
        String userNameSQL = "SELECT * FROM donor where username = ?";
        PreparedStatement stmt = con.prepareStatement(userNameSQL);
        stmt.setString(1, donor.getUsername());
        ResultSet rs = stmt.executeQuery();
        if (rs.next())
          throw new BadRequestException("username already exists please try with another username");

        String sql =
            "INSERT INTO donor (username, first_name, last_name, password, gender, date_of_birth, blood_group, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        // Set the parameter values for the query

        try {
          stmt = con.prepareStatement(sql);
          stmt.setString(1, donor.getUsername());
          stmt.setString(2, donor.getFirst_name());
          stmt.setString(3, donor.getLast_name());
          stmt.setString(4, donor.getPassword());
          stmt.setString(5, donor.getGender());
          stmt.setDate(6, donor.getDate_of_birth());
          stmt.setString(7, donor.getBlood_group());
          Timestamp createdAt = new Timestamp(System.currentTimeMillis());
          stmt.setTimestamp(8, createdAt);
          // Execute the query to insert the record
          int rowsInserted = stmt.executeUpdate();
          if (rowsInserted > 0) {
            System.out.println("A new record has been inserted.");
          } else {
            throw new GenericException("Failed to signup please try after some time");
          }

          // Remember to close the PreparedStatement and Connection objects when done
          stmt.close();
          dbConnector.closeConnection();
        } catch (SQLException e) {
          throw new GenericException(e.getMessage());
        }

        con.close();
      } else {
        throw new GenericException("Failed to establish a database connection.");
      }
    } catch (SQLException ex) {
      System.err.println("An error occurred while connecting to the database: " + ex.getMessage());
      throw new GenericException(
          "An error occurred while connecting to the database: " + ex.getMessage());
    }
  }

  public Donor loginDonor(Login login) {
    Connection con =
        dbConnector.getConnection(
            mapper.getUrl(), mapper.getRoot(), mapper.getPassword(), new HashMap<>());
    Donor donor = new Donor();
    if (con != null) {
      String sql = "SELECT * FROM donor WHERE username=? AND password=?";
      PreparedStatement stmt = null;
      try {
        stmt = con.prepareStatement(sql);
        stmt.setString(1, login.getUsername());
        stmt.setString(2, login.getPassword());

        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
          donor.setUsername(rs.getString("username"));
          donor.setFirst_name(rs.getString("first_name"));
          donor.setLast_name(rs.getString("last_name"));
          donor.setPassword(rs.getString("password"));
          donor.setGender(rs.getString("gender"));
          donor.setDate_of_birth(rs.getDate("date_of_birth"));
          donor.setBlood_group(rs.getString("blood_group"));
        } else {
          throw new GenericException(
              "User details not found please try to login with correct details");
        }
      } catch (SQLException e) {
        throw new GenericException(e.getMessage());
      }
    } else {
      throw new GenericException("Failed to connect to database");
    }
    return donor;
  }

  public ContestResponse getContest(String userName) {
    Connection con =
        dbConnector.getConnection(
            mapper.getUrl(), mapper.getRoot(), mapper.getPassword(), new HashMap<>());
    ContestResponse contestResponse = new ContestResponse();
    if (con != null) {
      String sqlJoined =
          "SELECT contest_id, name, description FROM contest WHERE contest_id in (select contest_id from contest_donation where donor_username = ?)";
      String sqlNotJoined =
          "SELECT contest_id, name, description FROM contest WHERE contest_id NOT IN (select contest_id from contest_donation where donor_username=? and contest_id is not null)";
      try {
        PreparedStatement stmt;
        stmt = con.prepareStatement(sqlJoined);
        stmt.setString(1, userName);
        ResultSet rs = stmt.executeQuery();
        List<Contest> joinedContest = new ArrayList<>();
        while (rs.next()) {
          Contest contest = new Contest();
          contest.setContestId(rs.getInt("contest_id"));
          contest.setName(rs.getString("name"));
          contest.setDescription(rs.getString("description"));
          joinedContest.add(contest);
        }

        stmt = con.prepareStatement(sqlNotJoined);
        stmt.setString(1, userName);
        rs = stmt.executeQuery();
        List<Contest> unJoinedContest = new ArrayList<>();
        while (rs.next()) {
          Contest contest = new Contest();
          contest.setContestId(rs.getInt("contest_id"));
          contest.setName(rs.getString("name"));
          contest.setDescription(rs.getString("description"));
          unJoinedContest.add(contest);
        }
        contestResponse.setJoinedContest(joinedContest);
        contestResponse.setActiveContest(unJoinedContest);
        stmt.close();
        dbConnector.closeConnection();
        con.close();
      } catch (Exception e) {
        System.out.println("Get contest failed with error - " + e.getMessage());
        throw new GenericException("Get contest failed with error - " + e.getMessage());
        // add error handling
      }
    } else {
      throw new GenericException("Failed to connect to database");
    }
    return contestResponse;
  }

  public ContestResponse joinContest(JoinContestRequest request) {
    try {
      Connection con =
          dbConnector.getConnection(
              mapper.getUrl(), mapper.getRoot(), mapper.getPassword(), new HashMap<>());

      if (con != null) {
        String sql = "INSERT INTO contest_donation (contest_id, donor_username) VALUES (?, ?)";
        // Set the parameter values for the query
        try {
          PreparedStatement stmt = con.prepareStatement(sql);
          stmt.setInt(1, request.getContestId());
          stmt.setString(2, request.getUserName());

          int rowsInserted = stmt.executeUpdate();
          if (rowsInserted == 0) {
            throw new GenericException("Join contest failed");
          }
          stmt.close();
          dbConnector.closeConnection();
        } catch (SQLException e) {
          throw new RuntimeException(e);
        }
        con.close();
        return getContest(request.getUserName());
      } else {
        throw new GenericException("Failed to join contest due to db connection issue");
      }
    } catch (SQLException ex) {
      System.err.println("An error occurred while connecting to the database: " + ex.getMessage());
      throw new GenericException(
          "An error occurred while connecting to the database: " + ex.getMessage());
    } catch (Exception ex) {
      System.err.println("An unexpected error occurred: " + ex.getMessage());
      throw new GenericException("An unexpected error occurred: " + ex.getMessage());
    }
  }

  public AppointmentResponse getAppointment(String donorUsername) {
    Connection con =
        dbConnector.getConnection(
            mapper.getUrl(), mapper.getRoot(), mapper.getPassword(), new HashMap<>());
    List<Appointment> appointmentsList = new ArrayList<>();
    AppointmentResponse appointmentResponse = new AppointmentResponse();
    if (con != null) {
      String sql =
          "SELECT * FROM appointment WHERE appointment_for =? and appointment_time >= Now() and checked_in = false order by appointment_time";
      PreparedStatement stmt = null;
      try {
        stmt = con.prepareStatement(sql);
        stmt.setString(1, donorUsername);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
          Appointment appointment = new Appointment();
          appointment.setAppointment_id(rs.getInt("appointment_id"));
          appointment.setContest_id(rs.getInt("contest_id"));
          appointment.setAppointment_time(rs.getDate("appointment_time"));
          appointment.setAppointment_for(rs.getString("appointment_for"));
          appointment.setAppointment_with(rs.getString("appointment_with"));
          appointmentsList.add(appointment);
        }

        stmt.close();
        dbConnector.closeConnection();
        con.close();
      } catch (Exception e) {
        System.out.println("Get contest failed");
        // add error handling
      }
      appointmentResponse.setUpcomingAppointments(appointmentsList);
    } else {
      throw new GenericException("Failed to connect to database");
    }
    return appointmentResponse;
  }

  public AppointmentResponse createAppointment(CreateAppointment createAppointmentRequest) {
    try {
      Connection con =
          dbConnector.getConnection(
              mapper.getUrl(), mapper.getRoot(), mapper.getPassword(), new HashMap<>());

      if (con != null) {
        String sql =
            "INSERT INTO appointment (appointment_time, appointment_with, appointment_for,contest_id) VALUES (?, ?, ?,?)";
        // Set the parameter values for the query
        try {
          PreparedStatement stmt = con.prepareStatement(sql);

          stmt.setDate(1, createAppointmentRequest.getAppointmentTime());
          stmt.setString(2, createAppointmentRequest.getAppointmentWith());
          stmt.setString(3, createAppointmentRequest.getAppointmentFor());
          stmt.setInt(4, createAppointmentRequest.getContestId());
          int rowsInserted = stmt.executeUpdate();
          if (rowsInserted == 0) {
            throw new GenericException("create appointment failed");
          }
          stmt.close();
          dbConnector.closeConnection();
        } catch (SQLException e) {
          throw new RuntimeException(e);
        }
        con.close();
        return getAppointment(createAppointmentRequest.getAppointmentFor());
      } else {
        throw new GenericException("Failed to create appointment due to db connection issue");
      }
    } catch (SQLException ex) {
      System.err.println("An error occurred while connecting to the database: " + ex.getMessage());
      throw new GenericException(
          "An error occurred while connecting to the database: " + ex.getMessage());
    } catch (BadRequestException e) {
      throw e;
    } catch (Exception ex) {
      System.err.println("An unexpected error occurred: " + ex.getMessage());
      throw new GenericException(ex.getMessage());
    }
  }

  public AppointmentResponse deleteAppointment(DeleteAppointmentRequest deleteAppointmentRequest) {
    Connection con = null;
    CallableStatement stmt = null;
    AppointmentResponse appointmentResponse = new AppointmentResponse();
    try {
      con =
          dbConnector.getConnection(
              mapper.getUrl(), mapper.getRoot(), mapper.getPassword(), new HashMap<>());
      if (con != null) {
        String storedProc = "{CALL delete_appointment(?)}";
        stmt = con.prepareCall(storedProc);
        stmt.setInt(1, deleteAppointmentRequest.getAppointmentId());
        stmt.execute();
        int rowsUpdated = stmt.getUpdateCount();
        if (rowsUpdated == 0) {
          System.out.println("Failed to Delete the record.");
          throw new BadRequestException("Delete appointment failed");
        }
        stmt.close();
        dbConnector.closeConnection();
        return getAppointment(deleteAppointmentRequest.getDonorUserName());
      } else {
        System.err.println("Failed to establish a database connection.");
        throw new GenericException("Failed to establish a database connection.");
      }
    } catch (SQLException ex) {
      System.err.println("An error occurred while connecting to the database: " + ex.getMessage());
      throw new GenericException("Error occurred: " + ex.getMessage());
    } catch (BadRequestException e) {
      throw e;
    } catch (Exception ex) {
      System.err.println("An unexpected error occurred: " + ex.getMessage());
      throw new GenericException(ex.getMessage());
    }
  }

  public Donor updateDonor(Donor donor) {
    Connection con = null;
    CallableStatement stmt = null;
    try {
      con =
          dbConnector.getConnection(
              mapper.getUrl(), mapper.getRoot(), mapper.getPassword(), new HashMap<>());
      if (con != null) {
        String storedProc = "{CALL update_donor_password(?,?)}";
        stmt = con.prepareCall(storedProc);
        stmt.setString(1, donor.getUsername());
        stmt.setString(2, donor.getPassword());
        stmt.execute();
        int rowsUpdated = stmt.getUpdateCount();
        if (rowsUpdated == 0) {
          System.out.println("Failed to Update the record.");
          throw new BadRequestException("Update appointment failed");
        }
        stmt.close();
        dbConnector.closeConnection();
        return donor;
      } else {
        System.err.println("Failed to establish a database connection.");
        throw new GenericException("Failed to establish a database connection.");
      }
    } catch (SQLException ex) {
      System.err.println("An error occurred while connecting to the database: " + ex.getMessage());
      throw new GenericException("Error occurred: " + ex.getMessage());
    } catch (BadRequestException e) {
      throw e;
    } catch (Exception ex) {
      System.err.println("An unexpected error occurred: " + ex.getMessage());
      throw new GenericException(ex.getMessage());
    }
  }

  public DonationResponse getDonations(String donorsUserName) {
    Connection con =
        dbConnector.getConnection(
            mapper.getUrl(), mapper.getRoot(), mapper.getPassword(), new HashMap<>());
    DonationResponse donationResponse = new DonationResponse();
    if (con != null) {
      String sql =
          "SELECT * FROM appointment WHERE appointment_id IN (select appointment_id from appointment where appointment_for = ?) order by appointment_time DESC";
      PreparedStatement stmt = null;
      try {
        stmt = con.prepareStatement(sql);
        stmt.setString(1, donorsUserName);
        ResultSet rs = stmt.executeQuery();
        List<Donation> donationList = new ArrayList<>();
        while (rs.next()) {
          Donation donation = new Donation();
          donation.setDonationId(rs.getInt("donation_id"));
          donation.setContestId(rs.getInt("contest_id"));
          donation.setAppointmentId(rs.getInt("appointment_id"));
          donation.setDateOfdonation(rs.getDate("date_of_donation"));
          donation.setQuantity(rs.getInt("date_of_donation"));
          donationList.add(donation);
        }
        donationResponse.setDonation(donationList);
        stmt.close();
        dbConnector.closeConnection();
        con.close();
      } catch (Exception e) {
        System.out.println("Get contest failed");
        throw new GenericException("Get contest failed with error" + e.getMessage());
      }
    } else {
      throw new GenericException("Failed to connect to database");
    }
    return donationResponse;
  }

  public LeaderBoard getLeaderBoard() {
    Connection con = null;
    CallableStatement cstmt = null;
    ResultSet rs = null;
    LeaderBoard board = new LeaderBoard();
    try {

      con =
          dbConnector.getConnection(
              mapper.getUrl(), mapper.getRoot(), mapper.getPassword(), new HashMap<>());
      if (con != null) {
        String sql = "{ call get_top_donors_per_contest() }";
        cstmt = con.prepareCall(sql);
        rs = cstmt.executeQuery();
        List<ContestDonation> contestDonationList = new ArrayList<>();
        while (rs.next()) {
          ContestDonation contestDonation = new ContestDonation();
          contestDonation.setContest(rs.getInt("contest"));
          contestDonation.setDonated(rs.getInt("donated"));
          contestDonation.setDonor(rs.getString("top_donor"));
          contestDonationList.add(contestDonation);
        }
        board.setDonations(contestDonationList);

      } else {
        throw new GenericException("Failed to connect to database");
      }
    } catch (Exception e) {
      System.out.println("Failed to load contest donations");
      throw new GenericException("Failed to load contest donations with error: " + e.getMessage());
    } finally {
      try {
        if (rs != null) {
          rs.close();
        }
        if (cstmt != null) {
          cstmt.close();
        }
        if (con != null) {
          con.close();
        }
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
    return board;
  }
}
