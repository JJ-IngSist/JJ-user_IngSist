package com.edu.austral.ingsis.controllers;

import com.edu.austral.ingsis.dtos.UserDTO;
import com.edu.austral.ingsis.entities.User;
import com.edu.austral.ingsis.services.user.UserService;
import com.edu.austral.ingsis.exception.AlreadyExistsEmailException;
import com.edu.austral.ingsis.exception.NotFoundException;
import com.edu.austral.ingsis.utils.ObjectMapper;
import com.edu.austral.ingsis.utils.ObjectMapperImpl;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static com.edu.austral.ingsis.utils.ConnectMicroservices.connectToMessageMicroservice;

@RestController
public class UserFollowController {

  private final ObjectMapper objectMapper;
  private final UserService userService;

  public UserFollowController(UserService userService) {
    this.userService = userService;
    this.objectMapper = new ObjectMapperImpl();
  }

  @PostMapping("/user/{id}/follow")
  public ResponseEntity<UserDTO> followUser(@PathVariable Long id,
                                            @RequestHeader (name="Authorization") String token) {
    try {
      final User user = userService.follow(id);
      connectToMessageMicroservice("/conversation/" + id + "/" + user.getId(), HttpMethod.POST, token);
      return ResponseEntity.ok(objectMapper.map(user, UserDTO.class));
    } catch (AlreadyExistsEmailException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You already follow this user");
    } catch (RuntimeException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You can't follow yourself");
    }
  }

  @PostMapping("/user/{id}/unfollow")
  public ResponseEntity<UserDTO> unfollowUser(@PathVariable Long id,
                                              @RequestHeader (name="Authorization") String token) {
    try {
      final User user = userService.unfollow(id);
      connectToMessageMicroservice("/conversation/" + id + "/" + user.getId(), HttpMethod.DELETE, token);
      return ResponseEntity.ok(objectMapper.map(user, UserDTO.class));
    } catch (NotFoundException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You don't follow this user");
    } catch (RuntimeException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You can't unfollow yourself");
    }
  }

  @GetMapping("/user/{id}/is-following")
  public ResponseEntity<Boolean> getIsFollowing(@PathVariable Long id) {
    return ResponseEntity.ok(userService.checkIfFollowing(id));
  }

  @GetMapping("/user/{id}/amount-followed")
  public ResponseEntity<Integer> getAmountFollowed(@PathVariable Long id) {
    return ResponseEntity.ok(userService.findFollowed(id).size());
  }

  @GetMapping("/user/{id}/amount-following")
  public ResponseEntity<Integer> getAmountFollowing(@PathVariable Long id) {
    return ResponseEntity.ok(userService.findFollowers(id).size());
  }

  @GetMapping("/user/{id}/following")
  public ResponseEntity<List<UserDTO>> getFollowing(@PathVariable Long id) {
    final List<User> followed = userService.findFollowed(id);
    return ResponseEntity.ok(objectMapper.map(followed, UserDTO.class));
  }

  @GetMapping("/user/followed")
  public ResponseEntity<List<UserDTO>> getFollowedByLogged() {
    final List<User> followed = userService.findFollowedByLogged();
    return ResponseEntity.ok(objectMapper.map(followed, UserDTO.class));
  }

  @GetMapping("/user/{id}/followers")
  public ResponseEntity<List<UserDTO>> getFollowers(@PathVariable Long id) {
    final List<User> followers = userService.findFollowers(id);
    return ResponseEntity.ok(objectMapper.map(followers, UserDTO.class));
  }
}
