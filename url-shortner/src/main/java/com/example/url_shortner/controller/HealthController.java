package com.example.url_shortner.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for health check endpoint
 */
@RestController
@Tag(name = "Health", description = "Health check endpoint")
public class HealthController {
    
    /**
     * GET /health - Health check endpoint
     * @return OK status
     */
    @Operation(
            summary = "Health check",
            description = "Returns the health status of the service"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Service is healthy"
            )
    })
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }
}
