package handlers

import (
	"encoding/json"
	"log"
	"net/http"
	"url-shortener/models"
	"url-shortener/service"
)

// URLHandler handles HTTP requests for URL shortening operations
type URLHandler struct {
	urlService *service.URLService
}

// NewURLHandler creates a new instance of URLHandler
func NewURLHandler(urlService *service.URLService) *URLHandler {
	return &URLHandler{
		urlService: urlService,
	}
}

// ShortenURL handles POST requests to shorten a URL
func (h *URLHandler) ShortenURL(w http.ResponseWriter, r *http.Request) {
	// Only allow POST method
	if r.Method != http.MethodPost {
		http.Error(w, "Method not allowed", http.StatusMethodNotAllowed)
		return
	}

	// Parse request body
	var req models.ShortenRequest
	if err := json.NewDecoder(r.Body).Decode(&req); err != nil {
		log.Printf("Error decoding request: %v", err)
		sendErrorResponse(w, "Invalid request body", http.StatusBadRequest)
		return
	}

	// Shorten the URL
	shortURL, err := h.urlService.ShortenURL(req.URL)
	if err != nil {
		log.Printf("Error shortening URL: %v", err)
		sendErrorResponse(w, err.Error(), http.StatusBadRequest)
		return
	}

	// Send success response
	response := models.ShortenResponse{
		ShortURL:    shortURL,
		OriginalURL: req.URL,
	}

	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(http.StatusOK)
	if err := json.NewEncoder(w).Encode(response); err != nil {
		log.Printf("Error encoding response: %v", err)
	}
}

// Redirect handles GET requests to redirect short URLs to original URLs
func (h *URLHandler) Redirect(w http.ResponseWriter, r *http.Request) {
	// Only allow GET method
	if r.Method != http.MethodGet {
		http.Error(w, "Method not allowed", http.StatusMethodNotAllowed)
		return
	}

	// Extract short code from URL path
	shortCode := r.URL.Path[1:] // Remove leading "/"

	if shortCode == "" {
		sendErrorResponse(w, "Short URL code is required", http.StatusBadRequest)
		return
	}

	// Get original URL
	originalURL, err := h.urlService.GetOriginalURL(shortCode)
	if err != nil {
		log.Printf("Error retrieving original URL: %v", err)
		sendErrorResponse(w, "Short URL not found", http.StatusNotFound)
		return
	}

	// Redirect to original URL
	http.Redirect(w, r, originalURL, http.StatusMovedPermanently)
}

// GetMetrics handles GET requests to retrieve top domains metrics
func (h *URLHandler) GetMetrics(w http.ResponseWriter, r *http.Request) {
	// Only allow GET method
	if r.Method != http.MethodGet {
		http.Error(w, "Method not allowed", http.StatusMethodNotAllowed)
		return
	}

	// Get top 3 domains
	topDomains := h.urlService.GetTopDomains(3)

	// Convert to response format
	domainMetrics := make([]models.DomainMetric, 0, len(topDomains))
	for _, domain := range topDomains {
		domainMetrics = append(domainMetrics, models.DomainMetric{
			Domain: domain["domain"].(string),
			Count:  domain["count"].(int),
		})
	}

	response := models.MetricsResponse{
		TopDomains: domainMetrics,
	}

	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(http.StatusOK)
	if err := json.NewEncoder(w).Encode(response); err != nil {
		log.Printf("Error encoding response: %v", err)
	}
}

// sendErrorResponse sends an error response in JSON format
func sendErrorResponse(w http.ResponseWriter, message string, statusCode int) {
	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(statusCode)
	response := models.ErrorResponse{Error: message}
	if err := json.NewEncoder(w).Encode(response); err != nil {
		log.Printf("Error encoding error response: %v", err)
	}
}
