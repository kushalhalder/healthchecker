package com.udaan.healthchecker.controllers;

import com.udaan.healthchecker.security.services.UserDetailsImpl;
import com.udaan.healthchecker.service.InfluxDBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/health")
public class PingController {

  @Autowired
  AuthenticationManager authenticationManager;

  @Autowired
  private InfluxDBService influxDBService;

  @GetMapping("/ping")
  public String push() {
    UserDetailsImpl userDetails =
        (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();


    influxDBService.pushData(userDetails.getUsername(), userDetails.getEmail());

    return "OK";
  }
}
