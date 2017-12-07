package com.mg.common.metadata.service;

import com.mg.common.metadata.dao.MImportTemplateDao;
import com.mg.common.metadata.util.ExcelUtil;
import com.mg.framework.entity.metadata.*;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mg.framework.exception.ServiceException;
import com.mg.framework.sys.PropertyConfigurer;
import com.mg.framework.utils.UserHolder;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * 元数据数据导入
 * Created by liukefu on 2015/10/21.
 */
@Service
public class MetaDataImportServiceImpl implements MetaDataImportService {
    Logger logger = LoggerFactory.getLogger(getClass());
    private static final String STR_IGNORE = "#";
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    MetaDataQueryService metaDataQueryService;
    @Autowired
    CustomFormService customFormService;
    @Autowired
    MImportTemplateDao mImportTemplateDao;
    @Autowired
    MEnumService mEnumService;
    @Autowired
    MetaDataService metaDataService;
    @Autowired
    MetaDataExpressService metaDataExpressService;
    /**
     * 数据导入
     * @param path
     */
    @Transactional
    public void importData(String path) {
        if(!(path.endsWith(".xlsx") || path.endsWith(".xls"))){
            throw new ServiceException("不合法的文件类型,请上传Excel类型文件!");
        }

        //打开excel
        Workbook wb = null;
        FileInputStream inputStream = null;
        try {
            inputStream =new FileInputStream(new File(path));
            wb = WorkbookFactory.create(inputStream);

            int totalSheetNum = wb.getNumberOfSheets();
            for (int i = 0; i < totalSheetNum; i++) {
                Sheet sheet = wb.getSheetAt(i);
                String sheetName = sheet.getSheetName();
                if (sheetName.startsWith(STR_IGNORE)) {
                    logger.debug("ignore this sheet:{}", sheetName);
                    continue;
                }
                MObjectEntity mObjectEntity = metaDataQueryService.findMObjectByName(sheetName);
                if(mObjectEntity!=null){
                    importDataFromSheet(wb.getSheet(sheetName), mObjectEntity);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("open/create Excel failure, filePath={}", path);
            throw new ServiceException(e);
        }finally {
            if(inputStream!=null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void importDataFromSheet(Sheet sheet, MObjectEntity mObjectEntity) {

        //总列数、总行数
        Row rowHead = sheet.getRow(0);
        int totalRows = sheet.getLastRowNum();
        int totalColumns = rowHead.getLastCellNum();
        //pkProperties 修改功能
        List<MirrorPropertyEntity> pkProperties = new ArrayList<>();
        Map<String,Integer> pkPropertiesMap = new HashMap<>();
        Map<Integer,MirrorPropertyEntity> propertiesMap = new HashMap<>();
        List<MirrorPropertyEntity> properties = new ArrayList<>();
        //读取第一行标题
        for (int i = 0; i < totalColumns; i++) {
            Cell cell = rowHead.getCell(i);
            if (cell == null) {
                continue;
            }
            String metadataName = ExcelUtil.getStringValue2007(cell);
            if(StringUtils.isNotBlank(metadataName)){
                MirrorPropertyEntity property = metaDataQueryService.findMPropertyByName(mObjectEntity, metadataName);
                if(property!=null){
                    properties.add(property);
                    propertiesMap.put(i, property);
                    //如果是粗体，表示按字段唯一
                    Font font = sheet.getWorkbook().getFontAt(cell.getCellStyle().getFontIndex());
                    if (font.getBoldweight() == Font.BOLDWEIGHT_BOLD) {
                        pkProperties.add(property);
                        pkPropertiesMap.put(property.getPropertyPath(),i);
                    }
                }else{
                    logger.debug("cannot find metadata {}",metadataName);
                }
            }
        }
        //对象的所有属性
        List<MirrorPropertyEntity> mPropertyEntityList = metaDataQueryService.findMPropertyByBelongMObject(mObjectEntity);
        //读取数据列，第二行开始
        for (int i = 1; i <= totalRows; i++) {
            Row row = sheet.getRow(i);
            String firstValue = String.valueOf(row.getCell(0));
            if (row == null || StringUtils.isBlank(firstValue)) {
                continue;
            }

            List<Map<String,Object>> existList = new ArrayList<>();
            //指定了唯一的字段，则导入启用修改模式
            boolean isUpdate = false;
            if(pkProperties.size()>0){
                //读取外键的值
                for(MirrorPropertyEntity pkProperty:pkProperties){
                    Integer pkIndex = pkPropertiesMap.get(pkProperty.getPropertyPath());
                    Object value = ExcelUtil.getValue2007(row.getCell(pkIndex),pkProperty);

                    //转化成需要的值
                    value = getMetaDataValue(pkProperty,value);
                    pkProperty.setFieldValue(value);
                }
                MExpressionEntity expression = metaDataExpressService.createExpress(pkProperties);
                MExpressGroupEntity expressGroup = new MExpressGroupEntity(expression);
                existList = metaDataService.queryByMetaData(mObjectEntity,mPropertyEntityList, expressGroup);
            }
            if(existList.size()>0){
                isUpdate = true;
                //修改匹配的每条记录
                for (Map<String,Object> rowData:existList){
                    saveOrUpdate(mObjectEntity,row,rowData,propertiesMap,isUpdate,totalColumns);
                }
            }else{
                saveOrUpdate(mObjectEntity,row,new HashMap<String, Object>(),propertiesMap,isUpdate,totalColumns);
            }
        }
    }

    public void saveOrUpdate(MObjectEntity mObjectEntity, Row row, Map<String,Object> rowData,
                             Map<Integer,MirrorPropertyEntity> propertiesMap,
                             boolean isUpdate,
                             int totalColumns){
        //读取一行数据
        for (int j = 0; j < totalColumns; j++) {
            MirrorPropertyEntity property = propertiesMap.get(j);
            if(property == null){
                continue;
            }
            Object value = ExcelUtil.getValue2007(row.getCell(j),property);
            //转化成需要的值
            value = getMetaDataValue(property, value);
            rowData.put(property.getPropertyPath(), value);
        }
        if (!isUpdate) {
            customFormService.saveData(mObjectEntity.getId(),rowData);
        }else{
            customFormService.updateData(mObjectEntity.getId(),rowData);
        }
    }

    private Object getMetaDataValue(MirrorPropertyEntity property, Object value){
        if(value==null || property==null || (String.class.isAssignableFrom(value.getClass()) && StringUtils.isBlank((String)value))){
            return null;
        }
        if(property.getControllerType()==MControllerTypeEnum.mEnum){
            MEnumEntity enumEntity = mEnumService.findByName(property.getEnumName(), String.valueOf(value));
            if(enumEntity!=null){
                return enumEntity.getKey();
            }
            return null;
        }else if(property.getControllerType()==MControllerTypeEnum.bool){
            if(StringUtils.equals("是",(String)value)){
                return 1;
            }
            return 0;
        }else if(property.getControllerType()==MControllerTypeEnum.object){
            return metaDataService.queryIdByIdentifier(property.getMetaProperty().getMetaObject().getId(), String.valueOf(value));
        }
        return value;
    }
    /**
     * 创建导入数据模板
     * @param template
     * @return
     */
    public  boolean createExcelTemplate(MImportTemplateEntity template, String objId) {
        MObjectEntity mObjectEntity = metaDataQueryService.findMObjectById(objId);
        String sheetName = mObjectEntity.getName();
        String name = template.getTemplateName();
        String[] titles = template.getTitles();
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet(sheetName);
        HSSFRow row = sheet.createRow(0);
        HSSFCellStyle style = wb.createCellStyle();
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        for (int i = 0; i < titles.length; i++) {
            HSSFCell cell = row.createCell(i);
            cell.setCellValue(titles[i]);
            cell.setCellStyle(style);
        }

        FileOutputStream out = null;
        try {
            String instanceSeqId = UserHolder.getLoginUserTenantId();
            String path =    File.separator+instanceSeqId + File.separator
                    + "import"+ File.separator
                    + objId+ File.separator;

            long time = new Date().getTime();
            String realPath = PropertyConfigurer.getContextProperty("temppath") + path;
            ExcelUtil.makeDirs(realPath);
            realPath = realPath + time + ".xls";
            path = path + time + ".xls";
            template.setPath(path);
            out = new FileOutputStream(realPath);
            wb.write(out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }
    /**
     * 保存导入模板
     * @param templateEntity
     * @param objId
     * @return
     */
    public MImportTemplateEntity saveImportTemplate(MImportTemplateEntity templateEntity, String objId){
        MObjectEntity mObjectEntity = metaDataQueryService.findMObjectById(objId);

        templateEntity.setBelongMObject(mObjectEntity);
        mImportTemplateDao.save(templateEntity);
        return templateEntity;
    }

    /**
     * 查询对象下面的导入模板列表
     * @param objId
     * @return
     */
    public List<MImportTemplateEntity> queryImportTemplateList(String objId){
        JPAQuery query = new JPAQuery(entityManager);
        QMImportTemplateEntity obj = new QMImportTemplateEntity("mpro");

        List<MImportTemplateEntity> list = query.from(obj)
                .where(obj.belongMObject.id.eq(objId))

                .list(obj);

        return list;
    }
}
