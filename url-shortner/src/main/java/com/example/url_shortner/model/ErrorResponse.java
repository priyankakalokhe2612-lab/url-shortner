package com.example.url_shortner.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Error response model
 */
public class ErrorResponse {
    
    @JsonProperty("error")
    private String error;
    
    public ErrorResponse() {
    }
    
    public ErrorResponse(String error) {
        this.error = error;
    }
    
    public String getError() {
        return error;
    }
    
    public void setError(String error) {
        this.error = error;
    }
}
