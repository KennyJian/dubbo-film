package com.stylefeng.guns.api.user.vo;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

@Data
public class UserInfoModel implements Serializable {
    private String nickName;
    @Email
    private String email;
    @Size(max = 11,min = 11)
    private String userPhone;
    @Min(0)
    @Max(1)
    private Integer userSex;
    private String birthday;
    @Min(0)
    @Max(3)
    private Integer lifeState;
    @Size(max = 200)
    private String biography;
    private String headUrl;

}
