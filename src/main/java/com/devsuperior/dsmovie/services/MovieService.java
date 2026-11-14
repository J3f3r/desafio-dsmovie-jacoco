package com.devsuperior.dsmovie.services;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dsmovie.controllers.MovieController;
import com.devsuperior.dsmovie.controllers.MovieControllerV1;
import com.devsuperior.dsmovie.dto.MovieDTO;
import com.devsuperior.dsmovie.dto.MovieGenreDTO;
import com.devsuperior.dsmovie.dto.MovieV1DTO;
import com.devsuperior.dsmovie.entities.MovieEntity;
import com.devsuperior.dsmovie.repositories.MovieRepository;
import com.devsuperior.dsmovie.services.exceptions.DatabaseException;
import com.devsuperior.dsmovie.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

@Service
public class MovieService {

	//@Autowired
	private final MovieRepository repository;
	
	public MovieService(MovieRepository repository) {
		this.repository = repository;
	}

	@Transactional(readOnly = true)
	public Page<MovieDTO> findAll(String title, Pageable pageable) {
		Page<MovieEntity> result = repository.searchByTitle(title, pageable);
		return result.map(x -> new MovieDTO(x).add(linkTo(methodOn(MovieController.class).findAll(title, pageable)).withSelfRel())
					.add(linkTo(methodOn(MovieController.class).findById(x.getId())).withRel("Get movie by id")));
	}
	
	@Transactional(readOnly = true)
	public Page<MovieGenreDTO> findAllMovieGenre(String title, Pageable pageable) {
		Page<MovieEntity> result = repository.searchByTitle(title, pageable);
		return result.map(x -> new MovieGenreDTO(x).add(linkTo(methodOn(MovieController.class).findAll(title, pageable)).withSelfRel())
					.add(linkTo(methodOn(MovieController.class).findById(x.getId())).withRel("Get movie by id")));
	}// nao precisa de v1 pois nao sofre alteracao com hateoa como os demais

	@Transactional(readOnly = true)
	public MovieDTO findById(Long id) {
		MovieEntity result = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Recurso não encontrado"));
		MovieDTO dto = new MovieDTO(result)
				.add(linkTo(methodOn(MovieController.class).findById(id)).withSelfRel())
				.add(linkTo(methodOn(MovieController.class).findAll("", Pageable.unpaged())).withRel("All movies"))
				.add(linkTo(methodOn(MovieController.class).update(id, new MovieDTO())).withRel("Update movie"))
				.add(linkTo(methodOn(MovieController.class).delete(id)).withRel("Delete movie"));
		return dto;
	}
	
	@Transactional(readOnly = true)
	public MovieGenreDTO findByIdMovieGenre(Long id) {
		MovieEntity result = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Recurso não encontrado"));
		MovieGenreDTO dto = new MovieGenreDTO(result)
				.add(linkTo(methodOn(MovieControllerV1.class).findById(id)).withSelfRel())
				.add(linkTo(methodOn(MovieControllerV1.class).findAll("", Pageable.unpaged())).withRel("All movies"))
				.add(linkTo(methodOn(MovieControllerV1.class).update(id, new MovieGenreDTO())).withRel("Update movie"))
				.add(linkTo(methodOn(MovieControllerV1.class).delete(id)).withRel("Delete movie"));
		return dto;
	}
	
	@Transactional(readOnly = true)
	public MovieV1DTO findByIdV1(Long id) {// DTO puro para evitar conflito entre hateoas e jackson
		MovieEntity result = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Recurso não encontrado"));
		return new MovieV1DTO(result);
	}

	@Transactional
	public MovieDTO insert(MovieDTO dto) {
		MovieEntity entity = new MovieEntity();
		copyDtoToEntity(dto, entity);
		entity = repository.save(entity);
		return new MovieDTO(entity).add(linkTo(methodOn(MovieController.class).findById(entity.getId())).withRel("Get movie by id"));
	}
	
