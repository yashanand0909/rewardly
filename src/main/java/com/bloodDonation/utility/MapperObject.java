package com.bloodDonation.utility;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@Data
@PropertySource("classpath:config.properties")
public class MapperObject {
  @Value("${root}")
  private String root;

  @Value("${db.url}")
  private String url;

  @Value("${password}")
  private String password;
}
