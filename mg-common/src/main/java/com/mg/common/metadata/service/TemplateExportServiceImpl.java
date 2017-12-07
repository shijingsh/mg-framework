package com.mg.common.metadata.service;

import com.mg.common.metadata.vo.MObjectExportVo;
import com.mg.common.metadata.util.MetaDataUtils;
import com.mg.framework.entity.metadata.MEnumEntity;
import com.mg.framework.entity.metadata.MObjectEntity;
import com.mg.framework.entity.metadata.MirrorPropertyEntity;
import com.mg.framework.entity.metadata.MControllerTypeEnum;
import com.mg.framework.sys.PropertyConfigurer;
import net.sf.jxls.exception.ParsePropertyException;
import net.sf.jxls.transformer.XLSTransformer;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 模板类数据导出
 * Created by liukefu on 2016/4/21.
 */
@Service
public class TemplateExportServiceImpl implements TemplateExportService {
    @Autowired
    MetaDataQueryService metaDataQueryService;
    @Autowired
    MetaDataService metaDataService;
    @Autowired
    MEnumService mEnumService;

    /**
     * 根据模板生成Excel文件.
     *
     * @param templateFileName
     * @param beanParams
     * @param resultFileName
     */
    public String createExcel(String templateFileName, Map<String, Object> beanParams, String resultFileName) {
        //创建XLSTransformer对象
        XLSTransformer transformer = new XLSTransformer();
        try {
            //生成Excel文件
            transformer.transformXLS(templateFileName, beanParams, resultFileName);
        } catch (ParsePropertyException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        }
        return resultFileName;
    }

