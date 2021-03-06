package com.stylefeng.guns.rest.modular.film.vo;

import lombok.Data;
import com.stylefeng.guns.api.film.vo.BannerVO;
import com.stylefeng.guns.api.film.vo.FilmInfo;
import com.stylefeng.guns.api.film.vo.FilmVO;
import java.io.Serializable;
import java.util.List;

@Data
public class FilmIndexVO implements Serializable {

    private List<BannerVO> banners;

    private FilmVO hotFilms;

    private FilmVO soonFilms;

    private List<FilmInfo> boxRanking;

    private List<FilmInfo> expectRanking;

    private List<FilmInfo> top100;


}
