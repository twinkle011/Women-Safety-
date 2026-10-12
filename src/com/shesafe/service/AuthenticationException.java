package com.shesafe.service;

/**
 * Custom exception representing authentication failures
 * (e.g., incorrect credentials, unauthenticated access).
 */
public class AuthenticationException extends Exception {

    public AuthenticationException(String message) {
        super(message);
    }
}
