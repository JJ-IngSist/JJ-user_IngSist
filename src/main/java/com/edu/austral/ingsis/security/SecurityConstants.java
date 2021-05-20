package com.edu.austral.ingsis.security;

public class SecurityConstants {

    public static final String SECRET = "kRaaUVEdNpUz7CiVUB0uj8dU";
    public static final long EXPIRATION_TIME = 864_000_000; // 10 days
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String REGISTER_URL = "/register";
    public static final String LOGIN_URL = "/login";
    public static final String POST_URL = "/post/**";
    public static final String USER_URL = "/user/**";
}
