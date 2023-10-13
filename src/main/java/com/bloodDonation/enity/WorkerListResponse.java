package com.bloodDonation.enity;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WorkerListResponse extends BaseApiResponse {
  List<Worker> workers;

  public WorkerListResponse() {}
}
