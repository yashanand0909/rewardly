package com.bloodDonation.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class DataBaseConnector {
  private static Connection con;

  public Connection getConnection(
      String url, String username, String password, Map<Integer, Boolean> map) {

    try {
      Class.forName("com.mysql.cj.jdbc.Driver"); // Driver name
    } catch (ClassNotFoundException e) {
      map.put(4000, true);
      return null;
    }

    try {
      con = DriverManager.getConnection(url, username, password);
    } catch (SQLException e) {
      map.put(e.getErrorCode(), true);
      return null;
    }
    System.out.println(con + "I m con");
    return con;
  }

  public void closeConnection() {
    try {
      con.close();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
