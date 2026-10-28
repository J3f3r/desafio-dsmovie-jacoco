package com.devsuperior.dsmovie.dto;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import com.devsuperior.dsmovie.entities.ScoreEntity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public class ScoreDTO {
	
	private static final DecimalFormat df = new DecimalFormat("#.##", new DecimalFormatSymbols(Locale.US));

	@Schema(description = "Movie ID", example = "1")
	@NotNull(message = "Required field")
	private Long movieId;

	@Schema(description = "Movie score given by the user (from 0 to 5)", example = "4.5")
	@PositiveOrZero(message = "Score should be greater than or equal to zero")
	@Max(value = 5, message = "Score should not be greater than five")
	private Double score;

	public ScoreDTO() {

	}

	public ScoreDTO(Long movieId, Double score) {
		this.movieId = movieId;
		this.score = Double.valueOf(df.format(score));
	}
	
	public ScoreDTO(ScoreEntity score) {
		this.movieId = score.getId().getMovie().getId();
		this.score = Double.valueOf(df.format(score.getValue()));
	}
	
	public Long getMovieId() {
		return movieId;
	}

	public Double getScore() {
		return score;
	}
}
