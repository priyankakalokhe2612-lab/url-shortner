package com.example.url_shortner.exception;

/**
 * Exception thrown when a URL is invalid
 */
public class InvalidUrlException extends RuntimeException {
    
    public InvalidUrlException(String message) {
        super(message);
    }
}
