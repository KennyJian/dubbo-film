package com.stylefeng.guns.rest.modular.order;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.rpc.RpcContext;
import com.baomidou.mybatisplus.plugins.Page;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.stylefeng.guns.api.alipay.AliPayServiceAPI;
import com.stylefeng.guns.api.alipay.vo.AliPayInfoVO;
import com.stylefeng.guns.api.alipay.vo.AliPayResultVO;
import com.stylefeng.guns.api.order.OrderServiceApi;
import com.stylefeng.guns.api.order.vo.OrderVO;
import com.stylefeng.guns.core.util.TokenBucket;
import com.stylefeng.guns.core.util.ToolUtil;
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

    private static final String IMG_PRE="http://www.chong10010.cn/";

    @Reference(interfaceClass = OrderServiceApi.class)
    private OrderServiceApi orderServiceApi;

    @Reference(interfaceClass = AliPayServiceAPI.class)
    private AliPayServiceAPI aliPayServiceAPI;

    public ResponseVO error(Integer fieldId,String soldSeats,String seatsName){
        return ResponseVO.serviceFail("抱歉,下单的人太多了,请稍后重试");
    }

    //购票

    /**
     * 信号量隔离
     * 线程池隔离
     * 线程切换 (取不到用户信息)
     */
    @HystrixCommand(fallbackMethod="error",commandProperties={
            @HystrixProperty(name="execution.isolation.strategy",value="THREAD"),
            @HystrixProperty(name="execution.isolation.thread.timeoutInMilliseconds",value="4000"),
            @HystrixProperty(name="circuitBreaker.requestVolumeThreshold",value="10"),
            @HystrixProperty(name="circuitBreaker.errorThresholdPercentage",value="50")},
            threadPoolProperties={
                    @HystrixProperty(name="coreSize",value="1"),
                    @HystrixProperty(name="maxQueueSize",value="10"),
                    @HystrixProperty(name="keepAliveTimeMinutes",value="1000"),
                    @HystrixProperty(name="queueSizeRejectionThreshold",value="8"),
                    @HystrixProperty(name="metrics.rollingStats.numBuckets",value="12"),
                    @HystrixProperty(name="metrics.rollingStats.timeInMilliseconds",value="1500")
    })
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

    @RequestMapping(value = "getPayInfo",method = RequestMethod.POST)
    public ResponseVO getPayInfo(@RequestParam("orderId") String orderId){
        String userId= CurrentUser.getCurrentUser();
        if (userId==null||userId.trim().length()==0) {
            return ResponseVO.serviceFail("抱歉,用户未登陆");
        }
        //订单二维码返回结果
        AliPayInfoVO aliPayInfoVO=aliPayServiceAPI.getQRCode(orderId);
        return ResponseVO.success(IMG_PRE,aliPayInfoVO);
    }

    @RequestMapping(value = "getPayResult",method = RequestMethod.POST)
    public ResponseVO getPayResult(@RequestParam("orderId") String orderId,
                                   @RequestParam(value = "tryNums",required = false,defaultValue = "1") Integer tryNums){
        String userId= CurrentUser.getCurrentUser();
        if (userId==null||userId.trim().length()==0) {
            return ResponseVO.serviceFail("抱歉,用户未登陆");
        }
        //将当前登陆人的信息传递给后端
        RpcContext.getContext().setAttachment("userId",userId);

        //判断是否支付超时
        if (tryNums>=4){
            return ResponseVO.serviceFail("订单支付失败,请稍后重试");
        }
        AliPayResultVO aliPayResultVO=aliPayServiceAPI.getOrderStatus(orderId);
        if (aliPayResultVO==null|| ToolUtil.isEmpty(aliPayResultVO.getOrderId())){
            AliPayResultVO serviceFailVO=new AliPayResultVO();
            serviceFailVO.setOrderId(orderId);
            serviceFailVO.setOrderStatus(0);
            serviceFailVO.setOrderMsg("支付不成功");
            return ResponseVO.success(serviceFailVO);
        }
        return ResponseVO.success(aliPayResultVO);
    }
}
