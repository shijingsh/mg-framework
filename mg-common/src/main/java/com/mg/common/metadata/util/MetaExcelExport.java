package com.mg.common.metadata.util;

import com.mg.common.utils.DateUtil;
import com.mg.common.metadata.service.MEnumService;
import com.mg.framework.entity.metadata.MEnumEntity;
import com.mg.framework.entity.metadata.MirrorPropertyEntity;
import com.mg.framework.entity.metadata.MControllerTypeEnum;
import com.mg.framework.sys.PropertyConfigurer;
import com.mg.framework.entity.vo.TableHeaderCellVO;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 处理Excel导出
 */
@Component
public class MetaExcelExport {
	private Logger logger = LoggerFactory.getLogger(MetaExcelExport.class);
	@Autowired
	MEnumService mEnumService;
	/**
	 * 导出Excel数据
	 */
	public String expExcel(List<MirrorPropertyEntity> mproperties, List<Map<String,Object>> list, String title) {

		// 创建一个工作簿
		HSSFWorkbook workBook = new HSSFWorkbook();
		// 创建一个工作表，设定sheet名字
		HSSFSheet sheet = workBook.createSheet(title);
		sheet.setDefaultColumnWidth(15);
		sheet.setDefaultRowHeightInPoints(20f);
		//创建Excel表头
		createHeader(workBook, sheet, mproperties);
		//从第二行开始写数据
		createData(sheet,mproperties,list);
		//保存excel到服务器
		return saveExcel(workBook);
	}

	/**
	 * 导出Excel数据
	 */
	public String expExcelTableHeader(List<TableHeaderCellVO> columns,List<Map<String,Object>> list,String title) {

		// 创建一个工作簿
		HSSFWorkbook workBook = new HSSFWorkbook();
		// 创建一个工作表，设定sheet名字
		HSSFSheet sheet = workBook.createSheet(title);
		sheet.setDefaultColumnWidth(15);
		sheet.setDefaultRowHeightInPoints(20f);
		//创建Excel表头
		createTableHeader(workBook, sheet, columns);
		//从第二行开始写数据
		createTableHeaderData(sheet, columns, list);
		//保存excel到服务器
		return saveExcel(workBook);
	}
	/**
	 * 创建Excel表头
	 * @param sheet		当前Excel的Sheet
	 */
	public void createHeader(HSSFWorkbook wb, HSSFSheet sheet, List<MirrorPropertyEntity> mproperties){
		// 创建一个单元格，从0开始
		HSSFRow row = sheet.createRow(0);
	    int index =0;
        for (short i = 0; i < mproperties.size(); i++,index++) {
            HSSFCell cell = row.createCell(index);
            HSSFRichTextString text = new HSSFRichTextString(mproperties.get(i).getName());
            cell.setCellStyle(createRedFont(wb));
            cell.setCellValue(text);
        }
	}

