package com.mg.common.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.mg.framework.entity.metadata.MExpressGroupEntity;
import com.mg.framework.entity.metadata.MObjectEntity;
import com.mg.framework.entity.model.BaseEntity;

import javax.persistence.*;

/**
 * 数据范围表
 * @author liukefu
 */
@Entity
@Table(name="p_role_data_scope")
public class RoleDataScopeEntity extends BaseEntity {

    /**
     * 所属角色对象
     */
    @ManyToOne
    @JoinColumn(name="role_id")
    @JSONField(serialize=false,deserialize=false)
    private RoleEntity belongRole;

    /**
     * 所属元数据对象
     */
    @ManyToOne
    @JoinColumn(name = "belong_mobject_id")
    protected MObjectEntity belongMObject;

    /**
     * 动态数据范围
     */
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "express_id")
    protected MExpressGroupEntity dataScope;

    public RoleEntity getBelongRole() {
        return belongRole;
    }

    public void setBelongRole(RoleEntity belongRole) {
        this.belongRole = belongRole;
    }

    public MObjectEntity getBelongMObject() {
        return belongMObject;
    }

    public void setBelongMObject(MObjectEntity belongMObject) {
        this.belongMObject = belongMObject;
    }

    public MExpressGroupEntity getDataScope() {
        return dataScope;
    }

    public void setDataScope(MExpressGroupEntity dataScope) {
        this.dataScope = dataScope;
    }
}
