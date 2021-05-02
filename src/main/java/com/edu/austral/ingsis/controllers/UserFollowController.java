package com.edu.austral.ingsis.controllers;

import com.edu.austral.ingsis.dtos.UserDTO;
import com.edu.austral.ingsis.entities.User;
import com.edu.austral.ingsis.services.UserService;
import com.edu.austral.ingsis.utils.AlreadyExistsException;
import com.edu.austral.ingsis.utils.NotFoundException;
import com.edu.austral.ingsis.utils.ObjectMapper;
import com.edu.austral.ingsis.utils.ObjectMapperImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
public class UserFollowController {

  private final ObjectMapper objectMapper;
  private final UserService userService;

  public UserFollowController(UserService userService) {
    this.userService = userService;
    this.objectMapper = new ObjectMapperImpl();
  }

  @PostMapping("/user/{id}/follow")
  public ResponseEntity<UserDTO> followUser(@PathVariable Long id) {
    try {
      final User user = userService.follow(id);
      return ResponseEntity.ok(objectMapper.map(user, UserDTO.class));
    } catch (AlreadyExistsException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You already follow this user");
    } catch (RuntimeException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You can't follow yourself");
    }
  }

  @PostMapping("/user/{id}/unfollow")
  public ResponseEntity<UserDTO> unfollowUser(@PathVariable Long id) {
    try {
      final User user = userService.unfollow(id);
      return ResponseEntity.ok(objectMapper.map(user, UserDTO.class));
    } catch (NotFoundException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You don't follow this user");
    } catch (RuntimeException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You can't unfollow yourself");
    }
  }

  @GetMapping("/user/{id}/followed")
  public ResponseEntity<List<UserDTO>> getFollowed(@PathVariable Long id) {
    final List<User> followed = userService.findFollowed(id);
    return ResponseEntity.ok(objectMapper.map(followed, UserDTO.class));
  }

  @GetMapping("/user/{id}/followers")
  public ResponseEntity<List<UserDTO>> getFollowers(@PathVariable Long id) {
    final List<User> followers = userService.findFollowers(id);
    return ResponseEntity.ok(objectMapper.map(followers, UserDTO.class));
  }
}
