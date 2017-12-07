package com.mg.common.metadata.service;

import com.mg.common.metadata.dao.MEnumDao;
import com.mg.framework.entity.metadata.MEnumEntity;
import com.mg.framework.entity.metadata.MirrorPropertyEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liukefu on 2015/9/6.
 */
@Service
public class MEnumServiceImpl implements MEnumService{
    @Autowired
    private MEnumDao mEnumDao;
    /**
     * 查询所有的枚举类型
     * @return
     */
    @Override
    public List<MEnumEntity> findAllEnum() {
        return mEnumDao.findAllEnum();
    }
    /**
     * 根据枚举名称，查询枚举类型
     * @param enumName
     * @return
     */
    @Override
    public List<MEnumEntity> findByEnumName(String enumName) {
        return mEnumDao.findByEnumName(enumName);
    }

    /**
     * 根据枚举key，查询枚举类型
     * @param key
     * @return
     */
    public MEnumEntity findByKey(String enumName, String key) {
        return mEnumDao.findByKey(enumName,key);
    }

    /**
     * 根据枚举名称，查询枚举类型
     * @param enumName
     * @param name
     * @return
     */
    public MEnumEntity findByName(String enumName, String name) {
        return mEnumDao.findByName(enumName,name);
    }
    /**
     * 根据元数据列表，查询依赖的枚举类型
     * @param list
     * @return
     */
    public Map<String,List<MEnumEntity>> findByProperties(List<MirrorPropertyEntity> list) {
        Map<String,List<MEnumEntity>> map = new HashMap<>();

        for(MirrorPropertyEntity propertyEntity:list){
            String enumName = propertyEntity.getEnumName();
            if(StringUtils.isNotBlank(enumName)){
                List<MEnumEntity> t =    mEnumDao.findByEnumName(enumName);
                if(map.get(enumName)==null){
                    map.put(enumName,t);
                }
            }
        }
        return map;
    }
}
