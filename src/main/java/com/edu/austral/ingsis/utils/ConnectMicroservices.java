package com.edu.austral.ingsis.utils;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

public class ConnectMicroservices {

  @Value("${post_url}")
  private static String postUrl;

  private final static RestTemplate restTemplate = new RestTemplate();

  public static HttpEntity<Object> getRequestEntity(String token) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    if (!token.isEmpty())
      headers.set("Authorization", token);
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

  public static String connectToPostMicroservice(String url, HttpMethod method, String token) {
    final ResponseEntity<String> responseEntity = restTemplate.exchange(postUrl + url,
            method,
            getRequestEntity(token),
            String.class);
    return responseEntity.getBody();
  }
}
