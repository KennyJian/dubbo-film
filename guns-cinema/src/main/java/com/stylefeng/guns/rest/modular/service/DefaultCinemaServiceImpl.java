package com.stylefeng.guns.rest.modular.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.cinema.CinemaServiceApi;
import com.stylefeng.guns.api.cinema.vo.*;
import com.stylefeng.guns.rest.common.persistence.dao.*;
import com.stylefeng.guns.rest.common.persistence.model.CinemaAreaDictT;
import com.stylefeng.guns.rest.common.persistence.model.CinemaBrandDictT;
import com.stylefeng.guns.rest.common.persistence.model.CinemaCinemaT;
import com.stylefeng.guns.rest.common.persistence.model.CinemaHallDictT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Service(interfaceClass = CinemaServiceApi.class)
public class DefaultCinemaServiceImpl implements CinemaServiceApi {

    @Autowired
    private CinemaAreaDictTMapper cinemaAreaDictTMapper;

    @Autowired
    private CinemaBrandDictTMapper cinemaBrandDictTMapper;

    @Autowired
    private CinemaCinemaTMapper cinemaCinemaTMapper;

    @Autowired
    private CinemaFieldTMapper cinemaFieldTMapper;

    @Autowired
    private CinemaHallDictTMapper cinemaHallDictTMapper;

    @Autowired
    private CinemaHallFilmInfoTMapper cinemaHallFilmInfoTMapper;


    @Override
    public Page<CinemaVO> getCinemas(CinemaQueryVO cinemaQueryVO) {
        //业务实体集合
        List<CinemaVO> cinemas=new ArrayList<>();

        Page<CinemaCinemaT> page=new Page<>(cinemaQueryVO.getNowPage(),cinemaQueryVO.getPagsSize());
        //判断是否传入查询条件->brandId,distId,hallType是否==99
        EntityWrapper<CinemaCinemaT> entityWrapper=new EntityWrapper<>();
        if(cinemaQueryVO.getBrandId()!=99){
            entityWrapper.eq("brand_id",cinemaQueryVO.getBrandId());
        }
        if(cinemaQueryVO.getDistrictId()!=99){
            entityWrapper.eq("area_id",cinemaQueryVO.getDistrictId());
        }
        if(cinemaQueryVO.getHallType()!=99){ //%#3#%
            entityWrapper.eq("hall_ids","%#+"+cinemaQueryVO.getHallType()+"+#%");
        }
        //将数据实体转换为业务实体
        List<CinemaCinemaT> cinemaCinemaTS=cinemaCinemaTMapper.selectPage(page,entityWrapper);
        for(CinemaCinemaT cinemaCinemaT:cinemaCinemaTS){
            CinemaVO cinemaVO=new CinemaVO();
            cinemaVO.setUuid(cinemaCinemaT.getUuid()+"");
            cinemaVO.setMinimumPrice(cinemaCinemaT.getMinimumPrice()+"");
            cinemaVO.setCinemaName(cinemaCinemaT.getCinemaName());
            cinemaVO.setAddress(cinemaCinemaT.getCinemaAddress());
            cinemas.add(cinemaVO);
        }
        //根据条件,判断影院列表总数
        long counts=cinemaCinemaTMapper.selectCount(entityWrapper);
        //组织返回对象
        Page<CinemaVO> result=new Page<>();
        result.setRecords(cinemas);
        result.setSize(cinemaQueryVO.getPagsSize());
        result.setTotal(counts);
        return result;
    }

    //2.根据条件获取品牌列表[除了99以为 其他数字为isActive]
    @Override
    public List<BrandVO> getBrands(int brandId) {
        boolean flag=false;
        List<BrandVO> brandVOS=new ArrayList<>();
        //判断brandId是否存在
        CinemaBrandDictT cinemaBrandDictT=cinemaBrandDictTMapper.selectById(brandId);
        //判断brandId是否等于99
        if(brandId==99||cinemaBrandDictT==null||cinemaBrandDictT.getUuid()==null){
            flag=true;
        }
        //查询所有列表
        List<CinemaBrandDictT> cinemaBrandDictTS=cinemaBrandDictTMapper.selectList(null);
        //判断flag如果为true,则将99设置为isActive
        for(CinemaBrandDictT brand:cinemaBrandDictTS){
            BrandVO brandVO=new BrandVO();
            brandVO.setBrandName(brand.getShowName());
            brandVO.setBrandId(brand.getUuid()+"");
            //如果flag为true 则需要99 如果为false 则匹配上的内容为active
            if(flag){
                if(brand.getUuid()==99){
                    brandVO.setActive(true);
                }
            }else {
                if (brand.getUuid()==brandId){
                    brandVO.setActive(true);
                }

            }
            brandVOS.add(brandVO);
        }
        return brandVOS;
    }

