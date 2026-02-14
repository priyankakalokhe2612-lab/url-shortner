package com.example.url_shortner.controller;

import com.example.url_shortner.exception.UrlNotFoundException;
import com.example.url_shortner.model.ErrorResponse;
import com.example.url_shortner.service.UrlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for URL redirection
 * Handles root-level redirects from short codes to original URLs
 */
@RestController
@Tag(name = "Redirect", description = "Redirect endpoint for short URLs")
public class RedirectController {
    
    private final UrlService urlService;
    
    public RedirectController(UrlService urlService) {
        this.urlService = urlService;
    }
    
    /**
     * GET /{shortCode} - Redirects to the original URL
     * @param shortCode the short code (e.g., "pZqm5765")
     * @return redirect response
     */
    @Operation(
            summary = "Redirect to original URL",
            description = "Redirects the short URL to its original URL. Returns HTTP 301 (Moved Permanently). " +
                         "Enter only the short code (e.g., 'pZqm5765'), not the full URL."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "301",
                    description = "Redirect to original URL"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Short URL not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/{shortCode}")
    public ResponseEntity<?> redirect(
            @Parameter(
                    description = "The short code (e.g., 'pZqm5765'). Do not include the full URL.",
                    required = true,
                    example = "pZqm5765"
            )
            @PathVariable String shortCode) {
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
}
