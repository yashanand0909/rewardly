package com.bloodDonation.enity;

import java.sql.Date;
import lombok.Data;

@Data
public class CreateAppointment {
  private Date appointmentTime;
  private String appointmentWith;
  private String appointmentFor;
  private int contestId;
}
