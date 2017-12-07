package com.mg.framework.entity.metadata;

import com.alibaba.fastjson.annotation.JSONField;
import com.mg.framework.entity.model.BaseEntity;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

/**
 * 数据模板
 * @author liukefu
 */
@Entity
@Table(name="sys_meta_template")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class MTemplateEntity extends BaseEntity {
    /**
     * 所属元数据对象
     */
    @JSONField(serialize = false, deserialize = false)
    @ManyToOne
    @JoinColumn(name = "belong_mobject_id")
    protected MObjectEntity belongMObject;
    /**
     * 模板名称
     */
    private String name;
    /**
     * 母模板：生成模板的模板
     */
    @Lob
    @Basic(fetch = FetchType.EAGER)
    @JSONField(serialize = false, deserialize = false)
    private String templateSource;
    /**
     * 模板内容
     */
    @Lob
    @Basic(fetch = FetchType.EAGER)
    @JSONField(serialize = false, deserialize = false)
    private String template;
    /**
     * 模板类型枚举 MTemplateTypeEnum
     * 包括：数据录入型、数据显示型
     */
    @Enumerated(EnumType.STRING)
    private MTemplateTypeEnum templateType = MTemplateTypeEnum.DataEntry;
    /**
     * 排序
     */
    private Integer sort = 0;
    /**
     * 是否为系统模板
     * 即自动生成的模板
     */
    private Boolean isSystem = false;
    /**
     * 存放临时的模板内容
     */
    @Transient
    private String templateStr;

    public MTemplateEntity() {
    }

    public MTemplateEntity(MObjectEntity belongMObject, String templateName, String template) {
        this.belongMObject = belongMObject;
        this.name = templateName;
        this.template = template;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public MObjectEntity getBelongMObject() {
        return belongMObject;
    }

    public void setBelongMObject(MObjectEntity belongMObject) {
        this.belongMObject = belongMObject;
    }

    public MTemplateTypeEnum getTemplateType() {
        return templateType;
    }

    public void setTemplateType(MTemplateTypeEnum templateType) {
        this.templateType = templateType;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public String getTemplateSource() {
        return templateSource;
    }

    public void setTemplateSource(String templateSource) {
        this.templateSource = templateSource;
    }

    public Boolean getIsSystem() {
        return isSystem;
    }

    public void setIsSystem(Boolean isSystem) {
        this.isSystem = isSystem;
    }

    public String getTemplateStr() {
        return templateStr;
    }

    public void setTemplateStr(String templateStr) {
        this.templateStr = templateStr;
    }
}
