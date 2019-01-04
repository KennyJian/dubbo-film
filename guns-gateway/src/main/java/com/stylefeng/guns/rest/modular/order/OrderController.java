package com.stylefeng.guns.rest.modular.order;

import com.alibaba.dubbo.config.annotation.Reference;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.order.OrderServiceApi;
import com.stylefeng.guns.api.order.vo.OrderVO;
import com.stylefeng.guns.core.util.TokenBucket;
import com.stylefeng.guns.rest.common.CurrentUser;
import com.stylefeng.guns.rest.modular.vo.ResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "/order/")
public class OrderController {

    private static TokenBucket tokenBucket=new TokenBucket();

    @Reference(interfaceClass = OrderServiceApi.class)
    private OrderServiceApi orderServiceApi;

    //购票
    @RequestMapping(value = "buyTickets",method = RequestMethod.POST)
    public ResponseVO buyTickets(Integer fieldId,String soldSeats,String seatsName){

        if (tokenBucket.getToken()) {
            try {
                //验证售出的票是否为真
                boolean isTrue=orderServiceApi.isTrueSeats(fieldId+"",soldSeats);
                //已经销售的座位里,有没有这些座位
                boolean isNotSold=orderServiceApi.isNotSoldSeats(fieldId+"",soldSeats);
                if (isTrue&&isNotSold){
                    //创建订单信息,注意获取登陆人
                    String userId= CurrentUser.getCurrentUser();
                    if (userId==null||userId.trim().length()==0){
                        return ResponseVO.serviceFail("用户未登录");
                    }
                    OrderVO orderVO=orderServiceApi.saveOrderInfo(fieldId,soldSeats,seatsName,Integer.parseInt(userId));
                    if (orderVO==null){
                        log.error("购票未成功");
                        return ResponseVO.serviceFail("购票业务异常");
                    }else {
                        return ResponseVO.success(orderVO);
                    }
                }else {
                    return ResponseVO.serviceFail("订单中的座位编号有问题");
                }
            } catch (Exception e) {
                log.error("购票业务异常",e);
                return ResponseVO.serviceFail("购票业务异常");
            }
        }else {
            return ResponseVO.serviceFail("购票人数过多,请稍后再试");
        }
    }

    @RequestMapping(value = "getOrderInfo",method = RequestMethod.POST)
    public ResponseVO getOrderInfo(@RequestParam(value = "nowPage",required = false,defaultValue = "1")Integer nowPage,
                                   @RequestParam(value = "pageSize",required = false,defaultValue = "5")Integer pageSize){

        //获取当前登陆人的信息
        String userId=CurrentUser.getCurrentUser();
        //使用当前登陆人获取已经购买的订单
        Page<OrderVO> page=new Page<>(nowPage,pageSize);
        if (userId!=null&&userId.trim().length()>0){
            Page<OrderVO> result = orderServiceApi.getOrderByUserId(Integer.parseInt(userId), page);
            return ResponseVO.success(nowPage,(int)result.getPages(),"",result.getRecords());
        }else {
            return ResponseVO.serviceFail("用户未登录");
        }
    }
}
