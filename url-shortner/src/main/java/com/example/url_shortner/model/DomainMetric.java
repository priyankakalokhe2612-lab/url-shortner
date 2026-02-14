package com.example.url_shortner.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Domain metric model for metrics API
 */
public class DomainMetric {
    
    @JsonProperty("domain")
    private String domain;
    
    @JsonProperty("count")
    private int count;
    
    public DomainMetric() {
    }
    
    public DomainMetric(String domain, int count) {
        this.domain = domain;
        this.count = count;
    }
    
    public String getDomain() {
        return domain;
    }
    
    public void setDomain(String domain) {
        this.domain = domain;
    }
    
    public int getCount() {
        return count;
    }
    
    public void setCount(int count) {
        this.count = count;
    }
}
