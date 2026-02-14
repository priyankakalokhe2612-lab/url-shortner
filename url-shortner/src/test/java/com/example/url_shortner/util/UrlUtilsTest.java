package com.example.url_shortner.util;

import com.example.url_shortner.exception.InvalidUrlException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UrlUtilsTest {
    
    @Test
    void testValidateAndNormalizeUrl_WithHttpUrl() {
        String url = "http://example.com";
        String result = UrlUtils.validateAndNormalizeUrl(url);
        assertEquals("http://example.com", result);
    }
    
    @Test
    void testValidateAndNormalizeUrl_WithHttpsUrl() {
        String url = "https://example.com";
        String result = UrlUtils.validateAndNormalizeUrl(url);
        assertEquals("https://example.com", result);
    }
    
    @Test
    void testValidateAndNormalizeUrl_WithoutScheme() {
        String url = "example.com";
        String result = UrlUtils.validateAndNormalizeUrl(url);
        assertEquals("https://example.com", result);
    }
    
    @Test
    void testValidateAndNormalizeUrl_WithPath() {
        String url = "example.com/path/to/resource";
        String result = UrlUtils.validateAndNormalizeUrl(url);
        assertEquals("https://example.com/path/to/resource", result);
    }
    
    @Test
    void testValidateAndNormalizeUrl_WithQueryParams() {
        String url = "example.com?param=value";
        String result = UrlUtils.validateAndNormalizeUrl(url);
        assertEquals("https://example.com?param=value", result);
    }
    
    @Test
    void testValidateAndNormalizeUrl_EmptyUrl() {
        assertThrows(InvalidUrlException.class, () -> {
            UrlUtils.validateAndNormalizeUrl("");
        });
    }
    
    @Test
    void testValidateAndNormalizeUrl_NullUrl() {
        assertThrows(InvalidUrlException.class, () -> {
            UrlUtils.validateAndNormalizeUrl(null);
        });
    }
    
    @Test
    void testValidateAndNormalizeUrl_InvalidUrl() {
        assertThrows(InvalidUrlException.class, () -> {
            UrlUtils.validateAndNormalizeUrl("not a valid url");
        });
    }
    
    @Test
    void testValidateAndNormalizeUrl_InvalidScheme() {
        assertThrows(InvalidUrlException.class, () -> {
            UrlUtils.validateAndNormalizeUrl("ftp://example.com");
        });
    }
    
    @Test
    void testExtractDomain_WithHttp() {
        String url = "http://example.com/path";
        String domain = UrlUtils.extractDomain(url);
        assertEquals("example.com", domain);
    }
    
    @Test
    void testExtractDomain_WithHttps() {
        String url = "https://example.com/path";
        String domain = UrlUtils.extractDomain(url);
        assertEquals("example.com", domain);
    }
    
    @Test
    void testExtractDomain_WithPort() {
        String url = "https://example.com:8080/path";
        String domain = UrlUtils.extractDomain(url);
        assertEquals("example.com", domain);
    }
    
    @Test
    void testExtractDomain_WithSubdomain() {
        String url = "https://www.example.com/path";
        String domain = UrlUtils.extractDomain(url);
        assertEquals("www.example.com", domain);
    }
    
    @Test
    void testExtractDomain_InvalidUrl() {
        String url = "invalid-url";
        String domain = UrlUtils.extractDomain(url);
        assertEquals("unknown", domain);
    }
    
    @Test
    void testGenerateShortCode_ConsistentOutput() {
        String url = "https://example.com";
        String code1 = UrlUtils.generateShortCode(url);
        String code2 = UrlUtils.generateShortCode(url);
        assertEquals(code1, code2);
    }
    
    @Test
    void testGenerateShortCode_DifferentUrls() {
        String url1 = "https://example.com";
        String url2 = "https://google.com";
        String code1 = UrlUtils.generateShortCode(url1);
        String code2 = UrlUtils.generateShortCode(url2);
        assertNotEquals(code1, code2);
    }
    
    @Test
    void testGenerateShortCode_Length() {
        String url = "https://example.com";
        String code = UrlUtils.generateShortCode(url);
        assertTrue(code.length() <= 8);
        assertFalse(code.isEmpty());
    }
    
}
