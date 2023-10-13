package com.bloodDonation.enity;

import java.util.List;
import lombok.Data;

@Data
public class ContestResponse extends BaseApiResponse {
  private List<Contest> joinedContest;
  private List<Contest> ActiveContest;
}
