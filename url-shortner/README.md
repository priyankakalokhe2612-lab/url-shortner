# URL Shortener Service

A simple, efficient URL shortener service built with Java Spring Boot that provides REST API endpoints for shortening URLs, redirecting short URLs, and retrieving metrics.

## Features

- **URL Shortening**: Accepts a URL via REST API and returns a shortened URL
- **Idempotent**: Same URL always returns the same shortened URL
- **Redirection**: Short URLs redirect to their original URLs
- **In-Memory Storage**: Fast, thread-safe in-memory storage for URL mappings
- **Metrics API**: Returns top 3 domains that have been shortened the most

## API Endpoints

### 1. Shorten URL
**POST** `/api/shorten`

Request body:
```json
{
  "url": "https://www.example.com/very/long/url"
}
```

Response:
```json
{
  "short_url": "http://localhost:8080/abc12345",
  "original_url": "https://www.example.com/very/long/url"
}
```

### 2. Redirect
**GET** `/{shortCode}`

Redirects to the original URL (HTTP 301).

### 3. Metrics
**GET** `/api/metrics`

Response:
```json
{
  "top_domains": [
    {
      "domain": "youtube.com",
      "count": 6
    },
    {
      "domain": "stackoverflow.com",
      "count": 4
    },
    {
      "domain": "wikipedia.org",
      "count": 2
    }
  ]
}
```

### 4. Health Check
**GET** `/health`

Returns `OK` if the service is running.

## Running the Application

### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher

### Installation

1. Build the project:
```bash
cd url-shortner
mvn clean install
```

2. Run the application:
```bash
mvn spring-boot:run
```

Or run the JAR:
```bash
java -jar target/url-shortner-0.0.1-SNAPSHOT.jar
```

The server will start on port 8080 by default.

### Configuration

Edit `src/main/resources/application.properties`:
- `server.port`: Server port (default: 8080)
- `app.base-url`: Base URL for short URLs (default: http://localhost:8080)

## Project Structure

```
url-shortner/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/url_shortner/
│   │   │       ├── UrlShortnerApplication.java    # Main application class
│   │   │       ├── controller/                    # REST controllers
│   │   │       │   └── UrlController.java
│   │   │       ├── service/                       # Business logic
│   │   │       │   └── UrlService.java
│   │   │       ├── storage/                       # In-memory storage
│   │   │       │   └── UrlStorage.java
│   │   │       ├── model/                         # Data models
│   │   │       │   ├── ShortenRequest.java
│   │   │       │   ├── ShortenResponse.java
│   │   │       │   ├── ErrorResponse.java
│   │   │       │   ├── DomainMetric.java
│   │   │       │   └── MetricsResponse.java
│   │   │       ├── exception/                     # Custom exceptions
│   │   │       │   ├── InvalidUrlException.java
│   │   │       │   └── UrlNotFoundException.java
│   │   │       └── util/                          # Utility functions
│   │   │           └── UrlUtils.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
└── pom.xml
```

## Code Quality

- Clean, readable code with proper naming conventions
- Comprehensive exception handling with custom exceptions
- Thread-safe in-memory storage using ConcurrentHashMap and ReadWriteLock
- Proper HTTP status codes and error responses
- Well-structured package organization following Spring Boot best practices
- Dependency injection using Spring's @Service and @Component annotations

## Example Usage

### Shorten a URL
```bash
curl -X POST http://localhost:8080/api/shorten \
  -H "Content-Type: application/json" \
  -d '{"url": "https://www.youtube.com/watch?v=dQw4w9WgXcQ"}'
```

### Get Metrics
```bash
curl http://localhost:8080/api/metrics
```

### Redirect (in browser)
Visit: `http://localhost:8080/abc12345`

### Health Check
```bash
curl http://localhost:8080/health
```
