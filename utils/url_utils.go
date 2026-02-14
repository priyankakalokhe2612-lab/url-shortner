package utils

import (
	"crypto/md5"
	"encoding/base64"
	"fmt"
	"net/url"
	"strings"
)

// ValidateURL validates if a string is a valid URL
func ValidateURL(urlString string) error {
	if urlString == "" {
		return fmt.Errorf("URL cannot be empty")
	}

	// Add scheme if missing
	if !strings.HasPrefix(urlString, "http://") && !strings.HasPrefix(urlString, "https://") {
		urlString = "https://" + urlString
	}

	parsedURL, err := url.Parse(urlString)
	if err != nil {
		return fmt.Errorf("invalid URL format: %w", err)
	}

	if parsedURL.Scheme != "http" && parsedURL.Scheme != "https" {
		return fmt.Errorf("URL must use http or https scheme")
	}

	if parsedURL.Host == "" {
		return fmt.Errorf("URL must have a valid host")
	}

	return nil
}

// NormalizeURL normalizes a URL by adding scheme if missing
func NormalizeURL(urlString string) string {
	if strings.HasPrefix(urlString, "http://") || strings.HasPrefix(urlString, "https://") {
		return urlString
	}
	return "https://" + urlString
}

// ExtractDomain extracts the domain from a URL
func ExtractDomain(urlString string) (string, error) {
	normalizedURL := NormalizeURL(urlString)
	parsedURL, err := url.Parse(normalizedURL)
	if err != nil {
		return "", fmt.Errorf("failed to parse URL: %w", err)
	}

	host := parsedURL.Host
	// Remove port if present
	if idx := strings.Index(host, ":"); idx != -1 {
		host = host[:idx]
	}

	return host, nil
}

// GenerateShortURL generates a short URL from the original URL using MD5 hash
func GenerateShortURL(originalURL string) string {
	// Use MD5 hash of the URL
	hash := md5.Sum([]byte(originalURL))
	
	// Encode to base64 and take first 8 characters
	encoded := base64.URLEncoding.EncodeToString(hash[:])
	
	// Remove padding and take first 8 characters
	shortCode := strings.TrimRight(encoded, "=")
	if len(shortCode) > 8 {
		shortCode = shortCode[:8]
	}
	
	return shortCode
}
