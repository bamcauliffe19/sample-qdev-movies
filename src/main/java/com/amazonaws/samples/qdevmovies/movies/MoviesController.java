package com.amazonaws.samples.qdevmovies.movies;

import com.amazonaws.samples.qdevmovies.utils.MovieIconUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;

@Controller
public class MoviesController {
    private static final Logger logger = LogManager.getLogger(MoviesController.class);

    @Autowired
    private MovieService movieService;

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/movies")
    public String getMovies(org.springframework.ui.Model model,
                           @RequestParam(value = "name", required = false) String name,
                           @RequestParam(value = "id", required = false) Long id,
                           @RequestParam(value = "genre", required = false) String genre) {
        logger.info("Ahoy! Fetching movies with search criteria - name: {}, id: {}, genre: {}", name, id, genre);
        
        List<Movie> movies;
        boolean isSearch = (name != null && !name.trim().isEmpty()) || 
                          (id != null && id > 0) || 
                          (genre != null && !genre.trim().isEmpty());
        
        if (isSearch) {
            movies = movieService.searchMovies(name, id, genre);
            model.addAttribute("searchPerformed", true);
            model.addAttribute("searchName", name);
            model.addAttribute("searchId", id);
            model.addAttribute("searchGenre", genre);
            
            if (movies.isEmpty()) {
                model.addAttribute("noResults", true);
                model.addAttribute("pirateMessage", "Arrr! No treasures found matching yer search, matey! Try different criteria or sail back to see all movies.");
            }
        } else {
            movies = movieService.getAllMovies();
            model.addAttribute("searchPerformed", false);
        }
        
        model.addAttribute("movies", movies);
        model.addAttribute("allGenres", movieService.getAllGenres());
        return "movies";
    }

    @GetMapping("/movies/{id}/details")
    public String getMovieDetails(@PathVariable("id") Long movieId, org.springframework.ui.Model model) {
        logger.info("Fetching details for movie ID: {}", movieId);
        
        Optional<Movie> movieOpt = movieService.getMovieById(movieId);
        if (!movieOpt.isPresent()) {
            logger.warn("Movie with ID {} not found", movieId);
            model.addAttribute("title", "Movie Not Found");
            model.addAttribute("message", "Movie with ID " + movieId + " was not found.");
            return "error";
        }
        
        Movie movie = movieOpt.get();
        model.addAttribute("movie", movie);
        model.addAttribute("movieIcon", MovieIconUtils.getMovieIcon(movie.getMovieName()));
        model.addAttribute("allReviews", reviewService.getReviewsForMovie(movie.getId()));
        
        return "movie-details";
    }

    /**
     * REST API endpoint for movie search - returns JSON response
     * Ahoy! This be the treasure map for other ships (applications) to find movies!
     * 
     * @param name Movie name to search for
     * @param id Specific movie ID to find  
     * @param genre Genre to filter by
     * @return JSON list of matching movies
     */
    @GetMapping("/movies/search")
    @ResponseBody
    public List<Movie> searchMoviesApi(@RequestParam(value = "name", required = false) String name,
                                      @RequestParam(value = "id", required = false) Long id,
                                      @RequestParam(value = "genre", required = false) String genre) {
        logger.info("Ahoy! API search request - name: {}, id: {}, genre: {}", name, id, genre);
        
        // Validate parameters
        if ((name == null || name.trim().isEmpty()) && 
            (id == null || id <= 0) && 
            (genre == null || genre.trim().isEmpty())) {
            logger.warn("Arrr! Empty search criteria provided to API");
            return movieService.getAllMovies(); // Return all movies if no criteria provided
        }
        
        try {
            List<Movie> results = movieService.searchMovies(name, id, genre);
            logger.info("API search completed! Found {} treasures", results.size());
            return results;
        } catch (Exception e) {
            logger.error("Scurvy bug in search API: {}", e.getMessage());
            throw new RuntimeException("Arrr! Something went wrong with the search, matey!", e);
        }
    }
}