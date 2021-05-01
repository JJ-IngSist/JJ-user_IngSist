package com.edu.austral.ingsis.repositories;

import com.edu.austral.ingsis.entities.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {

  Optional<User> findByUsername(String username);

  Optional<User> findByEmail(String email);
}
