package com.stylefeng.guns.api.comment.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class CommentVO implements Serializable{


    private String uuid;
    private String comment;
    private Date createTime;
    private String userName;
    private String headUrl;
}
