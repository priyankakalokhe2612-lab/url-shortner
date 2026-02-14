package models

// ShortenRequest represents the request body for shortening a URL
type ShortenRequest struct {
	URL string `json:"url"`
}

// ShortenResponse represents the response after shortening a URL
type ShortenResponse struct {
	ShortURL string `json:"short_url"`
	OriginalURL string `json:"original_url"`
}

// ErrorResponse represents an error response
type ErrorResponse struct {
	Error string `json:"error"`
}

// DomainMetric represents a domain with its count
type DomainMetric struct {
	Domain string `json:"domain"`
	Count  int    `json:"count"`
}

// MetricsResponse represents the metrics API response
type MetricsResponse struct {
	TopDomains []DomainMetric `json:"top_domains"`
}
