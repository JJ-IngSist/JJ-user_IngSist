package com.edu.austral.ingsis.controllers;

import com.edu.austral.ingsis.dtos.CreateUserDTO;
import com.edu.austral.ingsis.dtos.LoggedUserDTO;
import com.edu.austral.ingsis.dtos.SignInUserDTO;
import com.edu.austral.ingsis.dtos.UserDTO;
import com.edu.austral.ingsis.entities.User;
import com.edu.austral.ingsis.security.JWTConfigurer;
import com.edu.austral.ingsis.security.JWTToken;
import com.edu.austral.ingsis.security.TokenProvider;
import com.edu.austral.ingsis.services.user.UserService;
import com.edu.austral.ingsis.exception.NotFoundException;
import com.edu.austral.ingsis.utils.ObjectMapper;
import com.edu.austral.ingsis.utils.ObjectMapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

@RestController
public class AuthController {

  private final ObjectMapper objectMapper;
  private final UserService userService;
  private final TokenProvider tokenProvider;
  @Autowired
  private AuthenticationProvider manager;

  public AuthController(UserService userService, TokenProvider tokenProvider) {
    this.userService = userService;
    this.tokenProvider = tokenProvider;
    this.objectMapper = new ObjectMapperImpl();
  }

  @PostMapping("/login")
  public ResponseEntity<LoggedUserDTO> authenticate(@Valid @RequestBody SignInUserDTO signInUserDTO) {
    if(!userService.checkUsername(signInUserDTO.getUsername()))
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The user is not registered");
    User user = userService.findByUsername(signInUserDTO.getUsername());
    UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
    try {
      Authentication authentication = this.manager.authenticate(token);
      SecurityContextHolder.getContext().setAuthentication(authentication);
      String jwt = tokenProvider.createToken(authentication);
      HttpHeaders httpHeaders = new HttpHeaders();
      httpHeaders.add(JWTConfigurer.AUTHORIZATION_HEADER, "Bearer " + jwt);
      return new ResponseEntity<>(new LoggedUserDTO(user.getUsername(), new JWTToken(jwt)), httpHeaders, HttpStatus.OK);
    } catch (BadCredentialsException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }
  }

  @PostMapping("/register")
  public ResponseEntity<UserDTO> registerUser(@RequestBody @Valid CreateUserDTO createUserDTO) {
    final User user = userService.save(objectMapper.map(createUserDTO, User.class));
    return ResponseEntity.ok(objectMapper.map(user, UserDTO.class));
  }

  @GetMapping("/user/logged")
  public ResponseEntity<UserDTO> getLoggedUser() {
    try {
      final User user = userService.findLogged();
      return ResponseEntity.ok(objectMapper.map(user, UserDTO.class));
    } catch (NotFoundException e) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }
  }
}
