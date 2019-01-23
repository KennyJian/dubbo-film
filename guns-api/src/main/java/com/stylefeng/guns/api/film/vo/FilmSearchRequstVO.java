package com.stylefeng.guns.api.film.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class FilmSearchRequstVO implements Serializable {

    String filmName;
    Integer nowPage=1;
    Integer pageSize=18;
}
