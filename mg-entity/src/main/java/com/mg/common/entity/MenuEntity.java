package com.mg.common.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.mg.common.entity.vo.MenuDeepComparator;
import com.mg.common.entity.vo.MenuTypeEnum;
import com.mg.framework.entity.model.BaseEntity;
import com.mg.framework.utils.StatusEnum;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

/**
 * 菜单表
 * 菜单的设计和原来有所区别，一个普通的url也称为"url菜单"
 * 由MenuTypeEnum.resource来区分
 * @author liukefu
 */
@Entity
@Table(name="sys_menu")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class MenuEntity  extends BaseEntity {

    /**
     * 菜单名称
     */
    private String name;

    /**
     * 菜单跳转路径
     */
    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String path;

    /**
     * 菜单icon路径
     */
    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String iconPath;
    /**
     * 排序值
     */
    private Integer sort = 0;

    /**
     * 层级
     */
    private Integer deep = 1;

    /**
     * 状态
     */
    private Integer status = StatusEnum.STATUS_VALID;

    /**
     * 菜单类型
     * 如果类型为资源，不显示成菜单，权限模块使用
     */
    @Enumerated(EnumType.STRING)
    private MenuTypeEnum type = MenuTypeEnum.menu;
    /**
     * 上级菜单
     */
    @ManyToOne
    @JoinColumn(name = "belong_menu_id")
    @JSONField(serialize = false, deserialize = false)
    private MenuEntity belongMenu;
    /**
     * 子菜单
     * （所有子菜单）
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY,mappedBy = "belongMenu")
    @JSONField(serialize = false, deserialize = false)
    private List<MenuEntity> menus = new ArrayList<>();

    /**
     * 上级菜单
     */
    @Transient
    private String parentMenuId ;
    /**
     * 当前菜单
     */
    @Transient
    private Boolean isSelect = false;

    /**
     * 是否有权限的
     */
    @Transient
    private Boolean isAuthority = false;

    /**
     * 是否选中
     */
    @Transient
    private Boolean checked = false;
    /**
     * 子菜单列表
     * （有权限的子菜单）
     */
    @Transient
    private TreeSet<MenuEntity> childList = new TreeSet(new MenuDeepComparator());

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getIconPath() {
        return iconPath;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public MenuTypeEnum getType() {
        return type;
    }

    public void setType(MenuTypeEnum type) {
        this.type = type;
    }

    public MenuEntity getBelongMenu() {
        return belongMenu;
    }

    public void setBelongMenu(MenuEntity belongMenu) {
        this.belongMenu = belongMenu;
    }

    public Integer getDeep() {
        return deep;
    }

    public void setDeep(Integer deep) {
        this.deep = deep;
    }

    public List<MenuEntity> getMenus() {
        return menus;
    }

    public void setMenus(List<MenuEntity> menus) {
        this.menus = menus;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getParentMenuId() {
        if(belongMenu!=null) {
            parentMenuId = belongMenu.getId();
        }
        return parentMenuId;
    }

    public void setParentMenuId(String parentMenuId) {
        if(belongMenu!=null) {
            this.parentMenuId = belongMenu.getId();
        }
    }

    public Boolean getIsSelect() {
        return isSelect;
    }

    public void setIsSelect(Boolean isSelect) {
        this.isSelect = isSelect;
    }

    public Boolean getIsAuthority() {
        return isAuthority;
    }

    public void setIsAuthority(Boolean isAuthority) {
        this.isAuthority = isAuthority;
    }

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }

    public TreeSet<MenuEntity> getChildList() {
        return childList;
    }

    public void setChildList(TreeSet<MenuEntity> childList) {
        this.childList = childList;
    }
}
