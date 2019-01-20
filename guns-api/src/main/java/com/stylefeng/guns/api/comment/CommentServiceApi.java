package com.stylefeng.guns.api.comment;


import com.stylefeng.guns.api.comment.vo.CommentVO;

import java.util.List;

public interface CommentServiceApi {

    boolean commitComment(Integer filmId,String comment,Integer userId);

    List<CommentVO> getComment(Integer filmId);
}
