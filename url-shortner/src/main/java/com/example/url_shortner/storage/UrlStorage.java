package com.example.url_shortner.storage;

import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * In-memory storage for URL mappings
 * Thread-safe implementation using ConcurrentHashMap and ReadWriteLock
 */
@Component
public class UrlStorage {
    
    // Maps short code to original URL
    private final Map<String, String> shortToOriginal = new ConcurrentHashMap<>();
    
    // Maps original URL to short code (for idempotency)
    private final Map<String, String> originalToShort = new ConcurrentHashMap<>();
    
    // Maps domain to count
    private final Map<String, Integer> domainCounts = new ConcurrentHashMap<>();
    
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    
    /**
     * Retrieves the original URL for a given short code
     * @param shortCode the short code
     * @return the original URL, or null if not found
     */
    public String getOriginalUrl(String shortCode) {
        lock.readLock().lock();
        try {
            return shortToOriginal.get(shortCode);
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Retrieves the short code for a given original URL
     * @param originalUrl the original URL
     * @return the short code, or null if not found
     */
    public String getShortCode(String originalUrl) {
        lock.readLock().lock();
        try {
            return originalToShort.get(originalUrl);
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Stores the mapping between short code and original URL
     * @param shortCode the short code
     * @param originalUrl the original URL
     */
    public void storeUrl(String shortCode, String originalUrl) {
        lock.writeLock().lock();
        try {
            shortToOriginal.put(shortCode, originalUrl);
            originalToShort.put(originalUrl, shortCode);
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * Increments the count for a domain
     * @param domain the domain name
     */
    public void incrementDomainCount(String domain) {
        lock.writeLock().lock();
        try {
            domainCounts.merge(domain, 1, Integer::sum);
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * Gets all domain counts
     * @return a copy of the domain counts map
     */
    public Map<String, Integer> getDomainCounts() {
        lock.readLock().lock();
        try {
            return new ConcurrentHashMap<>(domainCounts);
        } finally {
            lock.readLock().unlock();
        }
    }
}