    //3.获取行政区域列表
    @Override
    public List<AreaVO> getAreas(int areaId) {
        boolean flag=false;
        List<AreaVO> areaVOS=new ArrayList<>();
        //判断brandId是否存在
        CinemaAreaDictT cinemaAreaDictT=cinemaAreaDictTMapper.selectById(areaId);
        //判断brandId是否等于99
        if(areaId==99||cinemaAreaDictT==null||cinemaAreaDictT.getUuid()==null){
            flag=true;
        }
        //查询所有列表
        List<CinemaAreaDictT> cinemaAreaDictTS=cinemaAreaDictTMapper.selectList(null);
        //判断flag如果为true,则将99设置为isActive
        for(CinemaAreaDictT area:cinemaAreaDictTS){
            AreaVO areaVO=new AreaVO();
            areaVO.setAreaName(area.getShowName());
            areaVO.setAreaId(area.getUuid()+"");
            //如果flag为true 则需要99 如果为false 则匹配上的内容为active
            if(flag){
                if(area.getUuid()==99){
                    areaVO.setActive(true);
                }
            }else {
                if (area.getUuid()==areaId){
                    areaVO.setActive(true);
                }

            }
            areaVOS.add(areaVO);
        }
        return areaVOS;
    }

    //4.获取影厅类型列表
    @Override
    public List<HallTypeVO> getHallTypes(int hallType) {
        boolean flag=false;
        List<HallTypeVO> hallTypeVOS=new ArrayList<>();
        //判断brandId是否存在
        CinemaHallDictT cinemaHallDictT=cinemaHallDictTMapper.selectById(hallType);
        //判断brandId是否等于99
        if(hallType==99||cinemaHallDictT==null||cinemaHallDictT.getUuid()==null){
            flag=true;
        }
        //查询所有列表
        List<CinemaHallDictT> cinemaHallDictTS=cinemaHallDictTMapper.selectList(null);
        //判断flag如果为true,则将99设置为isActive
        for(CinemaHallDictT hall:cinemaHallDictTS){
            HallTypeVO hallTypeVO=new HallTypeVO();
            hallTypeVO.setHallTypeName(hall.getShowName());
            hallTypeVO.setHallTypeId(hall.getUuid()+"");
            //如果flag为true 则需要99 如果为false 则匹配上的内容为active
            if(flag){
                if(hall.getUuid()==99){
                    hallTypeVO.setActive(true);
                }
            }else {
                if (hall.getUuid()==hallType){
                    hallTypeVO.setActive(true);
                }

            }
            hallTypeVOS.add(hallTypeVO);
        }
        return hallTypeVOS;
    }

    //5.根据影院编号,获取影院信息
    @Override
    public CinemaInfoVO getCinemaInfoById(int cinemaId) {
        //数据实体
        CinemaCinemaT cinemaCinemaT=cinemaCinemaTMapper.selectById(cinemaId);
        //将数据实体转换成业务实体
        CinemaInfoVO cinemaInfoVO=new CinemaInfoVO();
        cinemaInfoVO.setCinemaAddress(cinemaCinemaT.getCinemaAddress());
        cinemaInfoVO.setImgUrl(cinemaCinemaT.getImgAddress());
        cinemaInfoVO.setCinemaPhone(cinemaCinemaT.getCinemaPhone());
        cinemaInfoVO.setCinemaName(cinemaCinemaT.getCinemaName());
        cinemaInfoVO.setCinemaId(cinemaCinemaT.getUuid()+"");
        return cinemaInfoVO;
    }

    //6.获取所有电影的信息和对应的放映场次信息,根据影院编号
    @Override
    public List<FilmInfoVO> getFilmInfoByCinemaId(int cinemaId) {
        List<FilmInfoVO> filmInfos=cinemaFieldTMapper.getFilmInfos(cinemaId);
        return filmInfos;
    }
    //7.根据放映场次ID获取放映信息
    @Override
    public HallInfoVO getFilmFieldInfo(int fieldId) {
        HallInfoVO hallInfoVO=cinemaFieldTMapper.getHallInfo(fieldId);
        return hallInfoVO;
    }
    //8.根据放映场次查询播放的电影编号,然后根据电影编号获取对应的电影信息
    @Override
    public FilmInfoVO getFilmInfoByFieldId(int fieldId) {
        FilmInfoVO filmInfoVO=cinemaFieldTMapper.getFilmInfoById(fieldId);
        return filmInfoVO;
    }
}
