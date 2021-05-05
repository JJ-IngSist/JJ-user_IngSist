package com.edu.austral.ingsis.dtos;

import java.util.List;

public class FilterUserPostDTO {

  private List<UserDTO> users;
  private List<PostDTO> posts;

  public FilterUserPostDTO(List<UserDTO> users, List<PostDTO> posts) {
    this.users = users;
    this.posts = posts;
  }

  public List<UserDTO> getUsers() {
    return users;
  }

  public void setUsers(List<UserDTO> users) {
    this.users = users;
  }

  public List<PostDTO> getPosts() {
    return posts;
  }

  public void setPosts(List<PostDTO> posts) {
    this.posts = posts;
  }
}
