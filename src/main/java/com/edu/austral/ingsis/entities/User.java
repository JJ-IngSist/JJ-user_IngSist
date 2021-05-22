package com.edu.austral.ingsis.entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id")
  private Long id;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "username", nullable = false)
  private String username;

  @Column(name = "email", nullable = false)
  private String email;

  @Column(name = "password", nullable = false)
  private String password;

  @Column(name = "description", length = 200)
  private String description;

  @ManyToMany
  private List<User> followed = new ArrayList<>();

  @ElementCollection
  @CollectionTable(name = "liked_posts", joinColumns = @JoinColumn(name = "id"))
  @Column(name = "liked")
  private List<Long> likedPostIds = new ArrayList<>();

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public String getUsername() {
    return username;
  }

  public String getEmail() {
    return email;
  }

  public String getPassword() {
    return password;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public List<User> getFollowed() {
    return followed;
  }

  public void setFollowed(List<User> followed) {
    this.followed = followed;
  }

  public List<Long> getLikedPostIds() {
    return likedPostIds;
  }

  public void setLikedPostIds(List<Long> likedPostIds) {
    this.likedPostIds = likedPostIds;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }
}
