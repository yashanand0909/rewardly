package com.bloodDonation.enity;

import java.util.List;
import lombok.Data;

@Data
public class AppointmentResponse extends BaseApiResponse {
  List<Appointment> upcomingAppointments;
}
