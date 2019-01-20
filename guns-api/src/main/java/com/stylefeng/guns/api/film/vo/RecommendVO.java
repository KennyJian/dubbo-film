package com.stylefeng.guns.api.film.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class RecommendVO implements Serializable {

    private Integer uuid;
    private String filmName;
    private String imgAddress;
}
