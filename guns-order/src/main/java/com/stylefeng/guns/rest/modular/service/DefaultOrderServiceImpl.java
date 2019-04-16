package com.stylefeng.guns.rest.modular.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.cinema.CinemaServiceApi;
import com.stylefeng.guns.api.cinema.vo.FilmInfoVO;
import com.stylefeng.guns.api.cinema.vo.OrderQueryVO;
import com.stylefeng.guns.api.order.OrderServiceApi;
import com.stylefeng.guns.api.order.vo.OrderVO;
import com.stylefeng.guns.core.util.UUIDUtil;
import com.stylefeng.guns.rest.common.orderenum.OrderStatusEnum;
import com.stylefeng.guns.rest.common.persistence.dao.OrderOrderTMapper;
import com.stylefeng.guns.rest.common.persistence.model.OrderOrderT;
import com.stylefeng.guns.rest.common.util.FTPUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@Service(interfaceClass = OrderServiceApi.class)
public class DefaultOrderServiceImpl implements OrderServiceApi {

    @Reference(interfaceClass = CinemaServiceApi.class)
    private CinemaServiceApi cinemaServiceApi;

    @Autowired
    private OrderOrderTMapper orderOrderTMapper;

    @Autowired
    private FTPUtil ftpUtil;

    @Override
    public boolean isTrueSeats(String fieldId, String seats) {
        //根据FieldId找到对应的座位位置图
        String seatPath=cinemaServiceApi.getSeatsByFieldId(fieldId);
        try {
            //读取位置图,判断seats是否为真
            String fileStrByAddress = ftpUtil.getFileStrByAddress(seatPath);
            //将fileStrByAddress转换为JSON对象
            JSONObject jsonObject=JSONObject.parseObject(fileStrByAddress);
            //seats=1,2,3 ids="1,3,4,5,6,7,8,9"
            String ids = jsonObject.get("ids").toString();
            String[] seatArrs=seats.split(",");
            String[] idArrs=ids.split(",");
            int isTrue=0;
            for(String id:idArrs){
                for(String seat:seatArrs){
                    if(seat.equalsIgnoreCase(id)){
                        isTrue++;
                    }
                }
            }
            //如果匹配上的数量与已售座位数一致,则表示全都匹配上了
            if (isTrue==seatArrs.length){
                return true;
            }
            return false;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    //判断是否为已售座位
    @Override
    public boolean isNotSoldSeats(String fieldId, String seats) {
        EntityWrapper entityWrapper=new EntityWrapper();
        entityWrapper.eq("field_id",fieldId);
        List<OrderOrderT> orderOrderTS = orderOrderTMapper.selectList(entityWrapper);
        String[] seatArrs=seats.split(",");
        //有任何一个编号匹配上 则直接返回失败
        for(OrderOrderT orderOrderT:orderOrderTS){
            String[] ids=orderOrderT.getSeatsIds().split(",");
            for(String id:ids){
                for(String seat:seatArrs){
                    if (id.equalsIgnoreCase(seat)){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private static double getTotalPrice(int solds,double filmPrice){
        BigDecimal soldsDeci=new BigDecimal(solds);
        BigDecimal filmPriceDeci=new BigDecimal(filmPrice);
        BigDecimal result=soldsDeci.multiply(filmPriceDeci);
        //四舍五入 取小数点后两位
        BigDecimal bigDecimal=result.setScale(2, RoundingMode.HALF_UP);
        return bigDecimal.doubleValue();
    }


    @Override
    public OrderVO saveOrderInfo(Integer fieldId, String soldSeats, String seatsName, Integer userId) {
        //编号
        String uuid= UUIDUtil.genUuid();
        ///影片信息
        FilmInfoVO filmInfoVO=cinemaServiceApi.getFilmInfoByFieldId(fieldId);
        Integer filmId=Integer.parseInt(filmInfoVO.getFilmId());
        //影院信息
        OrderQueryVO orderQueryVO = cinemaServiceApi.getOrderNeeds(fieldId);
        Integer cinemaId=Integer.parseInt(orderQueryVO.getCinemaId());
        double filmPrice=Double.parseDouble(orderQueryVO.getFilmPrice());
        //求订单金额  1,2,3,4,5
        int solds=soldSeats.split(",").length;
        double totalPrice=getTotalPrice(solds,filmPrice);

        OrderOrderT orderOrderT=new OrderOrderT();
        orderOrderT.setUuid(uuid);
        orderOrderT.setSeatsName(seatsName);
        orderOrderT.setSeatsIds(soldSeats);
        orderOrderT.setOrderUser(userId);
        orderOrderT.setOrderPrice(totalPrice);
        orderOrderT.setFilmPrice(filmPrice);
        orderOrderT.setFilmId(filmId);
        orderOrderT.setFieldId(fieldId);
        orderOrderT.setCinemaId(cinemaId);

        Integer insert = orderOrderTMapper.insert(orderOrderT);
        if (insert>0){
            //返回查询结构
            OrderVO orderVO=orderOrderTMapper.getOrderInfoById(uuid);
//            orderVO.setOrderStatus(OrderStatusEnum.getMsg(Integer.parseInt(orderVO.getOrderStatus())));
            if (orderVO==null || orderVO.getOrderId()==null){
                log.error("订单信息查询失败,订单编号为{}",uuid);
                return null;
            }else {
                return orderVO;
            }
        }else {
            //插入出错
            log.error("订单插入失败");
            return null;
        }
    }

    @Override
    public Page<OrderVO> getOrderByUserId(Integer userId,Page<OrderVO> page) {
        Page<OrderVO> result=new Page<>();
        if (userId==null){
            log.error("订单查询业务失败,用户编号未传入");
            return null;
        }else {
            List<OrderVO> orderByUserId=orderOrderTMapper.getOrderInfoByUserId(userId,page);
            if (orderByUserId==null&&orderByUserId.size()==0){
                result.setTotal(0);
                result.setRecords(new ArrayList<>());
                return result;
            }else {
                //获取订单总数
                EntityWrapper<OrderOrderT> entityWrapper=new EntityWrapper<>();
                entityWrapper.eq("order_user",userId);
                Integer counts = orderOrderTMapper.selectCount(entityWrapper);
                result.setTotal(counts);
                result.setRecords(orderByUserId);
                return result;
            }
        }
    }

    //根据放映查询,获取所有的已售座位
    @Override
    public String getSoldSeatsByFieldId(Integer fieldId) {
        if (fieldId==null){
            log.error("查询已售座位错误,未传入任何场次编号");
            return "";
        }else {
            String soldSeatsByFieldId = orderOrderTMapper.getSoldSeatsByFieldId(fieldId);
            return soldSeatsByFieldId;
        }
    }

    @Override
    public OrderVO getOrderInfoById(String orderId) {
        OrderVO orderInfoById = orderOrderTMapper.getOrderInfoById(orderId);
        return orderInfoById;
    }

    @Override
    public boolean paySuccess(String orderId) {
        OrderOrderT orderOrderT=new OrderOrderT();
        orderOrderT.setUuid(orderId);
        orderOrderT.setOrderStatus(1);

        Integer resultCount = orderOrderTMapper.updateById(orderOrderT);
        if(resultCount>=1){
            return true;
        }
        return false;
    }

    @Override
    public boolean payFail(String orderId,boolean isTimeOut) {
        OrderOrderT orderOrderT=new OrderOrderT();
        orderOrderT.setUuid(orderId);
        if(isTimeOut){
          orderOrderT.setOrderStatus(2);
        }
        Integer resultCount = orderOrderTMapper.updateById(orderOrderT);
        if(resultCount>=1){
            return true;
        }
        return false;
    }

}