    public String createExcel(MObjectEntity mObjectEntity, List<String> ids, String templatePath) {
        File file = createFile();
        InputStream is = null;
        OutputStream os = null;
        try {

            //所有的数据
            List<Map<String, Object>> list = new ArrayList<>();
            //所有的sheet
            List<String> listSheetNames = new ArrayList<>();
            //所有的图片
            List<String> picturesList = new ArrayList<>();
            MObjectExportVo exportVo = getMetaDataInfo(templatePath, mObjectEntity);
            List<MirrorPropertyEntity> propertyList = exportVo.getPropertyList();
            for (String id : ids) {
                if(StringUtils.isBlank(id) || id.equalsIgnoreCase("null")){
                    continue;
                }
                Map<String, Object> dataMap = metaDataService.queryById(mObjectEntity.getId(), id, propertyList);
                list.add(dataMap);
                //sheet name
                String name = (String) dataMap.get(MetaDataUtils.META_FIELD_NAME);
                listSheetNames.add(name);
                //头像特殊处理
                if(exportVo.getPictureProperty()!=null){
                    String picturePath = (String) dataMap.get(exportVo.getPictureProperty().getPropertyPath());
                    picturesList.add(picturePath);
                }
                //转化枚举类型
                transformerToDisplay(propertyList,dataMap);
                //结构化属性
                String mainObjectId = (String) dataMap.get(MetaDataUtils.META_FIELD_ID);
                for (MirrorPropertyEntity propertyEntity : exportVo.getStructList()) {
                    if (propertyEntity.getControllerType() == MControllerTypeEnum.subType) {
                        List<Map<String, Object>> subList = metaDataService.queryStructsByMetaData(propertyEntity, mainObjectId);

                        dataMap.put(propertyEntity.getPropertyPath(), subList);
                        //转化枚举类型
                        transformerToDisplay(propertyEntity,subList);
                    }
                }
            }

            XLSTransformer transformer = new XLSTransformer();
            is = new FileInputStream(templatePath);
            HSSFWorkbook workBook = (HSSFWorkbook) transformer.transformMultipleSheetsList(is, list, listSheetNames, "obj", new HashMap(), 0);
            //创建头像
            createPicture(workBook,exportVo,picturesList);

            //wb.write(new FileOutputStream(file));
            os = new FileOutputStream(file); //导出文件流
            workBook.write(os);  //写导出文件
            os.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(is!=null){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(os!=null){
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return file.getName();
    }

    private void transformerToDisplay(List<MirrorPropertyEntity> propertyList, Map<String, Object> dataMap) {

        for (MirrorPropertyEntity propertyEntity : propertyList) {
            if (propertyEntity.getControllerType() == MControllerTypeEnum.mEnum
                    && StringUtils.isNotBlank(propertyEntity.getEnumName())) {
                List<MEnumEntity> temList = mEnumService.findByEnumName(propertyEntity.getEnumName());
                String value = String.valueOf(dataMap.get(propertyEntity.getPropertyPath()));
                for (MEnumEntity enumEntity : temList) {
                    if (enumEntity.getKey().equals(value)) {
                        dataMap.put(propertyEntity.getPropertyPath(), enumEntity.getName());
                        break;
                    }
                }
            } else if (propertyEntity.getControllerType() == MControllerTypeEnum.object) {
                String value = String.valueOf(dataMap.get(MetaDataUtils.getNamePropertyPath(propertyEntity)));
                if(StringUtils.isNotBlank(value)){
                    dataMap.put(propertyEntity.getPropertyPath(), value);
                }
            } else if (propertyEntity.getControllerType() == MControllerTypeEnum.bool) {
                String value = String.valueOf(dataMap.get(propertyEntity.getPropertyPath()));
                if ("1".equals(value) || "true".equals(value)) {
                    dataMap.put(propertyEntity.getPropertyPath(), "是");
                } else {
                    dataMap.put(propertyEntity.getPropertyPath(), "否");
                }
            } else if (propertyEntity.getControllerType() == MControllerTypeEnum.date) {
                Date value = (Date)dataMap.get(propertyEntity.getPropertyPath());
                if(value!=null){
                    String dateStr = com.mg.common.utils.DateUtil.convertDateToString(value);
                    dataMap.put(propertyEntity.getPropertyPath(), dateStr);
                }
            } else if (StringUtils.equals(MetaDataUtils.META_FIELD_STATUS, propertyEntity.getPropertyPath())) {
                String value = String.valueOf(dataMap.get(propertyEntity.getPropertyPath()));
                if ("1".equals(value)) {
                    dataMap.put(propertyEntity.getPropertyPath(), "有效");
                } else {
                    dataMap.put(propertyEntity.getPropertyPath(), "无效");
                }
            }
        }
    }

    private void transformerToDisplay(MirrorPropertyEntity propertyEntity, List<Map<String, Object>> subList) {

        List<MirrorPropertyEntity> propertyList = metaDataQueryService.findMPropertyByBelongMObject(propertyEntity.getMetaProperty().getMetaObject());
        for(Map<String, Object> map:subList){
            transformerToDisplay(propertyList,map);
        }
    }

    private MObjectExportVo getMetaDataInfo(String templatePath, MObjectEntity mObjectEntity) {

        InputStream is = null;
        MObjectExportVo exportVo = new MObjectExportVo();
        try {
            is = new FileInputStream(templatePath);
            HSSFWorkbook wb = new HSSFWorkbook(is);

            HSSFSheet sheet = wb.getSheetAt(0);
            if (sheet != null) {
                int rowNum = sheet.getFirstRowNum();
                while (rowNum <= sheet.getLastRowNum()) {
                    Row row = sheet.getRow(rowNum);
                    if (row == null) {
                        ++rowNum;
                        continue;
                    }
                    for (Cell cell : row) {
                        if (cell == null) {
                            continue;
                        }
                        String value = cell.getStringCellValue();
                        List<String> matcherList = getExpress(value);
                        for(String property:matcherList){
                            if (StringUtils.isNotBlank(property)) {
                                String[] arr = property.split("\\.");
                                property = arr[arr.length-1];
                                MirrorPropertyEntity mProperty = metaDataQueryService.findMPropertyByBelongMObjectAndFieldName(mObjectEntity,mObjectEntity, property);
                                if (mProperty != null && mProperty.getControllerType() == MControllerTypeEnum.subType) {
                                    exportVo.getStructList().add(mProperty);
                                }
                                if (mProperty != null && mProperty.getControllerType() == MControllerTypeEnum.headPortrait) {
                                    exportVo.getPropertyList().add(mProperty);
                                    //头像特殊处理 图片固定大小： 2 × 4
                                    exportVo.setStartCol(cell.getColumnIndex());
                                    exportVo.setStartRow(cell.getRowIndex());
                                    exportVo.setEndCol(cell.getColumnIndex() + 2);
                                    exportVo.setEndRow(cell.getRowIndex() + 4);

                                    exportVo.setPictureProperty(mProperty);
                                    cell.setCellValue("  ");
                                } else if (mProperty != null){
                                    exportVo.getPropertyList().add(mProperty);
                                }
                            }
                        }
                    }
                    ++rowNum;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(is!=null){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        return exportVo;
    }

    /**
     * 获取jxls表达式内容
     * 不是表达式，则返回null
     *
     * @param value
     * @return
     */
    private List<String> getExpress(String value) {

        Pattern pattern = Pattern.compile("\\{(.*?)\\}");
        Matcher matcher = pattern.matcher(value);

        List<String> matcherList = new ArrayList<>();
        while (matcher.find()) {
            matcherList.add(matcher.group(1));
        }

        return matcherList;
    }

    private void createPicture(HSSFWorkbook workBook,MObjectExportVo exportVo,List<String> picturesList) throws IOException {

        for (int i = 0; i < picturesList.size(); i++) {
            HSSFSheet sheet = workBook.getSheetAt(i);
            if(StringUtils.isBlank(picturesList.get(i))){
                continue;
            }
            //String picture = "C:\\jira\\person.png";  //要插入的图片，可为png、jpg格式
            String picture = PropertyConfigurer.getContextProperty("temppath") + picturesList.get(i);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            BufferedImage BufferImg = ImageIO.read(new File(picture));
            ImageIO.write(BufferImg, "PNG", bos);
            HSSFPatriarch patriarch = sheet.createDrawingPatriarch();//创建绘图工具对象

            int col1 = exportVo.getStartCol();  //起始单元格列序号；
            int row1 = exportVo.getStartRow();  //起始单元格行序号;
            int col2 = exportVo.getEndCol();    //终止单元格列序号；
            int row2 = exportVo.getEndRow();    //终止单元格行序号；
            HSSFClientAnchor anchor = new HSSFClientAnchor(0, 0, 0, 0, (short) col1, row1, (short) col2, row2);
            patriarch.createPicture(anchor, workBook.addPicture(bos.toByteArray(), workBook.PICTURE_TYPE_PNG));
        }
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
            }
        }
        return file;
    }

    public static void main(String args[]) {

        Pattern pattern = Pattern.compile("\\{(.*?)\\}");
        Matcher matcher = pattern.matcher("${aaa}");
        if (matcher.find()) {

            System.out.println(matcher.group(1));
        }
    }
}
