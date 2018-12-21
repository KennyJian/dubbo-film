package com.stylefeng.guns.rest.modular.vo;

import com.stylefeng.guns.rest.modular.myenum.ResponseEnum;

public class ResponseVO<M> {

    //返回状态 0:成功 1:业务失败 999:系统异常
    private int status;
    //返回信息
    private String msg;
    //返回数据实体
    private M data;

    private ResponseVO(){}

    public static <M> ResponseVO success(M m){
        ResponseVO responseVO=new ResponseVO();
        responseVO.setStatus(ResponseEnum.SUCCESS.getCode());
        responseVO.setMsg(ResponseEnum.SUCCESS.getMsg());
        responseVO.setData(m);
        return responseVO;
    }


    public static <M> ResponseVO serviceFail(String msg){
        ResponseVO responseVO=new ResponseVO();
        responseVO.setStatus(ResponseEnum.BUSINESSFAILURE.getCode());
        responseVO.setMsg(msg);
        return responseVO;
    }

    public static <M> ResponseVO appFail(String msg){
        ResponseVO responseVO=new ResponseVO();
        responseVO.setStatus(ResponseEnum.SYSTEMERRO.getCode());
        responseVO.setMsg(msg);
        return responseVO;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public M getData() {
        return data;
    }

    public void setData(M data) {
        this.data = data;
    }
}
