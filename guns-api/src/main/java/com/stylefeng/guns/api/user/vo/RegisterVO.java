package com.stylefeng.guns.api.user.vo;


import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * 用户注册对象
 */
@Data
public class RegisterVO implements Serializable {

    @NotEmpty(message = "用户名不能为空")
    private String userName;

    @NotEmpty(message = "密码不能为空")
    private String userPwd;
}
