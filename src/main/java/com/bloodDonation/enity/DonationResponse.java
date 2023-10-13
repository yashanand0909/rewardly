package com.bloodDonation.enity;

import java.util.List;
import lombok.Data;

@Data
public class DonationResponse extends BaseApiResponse {
  private List<Donation> donation;
}
