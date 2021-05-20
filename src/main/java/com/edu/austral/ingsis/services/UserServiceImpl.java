package com.edu.austral.ingsis.services;

import com.edu.austral.ingsis.entities.User;
import com.edu.austral.ingsis.repositories.UserRepository;
import com.edu.austral.ingsis.utils.AlreadyExistsEmailException;
import com.edu.austral.ingsis.utils.AlreadyExistsUsernameException;
import com.edu.austral.ingsis.utils.NotFoundException;
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
    userRepository
            .findByEmail(user.getEmail())
            .ifPresent(found -> { throw new AlreadyExistsEmailException(); });
    userRepository
            .findByUsername(user.getUsername())
            .ifPresent(found -> { throw new AlreadyExistsUsernameException(); });
    return userRepository.save(user);
  }

  @Override
  public User getById(Long id) {
    return userRepository.findById(id).orElseThrow(NotFoundException::new);
  }

  @Override
  public User findLogged() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return userRepository.findByEmail(authentication.getName()).orElseThrow(NotFoundException::new);
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
              if(!old.getEmail().equalsIgnoreCase(user.getEmail()) || !old.getUsername().equalsIgnoreCase(user.getUsername())) {
                old.setEmail(user.getEmail());
                old.setUsername(user.getUsername());
                return save(old);
              } else return userRepository.save(old);
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
}
