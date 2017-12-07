package com.mg.common.metadata.service;

import com.mg.framework.entity.metadata.MInterfaceEntity;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * 元数据接口
 * Created by liukefu on 2015/12/18.
 */
public abstract class MInterfaceService {
    /**
     * 获取元数据对象数据
     * @param interfaceEntity 接口对象
     * @param personName 数据的唯一标识
     * @param startDate  时间范围起始
     * @param endDate    时间范围截至
     * @return
     */
   public abstract String getSysData(MInterfaceEntity interfaceEntity, String personName, Date startDate, Date endDate);



    public String first(String names){
        if(StringUtils.isBlank(names)){
            return "";
        }
        String [] arr = StringUtils.split(names, '.');

        return arr[0];
    }

    public String last(String names){
        if(StringUtils.isBlank(names)){
            return "";
        }
        String [] arr = StringUtils.split(names, '.');

        return arr[arr.length-1];
    }
}
