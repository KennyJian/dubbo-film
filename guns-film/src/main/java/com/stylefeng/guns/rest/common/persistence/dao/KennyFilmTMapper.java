package com.stylefeng.guns.rest.common.persistence.dao;

import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.film.vo.FilmDetailVO;
import com.stylefeng.guns.api.film.vo.FilmSearchResultVO;
import com.stylefeng.guns.rest.common.persistence.model.KennyFilmT;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 影片主表 Mapper 接口
 * </p>
 *
 * @author kenny
 * @since 2018-12-22
 */
public interface KennyFilmTMapper extends BaseMapper<KennyFilmT> {

    FilmDetailVO getFilmDetailByName(@Param("filmName")String fileName);

    FilmDetailVO getFilmDetailById(@Param("uuid")String uuid);

    List<FilmSearchResultVO> searchResult(@Param("filmName")String filmName, Page<FilmSearchResultVO> page);
}
