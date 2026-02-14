package com.example.url_shortner.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UrlStorageTest {
    
    private UrlStorage urlStorage;
    
    @BeforeEach
    void setUp() {
        urlStorage = new UrlStorage();
    }
    
    @Test
    void testStoreAndGetOriginalUrl() {
        String shortCode = "abc123";
        String originalUrl = "https://example.com";
        
        urlStorage.storeUrl(shortCode, originalUrl);
        String retrieved = urlStorage.getOriginalUrl(shortCode);
        
        assertEquals(originalUrl, retrieved);
    }
    
    @Test
    void testStoreAndGetShortCode() {
        String shortCode = "abc123";
        String originalUrl = "https://example.com";
        
        urlStorage.storeUrl(shortCode, originalUrl);
        String retrieved = urlStorage.getShortCode(originalUrl);
        
        assertEquals(shortCode, retrieved);
    }
    
    @Test
    void testGetOriginalUrl_NotFound() {
        String shortCode = "nonexistent";
        String result = urlStorage.getOriginalUrl(shortCode);
        
        assertNull(result);
    }
    
    @Test
    void testGetShortCode_NotFound() {
        String originalUrl = "https://nonexistent.com";
        String result = urlStorage.getShortCode(originalUrl);
        
        assertNull(result);
    }
    
    @Test
    void testIncrementDomainCount() {
        String domain = "example.com";
        
        urlStorage.incrementDomainCount(domain);
        urlStorage.incrementDomainCount(domain);
        urlStorage.incrementDomainCount(domain);
        
        var counts = urlStorage.getDomainCounts();
        assertEquals(3, counts.get(domain));
    }
    
    @Test
    void testIncrementDomainCount_MultipleDomains() {
        urlStorage.incrementDomainCount("example.com");
        urlStorage.incrementDomainCount("google.com");
        urlStorage.incrementDomainCount("example.com");
        
        var counts = urlStorage.getDomainCounts();
        assertEquals(2, counts.get("example.com"));
        assertEquals(1, counts.get("google.com"));
    }
    
    @Test
    void testGetDomainCounts_Empty() {
        var counts = urlStorage.getDomainCounts();
        assertTrue(counts.isEmpty());
    }
    
    @Test
    void testGetDomainCounts_Copy() {
        urlStorage.incrementDomainCount("example.com");
        
        var counts1 = urlStorage.getDomainCounts();
        var counts2 = urlStorage.getDomainCounts();
        
        // Should be separate instances
        assertNotSame(counts1, counts2);
        assertEquals(counts1, counts2);
    }
    
    @Test
    void testStoreUrl_Overwrite() {
        String shortCode = "abc123";
        String originalUrl1 = "https://example.com";
        String originalUrl2 = "https://google.com";
        
        urlStorage.storeUrl(shortCode, originalUrl1);
        urlStorage.storeUrl(shortCode, originalUrl2);
        
        String retrieved = urlStorage.getOriginalUrl(shortCode);
        assertEquals(originalUrl2, retrieved);
    }
}
