package com.stylefeng.guns.rest.modular.user;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.stylefeng.guns.api.user.UserAPI;
import com.stylefeng.guns.api.user.UserInfoModel;
import com.stylefeng.guns.api.user.UserModel;
import com.stylefeng.guns.core.util.MD5Util;
import com.stylefeng.guns.rest.common.persistence.dao.KennyUserTMapper;
import com.stylefeng.guns.rest.common.persistence.model.KennyUserT;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Service(interfaceClass = UserAPI.class,loadbalance = "roundrobin")
public class UserServiceImpl implements UserAPI {

    @Autowired
    private KennyUserTMapper kennyUserTMapper;

    @Override
    public int login(String username, String password) {
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
    public boolean register(UserModel userModel) {

        //判断该用户是否被注册过
        if(!checkUserName(userModel.getUserName())){
            return false;
        }
        //将注册信息实体转换为数据实体
        KennyUserT kennyUserT=new KennyUserT();
        BeanUtils.copyProperties(userModel,kennyUserT);

        //数据加密
        String md5Password= MD5Util.encrypt(userModel.getUserPwd());
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
    public UserInfoModel updateUserInfo(UserInfoModel userInfoModel) {
        KennyUserT kennyUserT=new KennyUserT();
        BeanUtils.copyProperties(userInfoModel,kennyUserT);
        Integer resultCount=kennyUserTMapper.updateById(kennyUserT);
        if(resultCount>0){
            return getUserInfo(kennyUserT.getUuid());
        }
        return userInfoModel;
    }
}
