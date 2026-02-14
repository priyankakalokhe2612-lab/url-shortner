package storage

import (
	"sync"
)

// Storage represents the in-memory storage for URL mappings
type Storage struct {
	mu              sync.RWMutex
	shortToOriginal map[string]string // Maps short URL to original URL
	originalToShort map[string]string // Maps original URL to short URL (for idempotency)
	domainCounts    map[string]int    // Maps domain to count of shortened URLs
}

// NewStorage creates a new instance of Storage
func NewStorage() *Storage {
	return &Storage{
		shortToOriginal: make(map[string]string),
		originalToShort: make(map[string]string),
		domainCounts:    make(map[string]int),
	}
}

// GetOriginalURL retrieves the original URL for a given short URL
func (s *Storage) GetOriginalURL(shortURL string) (string, bool) {
	s.mu.RLock()
	defer s.mu.RUnlock()
	originalURL, exists := s.shortToOriginal[shortURL]
	return originalURL, exists
}

// GetShortURL retrieves the short URL for a given original URL
func (s *Storage) GetShortURL(originalURL string) (string, bool) {
	s.mu.RLock()
	defer s.mu.RUnlock()
	shortURL, exists := s.originalToShort[originalURL]
	return shortURL, exists
}

// StoreURL stores the mapping between short URL and original URL
func (s *Storage) StoreURL(shortURL, originalURL string) {
	s.mu.Lock()
	defer s.mu.Unlock()
	s.shortToOriginal[shortURL] = originalURL
	s.originalToShort[originalURL] = shortURL
}

// IncrementDomainCount increments the count for a domain
func (s *Storage) IncrementDomainCount(domain string) {
	s.mu.Lock()
	defer s.mu.Unlock()
	s.domainCounts[domain]++
}

// GetDomainCounts returns a copy of all domain counts
func (s *Storage) GetDomainCounts() map[string]int {
	s.mu.RLock()
	defer s.mu.RUnlock()
	
	// Create a copy to avoid race conditions
	counts := make(map[string]int)
	for domain, count := range s.domainCounts {
		counts[domain] = count
	}
	return counts
}
