package com.mg.framework.entity.metadata;

import com.mg.framework.entity.model.BaseEntity;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 元数据 条件组
 * @author liukefu
 */
@Entity
@Table(name="sys_meta_express_group")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class MExpressGroupEntity extends BaseEntity {

    /**
     * 查询的元数据对象
     */
    @ManyToOne
    @JoinColumn(name = "mobject_id")
    private MObjectEntity metaObject;
    /**
     * 当动态组没有条件的时候，是否代表查询全部
     */
    private Boolean isEmptySearchAll = true;

    /**
     * 需要匹配的条件组合
     */
    @OneToOne(optional = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "match_express_id")
    private MExpressionEntity matched;

    /**
     * 需要排除在外的条件组合
     */
    @OneToOne(optional = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "except_express_id")
    private MExpressionEntity excepted;

    /**
     * 查询是否去重复
     */
    private Boolean isDistinct = false;

    /**
     * 手工指定对象的ID范围
     */
    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String objectIds ;
    /**
     * 当前的页数
     */
    @Transient
    private int pageNo = 1;

    /**
     * 当前每页所显示的行数。
     */
    @Transient
    private int pageSize = 15;

    /**
     * 扩展信息
     */
    @Transient
    private Object extendData;
    /**
     * 排序字段列表
     * 暂时不存储
     */
    @Transient
    private List<MOrderBy> orderByList = new ArrayList<>();

    @Transient
    private List<String> conditions = new ArrayList<>();

    public MExpressGroupEntity(MExpressionEntity matched) {
        this.matched = matched;
    }

    public MExpressGroupEntity(MExpressionEntity matched, MExpressionEntity excepted) {
        this.matched = matched;
        this.excepted = excepted;
    }

    public MExpressGroupEntity(MExpressionEntity matched, MExpressionEntity excepted, Boolean isEmptySearchAll) {
        this.isEmptySearchAll = isEmptySearchAll;
        this.matched = matched;
        this.excepted = excepted;
    }

    public MExpressGroupEntity() {
    }

    public String getObjectIds() {
        return objectIds;
    }

    public void setObjectIds(String objectIds) {
        this.objectIds = objectIds;
    }

    public Boolean getIsDistinct() {
        return isDistinct;
    }

    public void setIsDistinct(Boolean isDistinct) {
        this.isDistinct = isDistinct;
    }

    public List<MOrderBy> getOrderByList() {
        return orderByList;
    }

    public void setOrderByList(List<MOrderBy> orderByList) {
        this.orderByList = orderByList;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public Boolean getIsEmptySearchAll() {
        return isEmptySearchAll;
    }
    public void setIsEmptySearchAll(Boolean isEmptySearchAll) {
        this.isEmptySearchAll = isEmptySearchAll;
    }
    public void setEmptySearchAll(Boolean isEmptySearchAll) {
        this.isEmptySearchAll = isEmptySearchAll;
    }
    public MExpressionEntity getMatched() {
        return matched;
    }

    public void setMatched(MExpressionEntity matched) {
        this.matched = matched;
    }

    public MExpressionEntity getExcepted() {
        return excepted;
    }

    public void setExcepted(MExpressionEntity excepted) {
        this.excepted = excepted;
    }

    public MObjectEntity getMetaObject() {
        return metaObject;
    }

    public void setMetaObject(MObjectEntity metaObject) {
        this.metaObject = metaObject;
    }

    public Object getExtendData() {
        return extendData;
    }

    public void setExtendData(Object extendData) {
        this.extendData = extendData;
    }

    public List<String> getConditions() {
        return conditions;
    }

    public void setConditions(List<String> conditions) {
        this.conditions = conditions;
    }
}
