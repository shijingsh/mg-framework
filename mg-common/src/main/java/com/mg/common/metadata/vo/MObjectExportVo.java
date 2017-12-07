package com.mg.common.metadata.vo;

import com.mg.framework.entity.metadata.MirrorPropertyEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * 元数据对象vo
 * Created by liukefu on 2016/4/22.
 */
public class MObjectExportVo {

    String templatePath;

    private List<String> ids = new ArrayList<>();

    private Integer startCol;

    private Integer startRow;

    private Integer endCol;

    private Integer endRow;

    private String imgPath;

    private MirrorPropertyEntity pictureProperty;
    /**
     * 元数据列表
     */
    public List<MirrorPropertyEntity> propertyList = new ArrayList<>();
    /**
     * 结构化对象列表
     */
    public List<MirrorPropertyEntity> structList = new ArrayList<>();

    public MirrorPropertyEntity getPictureProperty() {
        return pictureProperty;
    }

    public void setPictureProperty(MirrorPropertyEntity pictureProperty) {
        this.pictureProperty = pictureProperty;
    }

    public String getTemplatePath() {
        return templatePath;
    }

    public void setTemplatePath(String templatePath) {
        this.templatePath = templatePath;
    }

    public List<String> getIds() {
        return ids;
    }

    public void setIds(List<String> ids) {
        this.ids = ids;
    }

    public Integer getStartCol() {
        return startCol;
    }

    public void setStartCol(Integer startCol) {
        this.startCol = startCol;
    }

    public Integer getStartRow() {
        return startRow;
    }

    public void setStartRow(Integer startRow) {
        this.startRow = startRow;
    }

    public Integer getEndCol() {
        return endCol;
    }

    public void setEndCol(Integer endCol) {
        this.endCol = endCol;
    }

    public Integer getEndRow() {
        return endRow;
    }

    public void setEndRow(Integer endRow) {
        this.endRow = endRow;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public List<MirrorPropertyEntity> getPropertyList() {
        return propertyList;
    }

    public void setPropertyList(List<MirrorPropertyEntity> propertyList) {
        this.propertyList = propertyList;
    }

    public List<MirrorPropertyEntity> getStructList() {
        return structList;
    }

    public void setStructList(List<MirrorPropertyEntity> structList) {
        this.structList = structList;
    }
}
