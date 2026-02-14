package com.example.url_shortner.controller;

import com.example.url_shortner.exception.InvalidUrlException;
import com.example.url_shortner.model.ShortenRequest;
import com.example.url_shortner.service.UrlService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UrlController.class)
class UrlControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private UrlService urlService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    void testShortenUrl_Success() throws Exception {
        String originalUrl = "https://example.com";
        String shortUrl = "http://localhost:8080/abc123";
        ShortenRequest request = new ShortenRequest(originalUrl);
        
        when(urlService.shortenUrl(originalUrl)).thenReturn(shortUrl);
        
        mockMvc.perform(post("/api/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.short_url").value(shortUrl))
                .andExpect(jsonPath("$.original_url").value(originalUrl));
        
        verify(urlService).shortenUrl(originalUrl);
    }
    
    @Test
    void testShortenUrl_InvalidUrl() throws Exception {
        String originalUrl = "invalid-url";
        ShortenRequest request = new ShortenRequest(originalUrl);
        
        when(urlService.shortenUrl(originalUrl))
                .thenThrow(new InvalidUrlException("Invalid URL format"));
        
        mockMvc.perform(post("/api/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
        
        verify(urlService).shortenUrl(originalUrl);
    }
    
    @Test
    void testShortenUrl_EmptyUrl() throws Exception {
        ShortenRequest request = new ShortenRequest("");
        
        mockMvc.perform(post("/api/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("URL is required"));
        
        verify(urlService, never()).shortenUrl(anyString());
    }
    
    @Test
    void testShortenUrl_NullUrl() throws Exception {
        ShortenRequest request = new ShortenRequest(null);
        
        mockMvc.perform(post("/api/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("URL is required"));
        
        verify(urlService, never()).shortenUrl(anyString());
    }
    
    @Test
    void testShortenUrl_InternalServerError() throws Exception {
        String originalUrl = "https://example.com";
        ShortenRequest request = new ShortenRequest(originalUrl);
        
        when(urlService.shortenUrl(originalUrl))
                .thenThrow(new RuntimeException("Database error"));
        
        mockMvc.perform(post("/api/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").exists());
        
        verify(urlService).shortenUrl(originalUrl);
    }
    
    @Test
    void testGetMetrics_Success() throws Exception {
        mockMvc.perform(get("/api/metrics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.top_domains").exists());
        
        verify(urlService).getTopDomains(3);
    }
    
    @Test
    void testShortenUrl_WrongMethod() throws Exception {
        mockMvc.perform(get("/api/shorten"))
                .andExpect(status().isMethodNotAllowed());
    }
    
    @Test
    void testGetMetrics_WrongMethod() throws Exception {
        mockMvc.perform(post("/api/metrics"))
                .andExpect(status().isMethodNotAllowed());
    }
}
