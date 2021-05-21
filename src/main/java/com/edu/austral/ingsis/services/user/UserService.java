package com.edu.austral.ingsis.services.user;

import com.edu.austral.ingsis.entities.User;

import java.util.List;

public interface UserService {

  User register(User user);

  User save(User user);

  User getById(Long id);

  User findLogged();

  User follow(Long id);

  User unfollow(Long id);

  User likePost(Long id);

  User dislikePost(Long id);

  List<User> findFollowed(Long id);

  List<User> findFollowedByLogged();

  List<User> findFollowers(Long id);

  List<User> getUsersWhoLikedPost(Long id);

  boolean checkIfLoggedLikedPost(Long id);

  List<User> findByRegex(String value);

  User update(Long id, User user);

  boolean checkUsername(String username);

  User findByUsername(String username);

  void delete(Long id);

  User updatePassword(String oldPassword, String password, User user);

  boolean checkPassword(String password, User user);
}
