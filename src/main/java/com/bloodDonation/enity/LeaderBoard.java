package com.bloodDonation.enity;

import java.util.List;
import lombok.Data;

@Data
public class LeaderBoard extends BaseApiResponse {
  private List<ContestDonation> donations;
}
