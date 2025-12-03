package com.amazonaws.samples.qdevmovies.movies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;
import org.springframework.ui.ExtendedModelMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class MoviesControllerTest {

    private MoviesController moviesController;
    private Model model;
    private MovieService mockMovieService;
    private ReviewService mockReviewService;

    @BeforeEach
    public void setUp() {
        moviesController = new MoviesController();
        model = new ExtendedModelMap();
        
        // Create mock services
        mockMovieService = new MovieService() {
            @Override
            public List<Movie> getAllMovies() {
                return Arrays.asList(
                    new Movie(1L, "Test Movie", "Test Director", 2023, "Drama", "Test description", 120, 4.5),
                    new Movie(2L, "Action Movie", "Action Director", 2022, "Action", "Action description", 110, 4.0),
                    new Movie(3L, "Comedy Film", "Comedy Director", 2021, "Comedy", "Comedy description", 95, 3.5)
                );
            }
            
            @Override
            public Optional<Movie> getMovieById(Long id) {
                if (id == 1L) {
                    return Optional.of(new Movie(1L, "Test Movie", "Test Director", 2023, "Drama", "Test description", 120, 4.5));
                } else if (id == 2L) {
                    return Optional.of(new Movie(2L, "Action Movie", "Action Director", 2022, "Action", "Action description", 110, 4.0));
                }
                return Optional.empty();
            }
            
            @Override
            public List<Movie> searchMovies(String name, Long id, String genre) {
                List<Movie> allMovies = getAllMovies();
                List<Movie> results = new ArrayList<>();
                
                // If searching by ID specifically, return that movie if it exists
                if (id != null && id > 0) {
                    Optional<Movie> movieById = getMovieById(id);
                    if (movieById.isPresent()) {
                        results.add(movieById.get());
                        return results;
                    } else {
                        return results; // Return empty list if ID not found
                    }
                }
                
                // Filter by name and/or genre
                for (Movie movie : allMovies) {
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
                
                return results;
            }
            
            @Override
            public List<String> getAllGenres() {
                return Arrays.asList("Action", "Comedy", "Drama");
            }
        };
        
        mockReviewService = new ReviewService() {
            @Override
            public List<Review> getReviewsForMovie(long movieId) {
                return new ArrayList<>();
            }
        };
        
        // Inject mocks using reflection
        try {
            java.lang.reflect.Field movieServiceField = MoviesController.class.getDeclaredField("movieService");
            movieServiceField.setAccessible(true);
            movieServiceField.set(moviesController, mockMovieService);
            
            java.lang.reflect.Field reviewServiceField = MoviesController.class.getDeclaredField("reviewService");
            reviewServiceField.setAccessible(true);
            reviewServiceField.set(moviesController, mockReviewService);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject mock services", e);
        }
    }

    @Test
    public void testGetMoviesWithoutSearch() {
        String result = moviesController.getMovies(model, null, null, null);
        assertNotNull(result);
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(3, movies.size());
        assertFalse((Boolean) model.getAttribute("searchPerformed"));
    }

    @Test
    public void testGetMoviesWithNameSearch() {
        String result = moviesController.getMovies(model, "Test", null, null);
        assertNotNull(result);
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(1, movies.size());
        assertEquals("Test Movie", movies.get(0).getMovieName());
        assertTrue((Boolean) model.getAttribute("searchPerformed"));
        assertEquals("Test", model.getAttribute("searchName"));
    }

    @Test
    public void testGetMoviesWithGenreSearch() {
        String result = moviesController.getMovies(model, null, null, "Action");
        assertNotNull(result);
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(1, movies.size());
        assertEquals("Action Movie", movies.get(0).getMovieName());
        assertTrue((Boolean) model.getAttribute("searchPerformed"));
        assertEquals("Action", model.getAttribute("searchGenre"));
    }

    @Test
    public void testGetMoviesWithIdSearch() {
        String result = moviesController.getMovies(model, null, 2L, null);
        assertNotNull(result);
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(1, movies.size());
        assertEquals("Action Movie", movies.get(0).getMovieName());
        assertTrue((Boolean) model.getAttribute("searchPerformed"));
        assertEquals(2L, model.getAttribute("searchId"));
    }

    @Test
    public void testGetMoviesWithNoResults() {
        String result = moviesController.getMovies(model, "NonExistent", null, null);
        assertNotNull(result);
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(0, movies.size());
        assertTrue((Boolean) model.getAttribute("searchPerformed"));
        assertTrue((Boolean) model.getAttribute("noResults"));
        assertNotNull(model.getAttribute("pirateMessage"));
    }

    @Test
    public void testSearchMoviesApi() {
        List<Movie> result = moviesController.searchMoviesApi("Test", null, null);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Movie", result.get(0).getMovieName());
    }

    @Test
    public void testSearchMoviesApiWithId() {
        List<Movie> result = moviesController.searchMoviesApi(null, 2L, null);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Action Movie", result.get(0).getMovieName());
    }

    @Test
    public void testSearchMoviesApiWithGenre() {
        List<Movie> result = moviesController.searchMoviesApi(null, null, "Comedy");
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Comedy Film", result.get(0).getMovieName());
    }

    @Test
    public void testSearchMoviesApiWithNoParams() {
        List<Movie> result = moviesController.searchMoviesApi(null, null, null);
        assertNotNull(result);
        assertEquals(3, result.size()); // Should return all movies
    }

    @Test
    public void testSearchMoviesApiWithEmptyParams() {
        List<Movie> result = moviesController.searchMoviesApi("", 0L, "");
        assertNotNull(result);
        assertEquals(3, result.size()); // Should return all movies
    }

    @Test
    public void testGetMovieDetails() {
        String result = moviesController.getMovieDetails(1L, model);
        assertNotNull(result);
        assertEquals("movie-details", result);
    }

    @Test
    public void testGetMovieDetailsNotFound() {
        String result = moviesController.getMovieDetails(999L, model);
        assertNotNull(result);
        assertEquals("error", result);
    }

    @Test
    public void testMovieServiceIntegration() {
        List<Movie> movies = mockMovieService.getAllMovies();
        assertEquals(3, movies.size());
        assertEquals("Test Movie", movies.get(0).getMovieName());
    }
}
