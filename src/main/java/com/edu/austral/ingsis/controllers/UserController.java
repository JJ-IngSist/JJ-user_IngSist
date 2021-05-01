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
import org.modelmapper.spi.ErrorMessage;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

@RestController
public class UserController {

  private final ObjectMapper objectMapper;
  private final UserService userService;
//  private final AuthenticationProvider authenticationProvider;

  public UserController(UserService userService) {
    this.userService = userService;
    this.objectMapper = new ObjectMapperImpl();
  }

//  @PostMapping("/login")
//  public ResponseEntity authenticate(@Valid @RequestBody SignInUserDTO signInUserDTO) {
//
//    if(!userService.checkUsername(signInUserDTO.getUsername())) return new ResponseEntity<>(new ErrorMessage("¡Las credenciales ingresadas son incorrectas!"),HttpStatus.UNAUTHORIZED);
//
//    User user = userService.findByUsername(signInUserDTO.getUsername());
//
//    UsernamePasswordAuthenticationToken authenticationToken =
//            new UsernamePasswordAuthenticationToken(signInUserDTO.getUsername(), signInUserDTO.getPassword());
//
//    try {
//      Authentication authentication = this.authenticationProvider.authenticate(authenticationToken);
//      SecurityContextHolder.getContext().setAuthentication(authentication);
//      String jwt = tokenProvider.createToken(authentication);
//
//      HttpHeaders httpHeaders = new HttpHeaders();
//      httpHeaders.add(JWTConfigurer.AUTHORIZATION_HEADER, "Bearer " + jwt);
//      return new ResponseEntity<>(new LoginResponse(new JWTToken(jwt), user.isAdmin(), user.getFirstName() + " " + user.getLastName()), httpHeaders, HttpStatus.OK);
//    }
//    catch (AuthenticationException e){
//      return new ResponseEntity<>(new ErrorMessage("¡Las credenciales ingresadas son incorrectas!"),HttpStatus.UNAUTHORIZED);
//    }
//  }

  @PostMapping("/register")
  public ResponseEntity<UserDTO> registerUser(@RequestBody @Valid CreateUserDTO createUserDTO) {
    final User user = userService.register(objectMapper.map(createUserDTO, User.class));
    return ResponseEntity.ok(objectMapper.map(user, UserDTO.class));
  }

  @GetMapping("/profile/{id}")
  public ResponseEntity<UserDTO> getUser(@PathVariable Long id) {
    final User user = userService.getById(id);
    return ResponseEntity.ok(objectMapper.map(user, UserDTO.class));
  }

  @GetMapping("/logged")
  public ResponseEntity<UserDTO> getLoggedUser() {
    final User user = userService.findLogged();
    return ResponseEntity.ok(objectMapper.map(user, UserDTO.class));
  }

  @PutMapping("/{id}")
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

  @DeleteMapping("/{id}")
  public ResponseEntity<UserDTO> updateUser(@PathVariable Long id) {
    userService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
