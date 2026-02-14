package com.example.url_shortner.exception;

/**
 * Exception thrown when a short URL is not found
 */
public class UrlNotFoundException extends RuntimeException {
    
    public UrlNotFoundException(String message) {
        super(message);
    }
}
