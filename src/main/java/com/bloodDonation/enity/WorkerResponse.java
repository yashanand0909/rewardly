package com.bloodDonation.enity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WorkerResponse extends BaseApiResponse {
  private Worker worker;

  public WorkerResponse() {}
}
