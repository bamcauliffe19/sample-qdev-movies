# Movie Service - Spring Boot Demo Application 🏴‍☠️

A simple movie catalog web application built with Spring Boot, demonstrating Java application development best practices with pirate-themed search functionality!

## Features

- **Movie Catalog**: Browse 12 classic movies with detailed information
- **🔍 Movie Search & Filtering**: Search for movies by name, ID, or genre with pirate-themed interface
- **Movie Details**: View comprehensive information including director, year, genre, duration, and description
- **Customer Reviews**: Each movie includes authentic customer reviews with ratings and avatars
- **REST API**: JSON endpoints for programmatic access to movie data
- **Responsive Design**: Mobile-first design that works on all devices
- **Modern UI**: Dark theme with gradient backgrounds and smooth animations
- **Pirate Language**: Arrr! Enjoy the pirate-themed search experience, matey!

## Technology Stack

- **Java 8**
- **Spring Boot 2.7.18**
- **Maven** for dependency management
- **Thymeleaf** for templating
- **Log4j 2.20.0**
- **JUnit 5.8.2**

## Quick Start

### Prerequisites

- Java 8 or higher
- Maven 3.6+

### Run the Application

```bash
git clone https://github.com/<youruser>/sample-qdev-movies.git
cd sample-qdev-movies
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### Access the Application

- **Movie List with Search**: http://localhost:8080/movies
- **Movie Details**: http://localhost:8080/movies/{id}/details (where {id} is 1-12)
- **Search API**: http://localhost:8080/movies/search

## Building for Production

```bash
mvn clean package
java -jar target/sample-qdev-movies-0.1.0.jar
```

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/amazonaws/samples/qdevmovies/
│   │       ├── movies/
│   │       │   ├── MoviesApplication.java    # Main Spring Boot application
│   │       │   ├── MoviesController.java     # REST controller with search endpoints
│   │       │   ├── MovieService.java         # Business logic with search functionality
│   │       │   ├── Movie.java                # Movie data model
│   │       │   ├── Review.java               # Review data model
│   │       │   └── ReviewService.java        # Review business logic
│   │       └── utils/
│   │           ├── MovieIconUtils.java       # Movie icon utilities
│   │           └── MovieUtils.java           # Movie validation utilities
│   └── resources/
│       ├── application.yml                   # Application configuration
│       ├── movies.json                       # Movie data
│       ├── mock-reviews.json                 # Mock review data
│       ├── log4j2.xml                        # Logging configuration
│       ├── static/css/                       # CSS styles with search form styling
│       └── templates/                        # Thymeleaf templates with search interface
└── test/                                     # Comprehensive unit tests
```

## API Endpoints

### Get All Movies (with Search)
```
GET /movies
```
Returns an HTML page displaying movies with search functionality.

**Query Parameters (optional):**
- `name` (string): Search by movie name (partial match, case-insensitive)
- `id` (long): Search by specific movie ID
- `genre` (string): Filter by genre (partial match, case-insensitive)

**Examples:**
```
http://localhost:8080/movies                           # All movies
http://localhost:8080/movies?name=Prison               # Search by name
http://localhost:8080/movies?genre=Drama               # Filter by genre
http://localhost:8080/movies?id=5                      # Find by ID
http://localhost:8080/movies?name=Family&genre=Crime   # Combined search
```

### Movie Search API (JSON)
```
GET /movies/search
```
Returns JSON array of movies matching search criteria.

**Query Parameters (optional):**
- `name` (string): Search by movie name (partial match, case-insensitive)
- `id` (long): Search by specific movie ID
- `genre` (string): Filter by genre (partial match, case-insensitive)

**Response Format:**
```json
[
  {
    "id": 1,
    "movieName": "The Prison Escape",
    "director": "John Director",
    "year": 1994,
    "genre": "Drama",
    "description": "Two imprisoned men bond over a number of years...",
    "duration": 142,
    "imdbRating": 5.0,
    "icon": "🎬"
  }
]
```

**Examples:**
```bash
# Search by name
curl "http://localhost:8080/movies/search?name=Prison"

# Filter by genre
curl "http://localhost:8080/movies/search?genre=Action"

# Find by ID
curl "http://localhost:8080/movies/search?id=3"

# Combined search
curl "http://localhost:8080/movies/search?name=The&genre=Drama"

# Get all movies (no parameters)
curl "http://localhost:8080/movies/search"
```

### Get Movie Details
```
GET /movies/{id}/details
```
Returns an HTML page with detailed movie information and customer reviews.

**Parameters:**
- `id` (path parameter): Movie ID (1-12)

**Example:**
```
http://localhost:8080/movies/1/details
```

## Search Features

### 🔍 Web Interface Search
- **Interactive Form**: Search form with input fields for name, ID, and genre
- **Genre Dropdown**: Pre-populated with all available genres
- **Pirate Theme**: Fun pirate language and emojis throughout the interface
- **Real-time Results**: Instant search results with clear feedback
- **No Results Handling**: Friendly pirate-themed messages when no movies are found
- **Search Persistence**: Form remembers your search criteria

### 🏴‍☠️ Search Capabilities
- **Partial Matching**: Search for "Prison" to find "The Prison Escape"
- **Case Insensitive**: "DRAMA", "drama", and "Drama" all work the same
- **Multiple Criteria**: Combine name and genre filters
- **ID Priority**: When searching by ID, other criteria are ignored
- **Whitespace Handling**: Leading/trailing spaces are automatically trimmed

### 📱 Mobile Responsive
- **Mobile-First Design**: Search form adapts to small screens
- **Touch-Friendly**: Large buttons and input fields for mobile devices
- **Responsive Grid**: Movie results display beautifully on all screen sizes

## Testing

Run the comprehensive test suite:

```bash
mvn test
```

### Test Coverage
- **MovieService Tests**: Complete coverage of search functionality
- **MoviesController Tests**: Web and API endpoint testing
- **Edge Cases**: Invalid parameters, empty results, whitespace handling
- **Integration Tests**: End-to-end search scenarios

## Troubleshooting

### Port 8080 already in use

Run on a different port:
```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

### Build failures

Clean and rebuild:
```bash
mvn clean compile
```

### Search not working

1. Check that movies.json is properly loaded
2. Verify log output for search operations
3. Test the API endpoints directly with curl

## Contributing

This project demonstrates modern Spring Boot development practices. Feel free to:
- Add more movies to the catalog
- Enhance the search functionality
- Improve the pirate theme
- Add more filtering options
- Enhance the responsive design

## License

This sample code is licensed under the MIT-0 License. See the LICENSE file.

---

*Arrr! May yer movie searches be fruitful and yer treasures be many, matey! 🏴‍☠️*