	/**
	 * 创建Excel表头
	 * @param sheet		当前Excel的Sheet
	 */
	public void createTableHeader(HSSFWorkbook wb, HSSFSheet sheet, List<TableHeaderCellVO> columns){
		// 创建一个单元格，从0开始
		HSSFRow row = sheet.createRow(0);
		int index =0;
		for (short i = 0; i < columns.size(); i++,index++) {
			HSSFCell cell = row.createCell(index);
			HSSFRichTextString text = new HSSFRichTextString(columns.get(i).getTitle());
			cell.setCellStyle(createRedFont(wb));
			cell.setCellValue(text);
		}
	}
	/**
	 * 保存Excel文件到服务器里面
	 * @param wb  欲导出的Excel文件
	 */
	public String saveExcel(HSSFWorkbook wb) {
		OutputStream os = null;
		File file=null;
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
	 * 从第二行开始写数据
	 * @author li.hao
	 * @param sheet		当前Sheet
	 */
	private void createData(HSSFSheet sheet, List<MirrorPropertyEntity> mproperties, List<Map<String,Object>> list) {
		int index = 1;

		for (Map<String,Object> obj : list) {
			Row row = sheet.createRow(index++);
			int index_ = 0;
			for(MirrorPropertyEntity mirrorPropertyEntity:mproperties){
				Cell cell = row.createCell(index_);
				Object value = getCellValue(obj,mirrorPropertyEntity);

				setCellValue(cell,mirrorPropertyEntity,value);

				index_++;
			}
		}
	}

	/**
	 * 从第二行开始写数据
	 * @author li.hao
	 * @param sheet		当前Sheet
	 */
	private void createTableHeaderData(HSSFSheet sheet, List<TableHeaderCellVO> columns, List<Map<String,Object>> list) {
		int index = 1;

		for (Map<String,Object> obj : list) {
			Row row = sheet.createRow(index++);
			int index_ = 0;
			for(TableHeaderCellVO tableHeaderCellVO:columns){
				Cell cell = row.createCell(index_);
				if(tableHeaderCellVO.getProperty()!=null){
					Object value = getCellValue(obj,tableHeaderCellVO.getProperty());
					setCellValue(cell,tableHeaderCellVO.getProperty(),value);
				}else{
					Object value = obj.get(tableHeaderCellVO.getField());
					cell.setCellValue(new HSSFRichTextString(""+value));
				}

				index_++;
			}
		}
	}
	/**
	 * 获取单元格显示的值
	 * @param obj
	 * @param mirrorPropertyEntity
	 * @return
	 */
	private Object getCellValue(Map<String,Object> obj,MirrorPropertyEntity mirrorPropertyEntity){
		if(mirrorPropertyEntity==null){
			return null;
		}
		if(mirrorPropertyEntity.getControllerType()== MControllerTypeEnum.mEnum
				&& StringUtils.isNotBlank(mirrorPropertyEntity.getEnumName())){
			List<MEnumEntity> temList = mEnumService.findByEnumName(mirrorPropertyEntity.getEnumName());
			String value = String.valueOf(obj.get(mirrorPropertyEntity.getPropertyPath()));
			for(MEnumEntity enumEntity:temList){
				if(enumEntity.getKey().equals(value)){
					return enumEntity.getName();
				}
			}
		}else if(mirrorPropertyEntity.getControllerType()== MControllerTypeEnum.object){
			return obj.get(MetaDataUtils.getObjectFieldValue(mirrorPropertyEntity));
		}else if(mirrorPropertyEntity.getControllerType()== MControllerTypeEnum.bool){
			String value = String.valueOf(obj.get(mirrorPropertyEntity.getPropertyPath()));
			if("1".equals(value)){
				return "是";
			}
			if("true".equals(value)){
				return "是";
			}
			return "否";
		}else if(StringUtils.equals(MetaDataUtils.META_FIELD_STATUS,mirrorPropertyEntity.getPropertyPath())){
			String value = String.valueOf(obj.get(mirrorPropertyEntity.getPropertyPath()));
			if("1".equals(value)){
				return "有效";
			}
			return "无效";
		}else{
			return obj.get(mirrorPropertyEntity.getPropertyPath());
		}
		return "";
	}

	/**
	 * 设置单元格的值
	 * @param cell
	 * @param mirrorPropertyEntity
	 * @param value
	 */
	private void setCellValue(Cell cell, MirrorPropertyEntity mirrorPropertyEntity, Object value){

		if(value==null){
			cell.setCellValue(new HSSFRichTextString(""));
			return;
		}

		switch (mirrorPropertyEntity.getControllerType()){
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
				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
				cell.setCellValue(new HSSFRichTextString(String.valueOf(value)));
				String dateStr = DateUtil.convertDateToString((Date)value);
				cell.setCellValue(dateStr);
				break;
			case number:
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				if(Long.class.isAssignableFrom(value.getClass())){
					cell.setCellValue((Long)value);
				}else if(Double.class.isAssignableFrom(value.getClass())){
					cell.setCellValue((Double)value);
				}else if(Integer.class.isAssignableFrom(value.getClass())){
					cell.setCellValue((Integer)value);
				}else if(BigDecimal.class.isAssignableFrom(value.getClass())){
					cell.setCellValue(((BigDecimal)value).doubleValue());
				}
				break;
		}

	}
	/**
	 * 设置Excel表头字体颜色
	 * @param wb	Excel文件
	 * @return: 	字体对象
	 */
	private HSSFCellStyle createRedFont(HSSFWorkbook wb){
		HSSFCellStyle style = wb.createCellStyle();
		HSSFFont font = wb.createFont();
		font.setColor(HSSFColor.BLACK.index);
		//font.setBoldweight(Font.BOLDWEIGHT_BOLD);
		style.setFont(font);
		style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		return style;
	}

	
	/**
	 * 创建Excel文件
	 * @return: File  Excel文件
	 */
	private File createFile() {
		String fileName = "export" + System.currentTimeMillis()+".xls" ;
		String path = PropertyConfigurer.getContextProperty("temppath")+fileName;
		File file = new File(path);
		if (!file.exists()) {
			file.getParentFile().mkdirs();
		}
		if(!file.exists()){
			try {
				file.createNewFile();
			} catch (IOException e) {
				logger.error("create file: {}", path);
			}
		}
		return file;
	}

}
