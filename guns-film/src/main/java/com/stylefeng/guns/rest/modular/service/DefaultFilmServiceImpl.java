package com.stylefeng.guns.rest.modular.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.film.FilmServiceApi;
import com.stylefeng.guns.api.film.vo.*;
import com.stylefeng.guns.core.util.DateUtil;
import com.stylefeng.guns.rest.common.persistence.dao.*;
import com.stylefeng.guns.rest.common.persistence.model.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Service(interfaceClass = FilmServiceApi.class)
public class DefaultFilmServiceImpl implements FilmServiceApi {

    @Autowired
    private KennyBannerTMapper kennyBannerTMapper;

    @Autowired
    private KennyFilmTMapper kennyFilmTMapper;

    @Autowired
    private KennyCatDictTMapper kennyCatDictTMapper;

    @Autowired
    private KennySourceDictTMapper kennySourceDictTMapper;

    @Autowired
    private KennyYearDictTMapper kennyYearDictTMapper;

    @Autowired
    private KennyFilmInfoTMapper kennyFilmInfoTMapper;

    @Autowired
    private KennyActorTMapper kennyActorTMapper;

    @Override
    public List<BannerVO> getBanners() {
        List<KennyBannerT> kennyBannerTList=kennyBannerTMapper.selectList(null);
        List<BannerVO> bannerVOList=new ArrayList<>();
        for(KennyBannerT kennyBannerT:kennyBannerTList){
            BannerVO bannerVO=new BannerVO();
            BeanUtils.copyProperties(kennyBannerT,bannerVO);
            bannerVOList.add(bannerVO);
        }
        return bannerVOList;
    }

    private List<FilmInfo> getFilmInfos(List<KennyFilmT> kennyFilmTList){
        List<FilmInfo> filmInfos=new ArrayList<>();
        for(KennyFilmT kennyFilmT:kennyFilmTList){
            FilmInfo filmInfo=new FilmInfo();
            filmInfo.setScore(kennyFilmT.getFilmScore());
            filmInfo.setImgAddress(kennyFilmT.getImgAddress());
            filmInfo.setFilmType(kennyFilmT.getFilmType());
            filmInfo.setFilmScore(kennyFilmT.getFilmScore());
            filmInfo.setFilmName(kennyFilmT.getFilmName());
            filmInfo.setFilmId(kennyFilmT.getUuid()+"");
            filmInfo.setExpectNum(kennyFilmT.getFilmPresalenum());
            filmInfo.setBoxNum(kennyFilmT.getFilmBoxOffice());
            filmInfo.setShowTime(DateUtil.getDay(kennyFilmT.getFilmTime()));
            filmInfos.add(filmInfo);
        }
        return filmInfos;
    }

    @Override
    public FilmVO getHotFilms(boolean isLimit,int nums,int nowPage,int sortId,int sourceId,int yearId,int catId) {
        FilmVO filmVO=new FilmVO();
        List<FilmInfo> filmInfos=new ArrayList<>();
        //热映影片的限制条件
        EntityWrapper<KennyFilmT> kennyFilmTEntityWrapper=new EntityWrapper<>();
        kennyFilmTEntityWrapper.eq("film_status","1");
        if (isLimit){
            Page<KennyFilmT> page=new Page<>(1,nums);
            List<KennyFilmT> kennyFilmTList=kennyFilmTMapper.selectPage(page,kennyFilmTEntityWrapper);
            filmInfos=getFilmInfos(kennyFilmTList);
            filmVO.setFilmNum(filmInfos.size());
            filmVO.setFilmInfoList(filmInfos);
        }else {
            //如果不是,则是列表页,同样需要限制内容为热映影片
            Page<KennyFilmT> page=null;
            //根据sortId的不同,来组织不同的page对象
            //1-按热门搜索 2-按时间搜索 3-按评价搜索
            switch (sortId){
                case 1:
                    page=new Page<>(nowPage,nums,"film_box_office");
                    break;
                case 2:
                    page=new Page<>(nowPage,nums,"film_time");
                    break;
                case 3:
                    page=new Page<>(nowPage,nums,"film_score");
                    break;
                 default:
                     page=new Page<>(nowPage,nums,"film_box_office");
                     break;
            }
            //如果sourceId,yearId,catId 不为99,则表示要按照对应的编号进行查询
            if (sourceId!=99){
                kennyFilmTEntityWrapper.eq("film_source",sourceId);
            }
            if (yearId!=99){
                kennyFilmTEntityWrapper.eq("film_date",yearId);
            }
            if (catId!=99){
                //#2#4#22
                String catStr="%#"+catId+"#%";
                kennyFilmTEntityWrapper.like("film_cats",catStr);
            }
            List<KennyFilmT> kennyFilmTList=kennyFilmTMapper.selectPage(page,kennyFilmTEntityWrapper);
            filmInfos=getFilmInfos(kennyFilmTList);
            filmVO.setFilmNum(filmInfos.size());
            //需要总页数totalCounts/nums ->0+1=1
            int totalCounts=kennyFilmTMapper.selectCount(kennyFilmTEntityWrapper);
            int totalPages=(totalCounts/nums)+1;
            filmVO.setFilmInfoList(filmInfos);
            filmVO.setTotalPage(totalPages);
            filmVO.setNowPage(nowPage);
        }
        return filmVO;
    }

