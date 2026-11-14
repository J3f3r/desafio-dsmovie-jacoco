package com.devsuperior.dsmovie.services;

import static org.mockito.ArgumentMatchers.any;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.devsuperior.dsmovie.dto.MovieDTO;
import com.devsuperior.dsmovie.dto.MovieGenreDTO;
import com.devsuperior.dsmovie.dto.MovieV1DTO;
import com.devsuperior.dsmovie.entities.Genre;
import com.devsuperior.dsmovie.entities.MovieEntity;
import com.devsuperior.dsmovie.repositories.MovieRepository;
import com.devsuperior.dsmovie.services.exceptions.DatabaseException;
import com.devsuperior.dsmovie.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dsmovie.tests.MovieFactory;

import jakarta.persistence.EntityNotFoundException;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)//todos os testes dessa classe são obrigados a utilizar todos os stubs declarados
public class MovieServiceTests {

	@InjectMocks
	private MovieService service;

	@Mock
	private MovieRepository repository;

	private Long existingMovieId, nonExistingMovieId, dependentMovieId;
	private MovieEntity movie;
	private String movieTitle;
	private PageImpl<MovieEntity> page;
	private MovieDTO movieDTO;
	private MovieGenreDTO movieGenreDTO;

	@BeforeEach
	void setUp() throws Exception {
		existingMovieId = 1L;
		nonExistingMovieId = 2L;
		dependentMovieId = 3L;

		movieTitle = "Test Movie";
		movie = MovieFactory.createMovieEntity();
		movie.setTitle(movieTitle);
		
		Genre genre = new Genre();//Cria, configura e associa o Genre à entidade MovieEntity para evitar NullPointerException
		genre.setId(1L);
		genre.setName("Ação");
		movie.setGenre(genre);
		
		page = new PageImpl<>(List.of(movie));
		movieDTO = new MovieDTO(movie);
		movieGenreDTO = new MovieGenreDTO(movie);// Instanciando os DTOs da v1 para suportar os testes de cobertura do JaCoCo

		Mockito.when(repository.findById(existingMovieId)).thenReturn(Optional.of(movie));
		Mockito.when(repository.findById(nonExistingMovieId)).thenReturn(Optional.empty());

		Mockito.when(repository.searchByTitle(Mockito.anyString(), any(Pageable.class))).thenReturn(page);

		Mockito.when(repository.save(any(MovieEntity.class))).thenReturn(movie);

		Mockito.when(repository.getReferenceById(existingMovieId)).thenReturn(movie);
		Mockito.when(repository.getReferenceById(nonExistingMovieId)).thenThrow(EntityNotFoundException.class);

		Mockito.when(repository.existsById(existingMovieId)).thenReturn(true);
		Mockito.when(repository.existsById(nonExistingMovieId)).thenReturn(false);
		Mockito.when(repository.existsById(dependentMovieId)).thenReturn(true);

		Mockito.doNothing().when(repository).deleteById(existingMovieId);
		Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentMovieId);
	}

	@Test
	public void findAllShouldReturnPagedMovieDTO() {
		Pageable pageable = PageRequest.of(0, 12);
		Page<MovieDTO> result = service.findAll(movieTitle, pageable);

		Assertions.assertNotNull(result);
		Assertions.assertEquals(1, result.getSize());
		Assertions.assertEquals(movieTitle, result.iterator().next().getTitle());
	}

	@Test
	public void findByIdShouldReturnMovieDTOWhenIdExists() {
		MovieDTO result = service.findById(existingMovieId);

		Assertions.assertNotNull(result);
		Assertions.assertEquals(existingMovieId, result.getId());
		Assertions.assertEquals(movieTitle, result.getTitle());
	}

	@Test
	public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.findById(nonExistingMovieId);
		});
	}
	
	@Test// V1 dto puro
	public void findByIdV1ShouldReturnMovieV1DTOWhenIdExists() {
		MovieV1DTO result = service.findByIdV1(existingMovieId);
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(movie.getId(), result.getId());
		Assertions.assertEquals(movie.getTitle(), result.getTitle());
	}
	
	@Test
	public void findByIdMovieGenreShouldReturnMovieGenreDTOWhenIdExists() {
		MovieGenreDTO result = service.findByIdMovieGenre(existingMovieId);
		Assertions.assertNotNull(result);
		Assertions.assertEquals(existingMovieId, result.getId());
	}

	@Test
	public void insertShouldReturnMovieDTO() {
		MovieDTO result = service.insert(movieDTO);

		Assertions.assertNotNull(result);
		Assertions.assertEquals(movie.getId(), result.getId());
		Mockito.verify(repository, Mockito.times(1)).save(any());//Verifica se o save do repository foi invocado 1 vez
	}
	
	@Test
	public void insertV1ShouldReturnMovieV1DTOWhenValidData() {
		MovieV1DTO v1Dto = MovieFactory.createMovieV1DTO();
		MovieV1DTO result = service.insertV1(v1Dto);
		
		Assertions.assertNotNull(result);
		Mockito.verify(repository, Mockito.times(1)).save(any());
	}
	
	@Test
	public void insertMovieGenreShouldReturnMovieGenreDTOWhenValidData() {
		MovieGenreDTO result = service.insertMovieGenre(movieGenreDTO);
		Assertions.assertNotNull(result);
		Mockito.verify(repository, Mockito.times(1)).save(any(MovieEntity.class));
	}

	@Test
	public void updateShouldReturnMovieDTOWhenIdExists() {
		MovieDTO result = service.update(existingMovieId, movieDTO);

		Assertions.assertNotNull(result);
		Assertions.assertEquals(existingMovieId, result.getId());
		Assertions.assertEquals(movieDTO.getTitle(), result.getTitle());
		Mockito.verify(repository, Mockito.times(1)).save(any());
	}

	@Test
	public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.update(nonExistingMovieId, movieDTO);
		});
	}

	@Test
	public void deleteShouldDoNothingWhenIdExists() {
		Assertions.assertDoesNotThrow(() -> {
			service.delete(existingMovieId);
		});
		
		// Verifica se o método deleteById do repository foi chamado exatamente 1 vez com o existingId
		Mockito.verify(repository, Mockito.times(1)).deleteById(existingMovieId);
	}

	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.delete(nonExistingMovieId);
		});
	}

	@Test
	public void deleteShouldThrowDatabaseExceptionWhenDependentId() {
		Assertions.assertThrows(DatabaseException.class, () -> {
			service.delete(dependentMovieId);
		});
	}
	
	//testes restantes para a cobertura total JaCoCo
	
	@Test
	public void findAllMovieGenreShouldReturnPagedMovieGenreDTO() {
		Pageable pageable = PageRequest.of(0, 12);
		Page<MovieGenreDTO> result = service.findAllMovieGenre(movieTitle, pageable);

		Assertions.assertNotNull(result);
		Assertions.assertEquals(1, result.getSize());
		Assertions.assertEquals(movieTitle, result.iterator().next().getTitle());
	}

	@Test
	public void updateMovieGenreShouldReturnMovieGenreDTOWhenIdExists() {
		MovieGenreDTO dto = new MovieGenreDTO(movie);
		MovieGenreDTO result = service.updateMovieGenre(existingMovieId, dto);

		Assertions.assertNotNull(result);
		Assertions.assertEquals(existingMovieId, result.getId());
		Assertions.assertEquals(dto.getTitle(), result.getTitle());
		Mockito.verify(repository, Mockito.times(1)).save(any());
	}

	@Test
	public void updateMovieGenreShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
		MovieGenreDTO dto = new MovieGenreDTO(movie);
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.updateMovieGenre(nonExistingMovieId, dto);
		});
	}

	@Test
	public void updateV1ShouldReturnMovieV1DTOWhenIdExists() {
		MovieV1DTO v1Dto = MovieFactory.createMovieV1DTO();
		MovieV1DTO result = service.updateV1(existingMovieId, v1Dto);

		Assertions.assertNotNull(result);
		Assertions.assertEquals(existingMovieId, result.getId());
		Assertions.assertEquals(v1Dto.getTitle(), result.getTitle());
		Mockito.verify(repository, Mockito.times(1)).save(any());
	}

	@Test
	public void updateV1ShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
		MovieV1DTO v1Dto = MovieFactory.createMovieV1DTO();
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.updateV1(nonExistingMovieId, v1Dto);
		});
	}

	@Test
	public void insertV1ShouldReturnMovieV1DTOWhenGenreIdIsNull() {
		MovieV1DTO v1Dto = MovieFactory.createMovieV1DTO();
		v1Dto.setGenreId(null); // Força a cobertura do branch false no copyDtoToEntity
		
		MovieV1DTO result = service.insertV1(v1Dto);
		
		Assertions.assertNotNull(result);
		Mockito.verify(repository, Mockito.times(1)).save(any());
	}
}