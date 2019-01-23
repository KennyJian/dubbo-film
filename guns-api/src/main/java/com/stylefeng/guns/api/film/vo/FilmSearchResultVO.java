package com.stylefeng.guns.api.film.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class FilmSearchResultVO implements Serializable {

    private String filmId;
    private String filmName;
    private String filmScore;
    private String filmCats;
    private String filmTime;

}
