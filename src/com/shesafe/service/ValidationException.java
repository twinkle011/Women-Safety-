package com.shesafe.service;

/**
 * Custom exception representing business validation errors
 * (e.g., malformed email, short password, empty required fields).
 */
public class ValidationException extends Exception {

    public ValidationException(String message) {
        super(message);
    }
}
