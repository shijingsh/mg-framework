package com.mg.common.metadata.dao;

import com.mg.framework.entity.metadata.MEnumEntity;

import java.util.List;

/**
 * Created by liukefu on 2015/9/6.
 */
public interface MEnumDao {
    /**
     * 查询所有的枚举类型
     * @return
     */
    public List<MEnumEntity> findAllEnum();
    /**
     * 根据枚举名称，查询枚举类型
     * @param enumName
     * @return
     */
    public List<MEnumEntity> findByEnumName(String enumName);

    /**
     * 根据枚举名称，查询枚举类型
     * @param enumName
     * @return
     */
    public MEnumEntity findByName(String enumName, String name);

    /**
     * 根据枚举ID，查询枚举类型
     * @param id
     * @return
     */
    public MEnumEntity findById(String id);

    /**
     * 根据枚举key，查询枚举类型
     * @param key
     * @return
     */
    public MEnumEntity findByKey(String enumName, String key);
}
