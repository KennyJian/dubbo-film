package com.stylefeng.guns.rest.common.persistence.dao;

import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.comment.vo.CommentVO;
import com.stylefeng.guns.rest.common.persistence.model.CommentCommentT;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author kenny
 * @since 2019-01-20
 */
public interface CommentCommentTMapper extends BaseMapper<CommentCommentT> {

    List<CommentVO> getCommentsByFilmId(@Param("filmId")Integer filmId, Page page);
}
