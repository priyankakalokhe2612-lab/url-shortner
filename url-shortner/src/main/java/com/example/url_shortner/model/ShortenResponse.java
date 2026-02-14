package com.example.url_shortner.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response model for URL shortening API
 */
public class ShortenResponse {
    
    @JsonProperty("short_url")
    private String shortUrl;
    
    @JsonProperty("original_url")
    private String originalUrl;
    
    public ShortenResponse() {
    }
    
    public ShortenResponse(String shortUrl, String originalUrl) {
        this.shortUrl = shortUrl;
        this.originalUrl = originalUrl;
    }
    
    public String getShortUrl() {
        return shortUrl;
    }
    
    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }
    
    public String getOriginalUrl() {
        return originalUrl;
    }
    
    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }
}
