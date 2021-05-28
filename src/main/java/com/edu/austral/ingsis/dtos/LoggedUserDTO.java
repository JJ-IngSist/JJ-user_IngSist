package com.edu.austral.ingsis.dtos;

import com.edu.austral.ingsis.security.JWTToken;

public class LoggedUserDTO {

  private Long id;
  private String username;
  private JWTToken token;

  public LoggedUserDTO(Long id, String username, JWTToken token) {
    this.id = id;
    this.username = username;
    this.token = token;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
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
