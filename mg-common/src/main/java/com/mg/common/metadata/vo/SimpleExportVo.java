package com.mg.common.metadata.vo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 简单的导出对象vo
 * Created by liukefu on 2016/4/22.
 */
public class SimpleExportVo {
    /**
     * 名称
     */
    private String name;
    /**
     * key,value
     */
    private List<Map<String,String>> list = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Map<String, String>> getList() {
        return list;
    }

    public void setList(List<Map<String, String>> list) {
        this.list = list;
    }
}
