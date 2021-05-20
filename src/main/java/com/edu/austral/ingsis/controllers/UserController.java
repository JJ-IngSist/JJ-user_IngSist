package com.edu.austral.ingsis.controllers;

import com.edu.austral.ingsis.dtos.*;
import com.edu.austral.ingsis.entities.User;
import com.edu.austral.ingsis.security.JWTConfigurer;
import com.edu.austral.ingsis.security.JWTToken;
import com.edu.austral.ingsis.security.TokenProvider;
import com.edu.austral.ingsis.services.user.UserService;
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

  public UserController(UserService userService) {
    this.userService = userService;
    this.objectMapper = new ObjectMapperImpl();
  }

  @GetMapping("/user/{id}")
  public ResponseEntity<UserDTO> getUser(@PathVariable Long id) {
    final User user = userService.getById(id);
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
