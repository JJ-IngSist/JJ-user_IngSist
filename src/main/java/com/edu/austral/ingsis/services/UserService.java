package com.edu.austral.ingsis.services;

import com.edu.austral.ingsis.entities.User;
import com.edu.austral.ingsis.repositories.UserRepository;
import com.edu.austral.ingsis.utils.AlreadyExistsException;
import com.edu.austral.ingsis.utils.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public User register(User user) {
    return userRepository.save(user);
  }

  public User save(User user) {
    userRepository
            .findByEmail(user.getEmail())
            .ifPresent(found -> { throw new AlreadyExistsException(); });
    return userRepository.save(user);
  }

  public User getById(Long id) {
    return userRepository.findById(id).orElseThrow(NotFoundException::new);
  }

  public User findLogged() {
//    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//    String email = ((org.springframework.security.core.userdetails.User) authentication.getPrincipal()).getUsername();
//    return userRepository.findByEmail(email).orElseThrow(NotFoundException::new);
    return getById(1L);
  }

  public User follow(Long id) {
    User logged = findLogged();
    User followed = getById(id);
    if (logged.getId().equals(followed.getId())) throw new RuntimeException();
    if (contains(logged.getFollowed(), followed)) throw new AlreadyExistsException();
    logged.getFollowed().add(followed);
    return userRepository.save(logged);
  }

  public User unfollow(Long id) {
    User logged = findLogged();
    User unfollowed = getById(id);
    if (logged.getId().equals(unfollowed.getId())) throw new RuntimeException();
    if (!contains(logged.getFollowed(), unfollowed)) throw new NotFoundException();
    logged.setFollowed(logged.getFollowed().stream().filter(f -> !f.getId().equals(unfollowed.getId())).collect(Collectors.toList()));
    return userRepository.save(logged);
  }

  public User likePost(Long id) {
    User user = findLogged();
    if(user.getLikedPostIds().contains(id)) throw new AlreadyExistsException();
    user.getLikedPostIds().add(id);
    return userRepository.save(user);
  }

  public User dislikePost(Long id) {
    User user = findLogged();
    if(!user.getLikedPostIds().contains(id)) throw new NotFoundException();
    user.setLikedPostIds(user.getLikedPostIds().stream().filter(p -> !p.equals(id)).collect(Collectors.toList()));
    return userRepository.save(user);
  }

  private boolean contains(List<User> followed, User user) {
    for (User u : followed) {
      if (u.getId().equals(user.getId())) return true;
    }
    return false;
  }

  public List<User> findFollowed(Long id) {
    return getById(id).getFollowed();
  }

  public List<User> findFollowedByLogged() {
    return findFollowed(findLogged().getId());
  }

  public List<User> findFollowers(Long id) {
    return userRepository.findFollowers(id);
  }

  public List<User> getUsersWhoLikedPost(Long id) {
    return userRepository.getUsersWhoLikedPost(id);
  }

  public boolean checkIfLoggedLikedPost(Long id) {
    User logged = findLogged();
    return logged.getLikedPostIds().contains(id);
  }

  public List<User> findByRegex(String value) {
    return userRepository.findByRegex(value);
  }

  public User update(Long id, User user) {
    return userRepository
            .findById(id)
            .map(old -> {
              old.setName(user.getName());
              old.setLastname(user.getLastname());
              if(!old.getEmail().equalsIgnoreCase(user.getEmail())) {
                old.setEmail(user.getEmail());
                return save(old);
              } else return userRepository.save(old);
            })
            .orElseThrow(NotFoundException::new);
  }

  public boolean checkUsername(String username) {
    return userRepository.findByUsername(username).isPresent();
  }

  public User findByUsername(String username) {
    return userRepository.findByUsername(username).orElseThrow(NotFoundException::new);
  }

  public void delete(Long id) {
    userRepository.delete(getById(id));
  }
}
