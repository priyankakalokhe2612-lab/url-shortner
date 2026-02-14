package service

import (
	"fmt"
	"url-shortener/storage"
	"url-shortener/utils"
)

// URLService handles the business logic for URL shortening
type URLService struct {
	storage *storage.Storage
	baseURL string
}

// NewURLService creates a new instance of URLService
func NewURLService(storage *storage.Storage, baseURL string) *URLService {
	return &URLService{
		storage: storage,
		baseURL: baseURL,
	}
}

// ShortenURL shortens a URL and returns the short URL
// If the URL was already shortened, it returns the existing short URL (idempotent)
func (s *URLService) ShortenURL(originalURL string) (string, error) {
	// Validate the URL
	if err := utils.ValidateURL(originalURL); err != nil {
		return "", fmt.Errorf("URL validation failed: %w", err)
	}

	// Normalize the URL
	normalizedURL := utils.NormalizeURL(originalURL)

	// Check if URL was already shortened (idempotency)
	if shortURL, exists := s.storage.GetShortURL(normalizedURL); exists {
		return fmt.Sprintf("%s/%s", s.baseURL, shortURL), nil
	}

	// Generate short code
	shortCode := utils.GenerateShortURL(normalizedURL)

	// Handle potential collisions (very rare but possible)
	// If collision occurs, append a counter
	counter := 0
	originalShortCode := shortCode
	for {
		if _, exists := s.storage.GetOriginalURL(shortCode); !exists {
			break
		}
		counter++
		shortCode = fmt.Sprintf("%s%d", originalShortCode, counter)
	}

	// Extract domain for metrics
	domain, err := utils.ExtractDomain(normalizedURL)
	if err != nil {
		// Log error but don't fail the request
		domain = "unknown"
	}

	// Store the mapping
	s.storage.StoreURL(shortCode, normalizedURL)
	s.storage.IncrementDomainCount(domain)

	// Return the full short URL
	return fmt.Sprintf("%s/%s", s.baseURL, shortCode), nil
}

// GetOriginalURL retrieves the original URL from a short URL
func (s *URLService) GetOriginalURL(shortCode string) (string, error) {
	originalURL, exists := s.storage.GetOriginalURL(shortCode)
	if !exists {
		return "", fmt.Errorf("short URL not found")
	}
	return originalURL, nil
}

// GetTopDomains returns the top N domains by count
func (s *URLService) GetTopDomains(n int) []map[string]interface{} {
	domainCounts := s.storage.GetDomainCounts()

	// Convert to slice for sorting
	type domainCount struct {
		domain string
		count  int
	}

	domains := make([]domainCount, 0, len(domainCounts))
	for domain, count := range domainCounts {
		domains = append(domains, domainCount{domain: domain, count: count})
	}

	// Simple bubble sort (for small datasets, this is fine)
	// For production, consider using sort.Slice
	for i := 0; i < len(domains)-1; i++ {
		for j := 0; j < len(domains)-i-1; j++ {
			if domains[j].count < domains[j+1].count {
				domains[j], domains[j+1] = domains[j+1], domains[j]
			}
		}
	}

	// Take top N
	if n > len(domains) {
		n = len(domains)
	}

	result := make([]map[string]interface{}, 0, n)
	for i := 0; i < n; i++ {
		result = append(result, map[string]interface{}{
			"domain": domains[i].domain,
			"count":  domains[i].count,
		})
	}

	return result
}
