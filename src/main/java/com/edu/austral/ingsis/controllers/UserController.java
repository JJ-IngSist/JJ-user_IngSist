package com.edu.austral.ingsis.controllers;

import com.edu.austral.ingsis.dtos.CreateUserDTO;
import com.edu.austral.ingsis.dtos.SignInUserDTO;
import com.edu.austral.ingsis.dtos.UpdateUserDTO;
import com.edu.austral.ingsis.dtos.UserDTO;
import com.edu.austral.ingsis.entities.User;
import com.edu.austral.ingsis.services.UserService;
import com.edu.austral.ingsis.utils.AlreadyExistsException;
import com.edu.austral.ingsis.utils.NotFoundException;
import com.edu.austral.ingsis.utils.ObjectMapper;
import com.edu.austral.ingsis.utils.ObjectMapperImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.ArrayList;

@RestController
@CrossOrigin(origins = "*", methods= {RequestMethod.GET,RequestMethod.POST,RequestMethod.DELETE,RequestMethod.PUT})
public class UserController {

  private final ObjectMapper objectMapper;
  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
    this.objectMapper = new ObjectMapperImpl();
  }

  @PostMapping("/login")
  public ResponseEntity<UserDTO> authenticate(@Valid @RequestBody SignInUserDTO signInUserDTO) {
    if(!userService.checkUsername(signInUserDTO.getUsername()))
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The user is not registered");
    User user = userService.findByUsername(signInUserDTO.getUsername());
    return ResponseEntity.ok(objectMapper.map(user, UserDTO.class));
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
    } catch (AlreadyExistsException e) {
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
