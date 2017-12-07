package com.mg.framework.entity.metadata;

import com.alibaba.fastjson.annotation.JSONField;
import com.mg.framework.entity.model.BaseEntity;
import com.mg.framework.utils.StatusEnum;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

/**
 * 我的收藏夹实体
 * @author liukefu
 */
@Entity
@Table(name="sys_meta_favorites")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class MFavoritesEntity extends BaseEntity {
    /**
     * 查询的元数据对象
     */
    @ManyToOne
    @JoinColumn(name = "belong_object_id")
    @JSONField(serialize = false, deserialize = false)
    protected MObjectEntity belongObject;
    /**
     * 名称
     */
    private String favoritesName;

    /**
     * 所属用户
     */
    private String userId;

    /**
     * 公共收藏，所有人共享
     */
    private Boolean isPublic = false;

    /**
     * 显示的元数据id列表，以分号分隔
     */
    private String properties;

    /**
     * 排序值
     */
    private Integer sort;
    /**
     * 有效状态
     */
    private Integer status = StatusEnum.STATUS_VALID;
    /**
     * 条件组对象
     */
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "express_group_id")
    private MExpressGroupEntity expressGroup;

    public MObjectEntity getBelongObject() {
        return belongObject;
    }

    public void setBelongObject(MObjectEntity belongObject) {
        this.belongObject = belongObject;
    }

    public String getFavoritesName() {
        return favoritesName;
    }

    public void setFavoritesName(String favoritesName) {
        this.favoritesName = favoritesName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    public String getProperties() {
        return properties;
    }

    public void setProperties(String properties) {
        this.properties = properties;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public MExpressGroupEntity getExpressGroup() {
        return expressGroup;
    }

    public void setExpressGroup(MExpressGroupEntity expressGroup) {
        this.expressGroup = expressGroup;
    }
}
