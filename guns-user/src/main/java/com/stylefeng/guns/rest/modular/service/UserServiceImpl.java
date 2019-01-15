package com.stylefeng.guns.rest.modular.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.stylefeng.guns.api.user.UserAPI;
import com.stylefeng.guns.api.user.vo.RegisterVO;
import com.stylefeng.guns.api.user.vo.UserInfoModel;
import com.stylefeng.guns.api.user.vo.UserModel;
import com.stylefeng.guns.core.util.MD5Util;
import com.stylefeng.guns.rest.common.persistence.dao.KennyUserTMapper;
import com.stylefeng.guns.rest.common.persistence.model.KennyUserT;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Component
@Service(interfaceClass = UserAPI.class,loadbalance = "roundrobin")
public class UserServiceImpl implements UserAPI {

    @Autowired
    private KennyUserTMapper kennyUserTMapper;

    @Override
    public int login(String username, String password) {
        //只完成了密码校验 还需维护登陆状态 TODO
        KennyUserT kennyUserT=new KennyUserT();
        kennyUserT.setUserName(username);
        KennyUserT result=kennyUserTMapper.selectOne(kennyUserT);
        if (result!=null&&result.getUuid()>0){
            String md5Password=MD5Util.encrypt(password);
            if (result.getUserPwd().equals(md5Password)){
                return result.getUuid();
            }
        }
        return 0;
    }

    @Override
    public boolean register(RegisterVO registerVO) {

        //判断该用户是否被注册过
        if(!checkUserName(registerVO.getUserName())){
            return false;
        }
        //将注册信息实体转换为数据实体
        KennyUserT kennyUserT=new KennyUserT();
        BeanUtils.copyProperties(registerVO,kennyUserT);

        //数据加密
        String md5Password= MD5Util.encrypt(registerVO.getUserPwd());
        kennyUserT.setUserPwd(md5Password);
        //将数据实体存入数据库
        Integer insertCount=kennyUserTMapper.insert(kennyUserT);
        if (insertCount>0){
            return true;
        }
        return false;
    }

    @Override
    public boolean checkUserName(String username) {
        EntityWrapper<KennyUserT> entityWrapper=new EntityWrapper<>();
        entityWrapper.eq("user_name",username);
        Integer result=kennyUserTMapper.selectCount(entityWrapper);
        if(result!=null&&result>0){
            return false;
        }
        return true;
    }

    @Override
    public UserInfoModel getUserInfo(int uuid) {
        KennyUserT kennyUserT=kennyUserTMapper.selectById(uuid);
        UserInfoModel userInfoModel=new UserInfoModel();
        BeanUtils.copyProperties(kennyUserT,userInfoModel);
        return userInfoModel;
    }

    @Override
    public UserInfoModel updateUserInfo(UserInfoModel userInfoModel,int userId) {
        KennyUserT kennyUserT=new KennyUserT();
        kennyUserT.setUuid(userId);
        BeanUtils.copyProperties(userInfoModel,kennyUserT);
        Integer resultCount=kennyUserTMapper.updateById(kennyUserT);
        if(resultCount>0){
            return getUserInfo(kennyUserT.getUuid());
        }
        return userInfoModel;
    }

    @Override
    public boolean uploadHead(MultipartFile file,int userId) {

        String fileName = file.getOriginalFilename();
        String filePath = "D:/ftp/head/";
        File dest = new File(filePath + fileName);
        try {
            file.transferTo(dest);
            KennyUserT kennyUserT=kennyUserTMapper.selectById(userId);
            kennyUserT.setHeadUrl("/head/"+fileName);
            kennyUserTMapper.updateById(kennyUserT);
            return true;
        } catch (IOException e) {

        }
        return false;
    }
}
