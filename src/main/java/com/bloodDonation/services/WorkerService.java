package com.bloodDonation.services;

import com.bloodDonation.enity.AppointmentResponse;
import com.bloodDonation.enity.Donation;
import com.bloodDonation.enity.Login;
import com.bloodDonation.enity.Worker;
import com.bloodDonation.enity.WorkerListResponse;
import com.bloodDonation.enity.WorkerResponse;
import com.bloodDonation.impl.WorkerProcessImpl;
import java.util.List;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/worker")
public class WorkerService {
  @Autowired WorkerProcessImpl workerProcessImpl;

  @PostMapping(path = "/registration", consumes = "application/json")
  public ResponseEntity<WorkerResponse> getWorkerDetails(@RequestBody Worker worker) {
    workerProcessImpl.createWorker(worker);
    return new ResponseEntity<>(new WorkerResponse(worker), HttpStatus.OK);
  }

  @PostMapping(path = "/login", consumes = "application/json")
  public ResponseEntity<WorkerResponse> getLoginDetails(@RequestBody Login login) {
    Worker worker = workerProcessImpl.loginWorker(login);
    return new ResponseEntity<>(new WorkerResponse(worker), HttpStatus.OK);
  }

  @GetMapping(path = "/", consumes = "application/json")
  public ResponseEntity<WorkerListResponse> getWorkers() {
    List<Worker> workers = workerProcessImpl.getWorkers();
    return new ResponseEntity<>(new WorkerListResponse(workers), HttpStatus.OK);
  }

  @PutMapping(path = "/registration", consumes = "application/json")
  public ResponseEntity<WorkerResponse> UpdateUserDetails(@RequestBody Worker worker) {
    workerProcessImpl.updateWorker(worker);
    return new ResponseEntity<>(new WorkerResponse(worker), HttpStatus.OK);
  }

  @GetMapping(path = "/appointment/{worker_username}", consumes = "application/json")
  public ResponseEntity<AppointmentResponse> getAppointments(
      @PathVariable(value = "worker_username") @NonNull String workerUsername) {
    AppointmentResponse appointmentResponse = workerProcessImpl.getAppointment(workerUsername);
    return new ResponseEntity<>(appointmentResponse, HttpStatus.OK);
  }

  @PostMapping(path = "/donation", consumes = "application/json")
  public ResponseEntity<String> addDonation(@RequestBody Donation donation) {
    workerProcessImpl.addDonation(donation);
    return new ResponseEntity<>("Donation added", HttpStatus.OK);
  }
}
