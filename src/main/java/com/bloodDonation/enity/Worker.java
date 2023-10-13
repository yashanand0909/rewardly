package com.bloodDonation.enity;

import java.sql.Date;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Data
@Getter
@Setter
public class Worker {

  public Worker() {}

  @NonNull private String username;
  @NonNull private String first_name;
  private String last_name;
  @NonNull private String password;
  @NonNull private String gender;
  @NonNull private Date dateOfJoining;
  @NonNull private Integer campId;
}
