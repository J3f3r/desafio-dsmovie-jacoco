package com.devsuperior.dsmovie.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dsmovie.dto.MovieV1DTO;
import com.devsuperior.dsmovie.tests.TokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class MovieControllerV1IT {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private TokenUtil tokenUtil;

	private ObjectMapper objectMapper = new ObjectMapper();

	private Long existingId;
	private Long nonExistingId;
	private Long dependentId;
	private Integer totalMovies;
	private MovieV1DTO movieV1DTO;
	private String token;

	@BeforeEach
	void setUp() throws Exception {
		existingId = 3L;
		nonExistingId = 1000L;
		dependentId = 1L;
		totalMovies = 29;
		
		// Inicializa o MovieV1DTO com dados válidos e gênero compatível com o import.sql (ID 1 = 'Ação')
		movieV1DTO = new MovieV1DTO();
		movieV1DTO.setTitle("Test Movie");
		movieV1DTO.setScore(4.5);
		movieV1DTO.setCount(10);
		movieV1DTO.setImage("https://www.url.com/image.png");
		movieV1DTO.setGenreId(1L);
		movieV1DTO.setGenre("Ação"); 
		
		// Autenticação OAuth2 dinâmica utilizando a usuária admin
		token = tokenUtil.obtainAccessToken(mockMvc, "maria@gmail.com", "123456");
	}

	@Test
	public void findAllShouldReturnPagedMoviesWhenParamsAreProvided() throws Exception {
		ResultActions result = mockMvc.perform(get("/v1/movies")
				.param("page", "0")
				.param("size", "12")
				.accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.content").exists());
		result.andExpect(jsonPath("$.totalElements").value(totalMovies));
	}

	@Test
	public void findByIdShouldReturnMovieWhenIdExists() throws Exception {
		ResultActions result = mockMvc.perform(get("/v1/movies/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.id").value(existingId));
	}

	@Test
	public void findByIdShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
		ResultActions result = mockMvc.perform(get("/v1/movies/{id}", nonExistingId)
				.accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isNotFound());
	}

	@Test
	public void insertShouldReturnCreatedMovieDTOWhenValidData() throws Exception {
		String jsonBody = objectMapper.writeValueAsString(movieV1DTO);

		ResultActions result = mockMvc.perform(post("/v1/movies")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonBody)
				.accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isCreated());
		result.andExpect(jsonPath("$.id").exists());
		result.andExpect(jsonPath("$.title").value(movieV1DTO.getTitle()));
	}

	@Test
	public void updateShouldReturnMovieDTOWhenIdExists() throws Exception {
		String jsonBody = objectMapper.writeValueAsString(movieV1DTO);

		ResultActions result = mockMvc.perform(put("/v1/movies/{id}", existingId)
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonBody)
				.accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.id").value(existingId));
		result.andExpect(jsonPath("$.title").value(movieV1DTO.getTitle()));
	}

	@Test
	public void updateShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
		String jsonBody = objectMapper.writeValueAsString(movieV1DTO);

		ResultActions result = mockMvc.perform(put("/v1/movies/{id}", nonExistingId)
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonBody)
				.accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isNotFound());
	}

	@Test
	public void deleteShouldReturnNoContentWhenIdExists() throws Exception {
		ResultActions result = mockMvc.perform(delete("/v1/movies/{id}", existingId)
				.header("Authorization", "Bearer " + token));

		result.andExpect(status().isNoContent());
	}

	@Test
	public void deleteShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
		ResultActions result = mockMvc.perform(delete("/v1/movies/{id}", nonExistingId)
				.header("Authorization", "Bearer " + token));

		result.andExpect(status().isNotFound());
	}

	@Test
	public void deleteShouldReturnBadRequestWhenDependentId() throws Exception {
		ResultActions result = mockMvc.perform(delete("/v1/movies/{id}", dependentId)
				.header("Authorization", "Bearer " + token));

		result.andExpect(status().isBadRequest());
	}
}