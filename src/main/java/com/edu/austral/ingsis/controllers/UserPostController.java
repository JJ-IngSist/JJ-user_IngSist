package com.edu.austral.ingsis.controllers;

import com.edu.austral.ingsis.services.UserService;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class UserPostController {

  private final UserService userService;
  private final RestTemplate restTemplate;

  public UserPostController(UserService userService) {
    this.userService = userService;
    restTemplate = new RestTemplate();
  }


}
