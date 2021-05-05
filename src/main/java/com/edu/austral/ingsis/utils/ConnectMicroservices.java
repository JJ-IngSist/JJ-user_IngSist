package com.edu.austral.ingsis.utils;

import com.edu.austral.ingsis.dtos.PostDTO;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

public class ConnectMicroservices {

  private final static RestTemplate restTemplate = new RestTemplate();

  public static HttpEntity<Object> getRequestEntity() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return new HttpEntity<>(headers);
  }

  public static String getFromJson(String user, String property) {
    try {
      JSONObject fieldsJson = new JSONObject(user);
      return fieldsJson.getString(property);
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return "";
  }

  public static String connectToPostMicroservice(String url, HttpMethod method) {
    final ResponseEntity<String> responseEntity = restTemplate.exchange("http://localhost:8081" + url,
            method,
            getRequestEntity(),
            String.class);
    return responseEntity.getBody();
  }
}
