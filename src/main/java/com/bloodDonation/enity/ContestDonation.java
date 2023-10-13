package com.bloodDonation.enity;

import lombok.Data;

@Data
public class ContestDonation {

  private int contest;
  private int donated;
  private String donor;

  public void setContest(int contest) {
    this.contest = contest;
  }

  public int getContest() {
    return contest;
  }

  public void setDonated(int donated) {
    this.donated = donated;
  }

  public void setDonor(String donor) {
    this.donor = donor;
  }
}
