package com.example.url_shortner.service;

import com.example.url_shortner.exception.InvalidUrlException;
import com.example.url_shortner.exception.UrlNotFoundException;
import com.example.url_shortner.storage.UrlStorage;
import com.example.url_shortner.util.UrlUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for URL shortening operations
 */
@Service
public class UrlService {
    
    private final UrlStorage urlStorage;
    private final String baseUrl;
    
    public UrlService(UrlStorage urlStorage, 
                     @Value("${app.base-url:http://localhost:8080}") String baseUrl) {
        this.urlStorage = urlStorage;
        this.baseUrl = baseUrl;
    }
    
    /**
     * Shortens a URL and returns the short URL
     * If the URL was already shortened, returns the existing short URL (idempotent)
     * @param originalUrl the original URL to shorten
     * @return the short URL
     * @throws InvalidUrlException if the URL is invalid
     */
    public String shortenUrl(String originalUrl) {
        // Validate and normalize the URL
        String normalizedUrl = UrlUtils.validateAndNormalizeUrl(originalUrl);
        
        // Check if URL was already shortened (idempotency)
        String existingShortCode = urlStorage.getShortCode(normalizedUrl);
        if (existingShortCode != null) {
            return baseUrl + "/" + existingShortCode;
        }
        
        // Generate short code
        String shortCode = UrlUtils.generateShortCode(normalizedUrl);
        
        // Handle potential collisions (very rare but possible)
        int counter = 0;
        String originalShortCode = shortCode;
        while (urlStorage.getOriginalUrl(shortCode) != null) {
            counter++;
            shortCode = originalShortCode + counter;
        }
        
        // Extract domain for metrics
        String domain = UrlUtils.extractDomain(normalizedUrl);
        
        // Store the mapping
        urlStorage.storeUrl(shortCode, normalizedUrl);
        urlStorage.incrementDomainCount(domain);
        
        // Return the full short URL
        return baseUrl + "/" + shortCode;
    }
    
    /**
     * Retrieves the original URL from a short code
     * @param shortCode the short code
     * @return the original URL
     * @throws UrlNotFoundException if the short code is not found
     */
    public String getOriginalUrl(String shortCode) {
        String originalUrl = urlStorage.getOriginalUrl(shortCode);
        if (originalUrl == null) {
            throw new UrlNotFoundException("Short URL not found");
        }
        return originalUrl;
    }
    
    /**
     * Gets the top N domains by count
     * @param n the number of top domains to return
     * @return list of domain metrics sorted by count (descending)
     */
    public List<com.example.url_shortner.model.DomainMetric> getTopDomains(int n) {
        Map<String, Integer> domainCounts = urlStorage.getDomainCounts();
        
        return domainCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(n)
                .map(entry -> new com.example.url_shortner.model.DomainMetric(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }
}
