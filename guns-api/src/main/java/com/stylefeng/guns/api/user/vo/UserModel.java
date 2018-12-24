package com.stylefeng.guns.api.user.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserModel implements Serializable {

    private String userName;

    private String userPwd;

    private String email;

    private String userPhone;

    private String address;

}
