package com.stylefeng.guns.rest.modular.film;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.rpc.RpcContext;
import com.stylefeng.guns.api.comment.CommentServiceApi;
import com.stylefeng.guns.api.comment.vo.CommentVO;
import com.stylefeng.guns.api.film.FilmAsyncServiceApi;
import com.stylefeng.guns.api.film.FilmServiceApi;
import com.stylefeng.guns.api.film.vo.*;
import com.stylefeng.guns.api.imgconst.ImgConst;
import com.stylefeng.guns.rest.modular.film.vo.FilmConditionVO;
import com.stylefeng.guns.rest.modular.film.vo.FilmIndexVO;
import com.stylefeng.guns.rest.modular.film.vo.FilmRequestVO;
import com.stylefeng.guns.rest.modular.vo.ResponseVO;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
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

    @Reference(interfaceClass = CommentServiceApi.class)
    private CommentServiceApi commentServiceApi;

    //获取首页信息接口
    @ApiOperation(value = "获取首页信息")
    @RequestMapping(value = "getIndex",method = RequestMethod.GET)
    public ResponseVO getIndex(){
        FilmIndexVO filmIndexVO=new FilmIndexVO();
        //获取banner信息
        filmIndexVO.setBanners(filmServiceApi.getBanners());
        //获取正在热映的电影
        filmIndexVO.setHotFilms(filmServiceApi.getHotFilms(true,8,1,1,99,99,99));
        //获取即将上映的电影
        filmIndexVO.setSoonFilms(filmServiceApi.getSoonFilms(true,8,1,1,99,99,99));
        //票房排行榜 正在上映的 票房前10名
        filmIndexVO.setBoxRanking(filmServiceApi.getBoxRanking());
        //获取受欢迎的榜单 预售前10名
        filmIndexVO.setExpectRanking(filmServiceApi.getExpectRanking());
        //获取前一百
        filmIndexVO.setTop100(filmServiceApi.getTop());
        return ResponseVO.success(ImgConst.IMGSRC,filmIndexVO);
    }

    @ApiOperation(value = "获取电影条件列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "catId", value = "类型", required = false, dataType = "String",defaultValue = "99"),
            @ApiImplicitParam(name = "sourceId", value = "区域", required = false, dataType = "String",defaultValue = "99"),
            @ApiImplicitParam(name = "yearId", value = "年代", required = false, dataType = "String",defaultValue = "99")
    })
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

    @ApiOperation(value = "获取电影列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "showType", value = "影片查询类型", required = false, dataType = "Integer",defaultValue = "1"),
            @ApiImplicitParam(name = "sortId", value = "排序类型", required = false, dataType = "Integer",defaultValue = "1"),
            @ApiImplicitParam(name = "sourceId", value = "片源Id", required = false, dataType = "Integer",defaultValue = "99"),
            @ApiImplicitParam(name = "catId", value = "影片类型Id", required = false, dataType = "Integer",defaultValue = "99"),
            @ApiImplicitParam(name = "yearId", value = "年代Id", required = false, dataType = "Integer",defaultValue = "99"),
            @ApiImplicitParam(name = "nowPage", value = "当前页", required = false, dataType = "Integer",defaultValue = "1"),
            @ApiImplicitParam(name = "pageSize", value = "页大小", required = false, dataType = "Integer",defaultValue = "18")
    })
    @RequestMapping(value = "getFilms",method = RequestMethod.GET)
    public ResponseVO getFilms(FilmRequestVO filmRequestVO){
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


        return ResponseVO.success(filmVO.getNowPage(),filmVO.getTotalPage(),"",filmVO.getFilmInfoList());
    }

    @ApiOperation(value = "根据电影Id或电影名查询电影详细信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "searchParam", value = "影片名称或Id", required = true, dataType = "String"),
            @ApiImplicitParam(name = "searchType", value = "按名称或Id查询 1-按名称 2按ID查找", required = true, dataType = "int")
    })
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

        //组织评论
        Integer filmeIdByInt=Integer.parseInt(filmId);
        List<CommentVO> comment = commentServiceApi.getComment(filmeIdByInt);

        //组织推荐电影
        filmAsyncServiceApi.getRecommends(filmId);
        Future<List<RecommendVO>> recommendVOFuture= RpcContext.getContext().getFuture();

        //组织成返回值
        filmDetail.setInfo04(infoRequestVO);
        filmDetail.setComments(comment);
        filmDetail.setRecommends(recommendVOFuture.get());
        return ResponseVO.success(filmDetail);
    }

    @ApiOperation(value = "根据电影名搜索相关电影")
    @ApiImplicitParams({
       @ApiImplicitParam(name = "filmName", value = "查找电影名称", required = true, dataType = "String"),
       @ApiImplicitParam(name = "nowPage", value = "当前页", required = false, dataType = "Integer",defaultValue = "1"),
       @ApiImplicitParam(name = "pageSize", value = "页大小", required = false, dataType = "Integer",defaultValue = "18")
    })
    @RequestMapping(value = "searchFilm",method = RequestMethod.GET)
    public ResponseVO getSearchResult(FilmSearchRequstVO filmSearchRequstVO){
        List<FilmSearchResultVO> filmSearchResultVOS =filmServiceApi.getSearchResults(filmSearchRequstVO);
        Integer totalPage=filmSearchResultVOS.size()/filmSearchRequstVO.getPageSize()+1;
        return ResponseVO.success(filmSearchRequstVO.getNowPage(),totalPage,"",filmSearchResultVOS);
    }
}
