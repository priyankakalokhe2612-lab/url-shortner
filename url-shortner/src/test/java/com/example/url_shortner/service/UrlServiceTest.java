package com.example.url_shortner.service;

import com.example.url_shortner.exception.InvalidUrlException;
import com.example.url_shortner.exception.UrlNotFoundException;
import com.example.url_shortner.model.DomainMetric;
import com.example.url_shortner.storage.UrlStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {
    
    @Mock
    private UrlStorage urlStorage;
    
    @InjectMocks
    private UrlService urlService;
    
    private static final String BASE_URL = "http://localhost:8080";
    
    @BeforeEach
    void setUp() {
        urlService = new UrlService(urlStorage, BASE_URL);
    }
    
    @Test
    void testShortenUrl_NewUrl() {
        String originalUrl = "https://example.com";
        String shortCode = "abc123";
        
        when(urlStorage.getShortCode(anyString())).thenReturn(null);
        when(urlStorage.getOriginalUrl(shortCode)).thenReturn(null);
        
        String result = urlService.shortenUrl(originalUrl);
        
        assertNotNull(result);
        assertTrue(result.startsWith(BASE_URL));
        verify(urlStorage).storeUrl(anyString(), anyString());
        verify(urlStorage).incrementDomainCount(anyString());
    }
    
    @Test
    void testShortenUrl_Idempotent() {
        String originalUrl = "https://example.com";
        String shortCode = "abc123";
        String normalizedUrl = "https://example.com";
        
        when(urlStorage.getShortCode(normalizedUrl)).thenReturn(shortCode);
        
        String result = urlService.shortenUrl(originalUrl);
        
        assertEquals(BASE_URL + "/" + shortCode, result);
        verify(urlStorage, never()).storeUrl(anyString(), anyString());
        verify(urlStorage, never()).incrementDomainCount(anyString());
    }
    
    @Test
    void testShortenUrl_InvalidUrl() {
        String invalidUrl = "not a valid url";
        
        assertThrows(InvalidUrlException.class, () -> {
            urlService.shortenUrl(invalidUrl);
        });
        
        verify(urlStorage, never()).storeUrl(anyString(), anyString());
    }
    
    @Test
    void testShortenUrl_EmptyUrl() {
        assertThrows(InvalidUrlException.class, () -> {
            urlService.shortenUrl("");
        });
    }
    
    @Test
    void testShortenUrl_NullUrl() {
        assertThrows(InvalidUrlException.class, () -> {
            urlService.shortenUrl(null);
        });
    }
    
    @Test
    void testShortenUrl_WithoutScheme() {
        String url = "example.com";
        
        when(urlStorage.getShortCode(anyString())).thenReturn(null);
        when(urlStorage.getOriginalUrl(anyString())).thenReturn(null);
        
        String result = urlService.shortenUrl(url);
        
        assertNotNull(result);
        assertTrue(result.startsWith(BASE_URL));
    }
    
    @Test
    void testGetOriginalUrl_Success() {
        String shortCode = "abc123";
        String originalUrl = "https://example.com";
        
        when(urlStorage.getOriginalUrl(shortCode)).thenReturn(originalUrl);
        
        String result = urlService.getOriginalUrl(shortCode);
        
        assertEquals(originalUrl, result);
    }
    
    @Test
    void testGetOriginalUrl_NotFound() {
        String shortCode = "nonexistent";
        
        when(urlStorage.getOriginalUrl(shortCode)).thenReturn(null);
        
        assertThrows(UrlNotFoundException.class, () -> {
            urlService.getOriginalUrl(shortCode);
        });
    }
    
    @Test
    void testGetTopDomains_Empty() {
        when(urlStorage.getDomainCounts()).thenReturn(java.util.Map.of());
        
        List<DomainMetric> result = urlService.getTopDomains(3);
        
        assertTrue(result.isEmpty());
    }
    
    @Test
    void testGetTopDomains_SingleDomain() {
        when(urlStorage.getDomainCounts()).thenReturn(
            java.util.Map.of("example.com", 5)
        );
        
        List<DomainMetric> result = urlService.getTopDomains(3);
        
        assertEquals(1, result.size());
        assertEquals("example.com", result.get(0).getDomain());
        assertEquals(5, result.get(0).getCount());
    }
    
    @Test
    void testGetTopDomains_MultipleDomains() {
        when(urlStorage.getDomainCounts()).thenReturn(
            java.util.Map.of(
                "youtube.com", 6,
                "stackoverflow.com", 4,
                "wikipedia.org", 2,
                "udemy.com", 8
            )
        );
        
        List<DomainMetric> result = urlService.getTopDomains(3);
        
        assertEquals(3, result.size());
        assertEquals("udemy.com", result.get(0).getDomain());
        assertEquals(8, result.get(0).getCount());
        assertEquals("youtube.com", result.get(1).getDomain());
        assertEquals(6, result.get(1).getCount());
        assertEquals("stackoverflow.com", result.get(2).getDomain());
        assertEquals(4, result.get(2).getCount());
    }
    
    @Test
    void testGetTopDomains_Limit() {
        when(urlStorage.getDomainCounts()).thenReturn(
            java.util.Map.of(
                "domain1.com", 10,
                "domain2.com", 8,
                "domain3.com", 6,
                "domain4.com", 4
            )
        );
        
        List<DomainMetric> result = urlService.getTopDomains(2);
        
        assertEquals(2, result.size());
    }
}
