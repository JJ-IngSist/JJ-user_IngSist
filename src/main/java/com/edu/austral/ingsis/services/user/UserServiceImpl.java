package com.edu.austral.ingsis.services.user;

import com.edu.austral.ingsis.entities.User;
import com.edu.austral.ingsis.exception.InvalidOldPasswordException;
import com.edu.austral.ingsis.repositories.UserRepository;
import com.edu.austral.ingsis.exception.AlreadyExistsEmailException;
import com.edu.austral.ingsis.exception.AlreadyExistsUsernameException;
import com.edu.austral.ingsis.exception.NotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public User register(User user) {
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    return userRepository.save(user);
  }

  @Override
  public User save(User user) {
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    if (!existsUsername(user) && !existsEmail(user)) {
      return userRepository.save(user);
    }
    throw new AlreadyExistsEmailException();
  }

  private boolean existsUsername(User user) {
    return userRepository
            .findByUsername(user.getUsername()).isPresent();
  }

  private boolean existsEmail(User user) {
    return userRepository
            .findByEmail(user.getEmail()).isPresent();
  }

  @Override
  public User getById(Long id) {
    return userRepository.findById(id).orElseThrow(NotFoundException::new);
  }

  @Override
  public User findLogged() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return userRepository.findByUsername(authentication.getName()).orElseThrow(NotFoundException::new);
  }

  @Override
  public User follow(Long id) {
    User logged = findLogged();
    User followed = getById(id);
    if (logged.getId().equals(followed.getId())) throw new RuntimeException();
    if (contains(logged.getFollowed(), followed)) throw new AlreadyExistsEmailException();
    logged.getFollowed().add(followed);
    return userRepository.save(logged);
  }

  @Override
  public User unfollow(Long id) {
    User logged = findLogged();
    User unfollowed = getById(id);
    if (logged.getId().equals(unfollowed.getId())) throw new RuntimeException();
    if (!contains(logged.getFollowed(), unfollowed)) throw new NotFoundException();
    logged.setFollowed(logged.getFollowed().stream().filter(f -> !f.getId().equals(unfollowed.getId())).collect(Collectors.toList()));
    return userRepository.save(logged);
  }

  @Override
  public User likePost(Long id) {
    User user = findLogged();
    if(user.getLikedPostIds().contains(id)) throw new AlreadyExistsEmailException();
    user.getLikedPostIds().add(id);
    return userRepository.save(user);
  }

  @Override
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

  @Override
  public List<User> findFollowed(Long id) {
    return getById(id).getFollowed();
  }

  @Override
  public List<User> findFollowedByLogged() {
    return findFollowed(findLogged().getId());
  }

  @Override
  public List<User> findFollowers(Long id) {
    return userRepository.findFollowers(id);
  }

  @Override
  public List<User> getUsersWhoLikedPost(Long id) {
    return userRepository.getUsersWhoLikedPost(id);
  }

  @Override
  public boolean checkIfLoggedLikedPost(Long id) {
    User logged = findLogged();
    return logged.getLikedPostIds().contains(id);
  }

  @Override
  public List<User> findByRegex(String value) {
    return userRepository.findByRegex(value);
  }

  @Override
  public User update(Long id, User user) {
    return userRepository
            .findById(id)
            .map(old -> {
              old.setName(user.getName());
              old.setDescription(user.getDescription());
              if(!old.getEmail().equalsIgnoreCase(user.getEmail())) {
                if(!existsEmail(user)) old.setEmail(user.getEmail());
                else throw new AlreadyExistsEmailException();
              }
              if(!old.getUsername().equalsIgnoreCase(user.getUsername())) {
                if(!existsUsername(user)) old.setUsername(user.getUsername());
                else throw new AlreadyExistsUsernameException();
              }
              return userRepository.save(old);
            })
            .orElseThrow(NotFoundException::new);
  }

  @Override
  public boolean checkUsername(String username) {
    return userRepository.findByUsername(username).isPresent();
  }

  @Override
  public User findByUsername(String username) {
    return userRepository.findByUsername(username).orElseThrow(NotFoundException::new);
  }

  @Override
  public void delete(Long id) {
    userRepository.delete(getById(id));
  }

  @Override
  public User updatePassword(String oldPassword, String password, User user) {
    if (checkValidPassword(oldPassword, user))
      user.setPassword(passwordEncoder.encode(password));
    else
      throw new InvalidOldPasswordException();
    return userRepository.save(user);
  }

  @Override
  public boolean checkPassword(String password, User user) {
    return checkValidPassword(password, user);
  }

  @Override
  public boolean checkIfFollowing(Long id) {
    for (User u : findFollowedByLogged()) {
      if (u.getId().equals(id)) return true;
    }
    return false;
  }

  private boolean checkValidPassword(String old, User user) {
    return passwordEncoder.matches(old, user.getPassword());
  }
}
