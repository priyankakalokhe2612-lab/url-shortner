package com.example.url_shortner.controller;

import com.example.url_shortner.exception.InvalidUrlException;
import com.example.url_shortner.exception.UrlNotFoundException;
import com.example.url_shortner.model.ErrorResponse;
import com.example.url_shortner.model.MetricsResponse;
import com.example.url_shortner.model.ShortenRequest;
import com.example.url_shortner.model.ShortenResponse;
import com.example.url_shortner.service.UrlService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for URL shortening operations
 */
@RestController
@RequestMapping("/api")
public class UrlController {
    
    private final UrlService urlService;
    
    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }
    
    /**
     * POST /api/shorten - Shortens a URL
     * @param request the shorten request containing the URL
     * @return the shorten response with short URL and original URL
     */
    @PostMapping("/shorten")
    public ResponseEntity<?> shortenUrl(@RequestBody ShortenRequest request) {
        try {
            if (request == null || request.getUrl() == null || request.getUrl().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("URL is required"));
            }
            
            String shortUrl = urlService.shortenUrl(request.getUrl());
            ShortenResponse response = new ShortenResponse(shortUrl, request.getUrl());
            return ResponseEntity.ok(response);
            
        } catch (InvalidUrlException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("An error occurred while shortening the URL: " + e.getMessage()));
        }
    }
    
    /**
     * GET /api/metrics - Returns top 3 domains
     * @return metrics response with top domains
     */
    @GetMapping("/metrics")
    public ResponseEntity<MetricsResponse> getMetrics() {
        MetricsResponse response = new MetricsResponse(urlService.getTopDomains(3));
        return ResponseEntity.ok(response);
    }
    
    /**
     * GET /{shortCode} - Redirects to the original URL
     * @param shortCode the short code
     * @return redirect response
     */
    @GetMapping("/{shortCode}")
    public ResponseEntity<?> redirect(@PathVariable String shortCode) {
        try {
            String originalUrl = urlService.getOriginalUrl(shortCode);
            return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY)
                    .header("Location", originalUrl)
                    .build();
        } catch (UrlNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("An error occurred: " + e.getMessage()));
        }
    }
    
    /**
     * GET /health - Health check endpoint
     * @return OK status
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }
}
