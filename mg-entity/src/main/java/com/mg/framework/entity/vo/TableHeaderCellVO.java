package com.mg.framework.entity.vo;


import com.mg.framework.entity.metadata.MEnumEntity;
import com.mg.framework.entity.metadata.MirrorPropertyEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * 通用表头单项的VO
 * @author xu.df
 *
 */

public class TableHeaderCellVO {
	
	//ID
	private String field;
	//名称
	private String title;
	//格子宽度
	private Integer width;
	//文字的位置
	private String align;
	//所跨越的table格子高度
	private Integer rowspan;
	//所跨越的table格子长度
	private Integer colspan;
	/**
	  * 字段校验器,目前只有 integer一项
	 */
	private String validator;

	/**
	 * 输入文字限制，目前只有 integer,float
	 */
	private String limit;

	/**
	 * 默认显示单位，即每个格子上增加的单位，如“￥”。目前默认为null
	 */
	private String unit;
	
	private String subTitle;
	private String fatherTitle;

    /**input, select, checkbox, custom, text,link,operation,images*/
    private String columnType = "text";

	//样式
	private String columnStyle="";

	private String sortType = null;
	//所属元数据（非必选项）
	private MirrorPropertyEntity property;
	//所属第一列（非必选项）
	private int columnIndex = 0;

	private List<MEnumEntity> filter = new ArrayList<>();
    public TableHeaderCellVO() {
    }

    public TableHeaderCellVO(String field, String title) {
        this.field = field;
        this.title = title;
    }
	public TableHeaderCellVO(String field, String title,int width) {
		this.field = field;
		this.title = title;
		this.width = width;
	}
	public TableHeaderCellVO(String field, String title,String columnType) {
		this(field,title);
		this.columnType = columnType;
	}

    public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	public String getTitle() {
		return title;
	}
	public void setColumnStyle(String columnStyle) {
		this.columnStyle = columnStyle;
	}
	public String getColumnStyle() {
		return columnStyle;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getFatherTitle() {
		return fatherTitle;
	}
	public void setFatherTitle(String fatherTitle) {
		this.fatherTitle = fatherTitle;
	}

	public Integer getWidth() {
		return width;
	}
	public void setWidth(Integer width) {
		this.width = width;
	}
	public Integer getRowspan() {
		return rowspan;
	}
	public void setRowspan(Integer rowspan) {
		this.rowspan = rowspan;
	}
	public Integer getColspan() {
		return colspan;
	}
	public void setColspan(Integer colspan) {
		this.colspan = colspan;
	}
	public String getAlign() {
		return align;
	}
	public void setAlign(String align) {
		this.align = align;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}

    public String getColumnType() {
        return columnType;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }

	public String getSubTitle() {
		return subTitle;
	}

	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}

	public String getValidator() {
		return validator;
	}

	public void setValidator(String validator) {
		this.validator = validator;
	}

	public String getLimit() {
		return limit;
	}

	public void setLimit(String limit) {
		this.limit = limit;
	}

	public String getSortType() {
		return sortType;
	}

	public void setSortType(String sortType) {
		this.sortType = sortType;
	}

	public List<MEnumEntity> getFilter() {
		return filter;
	}

	public void setFilter(List<MEnumEntity> filter) {
		this.filter = filter;
	}

	public MirrorPropertyEntity getProperty() {
		return property;
	}

	public void setProperty(MirrorPropertyEntity property) {
		this.property = property;
	}

	public int getColumnIndex() {
		return columnIndex;
	}

	public void setColumnIndex(int columnIndex) {
		this.columnIndex = columnIndex;
	}
}
