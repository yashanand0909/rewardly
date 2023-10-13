package com.bloodDonation.enity;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class Login {
  @NonNull private String username;
  @NonNull private String password;
}
