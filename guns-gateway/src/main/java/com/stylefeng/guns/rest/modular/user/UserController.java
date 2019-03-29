package com.stylefeng.guns.rest.modular.user;

import com.alibaba.dubbo.config.annotation.Reference;
import com.stylefeng.guns.api.imgconst.ImgConst;
import com.stylefeng.guns.api.user.UserAPI;
import com.stylefeng.guns.api.user.vo.RegisterVO;
import com.stylefeng.guns.api.user.vo.UserInfoModel;
import com.stylefeng.guns.api.user.vo.UserModel;
import com.stylefeng.guns.rest.common.CurrentUser;
import com.stylefeng.guns.rest.modular.vo.ResponseVO;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.io.Serializable;

@RequestMapping("/user/")
@RestController
public class UserController {

    @Reference(interfaceClass = UserAPI.class)
    private UserAPI userAPI;

    @ApiOperation(value = "用户注册")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userName", value = "用户注册账号", required = true, dataType = "String"),
            @ApiImplicitParam(name = "userPwd", value = "用户注册密码", required = true, dataType = "String")
    })
    @RequestMapping(value = "register",method = RequestMethod.POST)
    public ResponseVO register(@Valid RegisterVO registerVO, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return ResponseVO.serviceFail("用户名或密码不能为空");
        }else {
            boolean isSuccess = userAPI.register(registerVO);
            if (isSuccess) {
                return ResponseVO.success("注册成功");
            }
        }
        return ResponseVO.serviceFail("注册失败");
    }

    @ApiOperation(value = "查询用户是否存在")
    @ApiImplicitParam(name = "username", value = "用户名", required = true, dataType = "String")
    @RequestMapping(value = "check",method = RequestMethod.POST)
    public ResponseVO check(String username){
        if(username!=null&&username.trim().length()>0){
            //当返回true,表示用户名可用
            boolean notExists=userAPI.checkUserName(username);
            if (notExists){
                return ResponseVO.success("用户名不存在");
            }else {
                return ResponseVO.serviceFail("用户名已存在");
            }
        }
        return ResponseVO.serviceFail("用户名不能为空");
    }

    @ApiOperation(value = "用户登出")
    @RequestMapping(value = "logout",method = RequestMethod.POST)
    public ResponseVO logout(){
        /**
         * 应用:
         *      1.前端存储JWT【7天】 :JWT的刷新
         *      2.服务器端会存储活动用户信息【30分钟】
         *      3.JWT里的userId为key,查找活跃用户
         * 退出:
         *      1.前端删掉JWT
         *      2.后端服务器删除活跃用户缓存
         * 现状:
         *      1.前端删除掉JWT
         */
        return ResponseVO.success("用户退出成功");
    }


    @ApiOperation(value = "获取个人信息")
    @RequestMapping(value = "getUserInfo",method = RequestMethod.POST)
    public ResponseVO getUserInfo(){
        //获取当前登陆用户
        String userId= CurrentUser.getCurrentUser();
        if (userId!=null&&userId.trim().length()>0){
            //将用户ID传入后端进行查询
            int uuid=Integer.parseInt(userId);
            UserInfoModel userInfo=userAPI.getUserInfo(uuid);
            if (userInfo!=null){
                return ResponseVO.success(ImgConst.IMGSRC,userInfo);
            }else {
                return ResponseVO.appFail("用户信息查询失败");
            }
        }else {
            return ResponseVO.serviceFail("用户未登陆");
        }
    }

    @ApiOperation(value = "修改个人信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "nickName", value = "用户别名", required = false, dataType = "String"),
            @ApiImplicitParam(name = "email", value = "电子邮箱", required = false, dataType = "String"),
            @ApiImplicitParam(name = "userPhone", value = "手机号", required = false, dataType = "String"),
            @ApiImplicitParam(name = "userSex", value = "性别", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "birthday", value = "生日", required = false, dataType = "String"),
            @ApiImplicitParam(name = "lifeState", value = "婚姻状态", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "biography", value = "个人简介", required = false, dataType = "String")
    })
    @RequestMapping(value = "updateUserInfo",method = RequestMethod.POST)
    public ResponseVO updateUserInfo(@Valid UserInfoModel userInfoModel, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return ResponseVO.serviceFail("信息格式不正确");
        }else {
            //获取当前登陆用户
            String userId = CurrentUser.getCurrentUser();
            if (userId != null && userId.trim().length() > 0) {
                //将用户ID传入后端进行查询
                int uuid = Integer.parseInt(userId);
                UserInfoModel userInfo = userAPI.updateUserInfo(userInfoModel,uuid);
                if (userInfo != null) {
                    return ResponseVO.success(userInfo);
                } else {
                    return ResponseVO.appFail("用户信息修改失败");
                }
            } else {
                return ResponseVO.serviceFail("用户未登陆");
            }
        }
    }

    @ApiOperation(value = "上传头像")
    @ApiImplicitParam(name = "file", value = "文件", required = true, dataType = "MultipartFile")
    @RequestMapping(value = "/upload",method = RequestMethod.POST)
        public ResponseVO upload(MultipartFile headImgFile) throws IOException {
        if (headImgFile.isEmpty()) {
            return ResponseVO.serviceFail("上传失败，请选择文件");
        }
        String userId= CurrentUser.getCurrentUser();
        if (userId != null && userId.trim().length() > 0) {
            int uuid = Integer.parseInt(userId);
            String fileName = headImgFile.getOriginalFilename();
            // 获取文件后缀
            String prefix=fileName.substring(fileName.lastIndexOf("."));
            byte[] bytes=headImgFile.getBytes();
            boolean isUploadSuccess = userAPI.uploadHead(bytes,uuid,fileName,prefix);
            if (isUploadSuccess) {
                return ResponseVO.success("上传成功");
            }
            return ResponseVO.serviceFail("上传失败");
        } else {
            return ResponseVO.serviceFail("用户未登陆");
        }
    }

}
