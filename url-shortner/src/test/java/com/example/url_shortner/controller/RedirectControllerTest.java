package com.example.url_shortner.controller;

import com.example.url_shortner.exception.UrlNotFoundException;
import com.example.url_shortner.service.UrlService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RedirectController.class)
class RedirectControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private UrlService urlService;
    
    @Test
    void testRedirect_Success() throws Exception {
        String shortCode = "abc123";
        String originalUrl = "https://example.com";
        
        when(urlService.getOriginalUrl(shortCode)).thenReturn(originalUrl);
        
        mockMvc.perform(get("/" + shortCode))
                .andExpect(status().isMovedPermanently())
                .andExpect(header().string("Location", originalUrl));
        
        verify(urlService).getOriginalUrl(shortCode);
    }
    
    @Test
    void testRedirect_NotFound() throws Exception {
        String shortCode = "nonexistent";
        
        when(urlService.getOriginalUrl(shortCode))
                .thenThrow(new UrlNotFoundException("Short URL not found"));
        
        mockMvc.perform(get("/" + shortCode))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Short URL not found"));
        
        verify(urlService).getOriginalUrl(shortCode);
    }
    
    @Test
    void testRedirect_InternalServerError() throws Exception {
        String shortCode = "abc123";
        
        when(urlService.getOriginalUrl(shortCode))
                .thenThrow(new RuntimeException("Database error"));
        
        mockMvc.perform(get("/" + shortCode))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").exists());
        
        verify(urlService).getOriginalUrl(shortCode);
    }
    
    @Test
    void testRedirect_WrongMethod() throws Exception {
        String shortCode = "abc123";
        
        mockMvc.perform(post("/" + shortCode))
                .andExpect(status().isMethodNotAllowed());
        
        verify(urlService, never()).getOriginalUrl(anyString());
    }
}
