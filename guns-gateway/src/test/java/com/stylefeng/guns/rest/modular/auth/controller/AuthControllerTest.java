package com.stylefeng.guns.rest.modular.auth.controller;

import com.stylefeng.guns.rest.modular.auth.controller.dto.AuthRequest;
import com.stylefeng.guns.rest.modular.vo.ResponseVO;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

public class AuthControllerTest {

    @Autowired
    private AuthController authController;

    @Test
    public void test(){
        AuthRequest authRequest=new AuthRequest();
        authRequest.setUserName("kenny");
        authRequest.setPassword("admin123");
        ResponseVO authenticationToken = authController.createAuthenticationToken(authRequest);
        System.out.println(authenticationToken.getData());
    }

}