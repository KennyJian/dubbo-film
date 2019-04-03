package com.stylefeng.guns.rest.common.persistence.dao;

import com.stylefeng.guns.api.film.vo.ActorVO;
import com.stylefeng.guns.rest.common.persistence.model.KennyActorT;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 演员表 Mapper 接口
 * </p>
 *
 * @author kenny
 * @since 2018-12-22
 */
public interface KennyActorTMapper extends BaseMapper<KennyActorT> {

    List<ActorVO> getActors(@Param("filmId") String filmId);
}
