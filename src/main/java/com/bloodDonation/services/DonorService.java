package com.bloodDonation.services;

import com.bloodDonation.enity.AppointmentResponse;
import com.bloodDonation.enity.ContestResponse;
import com.bloodDonation.enity.CreateAppointment;
import com.bloodDonation.enity.DeleteAppointmentRequest;
import com.bloodDonation.enity.DonationResponse;
import com.bloodDonation.enity.Donor;
import com.bloodDonation.enity.DonorResponse;
import com.bloodDonation.enity.JoinContestRequest;
import com.bloodDonation.enity.LeaderBoard;
import com.bloodDonation.enity.Login;
import com.bloodDonation.impl.DonorProcessImpl;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/donor")
public class DonorService {
  @Autowired DonorProcessImpl donorProcessImpl;

  @PostMapping(path = "/registration", consumes = "application/json")
  public ResponseEntity<DonorResponse> getUserDetails(@RequestBody Donor donor) {
    donorProcessImpl.createDonor(donor);
    return new ResponseEntity<>(new DonorResponse(donor), HttpStatus.OK);
  }

  @PostMapping(path = "/login", consumes = "application/json")
  public ResponseEntity<DonorResponse> getLoginDetails(@RequestBody Login login) {
    Donor donor = donorProcessImpl.loginDonor(login);
    return new ResponseEntity<>(new DonorResponse(donor), HttpStatus.OK);
  }

  @GetMapping(path = "/contests/{donor_username}", consumes = "application/json")
  public ResponseEntity<ContestResponse> getContests(
      @PathVariable(value = "donor_username") @NonNull String donorUsername) {
    ContestResponse contestResponse = donorProcessImpl.getContest(donorUsername);
    return new ResponseEntity<>(contestResponse, HttpStatus.OK);
  }

  @PostMapping(path = "/contest", consumes = "application/json")
  public ResponseEntity<ContestResponse> JoinContests(
      @RequestBody JoinContestRequest joinContestRequest) {
    ContestResponse contestResponse = donorProcessImpl.joinContest(joinContestRequest);
    return new ResponseEntity<>(contestResponse, HttpStatus.OK);
  }

  @GetMapping(path = "/appointment/{donor_username}", consumes = "application/json")
  public ResponseEntity<AppointmentResponse> getAppointments(
      @PathVariable(value = "donor_username") @NonNull String donorUsername) {
    AppointmentResponse appointmentResponse = donorProcessImpl.getAppointment(donorUsername);
    return new ResponseEntity<>(appointmentResponse, HttpStatus.OK);
  }

  @PostMapping(path = "/appointment", consumes = "application/json")
  public ResponseEntity<AppointmentResponse> createAppointment(
      @RequestBody CreateAppointment createAppointment) {
    AppointmentResponse appointmentResponse = donorProcessImpl.createAppointment(createAppointment);
    return new ResponseEntity<>(appointmentResponse, HttpStatus.OK);
  }

  @DeleteMapping(path = "/appointment/", consumes = "application/json")
  public ResponseEntity<AppointmentResponse> DeleteAppointment(
      @RequestBody DeleteAppointmentRequest deleteAppointmentRequest) {
    AppointmentResponse appointmentResponse =
        donorProcessImpl.deleteAppointment(deleteAppointmentRequest);
    return new ResponseEntity<>(appointmentResponse, HttpStatus.OK);
  }

  @PutMapping(path = "/registration", consumes = "application/json")
  public ResponseEntity<DonorResponse> UpdateUserDetails(@RequestBody Donor donor) {
    donorProcessImpl.updateDonor(donor);
    return new ResponseEntity<>(new DonorResponse(donor), HttpStatus.OK);
  }

  @GetMapping(path = "/donation/{donor_username}", consumes = "application/json")
  public ResponseEntity<DonationResponse> getDonations(
      @PathVariable(value = "donor_username") @NonNull String donorUsername) {
    DonationResponse donationResponse = donorProcessImpl.getDonations(donorUsername);
    return new ResponseEntity<>(donationResponse, HttpStatus.OK);
  }

  @GetMapping(path = "/leaderBoard", consumes = "application/json")
  public ResponseEntity<LeaderBoard> getLeaderBoard() {
    LeaderBoard leaderBoard = donorProcessImpl.getLeaderBoard();
    return new ResponseEntity<>(leaderBoard, HttpStatus.OK);
  }
}
