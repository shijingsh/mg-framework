package com.mg.framework.entity.metadata;

import com.alibaba.fastjson.annotation.JSONField;
import com.mg.framework.entity.model.BaseEntity;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

/**
 * 元数据的定时任务
 * Created by liukefu on 2015/10/16.
 */
@Entity
@Table(name="sys_meta_object_script")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class MObjectScriptEntity extends BaseEntity {
    /**
     * 所属元数据对象
     */
    @ManyToOne
    @JoinColumn(name = "belong_mobject_id")
    @JSONField(serialize = false, deserialize = false)
    protected MObjectEntity belongMObject;
    /**
     * 脚本名称
     */
    private String name;
    /**
     * 记录创建时，执行
     */
    private Boolean execOnInsert = false;
    /**
     * 记录修改时，执行
     */
    private Boolean execOnUpdate = false;

    /**
     * 修改指定的字段时，才执行
     * 多个属性，以分号“;”分隔
     */
    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String execOnPropertiesUpdate ;
    /**
     * 是否周期性
     * 即：需要周期性执行的脚本；每年、每月或每日执行
     */
    private Boolean periodicity = false;
    /**
     * 是周期性时
     * 满足运行脚本的条件
     * 每天凌晨，执行一次检查
     */
    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String conditionScript;
    /**
     * 执行的脚本
     */
    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String execScript;

    /**
     * 是否是激活的.
     */
    private Boolean isEnable = true;



    public MObjectEntity getBelongMObject() {
        return belongMObject;
    }

    public void setBelongMObject(MObjectEntity belongMObject) {
        this.belongMObject = belongMObject;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getExecOnInsert() {
        return execOnInsert;
    }

    public void setExecOnInsert(Boolean execOnInsert) {
        this.execOnInsert = execOnInsert;
    }

    public Boolean getExecOnUpdate() {
        return execOnUpdate;
    }

    public void setExecOnUpdate(Boolean execOnUpdate) {
        this.execOnUpdate = execOnUpdate;
    }

    public String getConditionScript() {
        return conditionScript;
    }

    public void setConditionScript(String conditionScript) {
        this.conditionScript = conditionScript;
    }

    public String getExecScript() {
        return execScript;
    }

    public void setExecScript(String execScript) {
        this.execScript = execScript;
    }

    public Boolean getPeriodicity() {
        return periodicity;
    }

    public void setPeriodicity(Boolean periodicity) {
        this.periodicity = periodicity;
    }

    public Boolean getIsEnable() {
        return isEnable;
    }

    public void setIsEnable(Boolean isEnable) {
        this.isEnable = isEnable;
    }

    public String getExecOnPropertiesUpdate() {
        return execOnPropertiesUpdate;
    }

    public void setExecOnPropertiesUpdate(String execOnPropertiesUpdate) {
        this.execOnPropertiesUpdate = execOnPropertiesUpdate;
    }
}
