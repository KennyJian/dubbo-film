package com.stylefeng.guns.rest.common.orderenum;

public enum OrderStatusEnum {

    SUCCESS(0,"待支付"),
    INSERTFAIL(1,"已支付"),
    FAIL(2,"已关闭");

    int code;
    String msg;

    OrderStatusEnum(int code, String msg) {
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
        for(OrderStatusEnum orderStatusEnum:OrderStatusEnum.values()){
            if(orderStatusEnum.getCode()==code){
                return orderStatusEnum.getMsg();
            }
        }
        return null;
    }
}
