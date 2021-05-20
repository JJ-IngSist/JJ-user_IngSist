package com.edu.austral.ingsis.dtos;

import com.edu.austral.ingsis.security.JWTToken;

public class LoggedUserDTO {

  private String username;
  private JWTToken token;

  public LoggedUserDTO(String username, JWTToken token) {
    this.username = username;
    this.token = token;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public JWTToken getToken() {
    return token;
  }

  public void setToken(JWTToken token) {
    this.token = token;
  }
}
