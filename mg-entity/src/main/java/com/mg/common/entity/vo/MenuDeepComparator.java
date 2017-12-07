package com.mg.common.entity.vo;

import com.mg.common.entity.MenuEntity;

import java.util.Comparator;

/**
 * 菜单按照层级排序
 * Created by liukefu on 2015/9/17.
 */
public class MenuDeepComparator implements Comparator<MenuEntity> {

    @Override
    public int compare(MenuEntity o1, MenuEntity o2) {

        //先按照层级排序、再安装sort排序
        if(o1.getDeep() != o2.getDeep()){
            return o1.getDeep() - o2.getDeep();
        }

        return o1.getSort() - o2.getSort();
    }
}
