package com.edu.austral.ingsis.controllers;

import com.edu.austral.ingsis.dtos.*;
import com.edu.austral.ingsis.entities.User;
import com.edu.austral.ingsis.exception.AlreadyExistsEmailException;
import com.edu.austral.ingsis.exception.InvalidOldPasswordException;
import com.edu.austral.ingsis.exception.NotFoundException;
import com.edu.austral.ingsis.services.user.UserService;
import com.edu.austral.ingsis.utils.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

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

  @PostMapping("/changePassword")
  public ResponseEntity<UserDTO> changeUserPassword(@Valid @RequestBody ChangeUserPasswordDTO changeUserPasswordDTO) {
    User user = userService.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
    try {
      user = userService.updatePassword(changeUserPasswordDTO.getOldPassword(), changeUserPasswordDTO.getPassword(), user);
      return ResponseEntity.ok(objectMapper.map(user, UserDTO.class));
    } catch (InvalidOldPasswordException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The old password is wrong");
    }
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

  @GetMapping("/users")
  public ResponseEntity<List<UserDTO>> getAllUsers() {
    return ResponseEntity.ok(objectMapper.map(userService.findAll(), UserDTO.class));
  }
}
