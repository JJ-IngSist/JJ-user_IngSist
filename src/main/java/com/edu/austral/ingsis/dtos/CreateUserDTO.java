package com.edu.austral.ingsis.dtos;

import com.edu.austral.ingsis.utils.Patterns;

import javax.annotation.Nullable;
import javax.validation.constraints.Pattern;

public class CreateUserDTO {

  private String name;

  @Pattern(regexp = Patterns.PATTERN_EMAIL)
  private String email;

  @Pattern(regexp = Patterns.PATTERN_USERNAME)
  @Nullable
  private String username;

  @Pattern(regexp = Patterns.PATTERN_PASSWORD)
  @Nullable
  private String password;

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

  public String getPassword() {
    return password;
  }
}
