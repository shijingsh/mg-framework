package com.mg.report.util;

import com.mg.common.metadata.service.MEnumService;
import com.mg.common.utils.DateUtil;
import com.mg.framework.entity.metadata.MEnumEntity;
import com.mg.framework.entity.metadata.MirrorPropertyEntity;
import com.mg.framework.entity.metadata.MControllerTypeEnum;
import com.mg.framework.entity.vo.TableHeaderCellVO;
import com.mg.framework.sys.PropertyConfigurer;
import com.mg.report.entity.ReportDimenEntity;
import com.mg.report.entity.ReportDimenItemEntity;
import com.mg.report.service.ReportService;
import com.mg.report.vo.ReportRowDataVo;
import com.mg.report.vo.ReportVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 处理报表Excel导出
 */
@Component
public class ReportExcelExport {
    private Logger logger = LoggerFactory.getLogger(ReportExcelExport.class);
    @Autowired
    MEnumService mEnumService;
    @Autowired
    ReportService reportService;
    /**
     * 导出Excel数据
     *
     * @param reportVo
     * @return
     */
    @Transactional(readOnly = true)
    public String expExcel(ReportVo reportVo) {

        //创建一个工作簿
        HSSFWorkbook workBook = new HSSFWorkbook();
        // 创建一个工作表，设定sheet名字
        HSSFSheet sheet = workBook.createSheet(reportVo.getReport().getName());
        sheet.setDefaultColumnWidth(20);
        sheet.setDefaultRowHeightInPoints(20f);
        //创建Excel表头
        createHeader(workBook, sheet, reportVo);
        //从第二行开始写数据
        createData(sheet, reportVo);
        //保存excel到服务器
        return saveExcel(workBook);
    }

    /**
     * 创建Excel表头
     *
     * @param wb
     * @param sheet
     * @param reportVo
     */
    public void createHeader(HSSFWorkbook wb, HSSFSheet sheet, ReportVo reportVo) {

        List<List<TableHeaderCellVO>> headList = reportVo.getHeaderColumns();

        settingColumns(reportVo, headList);

        int rowIndex = 0;
        for (List<TableHeaderCellVO> heads : headList) {

            HSSFRow row = sheet.createRow(rowIndex);
            int columnIndex = 0;
            for (TableHeaderCellVO headerCellVO : heads) {
                HSSFCell cell ;
                if(rowIndex==0){
                    cell = row.createCell(columnIndex);
                }else{
                    cell = row.createCell( headerCellVO.getColumnIndex());
                }
                HSSFRichTextString text = new HSSFRichTextString(headerCellVO.getTitle());
                cell.setCellStyle(createFont(wb));
                cell.setCellValue(text);

                int colIndex = headerCellVO.getColumnIndex();
                int colspan = headerCellVO.getColspan();
                int rowspan = headerCellVO.getRowspan();
                int lastRow = rowIndex + rowspan - 1;
                int lastCol = colIndex + colspan - 1;
                sheet.addMergedRegion(new CellRangeAddress(
                        rowIndex, //first row (0-based)
                        lastRow,  //last row  (0-based)
                        colIndex, //first column (0-based)
                        lastCol  //last column  (0-based)
                ));

                columnIndex += headerCellVO.getColspan();
            }

            rowIndex++;
        }

    }

    private void settingColumns(ReportVo reportVo,List<List<TableHeaderCellVO>> headList) {
        //每个小项的子项个数
        List<ReportDimenItemEntity> itemEntities = reportService.findDimenItemsByReports(reportVo.getReport());
        //显示的小项
        Map<String,Integer> itemNumMap = new HashMap<>();
        for(ReportDimenItemEntity itemEntity:itemEntities){
            AtomicInteger itemNumAc = new AtomicInteger(0);
            getItemNum(itemEntity, itemNumAc);
            itemNumMap.put(itemEntity.getId()+"item",itemNumAc.get());

            AtomicInteger colspanAc = new AtomicInteger(0);
            getColSpanNum(itemEntity, colspanAc);
            itemNumMap.put(itemEntity.getId(),colspanAc.get());
        }

        int rowIndex = 0;
        Map<Integer, Integer> map = new HashMap<>();
        for (List<TableHeaderCellVO> heads : headList) {

            int columnIndex = 0;
            if(rowIndex>0){
                map = getColumnMap(reportVo,itemNumMap,headList.get(rowIndex-1));
            }
            for (TableHeaderCellVO headerCellVO : heads) {
                if (rowIndex == 0) {
                    headerCellVO.setColumnIndex(columnIndex);
                    columnIndex += headerCellVO.getColspan();
                } else {
                    Integer index = getColumn(map,headerCellVO);
                    headerCellVO.setColumnIndex(index);
                }
            }

            rowIndex++;
        }
    }

