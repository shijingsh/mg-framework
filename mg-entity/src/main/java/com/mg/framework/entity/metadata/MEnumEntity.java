package com.mg.framework.entity.metadata;

import com.mg.framework.entity.model.BaseEntity;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.IndexColumn;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 元数据 枚举数据
 * @author liukefu
 */
@Entity
@Table(name="sys_meta_enum")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class MEnumEntity extends BaseEntity {
    /**
     * 枚举名称
     * 比如：性别
     */
    @IndexColumn(name = "sys_meta_enum_enum_name")
    private String enumName;
    /**
     * 枚举唯一标识
     * 比如：0、1
     */
    @IndexColumn(name = "sys_meta_enum_key")
    @Column(name = "[key]")
    private String key;
    /**
     * 枚举值
     * 比如：男、女
     */
    @IndexColumn(name = "sys_meta_enum_name")
    private String name;
    /**
     * 默认选择
     */
    private boolean isDefault = false;

    /**
     * 默认排序
     */
    private Integer sort = 0;

    public MEnumEntity() {
    }

    public MEnumEntity(String id, String name) {
        super.id = id;
        this.name = name;
    }

    public MEnumEntity(String id, String enumName, String name) {
        super.id = id;
        this.enumName = enumName;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEnumName() {
        return enumName;
    }

    public void setEnumName(String enumName) {
        this.enumName = enumName;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setIsDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }
}
