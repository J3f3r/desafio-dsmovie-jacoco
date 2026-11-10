package com.devsuperior.dsmovie.controllers;

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

import com.devsuperior.dsmovie.dto.ScoreDTO;
import com.devsuperior.dsmovie.tests.TokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class ScoreControllerIT {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private TokenUtil tokenUtil;

	private ObjectMapper objectMapper = new ObjectMapper();

	private String token;
	private ScoreDTO scoreDTO;
	private Long existingMovieId;
	private Long nonExistingMovieId;

	@BeforeEach
	void setUp() throws Exception {
		existingMovieId = 1L; // ID de um filme existente no import.sql
		nonExistingMovieId = 1000L; // ID inexistente
		
		scoreDTO = new ScoreDTO(existingMovieId, 4.5);
		
		// Gera o token de acesso utilizando o componente dinâmico injetado
		token = tokenUtil.obtainAccessToken(mockMvc, "maria@gmail.com", "123456");
	}

	@Test
	public void saveScoreShouldReturnMovieDTOWhenValidData() throws Exception {
		String jsonBody = objectMapper.writeValueAsString(scoreDTO);

		ResultActions result = mockMvc.perform(put("/scores")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonBody)
				.accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.id").value(existingMovieId));
		result.andExpect(jsonPath("$.score").exists());
	}

	@Test
	public void saveScoreShouldReturnNotFoundWhenMovieDoesNotExist() throws Exception {
		ScoreDTO nonExistingScoreDTO = new ScoreDTO(nonExistingMovieId, 4.5);
		String jsonBody = objectMapper.writeValueAsString(nonExistingScoreDTO);

		ResultActions result = mockMvc.perform(put("/scores")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonBody)
				.accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isNotFound());
	}
}