    private void getColSpanNum(ReportDimenItemEntity itemEntity, AtomicInteger num){

        List<ReportDimenEntity> list = itemEntity.getDimenList();
        if(list.size()>0){
            for(ReportDimenEntity dimen:list){
                List<ReportDimenItemEntity> itemList = dimen.getItemList();
                for(ReportDimenItemEntity item:itemList){
                    getColSpanNum(item,num);
                }
            }
        }else{
            num.getAndIncrement();
        }

    }

    private void getItemNum(ReportDimenItemEntity itemEntity, AtomicInteger num){

        List<ReportDimenEntity> list = itemEntity.getDimenList();
        if(list.size()>0){
            for(ReportDimenEntity dimen:list){
                List<ReportDimenItemEntity> itemList = dimen.getItemList();
                for(ReportDimenItemEntity item:itemList){
                    num.getAndIncrement();
                    getItemNum(item,num);
                }
            }
        }
    }

    private Map<Integer,Integer> getColumnMap(ReportVo reportVo,Map<String,Integer> itemNumMap,List<TableHeaderCellVO> heads){

        Map<Integer, Integer> map = new HashMap<>();

        for (TableHeaderCellVO headerCellVO : heads) {
            int num = headerCellVO.getColspan();
            Integer itemNum = itemNumMap.get(headerCellVO.getField()+"item");
            Integer colSpan = itemNumMap.get(headerCellVO.getField());
            if (num > 1) {
                map.put(headerCellVO.getColumnIndex(), num);
            }
            if(colSpan!=null && colSpan>=1
                    && itemNum!=null && itemNum > 0 ){
                map.put(headerCellVO.getColumnIndex(), colSpan);
            }
        }

        return map;
    }

    private int getColumn(Map<Integer, Integer> map,TableHeaderCellVO headerCellVO) {

        Iterator<Integer> it = map.keySet().iterator();

        while (it.hasNext()) {
            Integer key = it.next();
            if (map.get(key) > 0) {
                Integer old = map.get(key+headerCellVO.getColspan());
                if(old==null){
                    old = 0;
                }
                map.put(key+headerCellVO.getColspan(), map.get(key) - headerCellVO.getColspan() + old);
                map.put(key, 0);
                return key;
            }
        }

        return 0;
    }

    /**
     * 保存Excel文件到服务器里面
     *
     * @param wb 欲导出的Excel文件
     */
    public String saveExcel(HSSFWorkbook wb) {
        OutputStream os = null;
        File file = null;
        try {
            file = createFile();
            os = new FileOutputStream(file);
            wb.write(os);
            os.close();
        } catch (Exception e) {
            logger.debug("export Excel file error :" + e.getMessage());
        }
        return file.getName();
    }

    /**
     * 数据
     *
     * @param sheet
     * @param reportVo
     */
    private void createData(HSSFSheet sheet, ReportVo reportVo) {
        Collection<ReportRowDataVo> list = reportVo.getList();
        int index = reportVo.getMaxLevel();
        HSSFCellStyle style = createNormalStyle(sheet.getWorkbook());
        List<TableHeaderCellVO> leafColumns = reportVo.getLeafColumns();
        for (ReportRowDataVo rowDataVo : list) {
            Row row = sheet.createRow(index++);
            int index_ = 0;
            Map<String, Object> map = rowDataVo.getRowData();
            for (TableHeaderCellVO cellVO : leafColumns) {
                Cell cell = row.createCell(index_);
                Object value = getCellValue(map, cellVO);

                setCellValue(cell, cellVO.getProperty(), value);

                index_++;
            }
            if (reportVo.getReport().getRowDimen().getIsDetail()) {
                List<Map<String, Object>> detailList = rowDataVo.getDetailList();
                for (Map<String, Object> detailMap : detailList) {
                    row = sheet.createRow(index++);
                    index_ = 0;
                    for (TableHeaderCellVO cellVO : leafColumns) {
                        Cell cell = row.createCell(index_);
                        //明细项行维度居中形成缩进
                        if(index_==0) {
                            cell.setCellStyle(style);
                        }
                        Object value = getCellValue(detailMap, cellVO);

                        setCellValue(cell, cellVO.getProperty(), value);

                        index_++;
                    }
                }
            }
        }
    }

