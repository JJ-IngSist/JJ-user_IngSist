package com.edu.austral.ingsis.dtos;

import com.edu.austral.ingsis.utils.Patterns;

import javax.validation.constraints.Pattern;

public class UpdateUserDTO {

  private String name;

  @Pattern(regexp = Patterns.PATTERN_EMAIL)
  private String email;

  @Pattern(regexp = Patterns.PATTERN_USERNAME)
  private String username;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }
}
