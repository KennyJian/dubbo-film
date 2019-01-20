package com.stylefeng.guns.api.film.vo;

import com.stylefeng.guns.api.comment.vo.CommentVO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class FilmDetailVO implements Serializable {

    private String filmId;
    private String filmName;
    private String filmEnName;
    private String imgAddress;
    private String score;
    private String scoreNum;
    private String totalBox;
    private String info01;
    private String info02;
    private String info03;
    private InfoRequestVO info04;

    //获取评论列表
    private List<CommentVO> comments;
    //获取推荐列表
    private List<RecommendVO> recommends;


}
