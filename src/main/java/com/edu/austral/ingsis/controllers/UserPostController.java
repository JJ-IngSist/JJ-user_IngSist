package com.edu.austral.ingsis.controllers;

import com.edu.austral.ingsis.dtos.UserDTO;
import com.edu.austral.ingsis.entities.User;
import com.edu.austral.ingsis.services.UserService;
import com.edu.austral.ingsis.utils.AlreadyExistsException;
import com.edu.austral.ingsis.utils.ObjectMapper;
import com.edu.austral.ingsis.utils.ObjectMapperImpl;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static com.edu.austral.ingsis.utils.ConnectMicroservices.getRequestEntity;

@RestController
public class UserPostController {

  private final ObjectMapper objectMapper;
  private final UserService userService;
  private final RestTemplate restTemplate;

  public UserPostController(UserService userService) {
    this.userService = userService;
    this.objectMapper = new ObjectMapperImpl();
    restTemplate = new RestTemplate();
  }

  @PostMapping("/user/like/{id}")
  public ResponseEntity<UserDTO> likePost(@PathVariable Long id) {
    try {
      final User user = userService.likePost(id);
      restTemplate.exchange("http://localhost:8081/post/like/" + id,
              HttpMethod.POST,
              getRequestEntity(),
              String.class);
      return ResponseEntity.ok(objectMapper.map(user, UserDTO.class));
    } catch (AlreadyExistsException e) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "You've already liked this post");
    }
  }

  @PostMapping("/user/dislike/{id}")
  public ResponseEntity<UserDTO> dislikePost(@PathVariable Long id) {
    try {
      final User user = userService.dislikePost(id);
      restTemplate.exchange("http://localhost:8081/post/dislike/" + id,
              HttpMethod.POST,
              getRequestEntity(),
              String.class);
      return ResponseEntity.ok(objectMapper.map(user, UserDTO.class));
    } catch (AlreadyExistsException e) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "You didn't like this post");
    }
  }

  @GetMapping("/user/{id}/liked")
  public ResponseEntity<List<UserDTO>> getUsersWhoLikedPost(@PathVariable Long id) {
    final List<User> users = userService.getUsersWhoLikedPost(id);
    return ResponseEntity.ok(objectMapper.map(users, UserDTO.class));
  }

  @GetMapping("/logged/{id}/liked")
  public boolean checkIfLoggedLikedPost(@PathVariable Long id) {
    return userService.checkIfLoggedLikedPost(id);
  }
}
