package com.devsuperior.dsmovie.tests;

import com.devsuperior.dsmovie.dto.MovieDTO;
import com.devsuperior.dsmovie.dto.MovieV1DTO;
import com.devsuperior.dsmovie.entities.MovieEntity;

public class MovieFactory {

	public static MovieEntity createMovieEntity() {
		MovieEntity movie = new MovieEntity(1L, "Test Movie", 0.0, 0,
				"https://www.themoviedb.org/t/p/w533_and_h300_bestv2/jBJWaqoSCiARWtfV0GlqHrcdidd.jpg");
		return movie;
	}

	public static MovieEntity createCustomMovie(Long id, String title) {
		// Sobrecarga para quando voce precisar testar IDs inexistentes ou titulos
		// especificos
		MovieEntity movie = createMovieEntity();
		movie.setId(id);
		movie.setTitle(title);
		return movie;
	}

	public static MovieDTO createMovieDTO() {
		MovieEntity movie = createMovieEntity();
		return new MovieDTO(movie);
	}

	// Novo método para testes da versão v1 (leitura e escrita)
	public static MovieV1DTO createMovieV1DTO() {
		return new MovieV1DTO(1L, "Test Movie V1", 4.5, 10,
				"https://www.themoviedb.org/t/p/w533_and_h300_bestv2/jBJWaqoSCiARWtfV0GlqHrcdidd.jpg", "Action", 1L);
	}
}
