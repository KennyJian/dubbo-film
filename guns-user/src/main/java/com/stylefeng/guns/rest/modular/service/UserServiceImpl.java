package com.stylefeng.guns.rest.modular.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.stylefeng.guns.api.user.UserAPI;
import com.stylefeng.guns.api.user.vo.RegisterVO;
import com.stylefeng.guns.api.user.vo.UserInfoModel;
import com.stylefeng.guns.core.util.MD5Util;
import com.stylefeng.guns.core.util.UUIDUtil;
import com.stylefeng.guns.rest.common.persistence.dao.KennyUserTMapper;
import com.stylefeng.guns.rest.common.persistence.model.KennyUserT;
import com.stylefeng.guns.rest.common.util.FTPUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;

@Component
@Service(interfaceClass = UserAPI.class,loadbalance = "roundrobin")
public class UserServiceImpl implements UserAPI {

    @Autowired
    private KennyUserTMapper kennyUserTMapper;

    @Autowired
    FTPUtil ftpUtil;

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
    public boolean uploadHead(byte[] bytes, int userId, String fileName, String prefix) {
        String RealFileName= UUIDUtil.genUuid();
        OutputStream output = null;
        BufferedOutputStream bufferedOutput = null;
        try {
//            File file= (File) File.createTempFile(RealFileName,prefix, new File("D://"));
            File file= (File) File.createTempFile(RealFileName,prefix, new File("/home/kenny/eache/head/"));
            output = new FileOutputStream(file);
            bufferedOutput = new BufferedOutputStream(output);
            bufferedOutput.write(bytes);
            boolean isSuccess=ftpUtil.uploadFile(file.getName(),file);
            if (file.exists()){
                file.delete();
            }
            if(isSuccess){
                KennyUserT kennyUserT=kennyUserTMapper.selectById(userId);
                kennyUserT.setHeadUrl("/head/"+file.getName());
                kennyUserTMapper.updateById(kennyUserT);
                return true;
            }
            return false;
        } catch (IOException e) {
            System.err.println("上传失败");
        } finally {
            if(null!=bufferedOutput){
                try {
                    bufferedOutput.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if(null != output){
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }
}
