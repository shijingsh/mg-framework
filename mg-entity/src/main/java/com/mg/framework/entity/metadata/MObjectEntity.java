package com.mg.framework.entity.metadata;

import com.mg.framework.entity.model.BaseEntity;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 元数据对象
 * 相当于数据表
 * @author liukefu
 */
@Entity
@Table(name="sys_meta_object")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class MObjectEntity extends BaseEntity {
    /**
     * 元数据对象名称
     * 比如：员工
     */
    private String name;
    /**
     * 第二名称（别名已经被占用）
     * 比如：业务量对象，再南昌的导入表格中为“原表”
     * 用于查询元数据对象时findByName使用，名称等于secondName时，也视为匹配
     */
    private String secondName;

    /**
     * 元数据对象所属模块名称
     * 比如：hr
     */
    private String moduleName  = "hr";
    /**
     * 表名称 t_employee
     */
    private String tableName;
    /**
     * 对象的唯一标识
     * 一般的元数据对象， name 是唯一标识
     * 这个字段可以自定义一个标识，比如编号作为唯一
     * queryIdByIdentifier 的时候，将通过这个唯一标识来检索
     */
    private String identifier = "name";
    /**
     * 是否是激活的.
     */
    private boolean isEnable = true;
    /**
     * 备注
     */
    private String remark;

    /**
     * 是否启用维护界面
     */
    private Boolean isManage = true;

    /**
     * 是否是树形选择界面
     * 对象选择器，以树形显示
     */
    private Boolean isTree = false;
    /**
     * 是否为历史表，记录时间轴的表，比如任职履历、调薪记录
     */
    private Boolean isHistory = false;

    /**
     * 是否为系统表，系统不参与业务
     * 比如：报表，提醒服务
     */
    private Boolean isSystemObject = false;

    /**
     * 是否启用权限控制
     */
    private Boolean isPermission = false;

    /**
     * 当 isTree = true 时;
     * 上级的关联字段
     */
    private String parentFieldName;
    /**
     * 对象的数据模板
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY,mappedBy = "belongMObject")
    @OrderBy("sort asc")
    protected List<MTemplateEntity> templates = new ArrayList<>();

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public boolean isEnable() {
        return isEnable;
    }

    public void setIsEnable(boolean isEnable) {
        this.isEnable = isEnable;
    }

    public Boolean getIsTree() {
        return isTree;
    }

    public void setIsTree(Boolean isTree) {
        this.isTree = isTree;
    }

    public Boolean getIsHistory() {
        return isHistory;
    }

    public void setIsHistory(Boolean isHistory) {
        this.isHistory = isHistory;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Boolean getIsManage() {
        return isManage;
    }

    public void setIsManage(Boolean isManage) {
        this.isManage = isManage;
    }

    public List<MTemplateEntity> getTemplates() {
        return templates;
    }

    public void setTemplates(List<MTemplateEntity> templates) {
        this.templates.clear();
        this.templates = templates;
    }

    public Boolean getIsSystemObject() {
        return isSystemObject;
    }

    public void setIsSystemObject(Boolean isSystemObject) {
        this.isSystemObject = isSystemObject;
    }

    public Boolean getIsPermission() {
        return isPermission;
    }

    public void setIsPermission(Boolean isPermission) {
        this.isPermission = isPermission;
    }

    public void addTemplate(MTemplateEntity template) {
        this.templates.add(template);
    }

    public String getParentFieldName() {
        return parentFieldName;
    }

    public void setParentFieldName(String parentFieldName) {
        this.parentFieldName = parentFieldName;
    }
}
