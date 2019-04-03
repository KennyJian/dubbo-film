package com.stylefeng.guns.rest.common.persistence.model;

import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author kenny
 * @since 2019-01-20
 */
@TableName("comment_like_t")
public class CommentLikeT extends Model<CommentLikeT> {

    private static final long serialVersionUID = 1L;

    @TableId("UUID")
    private String uuid;
    @TableField("comment_id")
    private String commentId;
    @TableField("user_id")
    private Integer userId;
    @TableField("create_time")
    private Date createTime;


    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    protected Serializable pkVal() {
        return this.uuid;
    }

    @Override
    public String toString() {
        return "CommentLikeT{" +
        "uuid=" + uuid +
        ", commentId=" + commentId +
        ", userId=" + userId +
        ", createTime=" + createTime +
        "}";
    }
}
