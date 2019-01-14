package com.stylefeng.guns.rest.modular.film;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.rpc.RpcContext;
import com.stylefeng.guns.api.film.FilmAsyncServiceApi;
import com.stylefeng.guns.api.film.FilmServiceApi;
import com.stylefeng.guns.api.film.vo.*;
import com.stylefeng.guns.rest.modular.film.vo.FilmConditionVO;
import com.stylefeng.guns.rest.modular.film.vo.FilmIndexVO;
import com.stylefeng.guns.rest.modular.film.vo.FilmRequestVO;
import com.stylefeng.guns.rest.modular.vo.ResponseVO;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@RestController
@RequestMapping("/film/")
public class FilmController {

    private static final String IMG_PRE="chong10010.cn/";

    @Reference(interfaceClass = FilmServiceApi.class)
    private FilmServiceApi filmServiceApi;

    @Reference(interfaceClass = FilmAsyncServiceApi.class,async = true)
    private FilmAsyncServiceApi filmAsyncServiceApi;

    //获取首页信息接口
    @RequestMapping(value = "getIndex",method = RequestMethod.GET)
    public ResponseVO getIndex(){
        FilmIndexVO filmIndexVO=new FilmIndexVO();
        //获取banner信息
        filmIndexVO.setBanners(filmServiceApi.getBanners());
        //获取正在热映的电影
        filmIndexVO.setHotFilms(filmServiceApi.getHotFilms(true,8,1,1,99,99,99));
        //获取即将上映的电影
        filmIndexVO.setSoonFilms(filmServiceApi.getSoonFilms(true,8,1,1,99,99,99));
        //票房排行榜
        filmIndexVO.setBoxRanking(filmServiceApi.getBoxRanking());
        //获取受欢迎的榜单
        filmIndexVO.setExpectRanking(filmServiceApi.getExpectRanking());
        //获取前一百
        filmIndexVO.setTop100(filmServiceApi.getTop());
        return ResponseVO.success(IMG_PRE,filmIndexVO);
    }

    @RequestMapping(value = "getConditionList",method = RequestMethod.GET)
    public ResponseVO getConditionList(@RequestParam(value ="catId" ,required = false,defaultValue = "99")String catId,
                                       @RequestParam(value ="sourceId" ,required = false,defaultValue = "99")String sourceId,
                                       @RequestParam(value ="yearId" ,required = false,defaultValue = "99")String yearId){
        FilmConditionVO filmConditionVO=new FilmConditionVO();
        //标识位
        boolean catFlag=false;
        //类型集合
        List<CatVO> cats=filmServiceApi.getCats();
        List<CatVO> catResult=new ArrayList<>();
        CatVO cat=new CatVO();
        for(CatVO catVO:cats){
            //判断集合是否存在catId,如果存在,则将对应的实体变成active状态
            if(catVO.getCatId().equals("99")){
                cat=catVO;
                continue;
            }
            if (catVO.getCatId().equals(catId)){
                catFlag=true;
                catVO.setActive(true);
            }else {
                catVO.setActive(false);
            }
            catResult.add(catVO);
            //如果不存在,则默认将全部变为Active状态
        }
        if(!catFlag){
            cat.setActive(true);
            catResult.add(cat);
        }else {
            cat.setActive(false);
            catResult.add(cat);
        }

        //片源集合
        boolean sourceFlag=false;
        List<SourceVO> sources=filmServiceApi.getSources();
        List<SourceVO> sourceResult=new ArrayList<>();
        SourceVO source=new SourceVO();
        for(SourceVO sourceVO:sources){
            //判断集合是否存在catId,如果存在,则将对应的实体变成active状态
            if(sourceVO.getSourceId().equals("99")){
                source=sourceVO;
                continue;
            }
            if (sourceVO.getSourceId().equals(sourceId)){
                sourceFlag=true;
                sourceVO.setActive(true);
            }else {
                sourceVO.setActive(false);
            }
            sourceResult.add(sourceVO);
        }
        //如果不存在,则默认将全部变为Active状态
        if(!sourceFlag){
            source.setActive(true);
            sourceResult.add(source);
        }else {
            source.setActive(false);
            sourceResult.add(source);
        }
        //年代集合
        boolean yearFlag=false;
        List<YearVO> years=filmServiceApi.getYears();
        List<YearVO> yearResult=new ArrayList<>();
        YearVO year=new YearVO();
        for(YearVO yearVO:years){
            //判断集合是否存在catId,如果存在,则将对应的实体变成active状态
            if(yearVO.getYearId().equals("99")){
                year=yearVO;
                continue;
            }
            if (yearVO.getYearId().equals(yearId)){
                yearFlag=true;
                yearVO.setActive(true);
            }else {
                yearVO.setActive(false);
            }
            yearResult.add(yearVO);
            //如果不存在,则默认将全部变为Active状态
        }
        if(!yearFlag){
            year.setActive(true);
            yearResult.add(year);
        }else {
            year.setActive(false);
            yearResult.add(year);
        }
        filmConditionVO.setCatInfo(catResult);
        filmConditionVO.setSourceInfo(sourceResult);
        filmConditionVO.setYearInfo(yearResult);
        return ResponseVO.success(filmConditionVO);
    }

