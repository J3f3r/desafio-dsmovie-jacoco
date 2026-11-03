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
	}

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

	public void delete(Long id) {
		if (!repository.existsById(id))
			throw new ResourceNotFoundException("Recurso não encontrado");
		try {
			repository.deleteById(id);
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
		// Aqui você também fará a atribuição do gênero à entidade, conforme o mapeamento do seu projeto:
		// entity.setGenre(...);
	}
}