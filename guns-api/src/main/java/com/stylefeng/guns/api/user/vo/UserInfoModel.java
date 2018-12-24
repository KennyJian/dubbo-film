package com.stylefeng.guns.api.user.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class UserInfoModel implements Serializable {
    private Integer uuid;
    private String userName;
    private String nickName;
    private String email;
    private String userPhone;
    private int userSex;
    private String bitrhday;
    private String lifeState;
    private String biography;
    private String address;
    private String headUrl;
    private Date beginTime;
    private Date updateTime;

}
