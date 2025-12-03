package com.amazonaws.samples.qdevmovies.movies;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

@Service
public class MovieService {
    private static final Logger logger = LogManager.getLogger(MovieService.class);
    private final List<Movie> movies;
    private final Map<Long, Movie> movieMap;

    public MovieService() {
        this.movies = loadMoviesFromJson();
        this.movieMap = new HashMap<>();
        for (Movie movie : movies) {
            movieMap.put(movie.getId(), movie);
        }
    }

    private List<Movie> loadMoviesFromJson() {
        List<Movie> movieList = new ArrayList<>();
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("movies.json");
            if (inputStream != null) {
                Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name());
                String jsonContent = scanner.useDelimiter("\\A").next();
                scanner.close();
                
                JSONArray moviesArray = new JSONArray(jsonContent);
                for (int i = 0; i < moviesArray.length(); i++) {
                    JSONObject movieObj = moviesArray.getJSONObject(i);
                    movieList.add(new Movie(
                        movieObj.getLong("id"),
                        movieObj.getString("movieName"),
                        movieObj.getString("director"),
                        movieObj.getInt("year"),
                        movieObj.getString("genre"),
                        movieObj.getString("description"),
                        movieObj.getInt("duration"),
                        movieObj.getDouble("imdbRating")
                    ));
                }
            }
        } catch (Exception e) {
            logger.error("Failed to load movies from JSON: {}", e.getMessage());
        }
        return movieList;
    }

    public List<Movie> getAllMovies() {
        return movies;
    }

    public Optional<Movie> getMovieById(Long id) {
        if (id == null || id <= 0) {
            return Optional.empty();
        }
        return Optional.ofNullable(movieMap.get(id));
    }

    /**
     * Searches for movies based on the provided criteria.
     * Arrr! This method be the treasure hunter that finds yer movies, matey!
     * 
     * @param name Movie name to search for (partial match, case-insensitive)
     * @param id Specific movie ID to find
     * @param genre Genre to filter by (partial match, case-insensitive)
     * @return List of movies matching the search criteria
     */
    public List<Movie> searchMovies(String name, Long id, String genre) {
        logger.info("Ahoy! Searching for movies with name: {}, id: {}, genre: {}", name, id, genre);
        
        List<Movie> results = new ArrayList<>();
        
        // If searching by ID specifically, return that movie if it exists
        if (id != null && id > 0) {
            Optional<Movie> movieById = getMovieById(id);
            if (movieById.isPresent()) {
                results.add(movieById.get());
                logger.info("Found treasure by ID: {}", id);
                return results;
            } else {
                logger.warn("No treasure found with ID: {}", id);
                return results; // Return empty list if ID not found
            }
        }
        
        // Filter by name and/or genre
        for (Movie movie : movies) {
            boolean matches = true;
            
            // Check name match (case-insensitive partial match)
            if (name != null && !name.trim().isEmpty()) {
                matches = matches && movie.getMovieName().toLowerCase()
                    .contains(name.trim().toLowerCase());
            }
            
            // Check genre match (case-insensitive partial match)
            if (genre != null && !genre.trim().isEmpty()) {
                matches = matches && movie.getGenre().toLowerCase()
                    .contains(genre.trim().toLowerCase());
            }
            
            if (matches) {
                results.add(movie);
            }
        }
        
        logger.info("Search completed! Found {} treasures matching yer criteria", results.size());
        return results;
    }

    /**
     * Gets all unique genres from the movie collection.
     * Useful for populating search dropdowns, ye savvy?
     * 
     * @return List of unique genres
     */
    public List<String> getAllGenres() {
        return movies.stream()
            .map(Movie::getGenre)
            .distinct()
            .sorted()
            .collect(Collectors.toList());
    }
}
