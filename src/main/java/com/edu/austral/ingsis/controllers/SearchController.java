package com.edu.austral.ingsis.controllers;

import com.edu.austral.ingsis.dtos.FilterUserPostDTO;
import com.edu.austral.ingsis.dtos.PostDTO;
import com.edu.austral.ingsis.dtos.UserDTO;
import com.edu.austral.ingsis.entities.User;
import com.edu.austral.ingsis.services.UserService;
import com.edu.austral.ingsis.utils.ObjectMapper;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.edu.austral.ingsis.utils.ConnectMicroservices.connectToPostMicroservice;
import static com.edu.austral.ingsis.utils.ConnectMicroservices.getFromJson;

@RestController
@CrossOrigin(origins = "*", methods= {RequestMethod.GET,RequestMethod.POST,RequestMethod.DELETE,RequestMethod.PUT})
public class SearchController {

  private final UserService userService;
  private final ObjectMapper objectMapper;

  public SearchController(UserService userService, ObjectMapper objectMapper) {
    this.userService = userService;
    this.objectMapper = objectMapper;
  }

  @GetMapping("/search/{value}")
  public ResponseEntity<FilterUserPostDTO> getUserOrPost(@PathVariable String value) {
    final List<User> filteredU = userService.findByRegex(value);
    final List<UserDTO> users = objectMapper.map(filteredU, UserDTO.class);
    String json = connectToPostMicroservice("/search/" + value, HttpMethod.GET);
    final List<PostDTO> posts = parsePosts(json);
    return ResponseEntity.ok(new FilterUserPostDTO(users, posts));
  }

  private List<PostDTO> parsePosts(String json) {
    String[] jsons = json.split("},\\{");
    List<PostDTO> posts = new ArrayList<>();
    for (String s: clean(jsons)) {
      PostDTO p = setPostDetails(s);
      posts.add(p);
    }
    return posts;
  }

  private String[] clean(String[] jsons) {
    if (jsons.length <= 1) return jsons;
    jsons[0] = jsons[0].substring(1) + "}";
    if (jsons.length > 2) {
      for (int i = 1; i < jsons.length-1; i++) {
        jsons[i] = "{" + jsons[i] + "}";
      }
    }
    jsons[jsons.length-1] = "{" + jsons[jsons.length-1].substring(0, jsons[jsons.length-1].length()-1);
    return jsons;
  }

  public static PostDTO setPostDetails(String json) {
    PostDTO postDTO = new PostDTO();
    postDTO.setId(Long.valueOf(getFromJson(json, "id")));
    postDTO.setText(getFromJson(json, "text"));
    postDTO.setThreadId(Long.valueOf(getFromJson(json, "threadId")));
    postDTO.setDate(LocalDate.parse(getFromJson(json, "date")));
    postDTO.setLikes(Integer.parseInt(getFromJson(json, "likes")));
    postDTO.setUserId(Long.valueOf(getFromJson(json, "user")));
    postDTO.setName(getFromJson(json, "name"));
    postDTO.setLastname(getFromJson(json, "lastname"));
    postDTO.setUsername(getFromJson(json, "username"));
    postDTO.setEmail(getFromJson(json, "email"));
    return postDTO;
  }
}
