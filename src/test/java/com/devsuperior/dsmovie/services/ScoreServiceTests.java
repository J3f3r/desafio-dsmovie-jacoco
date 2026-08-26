package com.devsuperior.dsmovie.services;

import static org.mockito.ArgumentMatchers.any;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.devsuperior.dsmovie.dto.MovieDTO;
import com.devsuperior.dsmovie.dto.ScoreDTO;
import com.devsuperior.dsmovie.entities.MovieEntity;
import com.devsuperior.dsmovie.entities.ScoreEntity;
import com.devsuperior.dsmovie.entities.UserEntity;
import com.devsuperior.dsmovie.repositories.MovieRepository;
import com.devsuperior.dsmovie.repositories.ScoreRepository;
import com.devsuperior.dsmovie.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dsmovie.tests.MovieFactory;
import com.devsuperior.dsmovie.tests.ScoreFactory;
import com.devsuperior.dsmovie.tests.UserFactory;

@ExtendWith(SpringExtension.class)
public class ScoreServiceTests {
	
	@InjectMocks
	private ScoreService service;
	
	@Mock
	private ScoreRepository repository;
	
	@Mock
	private MovieRepository movieRepository;
	
	@Mock
	private UserService userService;
	
	private Long existingMovieId, nonExistingMovieId;
	private MovieEntity movie;
	private UserEntity user;
	private ScoreEntity score;
	private ScoreDTO scoreDTO;
	
	@BeforeEach
	void setUp() throws Exception{
		MockitoAnnotations.openMocks(this);
		
		existingMovieId = 1L;
		nonExistingMovieId = 2L;
		
		movie = MovieFactory.createMovieEntity();
		user = UserFactory.createUserEntity();
		score = ScoreFactory.createScoreEntity();
		
		// Adiciona a relaçao entre o filme e a pontuacao para evitar divisao por zero na media
		score.setMovie(movie);
		movie.getScores().add(score);
		
		// DTO preparado para a chamada do servico
		scoreDTO = new ScoreDTO(existingMovieId, 4.5);
		
		Mockito.when(userService.authenticated()).thenReturn(user);
		
		Mockito.when(movieRepository.findById(existingMovieId)).thenReturn(Optional.of(movie));
		Mockito.when(movieRepository.findById(nonExistingMovieId)).thenReturn(Optional.empty());
		
		Mockito.when(repository.saveAndFlush(any())).thenReturn(score);
		Mockito.when(movieRepository.save(any())).thenReturn(movie);
	}
	
	@Test
	public void saveScoreShouldReturnMovieDTO() {
		
		MovieDTO result = service.saveScore(scoreDTO);
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(movie.getId(), result.getId());
		Assertions.assertEquals(movie.getTitle(), result.getTitle());
	}
	
	@Test
	public void saveScoreShouldThrowResourceNotFoundExceptionWhenNonExistingMovieId() {
		
		// Prepara um DTO contendo o ID inexistente
		ScoreDTO nonExistingScoreDTO = new ScoreDTO(nonExistingMovieId, 4.5);
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			
			service.saveScore(nonExistingScoreDTO);
		});
	}
}
