package com.mg.common.metadata.util;

import com.mg.framework.entity.metadata.MPropertyEntity;

import java.util.Comparator;

/**
 * Created by liukefu on 2015/9/17.
 */
public class MPropertyComparator implements Comparator<MPropertyEntity> {

    @Override
    public int compare(MPropertyEntity o1, MPropertyEntity o2) {

        return o1.getSort() - o2.getSort();
    }
}
