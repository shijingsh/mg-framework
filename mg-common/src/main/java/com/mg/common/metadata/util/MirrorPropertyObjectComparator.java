package com.mg.common.metadata.util;

import com.mg.framework.entity.metadata.MirrorPropertyEntity;
import com.mg.framework.entity.metadata.MControllerTypeEnum;

import java.util.Comparator;

/**
 * 对象类型优先排序
 * Created by liukefu on 2015/9/17.
 */
public class MirrorPropertyObjectComparator implements Comparator<MirrorPropertyEntity> {

    @Override
    public int compare(MirrorPropertyEntity o1, MirrorPropertyEntity o2) {
        if(o1.getControllerType() == MControllerTypeEnum.object && o2.getControllerType() == MControllerTypeEnum.object){
            return 0;
        }
        if(o1.getControllerType() == MControllerTypeEnum.object){
            return -1;
        }
        if(o2.getControllerType() == MControllerTypeEnum.object){
            return 1;
        }
        return o1.getSort() - o2.getSort();
    }
}
