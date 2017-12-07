package com.mg.common.metadata.service;

import com.mg.framework.entity.metadata.MEnumEntity;
import com.mg.framework.entity.metadata.MirrorPropertyEntity;

import java.util.List;
import java.util.Map;

/**
 * Created by liukefu on 2015/9/6.
 */
public interface MEnumService {
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
     * @param name
     * @return
     */
    public MEnumEntity findByName(String enumName, String name);

    /**
     * 根据枚举key，查询枚举类型
     * @param key
     * @return
     */
    public MEnumEntity findByKey(String enumName, String key);
    /**
     * 根据元数据列表，查询依赖的枚举类型
     * @param list
     * @return
     */
    public Map<String,List<MEnumEntity>> findByProperties(List<MirrorPropertyEntity> list);

}
