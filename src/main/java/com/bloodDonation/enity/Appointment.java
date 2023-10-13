package com.bloodDonation.enity;

import java.sql.Date;
import lombok.Data;

@Data
public class Appointment {

  private Integer appointment_id;
  private Date appointment_time;

  private String appointment_with;

  private String appointment_for;

  private int contest_id;
}