    /**
     * 获取单元格显示的值
     *
     * @param obj
     * @param cellVO
     * @return
     */
    private Object getCellValue(Map<String, Object> obj, TableHeaderCellVO cellVO) {
        String valueKey = cellVO.getField();
        MirrorPropertyEntity mirrorPropertyEntity = cellVO.getProperty();
        if (mirrorPropertyEntity!=null && mirrorPropertyEntity.getControllerType() == MControllerTypeEnum.mEnum
                && StringUtils.isNotBlank(mirrorPropertyEntity.getEnumName())) {
            List<MEnumEntity> temList;
            if(cellVO.getFilter()!=null && cellVO.getFilter().size()>0){
                temList = cellVO.getFilter();
            }else{
                temList = mEnumService.findByEnumName(mirrorPropertyEntity.getEnumName());
            }
            String value = String.valueOf(obj.get(valueKey));
            for (MEnumEntity enumEntity : temList) {
                if (enumEntity.getKey().equals(value)) {
                    return enumEntity.getName();
                }
            }
        } else if (mirrorPropertyEntity!=null && mirrorPropertyEntity.getControllerType() == MControllerTypeEnum.object) {
            return obj.get(valueKey);
        } else {
            return obj.get(valueKey);
        }
        return "";
    }

    /**
     * 设置单元格的值
     *
     * @param cell
     * @param mirrorPropertyEntity
     * @param value
     */
    private void setCellValue(Cell cell, MirrorPropertyEntity mirrorPropertyEntity, Object value) {

        if (value == null) {
            if(mirrorPropertyEntity==null){
                cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
                cell.setCellValue(0);
            }else{
                cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                cell.setCellValue(new HSSFRichTextString(""));
            }

            return;
        }
        if(mirrorPropertyEntity==null){
            cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
            if (Long.class.isAssignableFrom(value.getClass())) {
                cell.setCellValue((Long) value);
            } else if (Double.class.isAssignableFrom(value.getClass())) {
                cell.setCellValue((Double) value);
            } else if (Integer.class.isAssignableFrom(value.getClass())) {
                cell.setCellValue((Integer) value);
            } else if (BigDecimal.class.isAssignableFrom(value.getClass())) {
                cell.setCellValue(((BigDecimal) value).doubleValue());
            } else if (Date.class.isAssignableFrom(value.getClass())) {
                Date date = (Date) value;
                cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                cell.setCellValue(DateUtil.convertDateToString(date));
            }else{
                cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                cell.setCellValue(new HSSFRichTextString(String.valueOf(value)));
            }

            return;
        }

        switch (mirrorPropertyEntity.getControllerType()) {
            case text:
            case mEnum:
            case bool:
            case object:
            case subType:
            case image:
            case file:
                cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                cell.setCellValue(new HSSFRichTextString(String.valueOf(value)));
                break;
            case date:
                Date date = (Date) value;
                cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                cell.setCellValue(DateUtil.convertDateToString(date));
                break;
            case number:
                cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
                if (Long.class.isAssignableFrom(value.getClass())) {
                    cell.setCellValue((Long) value);
                } else if (Double.class.isAssignableFrom(value.getClass())) {
                    cell.setCellValue((Double) value);
                } else if (Integer.class.isAssignableFrom(value.getClass())) {
                    cell.setCellValue((Integer) value);
                } else if (BigDecimal.class.isAssignableFrom(value.getClass())) {
                    cell.setCellValue(((BigDecimal) value).doubleValue());
                }
                break;
        }

    }

    /**
     * 设置Excel表头字体颜色
     *
     * @param wb Excel文件
     * @return: 字体对象
     */
    private HSSFCellStyle createFont(HSSFWorkbook wb) {
        HSSFCellStyle style = wb.createCellStyle();
        HSSFFont font = wb.createFont();
        font.setColor(HSSFColor.BLACK.index);
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        style.setFont(font);
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        return style;
    }

    /**
     * 设置居中样式
     *
     * @param wb Excel文件
     * @return: 字体对象
     */
    private HSSFCellStyle createNormalStyle(HSSFWorkbook wb) {
        HSSFCellStyle style = wb.createCellStyle();

        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        return style;
    }
    /**
     * 创建Excel文件
     *
     * @return: File  Excel文件
     */
    private File createFile() {
        String fileName = "export" + System.currentTimeMillis() + ".xls";
        String path = PropertyConfigurer.getContextProperty("temppath") + fileName;
        File file = new File(path);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                logger.error("create file: {}", path);
            }
        }
        return file;
    }

}
