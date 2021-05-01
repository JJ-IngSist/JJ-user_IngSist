package com.edu.austral.ingsis.services;

import com.edu.austral.ingsis.entities.User;
import com.edu.austral.ingsis.repositories.UserRepository;
import com.edu.austral.ingsis.utils.AlreadyExistsException;
import com.edu.austral.ingsis.utils.NotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

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
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = ((org.springframework.security.core.userdetails.User) authentication.getPrincipal()).getUsername();
    return userRepository.findByEmail(email).orElseThrow(NotFoundException::new);
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
