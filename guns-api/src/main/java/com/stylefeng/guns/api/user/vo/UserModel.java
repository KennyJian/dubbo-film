package com.stylefeng.guns.api.user.vo;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@Data
public class UserModel implements Serializable {

    @NotEmpty(message = "用户名不能违抗")
    private String userName;

    private String userPwd;

    private String email;

    private String userPhone;

    private String address;

}
