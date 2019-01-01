package com.stylefeng.guns.rest.modular.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.film.FilmAsyncServiceApi;
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
@Service(interfaceClass = FilmAsyncServiceApi.class)
public class DefaultFilmAsyncServiceImpl implements FilmAsyncServiceApi {

    @Autowired
    private KennyFilmInfoTMapper kennyFilmInfoTMapper;

    @Autowired
    private KennyActorTMapper kennyActorTMapper;


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