    @Override
    public FilmVO getSoonFilms(boolean isLimit,int nums,int nowPage,int sortId,int sourceId,int yearId,int catId) {
        FilmVO filmVO=new FilmVO();
        List<FilmInfo> filmInfos=new ArrayList<>();
        //热映影片的限制条件
        EntityWrapper<KennyFilmT> kennyFilmTEntityWrapper=new EntityWrapper<>();
        kennyFilmTEntityWrapper.eq("film_status","2");
        if (isLimit){
            Page<KennyFilmT> page=new Page<>(1,nums);
            List<KennyFilmT> kennyFilmTList=kennyFilmTMapper.selectPage(page,kennyFilmTEntityWrapper);
            filmInfos=getFilmInfos(kennyFilmTList);
            filmVO.setFilmNum(filmInfos.size());
            filmVO.setFilmInfoList(filmInfos);
        }else {
            //如果不是,则是列表页,同样需要限制内容为即将上映影片
            Page<KennyFilmT> page=null;
            //根据sortId的不同,来组织不同的page对象
            //1-按热门搜索 2-按时间搜索 3-按评价搜索
            switch (sortId){
                case 1:
                    page=new Page<>(nowPage,nums,"film_preSaleNum");
                    break;
                case 2:
                    page=new Page<>(nowPage,nums,"film_time");
                    break;
                case 3:
                    page=new Page<>(nowPage,nums,"film_preSaleNum");
                    break;
                default:
                    page=new Page<>(nowPage,nums,"film_preSaleNum");
                    break;
            }
            //如果sourceId,yearId,catId 不为99,则表示要按照对应的编号进行查询
            if (sourceId!=99){
                kennyFilmTEntityWrapper.eq("film_source",sourceId);
            }
            if (yearId!=99){
                kennyFilmTEntityWrapper.eq("film_date",yearId);
            }
            if (catId!=99){
                //#2#4#22
                String catStr="%#"+catId+"#%";
                kennyFilmTEntityWrapper.like("film_source",catStr);
            }
            List<KennyFilmT> kennyFilmTList=kennyFilmTMapper.selectPage(page,kennyFilmTEntityWrapper);
            filmInfos=getFilmInfos(kennyFilmTList);
            filmVO.setFilmNum(filmInfos.size());
            //需要总页数totalCounts/nums ->0+1=1
            int totalCounts=kennyFilmTMapper.selectCount(kennyFilmTEntityWrapper);
            int totalPages=(totalCounts/nums)+1;
            filmVO.setFilmInfoList(filmInfos);
            filmVO.setTotalPage(totalPages);
            filmVO.setNowPage(nowPage);
        }
        return filmVO;
    }

