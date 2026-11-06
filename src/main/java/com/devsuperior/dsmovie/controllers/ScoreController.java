package com.devsuperior.dsmovie.controllers;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devsuperior.dsmovie.dto.MovieDTO;
import com.devsuperior.dsmovie.dto.MovieV1DTO;
import com.devsuperior.dsmovie.dto.ScoreDTO;
import com.devsuperior.dsmovie.services.ScoreService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/scores")
@Tag(name = "Score", description = "Controllers for Score movie")
public class ScoreController {
	
	private final ScoreService service;

	ScoreController(ScoreService service) {
		this.service = service;
	}
	
	@Operation(
			description = "Endpoint for save score",
			summary = "Save score",
			responses = {
					@ApiResponse(description = "Created", responseCode = "201"),
					@ApiResponse(description = "Bad Request", responseCode = "400"),
					@ApiResponse(description = "Unauthorized", responseCode = "401"),
					@ApiResponse(description = "Forbidden", responseCode = "403"),
					@ApiResponse(description = "Not Found", responseCode = "404"),
					@ApiResponse(description = "Unprocessable Entity", responseCode = "422")
			}
	)
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CLIENT')")
	@PutMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MovieDTO> saveScore(@Valid @RequestBody ScoreDTO dto) {
		MovieDTO movieDTO = service.saveScore(dto);
		return ResponseEntity.ok().body(movieDTO);
	}
	
	@Operation(
			description = "Save score (v1 - Pure DTO)",
			summary = "Save score (v1)",
			responses = {
					@ApiResponse(description = "OK", responseCode = "200"),
					@ApiResponse(description = "Bad Request", responseCode = "400"),
					@ApiResponse(description = "Unauthorized", responseCode = "401"),
					@ApiResponse(description = "Forbidden", responseCode = "403"),
					@ApiResponse(description = "Not Found", responseCode = "404"),
					@ApiResponse(description = "Unprocessable Entity", responseCode = "422")
			}
	)
	@SecurityRequirement(name = "bearerAuth")
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CLIENT')")
	@PutMapping(
			consumes = "application/vnd.devsuperior.dsmovie-v1+json",
			produces = "application/vnd.devsuperior.dsmovie-v1+json"
	)
	public ResponseEntity<MovieV1DTO> saveScoreV1(@Valid @RequestBody ScoreDTO dto) {
		MovieV1DTO movieDTO = service.saveScoreV1(dto);
		return ResponseEntity.ok().body(movieDTO);
	}
}
