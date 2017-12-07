package com.mg.framework.entity.metadata;

import com.alibaba.fastjson.annotation.JSONField;
import com.mg.framework.entity.model.BaseEntity;

import javax.persistence.*;

/**
 * 元数据导入模板
 * Created by liukefu on 2015/10/21.
 */
@Entity
@Table(name="sys_meta_import_template")
public class MImportTemplateEntity extends BaseEntity {

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
    private String templateName;
    /**
     * 模板路径
     */
    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String path;

    @Transient
    private String titles[];

    public MObjectEntity getBelongMObject() {
        return belongMObject;
    }

    public void setBelongMObject(MObjectEntity belongMObject) {
        this.belongMObject = belongMObject;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String[] getTitles() {
        return titles;
    }

    public void setTitles(String[] titles) {
        this.titles = titles;
    }
}
