package com.mg.framework.entity.metadata;

import com.mg.framework.entity.model.BaseEntity;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

/**
 * Created by liukefu on 2015/12/22.
 */
@Entity
@Table(name="sys_meta_sql")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class MSqlEntity extends BaseEntity {
    /**
     * 分类名称
     */
    private String categoryName;
    /**
     * sql脚本名称
     * 比如：例考成绩系数 考勤管理扣分等
     */
    private String name;
    /**
     * sql 脚本
     */
    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String sqlScript;
    /**
     * 脚本的执行顺序
     */
    private Integer sort;

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSqlScript() {
        return sqlScript;
    }

    public void setSqlScript(String sqlScript) {
        this.sqlScript = sqlScript;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }
}
