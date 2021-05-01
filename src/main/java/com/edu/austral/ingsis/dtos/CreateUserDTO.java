package com.edu.austral.ingsis.dtos;

import com.edu.austral.ingsis.utils.Patterns;
import javax.validation.constraints.Pattern;

public class CreateUserDTO {

  private String name;
  private String lastname;

  @Pattern(regexp = Patterns.PATTERN_EMAIL)
  private String email;

  @Pattern(regexp = Patterns.PATTERN_USERNAME)
  private String username;

  @Pattern(regexp = Patterns.PATTERN_PASSWORD)
  private String password;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getLastname() {
    return lastname;
  }

  public void setLastname(String lastname) {
    this.lastname = lastname;
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

  public void setPassword(String password) {
    this.password = password;
  }
}