    @Override
    public FilmVO getClassicFilms(int nums, int nowPage, int sortId, int sourceId, int yearId, int catId) {
        FilmVO filmVO=new FilmVO();
        List<FilmInfo> filmInfos=new ArrayList<>();
        //热映影片的限制条件
        EntityWrapper<KennyFilmT> kennyFilmTEntityWrapper=new EntityWrapper<>();
        kennyFilmTEntityWrapper.eq("film_status","3");
        //如果不是,则是列表页,同样需要限制内容为即将上映影片
        Page<KennyFilmT> page=null;
        //根据sortId的不同,来组织不同的page对象
        //1-按热门搜索 2-按时间搜索 3-按评价搜索
        switch (sortId){
            case 1:
                page=new Page<>(nowPage,nums,"film_box_office");
                break;
            case 2:
                page=new Page<>(nowPage,nums,"film_time");
                break;
            case 3:
                page=new Page<>(nowPage,nums,"film_score");
                break;
            default:
                page=new Page<>(nowPage,nums,"film_box_office");
                break;
        }
        //如果sourceId,yearId,catId 不为99,则表示要按照对应的编号进行查询
        if (sourceId!=99){
            kennyFilmTEntityWrapper.eq("film_source",sourceId);
        }
        if (yearId!=99){
            kennyFilmTEntityWrapper.eq("film_date",yearId);
        }
        if (catId!=99){
            //#2#4#22
            String catStr="%#"+catId+"#%";
            kennyFilmTEntityWrapper.like("film_cats",catStr);
        }
        List<KennyFilmT> kennyFilmTList=kennyFilmTMapper.selectPage(page,kennyFilmTEntityWrapper);
        filmInfos=getFilmInfos(kennyFilmTList);
        filmVO.setFilmNum(filmInfos.size());
        //需要总页数totalCounts/nums ->0+1=1
        int totalCounts=kennyFilmTMapper.selectCount(kennyFilmTEntityWrapper);
        int totalPages=(totalCounts/nums)+1;
        filmVO.setFilmInfoList(filmInfos);
        filmVO.setTotalPage(totalPages);
        filmVO.setNowPage(nowPage);
        return filmVO;
    }

    @Override
    public List<FilmInfo> getBoxRanking() {
        //条件->正在上映的 票房前10名
        //热映影片的限制条件
        EntityWrapper<KennyFilmT> kennyFilmTEntityWrapper=new EntityWrapper<>();
        kennyFilmTEntityWrapper.eq("film_status","1");
        Page<KennyFilmT> page=new Page<>(1,10,"film_box_office");
        List<KennyFilmT> kennyFilmTList=kennyFilmTMapper.selectPage(page,kennyFilmTEntityWrapper);

        return getFilmInfos(kennyFilmTList);
    }

    @Override
    public List<FilmInfo> getExpectRanking() {
        //条件->即将上映的 预售前10名
        //热映影片的限制条件
        EntityWrapper<KennyFilmT> kennyFilmTEntityWrapper=new EntityWrapper<>();
        kennyFilmTEntityWrapper.eq("film_status","2");
        Page<KennyFilmT> page=new Page<>(1,10,"film_preSaleNum");
        List<KennyFilmT> kennyFilmTList=kennyFilmTMapper.selectPage(page,kennyFilmTEntityWrapper);

        return getFilmInfos(kennyFilmTList);
    }

    @Override
    public List<FilmInfo> getTop() {
        //条件->正在上映的 评分前10名
        //热映影片的限制条件
        EntityWrapper<KennyFilmT> kennyFilmTEntityWrapper=new EntityWrapper<>();
        kennyFilmTEntityWrapper.eq("film_status","1");
        Page<KennyFilmT> page=new Page<>(1,10,"film_score");
        List<KennyFilmT> kennyFilmTList=kennyFilmTMapper.selectPage(page,kennyFilmTEntityWrapper);

        return getFilmInfos(kennyFilmTList);
    }

