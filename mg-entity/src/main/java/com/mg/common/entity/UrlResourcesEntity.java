package com.mg.common.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.mg.framework.entity.model.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * 角色的url资源集合
 * Created by liukefu on 2016/3/1.
 */
@Entity
@Table(name="p_url_resource")
public class UrlResourcesEntity extends BaseEntity {

    /**
     * 所属角色
     */
    @ManyToOne
    @JoinColumn(name = "belong_role_id")
    @JSONField(serialize = false, deserialize = false)
    private RoleEntity belongRole;

    /**
     * 菜单
     */
    @ManyToOne
    @JoinColumn(name = "menu_id")
    @JSONField(serialize = false, deserialize = false)
    private MenuEntity menu;

    public RoleEntity getBelongRole() {
        return belongRole;
    }

    public void setBelongRole(RoleEntity belongRole) {
        this.belongRole = belongRole;
    }

    public MenuEntity getMenu() {
        return menu;
    }

    public void setMenu(MenuEntity menu) {
        this.menu = menu;
    }
}
