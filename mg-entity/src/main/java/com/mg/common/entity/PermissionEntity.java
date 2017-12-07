package com.mg.common.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.mg.common.entity.vo.PermissionActionEnum;
import com.mg.framework.entity.metadata.MObjectEntity;
import com.mg.framework.entity.metadata.MirrorPropertyEntity;
import com.mg.framework.entity.model.BaseEntity;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;

/**
 * 权限表
 * @author liukefu
 */
@Entity
@Table(name="p_permission")
public class PermissionEntity extends BaseEntity {
    /**
     * 所属元数据对象
     */
    @ManyToOne
    @JoinColumn(name = "belong_mobject_id")
    protected MObjectEntity belongMObject;
    /**
     * 所属角色对象
     */
    @ManyToOne
    @JoinColumn(name="role_id")
    @JSONField(serialize=false,deserialize=false)
    private RoleEntity belongRole;
    /**
     * 字段权限对应的元数据字段
     */
    @ManyToOne
    @JoinColumn(name = "property")
    @NotFound(action= NotFoundAction.IGNORE)
    private MirrorPropertyEntity property;
    /**
     * 权限代码
     * 比如：view, update，none
     */
    @Enumerated(EnumType.STRING)
    private PermissionActionEnum action;

    public MObjectEntity getBelongMObject() {
        return belongMObject;
    }

    public void setBelongMObject(MObjectEntity belongMObject) {
        this.belongMObject = belongMObject;
    }

    public RoleEntity getBelongRole() {
        return belongRole;
    }

    public void setBelongRole(RoleEntity belongRole) {
        this.belongRole = belongRole;
    }

    public MirrorPropertyEntity getProperty() {
        return property;
    }

    public void setProperty(MirrorPropertyEntity property) {
        this.property = property;
    }

    public PermissionActionEnum getAction() {
        return action;
    }

    public void setAction(PermissionActionEnum action) {
        this.action = action;
    }
}
