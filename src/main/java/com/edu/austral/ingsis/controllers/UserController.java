package com.edu.austral.ingsis.controllers;

import com.edu.austral.ingsis.dtos.*;
import com.edu.austral.ingsis.entities.User;
import com.edu.austral.ingsis.security.JWTConfigurer;
import com.edu.austral.ingsis.security.JWTToken;
import com.edu.austral.ingsis.security.TokenProvider;
import com.edu.austral.ingsis.services.UserService;
import com.edu.austral.ingsis.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.ArrayList;

@RestController
public class UserController {

  private final ObjectMapper objectMapper;
  private final UserService userService;
  private final TokenProvider tokenProvider;
  @Autowired
  private AuthenticationProvider manager;

  public UserController(UserService userService, TokenProvider tokenProvider) {
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
    final User user = userService.register(objectMapper.map(createUserDTO, User.class));
    return ResponseEntity.ok(objectMapper.map(user, UserDTO.class));
  }

  @GetMapping("/user/{id}")
  public ResponseEntity<UserDTO> getUser(@PathVariable Long id) {
    final User user = userService.getById(id);
    return ResponseEntity.ok(objectMapper.map(user, UserDTO.class));
  }

  @GetMapping("/user/logged")
  public ResponseEntity<UserDTO> getLoggedUser() {
    final User user = userService.findLogged();
    return ResponseEntity.ok(objectMapper.map(user, UserDTO.class));
  }

  @PutMapping("/user/{id}")
  public ResponseEntity<UserDTO> updateUser(@PathVariable Long id,
                                            @RequestBody @Valid UpdateUserDTO updateUserDTO) {
    try {
      final User user = userService.update(id, objectMapper.map(updateUserDTO, User.class));
      return ResponseEntity.ok(objectMapper.map(user, UserDTO.class));
    } catch (AlreadyExistsEmailException e) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "This email is already in use");
    } catch (NotFoundException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The user is not registered");
    }
  }

  @DeleteMapping("/user/{id}")
  public ResponseEntity<UserDTO> deleteUser(@PathVariable Long id) {
    User user = userService.getById(id);
    user.setFollowed(new ArrayList<>());
    userService.register(user);
    userService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
