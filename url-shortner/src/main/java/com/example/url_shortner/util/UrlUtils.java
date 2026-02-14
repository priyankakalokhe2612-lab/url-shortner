package com.example.url_shortner.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Utility class for URL operations
 */
public class UrlUtils {
    
    private static final String DEFAULT_SCHEME = "https://";
    
    /**
     * Validates and normalizes a URL
     * @param urlString the URL string to validate
     * @return normalized URL
     * @throws InvalidUrlException if URL is invalid
     */
    public static String validateAndNormalizeUrl(String urlString) {
        if (urlString == null || urlString.trim().isEmpty()) {
            throw new com.example.url_shortner.exception.InvalidUrlException("URL cannot be empty");
        }
        
        String normalizedUrl = urlString.trim();
        
        // Add scheme if missing
        if (!normalizedUrl.startsWith("http://") && !normalizedUrl.startsWith("https://")) {
            normalizedUrl = DEFAULT_SCHEME + normalizedUrl;
        }
        
        // Validate URL format
        try {
            URL url = new URL(normalizedUrl);
            String scheme = url.getProtocol();
            if (!"http".equals(scheme) && !"https".equals(scheme)) {
                throw new com.example.url_shortner.exception.InvalidUrlException("URL must use http or https scheme");
            }
            if (url.getHost() == null || url.getHost().isEmpty()) {
                throw new com.example.url_shortner.exception.InvalidUrlException("URL must have a valid host");
            }
        } catch (MalformedURLException e) {
            throw new com.example.url_shortner.exception.InvalidUrlException("Invalid URL format: " + e.getMessage());
        }
        
        return normalizedUrl;
    }
    
    /**
     * Extracts the domain from a URL
     * @param urlString the URL string
     * @return the domain name
     */
    public static String extractDomain(String urlString) {
        try {
            URL url = new URL(urlString);
            String host = url.getHost();
            // Remove port if present
            int portIndex = host.indexOf(':');
            if (portIndex != -1) {
                host = host.substring(0, portIndex);
            }
            return host;
        } catch (MalformedURLException e) {
            return "unknown";
        }
    }
    
    /**
     * Generates a short code from a URL using MD5 hash
     * @param url the original URL
     * @return short code (8 characters)
     */
    public static String generateShortCode(String url) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(url.getBytes(StandardCharsets.UTF_8));
            
            // Encode to base64 and take first 8 characters
            String encoded = Base64.getUrlEncoder().withoutPadding().encodeToString(hashBytes);
            
            if (encoded.length() > 8) {
                return encoded.substring(0, 8);
            }
            return encoded;
        } catch (NoSuchAlgorithmException e) {
            // Fallback to simple hash code
            int hashCode = url.hashCode();
            return Integer.toHexString(Math.abs(hashCode)).substring(0, Math.min(8, Integer.toHexString(Math.abs(hashCode)).length()));
        }
    }
}
