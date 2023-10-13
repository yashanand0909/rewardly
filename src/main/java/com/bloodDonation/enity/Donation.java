package com.bloodDonation.enity;

import java.sql.Date;
import lombok.Data;

@Data
public class Donation {
  private Integer donationId;
  private Integer appointmentId;
  private Integer contestId;
  private Date dateOfdonation;
  private Integer quantity;
}