    @Override
    public List<CatVO> getCats() {
        List<CatVO> catVOList=new ArrayList<>();
        List<KennyCatDictT> kennyCatDictTList=kennyCatDictTMapper.selectList(null);
        for(KennyCatDictT kennyCatDictT:kennyCatDictTList){
            CatVO catVO=new CatVO();
            catVO.setCatId(kennyCatDictT.getUuid()+"");
            catVO.setCatName(kennyCatDictT.getShowName());

            catVOList.add(catVO);
        }
        return catVOList;
    }

    @Override
    public List<SourceVO> getSources() {
        List<SourceVO> sourceVOList=new ArrayList<>();
        List<KennySourceDictT> kennySourceDictTList=kennySourceDictTMapper.selectList(null);
        for(KennySourceDictT kennySourceDictT:kennySourceDictTList){
            SourceVO sourceVO=new SourceVO();
            sourceVO.setSourceId(kennySourceDictT.getUuid()+"");
            sourceVO.setSourceName(kennySourceDictT.getShowName());

            sourceVOList.add(sourceVO);
        }
        return sourceVOList;
    }

    @Override
    public List<YearVO> getYears() {
        List<YearVO> yearVOList=new ArrayList<>();
        List<KennyYearDictT> kennyYearDictTList=kennyYearDictTMapper.selectList(null);
        for(KennyYearDictT kennyYearDictT:kennyYearDictTList){
            YearVO yearVO=new YearVO();
            yearVO.setYearId(kennyYearDictT.getUuid()+"");
            yearVO.setYearName(kennyYearDictT.getShowName());

            yearVOList.add(yearVO);
        }
        return yearVOList;
    }

    @Override
    public FilmDetailVO getFilmDetail(int searchType, String searchParam) {
        FilmDetailVO filmDetailVO=null;
        //searchType 1-按名称 2按ID查找
        if (searchType==1){
            filmDetailVO=kennyFilmTMapper.getFilmDetailByName("%"+searchParam+"%");
        }else {
            filmDetailVO=kennyFilmTMapper.getFilmDetailById(searchParam);
        }
        return filmDetailVO;
    }

    private KennyFilmInfoT getFilmInfo(String filmId){
        KennyFilmInfoT kennyFilmInfoT=new KennyFilmInfoT();
        kennyFilmInfoT.setFilmId(filmId);
        kennyFilmInfoT=kennyFilmInfoTMapper.selectOne(kennyFilmInfoT);
        return kennyFilmInfoT;
    }

    @Override
    public FilmDescVO getFilmDesc(String filmId) {
        KennyFilmInfoT kennyFilmInfoT=getFilmInfo(filmId);
        FilmDescVO filmDescVO=new FilmDescVO();
        filmDescVO.setBiography(kennyFilmInfoT.getBiography());
        filmDescVO.setFilmId(filmId);
        return filmDescVO;
    }

    @Override
    public ImgVO getImgs(String filmId) {
        KennyFilmInfoT kennyFilmInfoT=getFilmInfo(filmId);
        //图片地址施五个以逗号为分割的链接URL
        String filmImgStr=kennyFilmInfoT.getFilmImgs();
        String[] filmImgs=filmImgStr.split(",");
        ImgVO imgVO=new ImgVO();
        imgVO.setMainImg(filmImgs[0]);
        imgVO.setImg01(filmImgs[1]);
        imgVO.setImg02(filmImgs[2]);
        imgVO.setImg03(filmImgs[3]);
        imgVO.setImg04(filmImgs[4]);

        return imgVO;
    }

    @Override
    public ActorVO getDectInfo(String filmId) {
        KennyFilmInfoT kennyFilmInfoT=getFilmInfo(filmId);
        //获取导演编号
        Integer directId=kennyFilmInfoT.getDirectorId();
        KennyActorT kennyActorT=kennyActorTMapper.selectById(directId);
        ActorVO actorVO=new ActorVO();
        actorVO.setImgAddress(kennyActorT.getActorImg());
        actorVO.setDirectorName(kennyActorT.getActorName());
        return actorVO;
    }

    @Override
    public List<ActorVO> getActors(String filmId) {
        List<ActorVO> actors=kennyActorTMapper.getActors(filmId);
        return actors;
    }
}
