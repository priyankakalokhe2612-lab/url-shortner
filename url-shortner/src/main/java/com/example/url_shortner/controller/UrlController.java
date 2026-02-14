package com.example.url_shortner.controller;

import com.example.url_shortner.exception.InvalidUrlException;
import com.example.url_shortner.model.ErrorResponse;
import com.example.url_shortner.model.MetricsResponse;
import com.example.url_shortner.model.ShortenRequest;
import com.example.url_shortner.model.ShortenResponse;
import com.example.url_shortner.service.UrlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for URL shortening operations
 */
@RestController
@RequestMapping("/api")
@Tag(name = "URL Shortener", description = "API endpoints for URL shortening, redirection, and metrics")
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
    @Operation(
            summary = "Shorten a URL",
            description = "Accepts a URL and returns a shortened URL. If the same URL is shortened again, it returns the existing short URL (idempotent)."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "URL shortened successfully",
                    content = @Content(schema = @Schema(implementation = ShortenResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid URL provided",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
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
    @Operation(
            summary = "Get top domains metrics",
            description = "Returns the top 3 domains that have been shortened the most number of times, sorted by count in descending order."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Metrics retrieved successfully",
                    content = @Content(schema = @Schema(implementation = MetricsResponse.class))
            )
    })
    @GetMapping("/metrics")
    public ResponseEntity<MetricsResponse> getMetrics() {
        MetricsResponse response = new MetricsResponse(urlService.getTopDomains(3));
        return ResponseEntity.ok(response);
    }
    
}
