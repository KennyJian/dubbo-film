package com.stylefeng.guns.rest.modular.cinema;

import com.alibaba.dubbo.config.annotation.Reference;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.cinema.CinemaServiceApi;
import com.stylefeng.guns.api.cinema.vo.*;
import com.stylefeng.guns.api.imgconst.ImgConst;
import com.stylefeng.guns.api.order.OrderServiceApi;
import com.stylefeng.guns.rest.modular.cinema.vo.CinemaConditionResponseVO;
import com.stylefeng.guns.rest.modular.cinema.vo.CinemaFieldResponseVO;
import com.stylefeng.guns.rest.modular.cinema.vo.CinemaFieldsResponseVO;
import com.stylefeng.guns.rest.modular.vo.ResponseVO;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.support.HttpRequestHandlerServlet;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/cinema/")
public class CinemaController {

//    @Reference(interfaceClass = CinemaServiceApi.class,cache = "lru") 添加缓存
    @Reference(interfaceClass = CinemaServiceApi.class)
    private CinemaServiceApi cinemaServiceApi;

    @Reference(interfaceClass = OrderServiceApi.class)
    private OrderServiceApi orderServiceApi;

    private static final String IMG_PRE="http://www.chong10010.cn/";

    @ApiOperation(value = "获取影城列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "brandId", value = "品牌Id", required = false, dataType = "Integer",defaultValue ="99"),
            @ApiImplicitParam(name = "districtId", value = "区域", required = false, dataType = "Integer",defaultValue ="99"),
            @ApiImplicitParam(name = "hallType", value = "影厅类型", required = false, dataType = "Integer",defaultValue ="99"),
            @ApiImplicitParam(name = "pagsSize", value = "页面大小", required = false, dataType = "Integer",defaultValue ="12"),
            @ApiImplicitParam(name = "nowPage", value = "当前页面", required = false, dataType = "Integer",defaultValue = "1")
    })
    @RequestMapping(value = "getCinemas",method = RequestMethod.GET)
    public ResponseVO getCinemas(CinemaQueryVO cinemaQueryVO){
        try {
            //按照五个条件进行筛选
            Page<CinemaVO> cinemas=cinemaServiceApi.getCinemas(cinemaQueryVO);
            //判断是否有满足条件的影院
            if (cinemas.getRecords()==null || cinemas.getRecords().size()==0){
                return ResponseVO.success("没有影院可查");
            }else {
                return ResponseVO.success(cinemas.getCurrent(),(int)cinemas.getPages(),"",cinemas.getRecords());
            }
        }catch (Exception e){
            log.error("获取影院列表异常",e);
            return ResponseVO.serviceFail("查询影院列表失败");
        }

    }

    //获取影院的查询条件

    /**
     * 1.热点数据->放缓存
     */
    @ApiOperation(value = "获取条件列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "brandId", value = "品牌Id", required = false, dataType = "Integer",defaultValue ="99"),
            @ApiImplicitParam(name = "districtId", value = "区域", required = false, dataType = "Integer",defaultValue ="99"),
            @ApiImplicitParam(name = "hallType", value = "影厅类型", required = false, dataType = "Integer",defaultValue ="99"),
    })
    @RequestMapping(value = "getCondition",method = RequestMethod.GET)
    public ResponseVO getCondition(CinemaQueryVO cinemaQueryVO){
        try {
            //获取三个集合 然后封装成一个对象 返回即可
            List<BrandVO> brands = cinemaServiceApi.getBrands(cinemaQueryVO.getBrandId());
            List<AreaVO> areas = cinemaServiceApi.getAreas(cinemaQueryVO.getDistrictId());
            List<HallTypeVO> hallTypes = cinemaServiceApi.getHallTypes(cinemaQueryVO.getHallType());

            CinemaConditionResponseVO cinemaConditionResponseVO=new CinemaConditionResponseVO();
            cinemaConditionResponseVO.setBrandList(brands);
            cinemaConditionResponseVO.setAreaList(areas);
            cinemaConditionResponseVO.setHallTypeList(hallTypes);

            return ResponseVO.success(cinemaConditionResponseVO);
        }catch (Exception e){
            log.error("获取条件列表失败",e);
            return ResponseVO.serviceFail("获取影院查询条件失败");
        }

    }

    @ApiOperation(value = "获取影院上映电影列表")
    @ApiImplicitParam(name = "cinemaId", value = "影院Id", required = true, dataType = "Integer")
    @RequestMapping(value = "getFields",method = RequestMethod.POST)
    public ResponseVO getFields(Integer cinemaId){
        try{
            CinemaInfoVO cinemaInfoVO=cinemaServiceApi.getCinemaInfoById(cinemaId);
            List<FilmInfoVO> filmInfoVOS=cinemaServiceApi.getFilmInfoByCinemaId(cinemaId);

            CinemaFieldsResponseVO cinemaFieldsResponseVO =new CinemaFieldsResponseVO();
            cinemaFieldsResponseVO.setCinemaInfo(cinemaInfoVO);
            cinemaFieldsResponseVO.setFilmList(filmInfoVOS);
            return ResponseVO.success(ImgConst.IMGSRC, cinemaFieldsResponseVO);
        }catch (Exception e){
            log.error("获取播放场地失败",e);
            return ResponseVO.serviceFail("获取播放场地失败");
        }
    }

    @ApiOperation(value = "获取场次信息,用于选座座位")
    @ApiImplicitParam(name = "fieldId", value = "场次Id", required = true, dataType = "Integer")
    @RequestMapping(value = "getFieldInfo",method = RequestMethod.POST)
    public ResponseVO getFieldInfo(Integer fieldId){
        try{
            CinemaInfoVO cinemaInfoById = cinemaServiceApi.getCinemaInfoById(cinemaServiceApi.getCinemaIdByFieldId(fieldId));
            FilmInfoVO filmInfoByFieldId = cinemaServiceApi.getFilmInfoByFieldId(fieldId);
            HallInfoVO filmFieldInfo = cinemaServiceApi.getFilmFieldInfo(fieldId);

            filmFieldInfo.setSoldSeats(orderServiceApi.getSoldSeatsByFieldId(fieldId));

            CinemaFieldResponseVO cinemaFieldResponseVO=new CinemaFieldResponseVO();
            cinemaFieldResponseVO.setCinemaInfo(cinemaInfoById);
            cinemaFieldResponseVO.setFilmInfo(filmInfoByFieldId);
            cinemaFieldResponseVO.setHallInfo(filmFieldInfo);
            cinemaFieldResponseVO.setBeginTime(cinemaServiceApi.getFieldBeginTime(fieldId));

            return ResponseVO.success(ImgConst.IMGSRC,cinemaFieldResponseVO);
        }catch (Exception e){
            log.error("获取选座信息失败",e);
            return ResponseVO.serviceFail("获取选座信息失败");
        }
    }
}
