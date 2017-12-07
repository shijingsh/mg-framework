package com.mg.common.entity;

import com.mg.framework.entity.metadata.MExpressGroupEntity;
import com.mg.framework.entity.model.BaseEntity;
import com.mg.framework.utils.StatusEnum;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 角色表
 * @author liukefu
 */
@Entity
@Table(name="sys_role")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class RoleEntity extends BaseEntity {
    /**
     * 角色名称
     */
    private String name;
    /**
     * 角色描述
     */
    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String description;

    /**
     * status
     * 0---->> 无效
     * 1---->> 有效
     */
    private Integer status = StatusEnum.STATUS_VALID;
    /**
     * 角色人员, 静态人员
     */
    @ManyToMany(fetch=FetchType.LAZY)
    private List<UserEntity> members = new ArrayList<>();

    /**
     * 角色动态匹配范围
     */
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "express_id")
    protected MExpressGroupEntity membersScope;

    /**
     * 字段权限
     */
    @ManyToMany(fetch = FetchType.LAZY,mappedBy = "belongRole")
    private List<PermissionEntity> permissionList = new ArrayList<>();

    /**
     * 数据范围
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY,mappedBy = "belongRole")
    private List<RoleDataScopeEntity> dataScopeList = new ArrayList<>();

    /**
     * 菜单资源范围
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY,mappedBy = "belongRole")
    private List<UrlResourcesEntity> urlResources = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<UserEntity> getMembers() {
        return members;
    }

    public void setMembers(List<UserEntity> members) {
        this.members = members;
    }

    public MExpressGroupEntity getMembersScope() {
        return membersScope;
    }

    public void setMembersScope(MExpressGroupEntity membersScope) {
        this.membersScope = membersScope;
    }

    public List<PermissionEntity> getPermissionList() {
        return permissionList;
    }

    public void setPermissionList(List<PermissionEntity> permissionList) {
        this.permissionList = permissionList;
    }

    public List<UrlResourcesEntity> getUrlResources() {
        return urlResources;
    }

    public void setUrlResources(List<UrlResourcesEntity> urlResources) {
        this.urlResources = urlResources;
    }

    public List<RoleDataScopeEntity> getDataScopeList() {
        return dataScopeList;
    }

    public void setDataScopeList(List<RoleDataScopeEntity> dataScopeList) {
        this.dataScopeList = dataScopeList;
    }
}
