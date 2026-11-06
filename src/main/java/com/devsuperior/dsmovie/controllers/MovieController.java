package com.devsuperior.dsmovie.controllers;

import java.net.URI;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.devsuperior.dsmovie.dto.MovieDTO;
import com.devsuperior.dsmovie.dto.MovieGenreDTO;
import com.devsuperior.dsmovie.dto.MovieV1DTO;
import com.devsuperior.dsmovie.services.MovieService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/movies")
@Tag(name = "Movies", description = "Controllers for Movies")
public class MovieController {

	private final MovieService service;

	MovieController(MovieService service) {
		this.service = service;
	}

	@Operation(
			description = "Get all movies",
			summary = "Get all movies",
			responses = {
					@ApiResponse(description = "OK", responseCode = "200")
			}
	)
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public Page<MovieDTO> findAll(
			@RequestParam(value="title", defaultValue = "") String title, 
			Pageable pageable) {
		return service.findAll(title, pageable);
	}
	
	@Operation(
			description = "Get all movies",
			summary = "Get all movies",
			responses = {
					@ApiResponse(description = "OK", responseCode = "200")
			}
	)
	@GetMapping(produces = "application/vnd.devsuperior.dsmovie-v1+json")
	public Page<MovieGenreDTO> findAllV1(
			@RequestParam(value="title", defaultValue = "") String title, 
			Pageable pageable) {
		return service.findAllMovieGenre(title, pageable);
	}// versionamento vie Media Type para retornar genre

	@Operation(
			description = "Get movie by id",
			summary = "Get movie by id",
			responses = {
					@ApiResponse(description = "OK", responseCode = "200"),
					@ApiResponse(description = "Not Found", responseCode = "404")
			}
	)
	@GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public MovieDTO findById(@PathVariable Long id) {
		return service.findById(id);
	}
	
	@Operation(
			description = "Get movie by id (v1 - Pure DTO)",
			summary = "Get movie by id (v1)",
			responses = {
					@ApiResponse(description = "OK", responseCode = "200"),
					@ApiResponse(description = "Not Found", responseCode = "404")
			}
	)
	@GetMapping(value = "/{id}", produces = "application/vnd.devsuperior.dsmovie-v1+json")
	public ResponseEntity<MovieV1DTO> findByIdV1(@PathVariable Long id) {
		MovieV1DTO dto = service.findByIdV1(id);
		return ResponseEntity.ok().body(dto);
	}

	@Operation(
			description = "Create a new movie",
			summary = "Create a new movie",
			responses = {
					@ApiResponse(description = "Created", responseCode = "201"),
					@ApiResponse(description = "Bad Request", responseCode = "400"),
					@ApiResponse(description = "Unauthorized", responseCode = "401"),
					@ApiResponse(description = "Forbidden", responseCode = "403"),
					@ApiResponse(description = "Unprocessable Entity", responseCode = "422")
			}
	)
	@SecurityRequirement(name = "bearerAuth")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MovieDTO> insert(@Valid @RequestBody MovieDTO dto) {
		dto = service.insert(dto);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(dto.getId()).toUri();
		return ResponseEntity.created(uri).body(dto);
	}
	
	@Operation(
			description = "Create a new movie with genre (v1 - Pure DTO)",
			summary = "Create a new movie with genre (v1)"
	)
	@SecurityRequirement(name = "bearerAuth")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PostMapping(
			consumes = "application/vnd.devsuperior.dsmovie-v1+json",
			produces = "application/vnd.devsuperior.dsmovie-v1+json"
	)
	public ResponseEntity<MovieV1DTO> insertV1(@Valid @RequestBody MovieV1DTO dto) {
		dto = service.insertV1(dto);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(dto.getId()).toUri();
		return ResponseEntity.created(uri).body(dto);
	}

	@Operation(
			description = "Update a movie",
			summary = "Update a movie",
			responses = {
					@ApiResponse(description = "Created", responseCode = "201"),
					@ApiResponse(description = "Bad Request", responseCode = "400"),
					@ApiResponse(description = "Unauthorized", responseCode = "401"),
					@ApiResponse(description = "Forbidden", responseCode = "403"),
					@ApiResponse(description = "Not Found", responseCode = "404"),
					@ApiResponse(description = "Unprocessable Entity", responseCode = "422")
			}
	)
	@SecurityRequirement(name = "bearerAuth")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MovieDTO> update(@PathVariable Long id, @Valid @RequestBody MovieDTO dto) {
		dto = service.update(id, dto);
		return ResponseEntity.ok().body(dto);
	}
	
	@Operation(
			description = "Update movie by id (v1 - Pure DTO)",
			summary = "Update movie by id (v1)",
			responses = {
					@ApiResponse(description = "OK", responseCode = "200"),
					@ApiResponse(description = "Not Found", responseCode = "404"),
					@ApiResponse(description = "Unprocessable Entity", responseCode = "422")
			}
	)
	@SecurityRequirement(name = "bearerAuth")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PutMapping(
			value = "/{id}",
			consumes = "application/vnd.devsuperior.dsmovie-v1+json",
			produces = "application/vnd.devsuperior.dsmovie-v1+json"
	)
	public ResponseEntity<MovieV1DTO> updateV1(@PathVariable Long id, @Valid @RequestBody MovieV1DTO dto) {
		dto = service.updateV1(id, dto);
		return ResponseEntity.ok().body(dto);
	}

	@Operation(
			description = "Delete a movie",
			summary = "Delete a movie",
			responses = {
					@ApiResponse(description = "Success", responseCode = "204"),
					@ApiResponse(description = "Bad Request", responseCode = "400"),
					@ApiResponse(description = "Unauthorized", responseCode = "401"),
					@ApiResponse(description = "Forbidden", responseCode = "403"),
					@ApiResponse(description = "Not Found", responseCode = "404"),
					@ApiResponse(description = "Unprocessable Entity", responseCode = "422")
			}
	)
	@SecurityRequirement(name = "bearerAuth")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MovieDTO> delete(@PathVariable Long id) {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}
}
