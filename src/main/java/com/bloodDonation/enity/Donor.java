package com.bloodDonation.enity;

import java.sql.Date;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class Donor {
  @NonNull private String username;
  @NonNull private String first_name;
  private String last_name;
  @NonNull private String password;
  @NonNull private String gender;
  @NonNull private Date date_of_birth;
  @NonNull private String blood_group;

  @Override
  public String toString() {
    return "Donor{"
        + "username='"
        + username
        + '\''
        + ", first_name='"
        + first_name
        + '\''
        + ", last_name='"
        + last_name
        + '\''
        + ", password='"
        + password
        + '\''
        + ", gender='"
        + gender
        + '\''
        + ", date_of_birth='"
        + date_of_birth
        + '\''
        + ", blood_group='"
        + blood_group
        + '\''
        + '}';
  }
}
