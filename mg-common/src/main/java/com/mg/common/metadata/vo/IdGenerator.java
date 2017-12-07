package com.mg.common.metadata.vo;

import org.hibernate.id.enhanced.Optimizer;

/**
 * Created by liukefu on 2015/11/11.
 */
public class IdGenerator implements java.io.Serializable {

    private String selectQuery;
    private String insertQuery;
    private String updateQuery;
    private String segmentValue;
    private Optimizer optimizer = null;

    public IdGenerator() {
    }

    public IdGenerator(String selectQuery, String insertQuery, String updateQuery,String segmentValue, Optimizer optimizer) {
        this.selectQuery = selectQuery;
        this.insertQuery = insertQuery;
        this.updateQuery = updateQuery;
        this.segmentValue = segmentValue;
        this.optimizer = optimizer;
    }

    public Optimizer getOptimizer() {
        return optimizer;
    }

    public void setOptimizer(Optimizer optimizer) {
        this.optimizer = optimizer;
    }

    public String getSegmentValue() {
        return segmentValue;
    }

    public void setSegmentValue(String segmentValue) {
        this.segmentValue = segmentValue;
    }

    public String getSelectQuery() {
        return selectQuery;
    }

    public void setSelectQuery(String selectQuery) {
        this.selectQuery = selectQuery;
    }

    public String getInsertQuery() {
        return insertQuery;
    }

    public void setInsertQuery(String insertQuery) {
        this.insertQuery = insertQuery;
    }

    public String getUpdateQuery() {
        return updateQuery;
    }

    public void setUpdateQuery(String updateQuery) {
        this.updateQuery = updateQuery;
    }
}
