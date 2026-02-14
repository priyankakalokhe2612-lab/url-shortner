package com.example.url_shortner.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request model for URL shortening API
 */
public class ShortenRequest {
    
    @JsonProperty("url")
    private String url;
    
    public ShortenRequest() {
    }
    
    public ShortenRequest(String url) {
        this.url = url;
    }
    
    public String getUrl() {
        return url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
}
