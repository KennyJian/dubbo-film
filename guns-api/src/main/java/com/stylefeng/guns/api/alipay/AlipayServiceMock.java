package com.stylefeng.guns.api.alipay;

import com.stylefeng.guns.api.alipay.vo.AliPayInfoVO;
import com.stylefeng.guns.api.alipay.vo.AliPayResultVO;

/**
 * 业务降级方法
 */
public class AlipayServiceMock implements AliPayServiceAPI {
    @Override
    public AliPayInfoVO getQRCode(String orderId) {
        return null;
    }

    @Override
    public AliPayResultVO getOrderStatus(String orderId,boolean isTimeOut) {
        AliPayResultVO aliPayResultVO=new AliPayResultVO();
        aliPayResultVO.setOrderId(orderId);
        aliPayResultVO.setOrderStatus(0);
        aliPayResultVO.setOrderMsg("获取订单结果失败,请稍后重试");
        return aliPayResultVO;
    }

    @Override
    public void checkOrderStatusInTime(String orderId) {

    }
}
