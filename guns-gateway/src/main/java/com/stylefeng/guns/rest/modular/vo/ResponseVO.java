package com.stylefeng.guns.rest.modular.vo;

import com.stylefeng.guns.rest.modular.myenum.ResponseEnum;
import lombok.Data;

@Data
public class ResponseVO<M> {

    //返回状态 0:成功 1:业务失败 999:系统异常
    private int status;
    //返回信息
    private String msg;
    //返回数据实体
    private M data;
    //图片前缀
    private String imgPre;
    //分页使用
    private int nowPage;
    private int totalPage;

    private ResponseVO(){}

    public static <M> ResponseVO success(String msg){
        ResponseVO responseVO=new ResponseVO();
        responseVO.setStatus(ResponseEnum.SUCCESS.getCode());
        responseVO.setMsg(msg);
        return responseVO;
    }

    public static <M> ResponseVO success(String imgPre,M m){
        ResponseVO responseVO=new ResponseVO();
        responseVO.setStatus(ResponseEnum.SUCCESS.getCode());
        responseVO.setMsg(ResponseEnum.SUCCESS.getMsg());
        responseVO.setData(m);
        responseVO.setImgPre(imgPre);
        return responseVO;
    }

    public static <M> ResponseVO success(int nowPage,int totalPage,String imgPre,M m){
        ResponseVO responseVO=new ResponseVO();
        responseVO.setStatus(ResponseEnum.SUCCESS.getCode());
        responseVO.setData(m);
        responseVO.setImgPre(imgPre);
        responseVO.setTotalPage(totalPage);
        responseVO.setNowPage(nowPage);
        return responseVO;
    }

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


}
