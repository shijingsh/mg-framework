package com.mg.report.vo;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liukefu on 2015/11/4.
 */
public class ReportRowDataVo implements java.io.Serializable {

    String rowDimenKey;

    String rowDimenName;
    /**
     * 行维度的数据
     * {
     *     rowDimenKey:dimenName
     * }
     */
    Map<String,Object> rowData = new HashMap<>();
    /**
     * 维度明显数据
     */
    @JSONField(serialize = false, deserialize = false)
    private List<Map<String,Object>> detailList = new ArrayList<>();
    /**
     * 明显数据如果匹配任意一个小项的条件，则保存在这里
     * 用于记录重复添加
     */
    @JSONField(serialize = false, deserialize = false)
    private Map<String,Object> matchedMap = new HashMap<>();
    /**
     * 明显数据如果匹配任意一个小项的条件，则保存在这里
     */
    private List<Map<String,Object>> matchedList = new ArrayList<>();

    /**
     * 列维度的数据
     */
    Map<String,DimenDataVO> dimenData = new HashMap<>();

    public ReportRowDataVo() {
    }

    public ReportRowDataVo(String rowDimenKey, String rowDimenName) {
        this.rowDimenKey = rowDimenKey;
        this.rowDimenName = rowDimenName;
    }

    public String getRowDimenKey() {
        return rowDimenKey;
    }

    public void setRowDimenKey(String rowDimenKey) {
        this.rowDimenKey = rowDimenKey;
    }

    public String getRowDimenName() {
        return rowDimenName;
    }

    public void setRowDimenName(String rowDimenName) {
        this.rowDimenName = rowDimenName;
    }

    public List<Map<String, Object>> getDetailList() {
        return detailList;
    }

    public void addMatchedDetail(Map<String, Object> map,String key){
        if(matchedMap.get(key)==null){
            matchedMap.put(key,"");
            matchedList.add(map);
        }
    }


    public void setDetailList(List<Map<String, Object>> detailList) {
        this.detailList = detailList;
    }

    public Map<String, Object> getRowData() {
        return rowData;
    }

    public void setRowData(Map<String, Object> rowData) {
        this.rowData = rowData;
    }

    public List<Map<String, Object>> getMatchedList() {
        return matchedList;
    }

    public void setMatchedList(List<Map<String, Object>> matchedList) {
        this.matchedList = matchedList;
    }

    public Map<String, DimenDataVO> getDimenData() {
        return dimenData;
    }

    public void setDimenData(Map<String, DimenDataVO> dimenData) {
        this.dimenData = dimenData;
    }
}
