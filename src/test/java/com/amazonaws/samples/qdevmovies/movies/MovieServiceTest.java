package com.amazonaws.samples.qdevmovies.movies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test class for MovieService search functionality
 * Arrr! These tests be making sure our treasure hunting methods work ship-shape!
 */
public class MovieServiceTest {

    private MovieService movieService;

    @BeforeEach
    public void setUp() {
        movieService = new MovieService();
    }

    @Test
    public void testGetAllMovies() {
        List<Movie> movies = movieService.getAllMovies();
        assertNotNull(movies);
        assertFalse(movies.isEmpty());
        assertTrue(movies.size() > 0);
    }

    @Test
    public void testGetMovieById() {
        Optional<Movie> movie = movieService.getMovieById(1L);
        assertTrue(movie.isPresent());
        assertEquals(1L, movie.get().getId());
    }

    @Test
    public void testGetMovieByIdNotFound() {
        Optional<Movie> movie = movieService.getMovieById(999L);
        assertFalse(movie.isPresent());
    }

    @Test
    public void testGetMovieByIdInvalid() {
        Optional<Movie> movie1 = movieService.getMovieById(null);
        assertFalse(movie1.isPresent());
        
        Optional<Movie> movie2 = movieService.getMovieById(0L);
        assertFalse(movie2.isPresent());
        
        Optional<Movie> movie3 = movieService.getMovieById(-1L);
        assertFalse(movie3.isPresent());
    }

    @Test
    public void testSearchMoviesByName() {
        List<Movie> results = movieService.searchMovies("Prison", null, null);
        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertTrue(results.stream().anyMatch(movie -> 
            movie.getMovieName().toLowerCase().contains("prison")));
    }

    @Test
    public void testSearchMoviesByNameCaseInsensitive() {
        List<Movie> results1 = movieService.searchMovies("PRISON", null, null);
        List<Movie> results2 = movieService.searchMovies("prison", null, null);
        List<Movie> results3 = movieService.searchMovies("Prison", null, null);
        
        assertNotNull(results1);
        assertNotNull(results2);
        assertNotNull(results3);
        assertEquals(results1.size(), results2.size());
        assertEquals(results2.size(), results3.size());
    }

    @Test
    public void testSearchMoviesByGenre() {
        List<Movie> results = movieService.searchMovies(null, null, "Drama");
        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertTrue(results.stream().allMatch(movie -> 
            movie.getGenre().toLowerCase().contains("drama")));
    }

    @Test
    public void testSearchMoviesByGenreCaseInsensitive() {
        List<Movie> results1 = movieService.searchMovies(null, null, "DRAMA");
        List<Movie> results2 = movieService.searchMovies(null, null, "drama");
        List<Movie> results3 = movieService.searchMovies(null, null, "Drama");
        
        assertNotNull(results1);
        assertNotNull(results2);
        assertNotNull(results3);
        assertEquals(results1.size(), results2.size());
        assertEquals(results2.size(), results3.size());
    }

    @Test
    public void testSearchMoviesById() {
        List<Movie> results = movieService.searchMovies(null, 1L, null);
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(1L, results.get(0).getId());
    }

    @Test
    public void testSearchMoviesByIdNotFound() {
        List<Movie> results = movieService.searchMovies(null, 999L, null);
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    public void testSearchMoviesByIdInvalid() {
        List<Movie> results1 = movieService.searchMovies(null, 0L, null);
        assertNotNull(results1);
        assertTrue(results1.isEmpty());
        
        List<Movie> results2 = movieService.searchMovies(null, -1L, null);
        assertNotNull(results2);
        assertTrue(results2.isEmpty());
    }

    @Test
    public void testSearchMoviesByNameAndGenre() {
        List<Movie> results = movieService.searchMovies("Family", null, "Crime");
        assertNotNull(results);
        
        // Should find movies that contain both "Family" in name AND "Crime" in genre
        for (Movie movie : results) {
            assertTrue(movie.getMovieName().toLowerCase().contains("family"));
            assertTrue(movie.getGenre().toLowerCase().contains("crime"));
        }
    }

    @Test
    public void testSearchMoviesWithEmptyName() {
        List<Movie> results1 = movieService.searchMovies("", null, null);
        List<Movie> results2 = movieService.searchMovies("   ", null, null);
        List<Movie> allMovies = movieService.getAllMovies();
        
        assertNotNull(results1);
        assertNotNull(results2);
        assertEquals(allMovies.size(), results1.size());
        assertEquals(allMovies.size(), results2.size());
    }

    @Test
    public void testSearchMoviesWithEmptyGenre() {
        List<Movie> results1 = movieService.searchMovies(null, null, "");
        List<Movie> results2 = movieService.searchMovies(null, null, "   ");
        List<Movie> allMovies = movieService.getAllMovies();
        
        assertNotNull(results1);
        assertNotNull(results2);
        assertEquals(allMovies.size(), results1.size());
        assertEquals(allMovies.size(), results2.size());
    }

    @Test
    public void testSearchMoviesWithAllNullParams() {
        List<Movie> results = movieService.searchMovies(null, null, null);
        List<Movie> allMovies = movieService.getAllMovies();
        
        assertNotNull(results);
        assertEquals(allMovies.size(), results.size());
    }

    @Test
    public void testSearchMoviesNoResults() {
        List<Movie> results = movieService.searchMovies("NonExistentMovie", null, null);
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    public void testSearchMoviesPartialMatch() {
        // Test partial matching works
        List<Movie> results = movieService.searchMovies("The", null, null);
        assertNotNull(results);
        assertFalse(results.isEmpty());
        
        // All results should contain "The" in the name
        for (Movie movie : results) {
            assertTrue(movie.getMovieName().toLowerCase().contains("the"));
        }
    }

    @Test
    public void testSearchMoviesGenrePartialMatch() {
        // Test partial matching for genre (e.g., "Sci" should match "Sci-Fi")
        List<Movie> results = movieService.searchMovies(null, null, "Sci");
        assertNotNull(results);
        
        // All results should contain "Sci" in the genre
        for (Movie movie : results) {
            assertTrue(movie.getGenre().toLowerCase().contains("sci"));
        }
    }

    @Test
    public void testGetAllGenres() {
        List<String> genres = movieService.getAllGenres();
        assertNotNull(genres);
        assertFalse(genres.isEmpty());
        
        // Check that genres are unique and sorted
        for (int i = 1; i < genres.size(); i++) {
            assertTrue(genres.get(i-1).compareTo(genres.get(i)) <= 0);
        }
        
        // Check that all genres from movies are included
        List<Movie> allMovies = movieService.getAllMovies();
        for (Movie movie : allMovies) {
            assertTrue(genres.contains(movie.getGenre()));
        }
    }

    @Test
    public void testSearchMoviesIdTakesPrecedence() {
        // When ID is provided, it should take precedence over name and genre
        List<Movie> results = movieService.searchMovies("SomeOtherName", 1L, "SomeOtherGenre");
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(1L, results.get(0).getId());
        
        // The name and genre filters should be ignored when ID is provided
        assertFalse(results.get(0).getMovieName().toLowerCase().contains("someothername"));
    }

    @Test
    public void testSearchMoviesWhitespaceHandling() {
        // Test that leading/trailing whitespace is handled correctly
        List<Movie> results1 = movieService.searchMovies("  Prison  ", null, null);
        List<Movie> results2 = movieService.searchMovies("Prison", null, null);
        
        assertNotNull(results1);
        assertNotNull(results2);
        assertEquals(results1.size(), results2.size());
    }
}