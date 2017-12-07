package com.mg.common.metadata.service;

import java.io.Serializable;

/**
 * 元数据ID generator
 * Created by liukefu on 2015/11/11.
 */
public interface MTableGeneratorService {
    /**
     * 产生对应表“tableName”的ID
     * @param tableName
     * @return
     */
    public Serializable generate(final String tableName);

    /**
     * 产生对应表“tableName”的ID
     * 指定序号的初始值
     * @param tableName
     * @param initialValue
     * @return
     */
    public Serializable generate(final String tableName,final int initialValue);
}
