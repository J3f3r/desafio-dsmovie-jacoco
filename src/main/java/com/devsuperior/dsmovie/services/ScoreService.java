package com.devsuperior.dsmovie.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dsmovie.dto.MovieDTO;
import com.devsuperior.dsmovie.dto.MovieV1DTO;
import com.devsuperior.dsmovie.dto.ScoreDTO;
import com.devsuperior.dsmovie.entities.MovieEntity;
import com.devsuperior.dsmovie.entities.ScoreEntity;
import com.devsuperior.dsmovie.entities.UserEntity;
import com.devsuperior.dsmovie.repositories.MovieRepository;
import com.devsuperior.dsmovie.repositories.ScoreRepository;
import com.devsuperior.dsmovie.services.exceptions.ResourceNotFoundException;

@Service
public class ScoreService {

	//@Autowired
	private final UserService userService;
	
	//@Autowired
	private final MovieRepository movieRepository;
	
	//@Autowired
	private final ScoreRepository scoreRepository;
	
	public ScoreService(UserService userService, MovieRepository movieRepository, ScoreRepository scoreRepository) {
		this.userService = userService;
		this.movieRepository = movieRepository;
		this.scoreRepository = scoreRepository;
	}
	
	@Transactional
	public MovieDTO saveScore(ScoreDTO dto) {
		
		UserEntity user = userService.authenticated();
		
		MovieEntity movie = movieRepository.findById(dto.getMovieId())
				.orElseThrow(() -> new ResourceNotFoundException("Recurso não encontrado"));		
		
		ScoreEntity score = new ScoreEntity();
		score.setMovie(movie);
		score.setUser(user);
		score.setValue(dto.getScore());
		
		// Grava e forca a sincronizacao imediata (flush) dessa nova nota na tabela de pontuacoes no banco de dados
		score = scoreRepository.saveAndFlush(score);
		
		
		// Percorre todas as notas cadastradas na lista de avaliações desse filme (movie.getScores()), soma todas as notas e divide pela quantidade total de avaliacoes, achando a media (avg
		double sum = 0.0;
		for (ScoreEntity s : movie.getScores()) {
			sum = sum + s.getValue();
		}
			
		double avg = sum / movie.getScores().size();
		
		movie.setScore(avg);
		movie.setCount(movie.getScores().size());
		
		movie = movieRepository.save(movie);
		
		return new MovieDTO(movie);
	}
	
	@Transactional
	public MovieV1DTO saveScoreV1(ScoreDTO dto) {
		UserEntity user = userService.authenticated();
		
		MovieEntity movie = movieRepository.findById(dto.getMovieId())
				.orElseThrow(() -> new ResourceNotFoundException("Recurso não encontrado"));		
		
		ScoreEntity score = new ScoreEntity();
		score.setMovie(movie);
		score.setUser(user);
		score.setValue(dto.getScore());
		
		score = scoreRepository.saveAndFlush(score);
		
		double sum = 0.0;
		for (ScoreEntity s : movie.getScores()) {
			sum = sum + s.getValue();
		}
			
		double avg = sum / movie.getScores().size();
		
		movie.setScore(avg);
		movie.setCount(movie.getScores().size());
		
		movie = movieRepository.save(movie);
		
		return new MovieV1DTO(movie); // Retorna o DTO puro da versão v1
	}
}
