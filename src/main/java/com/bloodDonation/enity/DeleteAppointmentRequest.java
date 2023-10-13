package com.bloodDonation.enity;

import lombok.Data;

@Data
public class DeleteAppointmentRequest {
  private Integer appointmentId;
  private String donorUserName;
}