	@Transactional
	public MovieGenreDTO insertMovieGenre(MovieGenreDTO dto) {
		MovieEntity entity = new MovieEntity();
		copyDtoToEntity(dto, entity);
		entity = repository.save(entity);
		return new MovieGenreDTO(entity).add(linkTo(methodOn(MovieControllerV1.class).findById(entity.getId())).withRel("Get movie by id"));
	}
	
	@Transactional
	public MovieV1DTO insertV1(MovieV1DTO dto) {// DTO puro para evitar conflito entre hateoas e jackson
		MovieEntity entity = new MovieEntity();
		copyDtoToEntity(dto, entity);
		entity = repository.save(entity);
		return new MovieV1DTO(entity);
	}

	@Transactional
	public MovieDTO update(Long id, MovieDTO dto) {
		try {
			MovieEntity entity = repository.getReferenceById(id);
			copyDtoToEntity(dto, entity);
			entity = repository.save(entity);
			return new MovieDTO(entity).add(linkTo(methodOn(MovieController.class).findById(entity.getId())).withRel("Get movie by id"));
		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException("Recurso não encontrado");
		}
	}
	
	@Transactional
	public MovieGenreDTO updateMovieGenre(Long id, MovieGenreDTO dto) {
		try {
			MovieEntity entity = repository.getReferenceById(id);
			copyDtoToEntity(dto, entity);
			entity = repository.save(entity);
			return new MovieGenreDTO(entity).add(linkTo(methodOn(MovieControllerV1.class).findById(entity.getId())).withRel("Get movie by id"));
		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException("Recurso não encontrado");
		}
	}
	
	@Transactional
	public MovieV1DTO updateV1(Long id, MovieV1DTO dto) {// DTO puro para evitar conflito entre hateoas e jackson
		try {
			MovieEntity entity = repository.getReferenceById(id);
			copyDtoToEntity(dto, entity);
			entity = repository.save(entity);
			return new MovieV1DTO(entity);
		} catch (jakarta.persistence.EntityNotFoundException e) {
			throw new com.devsuperior.dsmovie.services.exceptions.ResourceNotFoundException("Recurso não encontrado");
		}
	}

	public void delete(Long id) {
		if (!repository.existsById(id))
			throw new ResourceNotFoundException("Recurso não encontrado");
		try {
			repository.deleteById(id);
			repository.flush(); // Força o Hibernate a executar o SQL imediatamente e disparar a violação de integridade no H2
		} catch (DataIntegrityViolationException e) {
			throw new DatabaseException("Falha de integridade referencial");
		}
	}

	private void copyDtoToEntity(MovieDTO dto, MovieEntity entity) {
		entity.setTitle(dto.getTitle());
		entity.setScore(dto.getScore());
		entity.setCount(dto.getCount());
		entity.setImage(dto.getImage());
	}
	
	private void copyDtoToEntity(MovieGenreDTO dto, MovieEntity entity) {
		entity.setTitle(dto.getTitle());
		entity.setScore(dto.getScore());
		entity.setCount(dto.getCount());
		entity.setImage(dto.getImage());
		
		// Associa o gênero utilizando o ID 1L ('Ação' do import.sql) para evitar nulos
		com.devsuperior.dsmovie.entities.Genre genre = new com.devsuperior.dsmovie.entities.Genre();
		genre.setId(1L);
		entity.setGenre(genre);
	}
	
	
	private void copyDtoToEntity(MovieV1DTO dto, MovieEntity entity) {
		entity.setTitle(dto.getTitle());
		entity.setScore(dto.getScore());
		entity.setCount(dto.getCount());
		entity.setImage(dto.getImage());
		
		// Cria e associa o objeto Genre garantindo que nunca fique nulo
		com.devsuperior.dsmovie.entities.Genre genre = new com.devsuperior.dsmovie.entities.Genre();
		
		// Se o DTO trouxer um genreId válido, usa ele; caso contrário, assume o ID 1 ('Ação') do import.sql
		Long genreId = (dto.getGenreId() != null) ? dto.getGenreId() : 1L;
		
		genre.setId(genreId);
		entity.setGenre(genre);
	}
}