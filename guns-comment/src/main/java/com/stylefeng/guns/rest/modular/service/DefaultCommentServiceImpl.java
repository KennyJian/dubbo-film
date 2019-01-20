package com.stylefeng.guns.rest.modular.service;


import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.comment.CommentServiceApi;
import com.stylefeng.guns.api.comment.vo.CommentVO;
import com.stylefeng.guns.rest.common.persistence.dao.CommentCommentTMapper;
import com.stylefeng.guns.rest.common.persistence.dao.CommentLikeTMapper;
import com.stylefeng.guns.rest.common.persistence.model.CommentCommentT;
import com.stylefeng.guns.rest.common.persistence.model.CommentLikeT;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Service(interfaceClass = CommentServiceApi.class)
public class DefaultCommentServiceImpl implements CommentServiceApi{

    @Autowired
    private CommentCommentTMapper commentCommentTMapper;

    @Autowired
    private CommentLikeTMapper commentLikeTMapper;

    @Override
    public boolean commitComment(Integer filmId, String comment,Integer userId) {
        CommentCommentT commentCommentT=new CommentCommentT();
        commentCommentT.setFilmId(filmId);
        commentCommentT.setUserId(userId);
        commentCommentT.setComment(comment);
        commentCommentTMapper.insert(commentCommentT);
        return true;
    }

    @Override
    public List<CommentVO> getComment(Integer filmId) {
        Page<CommentCommentT> page=new Page<>(1,10);
        List<CommentVO> commentVOS=commentCommentTMapper.getCommentsByFilmId(filmId,page);
        return commentVOS;
    }
}
