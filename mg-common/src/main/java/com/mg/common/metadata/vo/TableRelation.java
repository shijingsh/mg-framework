package com.mg.common.metadata.vo;

import com.mg.framework.entity.metadata.MPropertyEntity;
import com.mg.framework.entity.metadata.MJoinedTypeEnum;

/**
 * Created by liukefu on 2016/1/15.
 */
public class TableRelation implements java.io.Serializable {
    /**
     * 主表
     */
    private MTable mainTable;
    /**
     * 连接表
     */
    private MTable joinedTable;

    /**
     * 连接关系
     */
    private MJoinedTypeEnum joinedType = MJoinedTypeEnum.LEFT_JOIN;

    /**
     * 主对象元数据
     * 为空，则为和主对象ID关联
     */
    private MPropertyEntity mainProperty;
    /**
     * 被关联的元数据
     */
    private MPropertyEntity mappedByProperty;

    public TableRelation() {
    }

    public TableRelation(String mainTableName, String joinedTableName, String mainAliasName, String joinedAliasName) {
        mainTable = new MTable(mainTableName,mainAliasName);
        joinedTable = new MTable(joinedAliasName,joinedTableName);
    }

    public TableRelation(MTable mainTable, MTable joinedTable, MPropertyEntity mainProperty) {
        this.mainTable = mainTable;
        this.joinedTable = joinedTable;
        this.mainProperty = mainProperty;
    }

    public MTable getMainTable() {
        return mainTable;
    }

    public void setMainTable(MTable mainTable) {
        this.mainTable = mainTable;
    }

    public MTable getJoinedTable() {
        return joinedTable;
    }

    public void setJoinedTable(MTable joinedTable) {
        this.joinedTable = joinedTable;
    }

    public MPropertyEntity getMainProperty() {
        return mainProperty;
    }

    public void setMainProperty(MPropertyEntity mainProperty) {
        this.mainProperty = mainProperty;
    }

    public MPropertyEntity getMappedByProperty() {
        return mappedByProperty;
    }

    public void setMappedByProperty(MPropertyEntity mappedByProperty) {
        this.mappedByProperty = mappedByProperty;
    }

    public MJoinedTypeEnum getJoinedType() {
        return joinedType;
    }

    public void setJoinedType(MJoinedTypeEnum joinedType) {
        this.joinedType = joinedType;
    }
}
