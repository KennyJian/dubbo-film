package com.stylefeng.guns.api.film.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class BannerVO implements Serializable {

    private Integer uuid;

    private String bannerAddress;

    private String bannerUrl;

}
