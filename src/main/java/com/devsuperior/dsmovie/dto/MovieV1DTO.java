package com.devsuperior.dsmovie.dto;

import com.devsuperior.dsmovie.entities.MovieEntity;

public class MovieV1DTO {

    private Long id;
    private String title;
    private Double score;
    private Integer count;
    private String image;
    private String genre;
    private Long genreId; // Adicionado para facilitar o mapeamento de escrita

    public MovieV1DTO() {
    }

    public MovieV1DTO(Long id, String title, Double score, Integer count, String image, String genre, Long genreId) {
        this.id = id;
        this.title = title;
        this.score = score;
        this.count = count;
        this.image = image;
        this.genre = genre;
        this.genreId = genreId;
    }

    public MovieV1DTO(MovieEntity entity) {
        id = entity.getId();
        title = entity.getTitle();
        score = entity.getScore();
        count = entity.getCount();
        image = entity.getImage();
        if (entity.getGenre() != null) {
            genre = entity.getGenre().getName();
            genreId = entity.getGenre().getId();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public Long getGenreId() {
        return genreId;
    }

    public void setGenreId(Long genreId) {
        this.genreId = genreId;
    }
}