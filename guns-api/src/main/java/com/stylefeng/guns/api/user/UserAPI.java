package com.stylefeng.guns.api.user;

import com.stylefeng.guns.api.user.vo.RegisterVO;
import com.stylefeng.guns.api.user.vo.UserInfoModel;
import com.stylefeng.guns.api.user.vo.UserModel;
import org.springframework.web.multipart.MultipartFile;

public interface UserAPI {

    int login(String username, String password);

    boolean register(RegisterVO registerVO);

    boolean checkUserName(String username);

    UserInfoModel getUserInfo(int uuid);

    UserInfoModel updateUserInfo(UserInfoModel userInfoModel,int userId);

    boolean uploadHead(MultipartFile file,int userId);
}