    @RequestMapping(value = "getFilms",method = RequestMethod.GET)
    public ResponseVO getFilms(FilmRequestVO filmRequestVO){
        String img_pre="http://www.chong10010.cn/";
        FilmVO filmVO=null;
        //根据showType判断影片查询类型
        switch (filmRequestVO.getShowType()){

            case 1:
                filmVO=filmServiceApi.getHotFilms(false,filmRequestVO.getPageSize(),filmRequestVO.getNowPage(),filmRequestVO.getSortId(),filmRequestVO.getSourceId(),filmRequestVO.getYearId(),filmRequestVO.getCatId());
                break;
            case 2:
                filmVO=filmServiceApi.getSoonFilms(false,filmRequestVO.getPageSize(),filmRequestVO.getNowPage(),filmRequestVO.getSortId(),filmRequestVO.getSourceId(),filmRequestVO.getYearId(),filmRequestVO.getCatId());
                break;
            case 3:
                filmVO=filmServiceApi.getClassicFilms(filmRequestVO.getPageSize(),filmRequestVO.getNowPage(),filmRequestVO.getSortId(),filmRequestVO.getSourceId(),filmRequestVO.getYearId(),filmRequestVO.getCatId());
                break;
            default:
                filmVO=filmServiceApi.getHotFilms(false,filmRequestVO.getPageSize(),filmRequestVO.getNowPage(),filmRequestVO.getSortId(),filmRequestVO.getSourceId(),filmRequestVO.getYearId(),filmRequestVO.getCatId());
                break;
        }
        //根据sortId排序
        //添加各种条件查询
        //判断当前是第几页


        return ResponseVO.success(filmVO.getNowPage(),filmVO.getTotalPage(),img_pre,filmVO.getFilmInfoList());
    }

    @RequestMapping(value = "films/{searchParam}",method = RequestMethod.GET)
    public ResponseVO films(@PathVariable("searchParam")String searchParam,int searchType) throws ExecutionException, InterruptedException {

        //根据searchType,判断查询类型
        FilmDetailVO filmDetail=filmServiceApi.getFilmDetail(searchType,searchParam);
        if(filmDetail==null){
            return ResponseVO.serviceFail("没有可查询的影片");
        }else if(filmDetail.getFilmId()==null||filmDetail.getFilmId().trim().length()==0){
            return ResponseVO.serviceFail("没有可查询的影片");
        }
        String filmId=filmDetail.getFilmId();
        //查询影片的详细信息->Dubbo的异步获取
        //获取影片描述信息
//        FilmDescVO filmDescVO=filmAsyncServiceApi.getFilmDesc(filmId);
        filmAsyncServiceApi.getFilmDesc(filmId);
        Future<FilmDescVO> filmDescVOFuture= RpcContext.getContext().getFuture();
        //获取图片信息
//        ImgVO imgVO=filmAsyncServiceApi.getImgs(filmId);
        filmAsyncServiceApi.getImgs(filmId);
        Future<ImgVO> imgVOFuture= RpcContext.getContext().getFuture();
        //获取导演信息
//        ActorVO directorVO=filmAsyncServiceApi.getDectInfo(filmId);
        filmAsyncServiceApi.getDectInfo(filmId);
        Future<ActorVO> actorVOFuture= RpcContext.getContext().getFuture();
        //获取演员信息
//        List<ActorVO> actors=filmAsyncServiceApi.getActors(filmId);
        filmAsyncServiceApi.getActors(filmId);
        Future<List<ActorVO>> actorsVOFutrue= RpcContext.getContext().getFuture();

        InfoRequestVO infoRequestVO=new InfoRequestVO();
        //组织Actor属性
        ActorRequestVO actorRequestVO=new ActorRequestVO();
        actorRequestVO.setActors(actorsVOFutrue.get());
        actorRequestVO.setDirector(actorVOFuture.get());

        //组织Info对象
        infoRequestVO.setActors(actorRequestVO);
        infoRequestVO.setBiography(filmDescVOFuture.get().getBiography());
        infoRequestVO.setFilmId(filmId);
        infoRequestVO.setImgVO(imgVOFuture.get());

        //组织成返回值
        filmDetail.setInfo04(infoRequestVO);
        return ResponseVO.success("http://www.chong10010.cn/",filmDetail);
    }

}
