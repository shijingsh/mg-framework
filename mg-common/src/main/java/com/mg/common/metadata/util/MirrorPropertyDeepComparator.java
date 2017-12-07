package com.mg.common.metadata.util;

import com.mg.framework.entity.metadata.MirrorPropertyEntity;

import java.util.Comparator;

/**
 * Created by liukefu on 2015/9/17.
 */
public class MirrorPropertyDeepComparator implements Comparator<MirrorPropertyEntity> {

    @Override
    public int compare(MirrorPropertyEntity o1, MirrorPropertyEntity o2) {

        return o1.getDeep() - o2.getDeep();
    }
}
