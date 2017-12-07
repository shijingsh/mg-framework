package com.mg.framework.entity.metadata;

/**
 * Created by liukefu on 2015/11/24.
 */
public class MOrderBy implements java.io.Serializable{

    private MirrorPropertyEntity property;

    private MOrderByEnum orderByEnum;

    public MirrorPropertyEntity getProperty() {
        return property;
    }

    public void setProperty(MirrorPropertyEntity property) {
        this.property = property;
    }

    public MOrderByEnum getOrderByEnum() {
        return orderByEnum;
    }

    public void setOrderByEnum(MOrderByEnum orderByEnum) {
        this.orderByEnum = orderByEnum;
    }
}
