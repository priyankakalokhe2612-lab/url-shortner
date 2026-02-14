package com.example.url_shortner.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Response model for metrics API
 */
public class MetricsResponse {
    
    @JsonProperty("top_domains")
    private List<DomainMetric> topDomains;
    
    public MetricsResponse() {
    }
    
    public MetricsResponse(List<DomainMetric> topDomains) {
        this.topDomains = topDomains;
    }
    
    public List<DomainMetric> getTopDomains() {
        return topDomains;
    }
    
    public void setTopDomains(List<DomainMetric> topDomains) {
        this.topDomains = topDomains;
    }
}
