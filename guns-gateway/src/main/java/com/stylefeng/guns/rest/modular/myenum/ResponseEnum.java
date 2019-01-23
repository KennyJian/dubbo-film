package com.stylefeng.guns.rest.modular.myenum;

public enum ResponseEnum {

    SUCCESS(200,"成功"),
    BUSINESSFAILURE(1,"业务失败"),
    SYSTEMERRO(999,"系统异常");

    int code;
    String msg;

    ResponseEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public static String getMsg(int code){
        for(ResponseEnum responseEnum:ResponseEnum.values()){
            if(responseEnum.getCode()==code){
                return responseEnum.getMsg();
            }
        }
        return null;
    }
}
