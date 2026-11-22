package com.flairs.libraryapi.auth;

// expiresIn is in seconds, like the OAuth 2 token response
public record TokenResponse(String accessToken, String tokenType, long expiresIn) {
}
