package com.stylefeng.guns.rest.modular.comment;


import com.alibaba.dubbo.config.annotation.Reference;
import com.stylefeng.guns.api.comment.CommentServiceApi;
import com.stylefeng.guns.rest.common.CurrentUser;
import com.stylefeng.guns.rest.modular.vo.ResponseVO;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/comment/")
public class CommentController {

    @Reference(interfaceClass = CommentServiceApi.class)
    private CommentServiceApi commentServiceApi;

    @ApiOperation(value = "提交评论")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "filmId", value = "电影id", required = true, dataType = "Integer"),
            @ApiImplicitParam(name = "comment", value = "评论", required = true, dataType = "String")
    })
    @RequestMapping(value = "commit",method = RequestMethod.POST)
    public ResponseVO commit(Integer filmId,String comment){
        //获取当前登陆人的信息
        String userId= CurrentUser.getCurrentUser();
        if (userId!=null&&userId.trim().length()>0){
            Integer userIdByInt=Integer.parseInt(userId);
            if(commentServiceApi.commitComment(filmId,comment,userIdByInt)){
                return ResponseVO.success("评论成功");
            }
            return ResponseVO.serviceFail("评论失败");
        }else {
            return ResponseVO.serviceFail("用户未登录");
        }
    }

}
