package com.mg.framework.entity.vo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通用前端表格显示对象.
 *
 * 分为：表头（含合并项）、行数据（Map形式，key为列ID，value为数据，建议直接放String）
 * 
 * var tableData = {

	    frozenColumns: [
	    [
	        {field: 'empId', title: '编号', width: 100, align: 'center', rowspan: 3},
	        {field: 'empName', title: '姓名', width: 100, align: 'center', rowspan: 3},
	    ],
	    [],
	    []
	],
	
	    columns: [
	        [
	            {field: 'endowBaseBefore', title: '养老金修改前', width: 100, align: 'center', rowspan: 3},
	            {field: 'endowBaseAfter', title: '养老金修改后', width: 100, align: 'center', rowspan: 3},
	            {field: 'endowBaseIndividualBefore', title: '养老金个人缴纳修改前', width: 100, align: 'center', colspan: 3},
	            {field: 'endowBaseIndividualAfter', title: "养老金个人缴纳修改后", width: 100, align: 'center', colspan: 8}
	        ],
	        [
	            {field: 'aa', title: '基本工资', width: 100, align: 'center', rowspan: 2},
	            {field: 'bb', title: '岗位津贴', width: 100, align: 'center', rowspan: 2},
	            {field: 'cc', title: '合计', width: 100, align: 'center', rowspan: 2},
	            {field: 'dd', title: '车贴', width: 100, align: 'center', colspan: 4},
	            {field: 'ee', title: 'A', width: 100, align: 'center', rowspan: 2},
	            {field: 'ff', title: 'B', width: 100, align: 'center', rowspan: 2},
	            {field: 'gg', title: 'C', width: 100, align: 'center', rowspan: 2},
	            {field: 'hh', title: 'D', width: 100, align: 'center', rowspan: 2}
	        ],
	        [
	            {field: 'mm1', title: '车贴1', width: 100, align: 'center', rowspan: 1},
	            {field: 'mm2', title: '车贴2', width: 100, align: 'center', rowspan: 1},
	            {field: 'mm3', title: '车贴3', width: 100, align: 'center', rowspan: 1},
	            {field: 'mm4', title: '车贴4', width: 100, align: 'center', rowspan: 1}
	        ]
	
	    rowData: [
	        {
	        	"_rowId":"a001",
	            "empId": "80852",
	            "empName": "张伟丽",
	            "endowBaseBefore": {
					value:4000,
					isValid:false
				},
	            "endowBaseAfter": null,
	            "endowBaseIndividualBefore": 150,
	            "endowBaseIndividualAfter": 10,

	        },
	        {
	        	"_rowId":"a002",
	            "empId": "80852",
	            "empName": "顾志娟",
	            "endowBaseBefore": 4000,
	            "endowBaseAfter": null,
	            "endowBaseIndividualBefore": 150,
	            "endowBaseIndividualAfter": 10
	        },
	        {
	         	"_rowId":"a003",
	            "empId": "80852",
	            "empName": "李建荣",
	            "endowBaseBefore": 4000,
	            "endowBaseAfter": null,
	            "endowBaseIndividualBefore": 150,
	            "endowBaseIndividualAfter": 10
	        },
	        {
	        	"_rowId":"a004",
	            "empId": "80852",
	            "empName": "王梦",
	            "endowBaseBefore": 4000,
	            "endowBaseAfter": null,
	            "endowBaseIndividualBefore": 150,
	            "endowBaseIndividualAfter": 10,
                "ss":{
                    value:11
                    options:[{text}]
}
	        }
	    ],
	
	    pageSize: 20,
	    pageNo: 1,
	    totalSize: 10000,
extendData:{
	pingguozhong:14,
 shenpizhong:1,
	dddd:ddd
	FFDFGD:112
}
	};
 */
public class TableDataVO {
    //固定层级<层级<格子>>
    private List<List<TableHeaderCellVO>> frozenColumns;
    //活动层级<层级<格子>>
    private List<List<TableHeaderCellVO>> columns;

	/**任务是否已经完成*/
	protected Boolean isFinish;

	public Boolean isFinish() {
		return isFinish;
	}

	public void setIsFinish(Boolean isFinish) {
		this.isFinish = isFinish;
	}

	/**
	 * 表头数据
	 */
	private List<TableHeaderCellVO> headColumns;

	public List<TableHeaderCellVO> getHeadColumns() {
		return headColumns;
	}

	public void setHeadColumns(List<TableHeaderCellVO> headColumns) {
		this.headColumns = headColumns;
	}

	/**
     * 行数据.
     * List是每行数据的集合
     * Map是每个格子的数据，key为列的ID（TableHeaderVO中的ID值），value为需要显示的数字、字符串或对象
     */
    private List<Map<String, Object>> rowData = new ArrayList<>();




    /**
     * 所返回表格的总记录数（不是当前返回的行数）
     */
    private long totalCount = 0;

    /**
     * 当前的页数
     */
    private int pageNo = 1;

    /**
     * 当前每页所显示的行数。如果本值大于rowDatas的size，那么基本就是到末尾了。
     */
    private int pageSize = 15;

    /**
     * rowDatas集合中，最后一条行数据的唯一标识。本属性一般用于显示更多时候向下查询。
     */
    private String lastRowId;

	private Object extendData;

    
	public List<Map<String, Object>> getRowData() {
		return rowData;
	}



	public void setRowData(List<Map<String, Object>> rowData) {
		this.rowData = rowData;
	}



	public long getTotalCount() {
		return totalCount;
	}



	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}



	public int getPageNo() {
		return pageNo;
	}



	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}



	public int getPageSize() {
		return pageSize;
	}



	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}



	public String getLastRowId() {
		return lastRowId;
	}



	public void setLastRowId(String lastRowId) {
		this.lastRowId = lastRowId;
	}

    public List<List<TableHeaderCellVO>> getFrozenColumns() {
        return frozenColumns;
    }

    public void setFrozenColumns(List<List<TableHeaderCellVO>> frozenColumns) {
        this.frozenColumns = frozenColumns;
    }

    public List<List<TableHeaderCellVO>> getColumns() {
        return columns;
    }

    public void setColumns(List<List<TableHeaderCellVO>> columns) {
        this.columns = columns;
    }

    public void putRowData(int row, String field, Object value ) {
        if(rowData == null)
            rowData = new ArrayList<>();

        while(rowData.size() < row+1) {
            rowData.add(new HashMap<String, Object>());
        }

        Map<String, Object> mapRowData = rowData.get(row);

        mapRowData.put(field, value);
    }

    public void addColumn(String field, String title) {
        addColumn(new TableHeaderCellVO(field, title));
    }


    public void addColumn(String field, String title, String type) {
        TableHeaderCellVO header = new TableHeaderCellVO(field, title);
        header.setColumnType(type);
        addColumn(header);
    }

    public void addColumn(TableHeaderCellVO headerCell) {
        if(columns == null)
            columns = new ArrayList<>();
        if(columns.size() == 0)
            columns.add(new ArrayList<TableHeaderCellVO>());

        columns.get(0).add(headerCell);
    }


    public int getRowSize() {
        return rowData.size();
    }

    public Object getCellValue(int row, String field) {
        if(row < 0 || row > getRowSize())
            return null;

        Map<String, Object> map = rowData.get(row);
        return map.get(field);
    }

	public Object getExtendData() {
		return extendData;
	}

	public void setExtendData(Object extendData) {
		this.extendData = extendData;
	}
}
