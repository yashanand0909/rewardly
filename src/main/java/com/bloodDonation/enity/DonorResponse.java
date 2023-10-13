package com.bloodDonation.enity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DonorResponse extends BaseApiResponse {
  private Donor donor;

  public DonorResponse() {}
}
