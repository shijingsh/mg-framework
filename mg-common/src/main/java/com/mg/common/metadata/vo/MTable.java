package com.mg.common.metadata.vo;

/**
 * Created by liukefu on 2016/1/15.
 */
public class MTable implements java.io.Serializable{
    /**
     * 表名称
     */
    private String name;
    /**
     * 别名，查询时的别名
     */
    private String aliasName;

    public MTable() {
    }

    public MTable(String name, String aliasName) {
        this.name = name;
        this.aliasName = aliasName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAliasName() {
        return aliasName;
    }

    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
    }
}
