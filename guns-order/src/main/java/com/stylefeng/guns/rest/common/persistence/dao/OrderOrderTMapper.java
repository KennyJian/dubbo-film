package com.stylefeng.guns.rest.common.persistence.dao;

import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.order.vo.OrderVO;
import com.stylefeng.guns.rest.common.persistence.model.OrderOrderT;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 订单信息表 Mapper 接口
 * </p>
 *
 * @author kenny
 * @since 2019-01-02
 */
public interface OrderOrderTMapper extends BaseMapper<OrderOrderT> {

    OrderVO getOrderInfoById(@Param("orderId")String orderId);

    List<OrderVO> getOrderInfoByUserId(@Param("userId")Integer userId, Page<OrderVO> page);

    String getSoldSeatsByFieldId(@Param("fieldId")Integer fieldId);

    Integer addOrder(OrderOrderT orderOrderT);

}
