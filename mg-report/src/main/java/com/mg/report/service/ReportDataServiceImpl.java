package com.mg.report.service;

import com.mg.common.metadata.service.MEnumService;
import com.mg.common.metadata.service.MetaDataExpressService;
import com.mg.common.metadata.service.MetaDataQueryService;
import com.mg.common.metadata.service.MetaDataService;
import com.mg.common.metadata.util.MetaDataUtils;
import com.mg.common.utils.DateUtil;
import com.mg.framework.entity.metadata.*;
import com.mg.framework.entity.vo.TableHeaderCellVO;
import com.mg.report.entity.*;
import com.mg.report.util.ReportDateUtils;
import com.mg.report.vo.DimenDataVO;
import com.mg.report.vo.ReportRowDataVo;
import com.mg.report.vo.ReportVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 统计报表数据处理类
 * Created by liukefu on 2015/11/4.
 */
@Service
public class ReportDataServiceImpl implements ReportDataService {
    @Autowired
    MetaDataService metaDataService;
    @Autowired
    MetaDataQueryService metaDataQueryService;
    @Autowired
    ReportService reportService;
    @Autowired
    MEnumService mEnumService;
    @Autowired
    MetaDataExpressService metaDataExpressService;
    /**
     * 返回报表数据
     *
     * @param reportId
     * @return
     */
    @Transactional(readOnly = true)
    public ReportVo queryReportData(String reportId) {

        ReportEntity reportEntity = reportService.findReport(reportId);
        MObjectEntity mObjectEntity = metaDataQueryService.findMObjectById(reportEntity.getObjectId());

        List<MirrorPropertyEntity> mPropertyEntityList = getDependentProperties(mObjectEntity, reportEntity);
        reportEntity.getExpressGroup().setPageSize(-1);
        List<Map<String, Object>> list = metaDataService.queryByMetaData(mObjectEntity, mPropertyEntityList, reportEntity.getExpressGroup());

        return createReportBody(reportEntity, list);
    }

    /**
     * 计算报表主体数据
     *
     * @param reportEntity
     * @param list
     * @return
     */
    public ReportVo createReportBody(ReportEntity reportEntity, List<Map<String, Object>> list) {
        //报表的表头
        ReportVo reportVo = createReportHeader(reportEntity);
        //所有的行维度数据，比如，所属组织
        Map<String, ReportRowDataVo> rowDataMap = reportVo.getRowDataMap();
        //行维度
        ReportDimenEntity rowDimen = reportEntity.getRowDimen();


        //所有的行维度数据
        if(!rowDimen.getIsAccordingNeed()){
            createReportBody4RowDimen(reportEntity,list,rowDataMap);
        }else{
            createReportBody4Data(reportEntity,list,rowDataMap);
        }

        reportVo.setList(rowDataMap.values());
        return reportVo;
    }
    /**
     * 设置报表的标题行数据
     * @param reportEntity
     * @return
     */
    private ReportVo createReportHeader(ReportEntity reportEntity) {
        ReportVo reportVo = new ReportVo();
        reportVo.setReport(reportEntity);
        //最大深度
        Integer maxLevel = 2;
        //-----------------------------------显示数据行时使用--------------------------------------------------
        //行维度
        ReportDimenEntity rowDimen = reportEntity.getRowDimen();
        TableHeaderCellVO rowDimenHeader = new TableHeaderCellVO(getRowDimenKey(rowDimen), rowDimen.getProperty().getName());
        rowDimenHeader.setRowspan(maxLevel);
        rowDimenHeader.setProperty(rowDimen.getProperty());
        //显示的列
        List<ReportColumnEntity> columns = reportEntity.getColumns();
        List<TableHeaderCellVO> showColumns = new ArrayList<>();
        for (ReportColumnEntity columnEntity : columns) {
            String namePropertyPath = columnEntity.getProperty().getPropertyPath();
            if(columnEntity.getProperty().getControllerType() == MControllerTypeEnum.object){
                namePropertyPath = MetaDataUtils.getNamePropertyPath(columnEntity.getProperty());
            }else if(columnEntity.getProperty().getControllerType() == MControllerTypeEnum.mEnum){

            }
            TableHeaderCellVO columnHeader = new TableHeaderCellVO(namePropertyPath, columnEntity.getProperty().getName());
            columnHeader.setRowspan(maxLevel);
            columnHeader.setProperty(columnEntity.getProperty());
            showColumns.add(columnHeader);
        }
        //显示的小项
        List<ReportDimenItemEntity> itemEntities = reportService.getReportLastItemList(reportEntity);
        List<TableHeaderCellVO> dimenColumns = new ArrayList<>();
        for (ReportDimenItemEntity itemEntity : itemEntities) {
            TableHeaderCellVO columnHeader = new TableHeaderCellVO(itemEntity.getId(), itemEntity.getAliasName());
            columnHeader.setRowspan(1);
            columnHeader.setProperty(itemEntity.getProperty());
            dimenColumns.add(columnHeader);
        }
        //--------------------------------------------------画表格的表头时使用--------------------------------------------
        createHeaderColumns(reportVo, reportEntity);

        reportVo.setRowDimen(rowDimenHeader);
        reportVo.setShowColumns(showColumns);
        reportVo.setDimenColumns(dimenColumns);
        return reportVo;
    }

    /**
     * 查询所有行维度的数据
     * @param rowDimen
     * @param expressGroup
     * @return
     */
    public List<Map<String, Object>> queryRowDimenData(ReportDimenEntity rowDimen, MExpressGroupEntity expressGroup){
        MirrorPropertyEntity property = rowDimen.getProperty();
        List<Map<String, Object>> list = new ArrayList<>();
        if(property.getControllerType()==MControllerTypeEnum.mEnum){
            //枚举类型的行维度
            List<MEnumEntity> enumList = mEnumService.findByEnumName(property.getEnumName());

            for (MEnumEntity enumEntity:enumList){
                Map<String, Object> map = new HashMap<>();
                map.put(MetaDataUtils.META_FIELD_ID,enumEntity.getId());
                map.put(MetaDataUtils.META_FIELD_KEY,enumEntity.getKey());
                map.put(MetaDataUtils.META_FIELD_NAME,enumEntity.getName());
                list.add(map);
            }
        }else if(property.getControllerType()==MControllerTypeEnum.object){
            //对象类型，查询所有对象
            MObjectEntity mObjectEntity = property.getMetaProperty().getMetaObject();

            MExpressGroupEntity rowDimenGroup = getRowDimenScope(rowDimen,expressGroup);
            list = metaDataService.queryByMetaData(mObjectEntity, rowDimenGroup);
        }


        return list;
    }

    /**
     * 根据统计范围，过滤行维度范围
     * 统计范围条件组中，凡是行维度下面的条件，都起到过滤作用
     * @param rowDimen
     * @param expressGroup
     * @return
     */
    private MExpressGroupEntity getRowDimenScope(ReportDimenEntity rowDimen, MExpressGroupEntity expressGroup){
        MirrorPropertyEntity property = rowDimen.getProperty();

        MObjectEntity mObjectEntity = property.getMetaProperty().getMetaObject();
        List<MirrorPropertyEntity> childProperties = metaDataQueryService.findMPropertyByRootMObject(property.getRootMObject(), mObjectEntity);
        Map<String,MirrorPropertyEntity> rowMap = new HashMap<>();
        rowMap.put(property.getId(),property);
        for(MirrorPropertyEntity propertyEntity:childProperties){
            rowMap.put(propertyEntity.getId(), propertyEntity);
        }

        MExpressGroupEntity rowDimenGroup = metaDataExpressService.createBlankExpressGroup(null);
        rowDimenGroup.setPageSize(5000);
        List<MExpressionEntity> expressions = expressGroup.getMatched().getExpressions();
        for(MExpressionEntity expressionEntity:expressions){
            MirrorPropertyEntity childProperty = rowMap.get(expressionEntity.getProperty().getId());
            if(childProperty != null){
                String fieldName = childProperty.getFieldName();
                if(StringUtils.equals(childProperty.getId(),property.getId())){
                    fieldName = MetaDataUtils.META_FIELD_ID;
                }
                MirrorPropertyEntity rowProperty = metaDataQueryService.findMPropertyByBelongMObjectAndFieldName(mObjectEntity,mObjectEntity, fieldName);
                MExpressionEntity rowDimenExpress = new MExpressionEntity(rowProperty,false,true);
                for(MExpressionEntity expression:expressionEntity.getExpressions()){
                    MExpressionEntity dimenExpress = new MExpressionEntity(rowProperty,false,false);
                    dimenExpress.setRelation(expression.getRelation());
                    dimenExpress.setValue(expression.getValue());
                    dimenExpress.setValueType(expression.getValueType());
                    rowDimenExpress.addExpressions(dimenExpress);
                }
                rowDimenGroup.getMatched().addExpressions(rowDimenExpress);
            }
        }

        return rowDimenGroup;
    }


    /**
     * 以维度为标准计算
     * @param reportEntity
     * @param list
     * @param rowDataMap
     * @return
     */
    public void createReportBody4RowDimen(ReportEntity reportEntity, List<Map<String, Object>> list, Map<String, ReportRowDataVo> rowDataMap) {
        //行维度
        ReportDimenEntity rowDimen = reportEntity.getRowDimen();
        String rowDimenKey = rowDimen.getProperty().getPropertyPath();
        String rowDimenNameKey = MetaDataUtils.getNamePropertyPath(rowDimen.getProperty());
        List<Map<String, Object>> rowDimenData = queryRowDimenData(rowDimen, reportEntity.getExpressGroup());
        for (Map<String, Object> map : rowDimenData) {
            Object dimen = map.get(MetaDataUtils.META_FIELD_ID);
            Object dimenName = map.get(MetaDataUtils.META_FIELD_NAME);
            String dimenKey = getRowDimenKey(String.valueOf(dimen));
            ReportRowDataVo rowData = rowDataMap.get(dimenKey);
            if (rowData == null) {
                rowData = new ReportRowDataVo(dimenKey,(String)dimenName);
                rowDataMap.put(String.valueOf(dimenKey), rowData);
            }
            //放入维度的名称
            rowData.getRowData().put(getRowDimenKey(rowDimen), dimenName);
        }
        //所有数据按照行维度分组存放
        for (Map<String, Object> map : list) {
            Object dimen = map.get(rowDimenKey);
            if(dimen==null){
                continue;
            }
            Object dimenName = map.get(rowDimenNameKey);
            if(dimenName==null){
                dimenName = "";
            }
            String rowDimenKeyValue = getRowDimenKey(String.valueOf(dimen));
            String rowDimenKeyName = String.valueOf(dimenName);

            ReportRowDataVo rowData = rowDataMap.get(rowDimenKeyValue);
            if (rowData == null) {
                rowData = new ReportRowDataVo(rowDimenKeyValue,rowDimenKeyName);
                rowDataMap.put(rowDimenKeyValue, rowData);
            }
            //行维度显示的数据
            rowData.getRowData().put(getRowDimenKey(rowDimen), rowDimenKeyName);
            rowData.getDetailList().add(map);
        }

        //每行数据和列维度的匹配情况
        List<ReportDimenEntity> columnDimens = reportEntity.getColumnDimens();
        Iterator<String> it = rowDataMap.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            ReportRowDataVo rowData = rowDataMap.get(key);
            //每个维度下的明细数据
            List<Map<String, Object>> detailList = rowData.getDetailList();
            for (Map<String, Object> map : detailList) {
                //每行数据与小项的匹配情况，如果匹配对应的列数值+1
                for (ReportDimenEntity dimenEntity : columnDimens) {
                    checkMatched(rowData, map, dimenEntity);
                }
            }
        }
    }

    /**
     * 以数据为标准计算
     * @param reportEntity
     * @param list
     * @param rowDataMap
     * @return
     */
    public void createReportBody4Data(ReportEntity reportEntity, List<Map<String, Object>> list, Map<String, ReportRowDataVo> rowDataMap) {
        //行维度
        ReportDimenEntity rowDimen = reportEntity.getRowDimen();
        String rowDimenKey = rowDimen.getProperty().getPropertyPath();
        String rowDimenNameKey = MetaDataUtils.getNamePropertyPath(rowDimen.getProperty());

        //所有数据按照行维度分组存放
        for (Map<String, Object> map : list) {
            Object dimen = map.get(rowDimenKey);
            if(dimen==null){
                dimen = "无";
            }
            Object dimenName = map.get(rowDimenNameKey);
            if(dimenName==null){
                dimenName = "无";
            }
            String rowDimenKeyValue = getRowDimenKey(String.valueOf(dimen));
            String rowDimenKeyName = String.valueOf(dimenName);

            ReportRowDataVo rowData = rowDataMap.get(rowDimenKeyValue);
            if (rowData == null) {
                rowData = new ReportRowDataVo(rowDimenKeyValue,rowDimenKeyName);
                rowDataMap.put(rowDimenKeyValue, rowData);
            }
            //行维度显示的数据
            rowData.getRowData().put(getRowDimenKey(rowDimen), rowDimenKeyName);
            rowData.getDetailList().add(map);
        }
        //每行数据和列维度的匹配情况
        List<ReportDimenEntity> columnDimens = reportEntity.getColumnDimens();
        Iterator<String> it = rowDataMap.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            ReportRowDataVo rowData = rowDataMap.get(key);
            //每个维度下的明细数据
            List<Map<String, Object>> detailList = rowData.getDetailList();
            for (Map<String, Object> map : detailList) {
                //每行数据与小项的匹配情况，如果匹配对应的列数值+1
                for (ReportDimenEntity dimenEntity : columnDimens) {
                    checkMatched(rowData, map, dimenEntity);
                }
            }
        }
    }
    /**
     * 每行数据与小项的匹配情况
     * 如果匹配对应的列数值+1
     *
     * @param rowData
     * @param map
     * @param dimenEntity
     */
    private void checkMatched(ReportRowDataVo rowData, Map<String, Object> map, ReportDimenEntity dimenEntity) {

        List<ReportDimenItemEntity> itemList = dimenEntity.getItemList();
        if (itemList != null) {
            for (ReportDimenItemEntity itemEntity : itemList) {
                if(itemEntity.getBelongDimen().getStatisticalMethodEnum()==StatisticalMethodEnum.FOR_SHOW
                        && isMatched(itemEntity, map)){
                    //只是显示项的时候，维度直接显示数字
                    MirrorPropertyEntity propertyValue = itemEntity.getProperty();
                    Object value = map.get(propertyValue.getPropertyPath());

                    //明细
                    map.put(itemEntity.getId(), value);

                    rowData.getRowData().put(itemEntity.getId(), value);
                    //找到匹配项的数据，添加到匹配列表
                    if(itemEntity.getIsLeaf()){
                        rowData.addMatchedDetail(map,(String)map.get(MetaDataUtils.META_FIELD_ID));
                    }
                }else if (isMatched(itemEntity, map)) { //如果数据匹配小项的条件
                    calculation(rowData, itemEntity, map);
                    //根据不同的统计方法分别计算
                    statisticalMethod(rowData,itemEntity,map);
                    //仅当数据匹配小项的时候，再看是否和小项的小项匹配
                    //小项下的维度的小项
                    List<ReportDimenEntity> dimenList = itemEntity.getDimenList();
                    for (ReportDimenEntity itemDimen : dimenList) {
                        checkMatched(rowData, map, itemDimen);
                    }
                }
            }
        }
    }

    /**
     * 获取小项的值
     * @param rowData
     * @param itemEntity
     * @return
     */
    private BigDecimal getValue(ReportRowDataVo rowData, ReportDimenItemEntity itemEntity, Map<String, Object> map){
        //元数据
        MirrorPropertyEntity property = itemEntity.getProperty();
        Object value = map.get(property.getPropertyPath());
        BigDecimal bigDecimal = null;
        switch (property.getMetaProperty().getFieldType()){
            case INTEGER:
                Integer num = (Integer) value;
                if(num==null){
                    num = 0;
                }
                bigDecimal = new BigDecimal(num);
                break;
            case LONG:
                Long lnum = (Long) value;
                if(lnum==null){
                    lnum = new Long(0);
                }
                bigDecimal = new BigDecimal(lnum);
                break;
            case DOUBLE:
                Double dnum = (Double) value;
                if(dnum==null){
                    dnum = new Double(0);
                }
                bigDecimal = new BigDecimal(dnum);
                break;
            case DECIMAL:
                bigDecimal = (BigDecimal) value;

                break;
        }

        return bigDecimal;
    }
    /**
     * 计算各项分值
     * 最大值、最小值、总数，总个数等项目
     * @param rowData
     * @param itemEntity
     * @param map
     */
    private void calculation(ReportRowDataVo rowData, ReportDimenItemEntity itemEntity, Map<String, Object> map){
        //元数据
        MirrorPropertyEntity property = itemEntity.getProperty();
        //明细在小项下面的个数为
        Object value = map.get(property.getPropertyPath());
        String name = (String)map.get(MetaDataUtils.META_FIELD_NAME);
        BigDecimal bigDecimal = getValue(rowData,itemEntity,map);
        map.put(itemEntity.getId(), value);
        map.put(getRowDimenKey(itemEntity.getBelongReport().getRowDimen()), name);

        DimenDataVO dimenDataVO = getDimenData(rowData, itemEntity);
        dimenDataVO.setCount(dimenDataVO.getCount() + 1);
        if(bigDecimal!=null){
            dimenDataVO.setTotal(dimenDataVO.getTotal().add(bigDecimal));
            if( bigDecimal.doubleValue()>dimenDataVO.getMaxValue().doubleValue()){
                dimenDataVO.setMaxValue(bigDecimal);
            }else if (bigDecimal.doubleValue()<dimenDataVO.getMinValue().doubleValue()){
                dimenDataVO.setMinValue(bigDecimal);
            }
        }
    }

    private DimenDataVO getDimenData(ReportRowDataVo rowData,ReportDimenItemEntity itemEntity){

        DimenDataVO vo = rowData.getDimenData().get(itemEntity.getId());
        if(vo==null){
            vo = new DimenDataVO();
            rowData.getDimenData().put(itemEntity.getId(), vo);
        }
        return vo;
    }

    /**
     * 根据不同的统计方法分别计算
     * @param rowData
     * @param itemEntity
     * @param map
     */
    private void statisticalMethod(ReportRowDataVo rowData, ReportDimenItemEntity itemEntity, Map<String, Object> map){
        //统计方法
        StatisticalMethodEnum statisticalMethodEnum = itemEntity.getBelongDimen().getStatisticalMethodEnum();
        switch (statisticalMethodEnum){
            case COUNT:
                statisticalMethodCount(rowData,itemEntity,map);
                break;
            case SUM:
                statisticalMethodNum(rowData, itemEntity, map,statisticalMethodEnum);
                break;
            case AVG:
                statisticalMethodNum(rowData, itemEntity, map,statisticalMethodEnum);
                break;
            case MAX:
                statisticalMethodNum(rowData, itemEntity, map,statisticalMethodEnum);
                break;
            case MIN:
                statisticalMethodNum(rowData, itemEntity, map,statisticalMethodEnum);
                break;
        }

    }

    private void statisticalMethodCount(ReportRowDataVo rowData, ReportDimenItemEntity itemEntity, Map<String, Object> map){
        //明细在小项下面的个数为1
        map.put(itemEntity.getId(), new Integer(1));

        DimenDataVO dimenDataVO = getDimenData(rowData, itemEntity);
        rowData.getRowData().put(itemEntity.getId(), dimenDataVO.getCount());
        //找到匹配项的数据，添加到匹配列表
        if(itemEntity.getIsLeaf()){
            rowData.addMatchedDetail(map,(String)map.get(MetaDataUtils.META_FIELD_ID));
        }
    }

    private void statisticalMethodNum(ReportRowDataVo rowData,ReportDimenItemEntity itemEntity,
                                      Map<String, Object> map,StatisticalMethodEnum statisticalMethodEnum){
        //元数据
        MirrorPropertyEntity property = itemEntity.getProperty();
        //明细在小项下面的个数为
        Object value = map.get(property.getPropertyPath());
        map.put(itemEntity.getId(), value);

        DimenDataVO dimenDataVO = getDimenData(rowData, itemEntity);

        switch (statisticalMethodEnum){
            case SUM:
                rowData.getRowData().put(itemEntity.getId(), dimenDataVO.getTotal());
                break;
            case AVG:
                Integer count = dimenDataVO.getCount();
                if(count!=0){
                    dimenDataVO.setAvg(dimenDataVO.getTotal().divide(new BigDecimal(count),2,BigDecimal.ROUND_HALF_UP));
                }
                rowData.getRowData().put(itemEntity.getId(), dimenDataVO.getAvg());
                break;
            case MAX:
                rowData.getRowData().put(itemEntity.getId(), dimenDataVO.getMaxValue());
                break;
            case MIN:
                rowData.getRowData().put(itemEntity.getId(), dimenDataVO.getMinValue());
                break;
        }

        //添加明显数据
        if(itemEntity.getIsLeaf()){
            rowData.addMatchedDetail(map,(String)map.get(MetaDataUtils.META_FIELD_ID));
        }
    }

    /**
     * 判断单个数据，是否落在小项的范围内
     *
     * @param itemEntity
     * @param map
     * @return
     */
    private boolean isMatched(ReportDimenItemEntity itemEntity, Map<String, Object> map) {

        MControllerTypeEnum controllerTypeEnum = itemEntity.getPropertyScope().getControllerType();
        switch (controllerTypeEnum) {
            case text:
            case mEnum:
                return selectScope(itemEntity,map);
            case number:
                return numberBetween(itemEntity,map);
            case bool:
            case date:
                return dateBetween(itemEntity, map);
            case object:
            case subType:
                return selectScope(itemEntity,map);
            default:
                break;
        }

        return false;
    }

    private boolean selectScope(ReportDimenItemEntity itemEntity, Map<String, Object> map) {
        String scope = itemEntity.getSelectScope();
        String arr[] = scope.split(";");
        String propertyPath = itemEntity.getPropertyScope().getPropertyPath();
        Object propertyValue = map.get(propertyPath);
        boolean isLike = itemEntity.getDataType() == MRelationEnum.LIKE;
        for(String value:arr){
            if(StringUtils.isNotBlank(value) && value.equals(propertyValue)){
                //等于
                return true;
            }
            if(StringUtils.isNotBlank(value) && isLike){
                //模糊匹配
                if(String.valueOf(propertyValue).indexOf(value) >= 0){
                    return true;
                }
            }
            if(StringUtils.isBlank(value)
                    && (propertyValue==null || StringUtils.isBlank(String.valueOf(propertyValue))) ){
                return true;
            }
        }
        return false;
    }

    private boolean numberBetween(ReportDimenItemEntity itemEntity, Map<String, Object> map) {

        String propertyPath = itemEntity.getPropertyScope().getPropertyPath();
        Object propertyValue = map.get(propertyPath);
        Double begin = itemEntity.getNumFrom();
        Double end = itemEntity.getNumTo();
        if(propertyValue==null) {

        } else if(Integer.class.isAssignableFrom(propertyValue.getClass())){
            Integer num = (Integer)propertyValue;
            if(num>=begin.intValue() && num <= end.intValue()){
                return true;
            }
        }else  if(Long.class.isAssignableFrom(propertyValue.getClass())){
            Long num = (Long)propertyValue;
            if(num>=begin.longValue() && num <= end.longValue()){
                return true;
            }
        }else  if(Double.class.isAssignableFrom(propertyValue.getClass())){
            Double num = (Double)propertyValue;
            if(num>=begin.doubleValue() && num <=end.doubleValue()){
                return true;
            }
        }else  if(BigDecimal.class.isAssignableFrom(propertyValue.getClass())){
            BigDecimal num = (BigDecimal)propertyValue;
            if(num.doubleValue()>=begin.doubleValue() && num.doubleValue() <=end.doubleValue()){
                return true;
            }
        }

        return false;
    }

    private boolean dateBetween(ReportDimenItemEntity itemEntity, Map<String, Object> map) {

        String propertyPath = itemEntity.getPropertyScope().getPropertyPath();
        Object propertyValue = map.get(propertyPath);

        Date begin = null;
        Date end = null;
        //指定区间
        if(itemEntity.getDataType()==1){
            begin = DateUtil.convertStringToDate(itemEntity.getDateFrom());
            end = DateUtil.convertStringToDate(itemEntity.getDateTo());
        }else if(itemEntity.getDataType()==2){
            //相对区间
            begin = ReportDateUtils.getDateScopeBegin(itemEntity.getSelectScope());
            end = ReportDateUtils.getDateScopeEnd(itemEntity.getSelectScope());
        }
        if(propertyValue==null) {

        } else  if(Date.class.isAssignableFrom(propertyValue.getClass())){
            Date num = (Date)propertyValue;
            if(num.getTime()>= begin.getTime() && num.getTime() <= end.getTime()){
                return true;
            }
        }

        return false;
    }

    /**
     * 获取报表依赖的元数据
     * @param mObjectEntity
     * @param reportEntity
     * @return
     */
    public List<MirrorPropertyEntity> getDependentProperties(MObjectEntity mObjectEntity, ReportEntity reportEntity) {
        List<MirrorPropertyEntity> mPropertyEntityList = new ArrayList<>();
        Map<String, String> map = new HashMap<>();//过滤重复使用
        //pk
        MirrorPropertyEntity pkProperty = metaDataQueryService.findPrimaryKeyMPropertyByBelongMObject(mObjectEntity);
        addProperty(map, mPropertyEntityList, pkProperty);
        //name
        MirrorPropertyEntity nameProperty = metaDataQueryService.findMPropertyByBelongMObjectAndFieldNameAndDeep(mObjectEntity, mObjectEntity, MetaDataUtils.META_FIELD_NAME, 0);
        addProperty(map, mPropertyEntityList, nameProperty);
        List<ReportColumnEntity> columns = reportEntity.getColumns();
        if (columns != null) {
            for (ReportColumnEntity columnEntity : columns) {
                addProperty(map, mPropertyEntityList, columnEntity.getProperty());
            }
        }
        List<ReportDimenEntity> columnDimens = reportEntity.getColumnDimens();
        if (columnDimens != null) {
            for (ReportDimenEntity dimenEntity : columnDimens) {
                getDimenDependentProperties(map, mPropertyEntityList, dimenEntity);
            }
        }
        return mPropertyEntityList;
    }

    /**
     * 获取维度依赖的元数据列表
     *
     * @param map
     * @param mPropertyEntityList
     * @param dimenEntity
     */
    private void getDimenDependentProperties(Map<String, String> map, List<MirrorPropertyEntity> mPropertyEntityList, ReportDimenEntity dimenEntity) {

        addProperty(map, mPropertyEntityList, dimenEntity.getProperty());
        addProperty(map, mPropertyEntityList, dimenEntity.getPropertyScope());
        List<ReportDimenItemEntity> itemList = dimenEntity.getItemList();
        if (itemList != null) {
            for (ReportDimenItemEntity dimenItemEntity : itemList) {

                List<ReportDimenEntity> columnDimens = dimenItemEntity.getDimenList();
                if (columnDimens != null) {
                    for (ReportDimenEntity dimen : columnDimens) {
                        getDimenDependentProperties(map, mPropertyEntityList, dimen);
                    }
                }
            }
        }
    }

    /**
     * 增加对一个元数据的依赖
     *
     * @param map
     * @param mPropertyEntityList
     * @param propertyEntity
     */
    private void addProperty(Map<String, String> map, List<MirrorPropertyEntity> mPropertyEntityList, MirrorPropertyEntity propertyEntity) {
        if (propertyEntity != null && map.get(propertyEntity.getId()) == null) {
            map.put(propertyEntity.getId(), "");
            mPropertyEntityList.add(propertyEntity);
        }
    }

    private List<List<TableHeaderCellVO>> createHeaderColumns(ReportVo reportVo,ReportEntity reportEntity){
        List<List<TableHeaderCellVO>> headerColumns = new ArrayList<>();
        List<TableHeaderCellVO> leafColumns = new ArrayList<>();
        List<TableHeaderCellVO> rowOne = new ArrayList<>();
        headerColumns.add(rowOne);

        List<ReportDimenItemEntity> itemEntities = reportService.findDimenItemsByReports(reportEntity);
        int maxLevel = 1;
        if(itemEntities!=null){
            maxLevel = itemEntities.get(itemEntities.size()-1).getDimenLev();
        }
        reportVo.setMaxLevel(maxLevel);
        //行维度
        ReportDimenEntity rowDimen = reportEntity.getRowDimen();
        TableHeaderCellVO rowDimenHeader = new TableHeaderCellVO(getRowDimenKey(rowDimen), rowDimen.getProperty().getName());
        rowDimenHeader.setColspan(1);
        rowDimenHeader.setRowspan(maxLevel);
        rowDimenHeader.setProperty(rowDimen.getProperty());
        rowOne.add(rowDimenHeader);
        leafColumns.add(rowDimenHeader);
        if(rowDimen.getProperty().getControllerType() == MControllerTypeEnum.mEnum){
            List<MEnumEntity> temList = mEnumService.findByEnumName(rowDimen.getProperty().getEnumName());
            //放入枚举值
            rowDimenHeader.setFilter(temList);
        }else if(rowDimen.getProperty().getControllerType()==MControllerTypeEnum.bool){
            MEnumEntity no = new MEnumEntity("0","否");
            MEnumEntity yes = new MEnumEntity("1","是");
            List<MEnumEntity> temList = new ArrayList<>();
            temList.add(yes);
            temList.add(no);
            rowDimenHeader.setFilter(temList);
        }

        //显示的列
        List<ReportColumnEntity> columns = reportEntity.getColumns();
        for (ReportColumnEntity columnEntity : columns) {
            String namePropertyPath = columnEntity.getProperty().getPropertyPath();
            TableHeaderCellVO columnHeader = new TableHeaderCellVO(namePropertyPath, columnEntity.getProperty().getName());
            if(columnEntity.getProperty().getControllerType() == MControllerTypeEnum.object){
                namePropertyPath = MetaDataUtils.getNamePropertyPath(rowDimen.getProperty());
                columnHeader.setField(namePropertyPath);
            }else if(columnEntity.getProperty().getControllerType() == MControllerTypeEnum.mEnum){
                List<MEnumEntity> temList = mEnumService.findByEnumName(columnEntity.getProperty().getEnumName());
                //放入枚举值
                columnHeader.setFilter(temList);
            }else if(columnEntity.getProperty().getControllerType()==MControllerTypeEnum.bool){
                MEnumEntity no = new MEnumEntity("0","否");
                MEnumEntity yes = new MEnumEntity("1","是");
                List<MEnumEntity> temList = new ArrayList<>();
                temList.add(yes);
                temList.add(no);
                columnHeader.setFilter(temList);
            }

            columnHeader.setColspan(1);
            columnHeader.setRowspan(maxLevel);
            columnHeader.setProperty(columnEntity.getProperty());
            rowOne.add(columnHeader);
            leafColumns.add(columnHeader);

        }
        //显示的小项
        for(ReportDimenItemEntity itemEntity:itemEntities){
            int level = itemEntity.getDimenLev()-1;
            List<TableHeaderCellVO> row = null;
            if(level>=headerColumns.size()){
                row = new ArrayList<>();
                headerColumns.add(row);
            }else{
                row = headerColumns.get(level);
            }
            TableHeaderCellVO columnHeader = new TableHeaderCellVO(itemEntity.getId(), itemEntity.getAliasName());
            AtomicInteger colspanAc = new AtomicInteger(0);
            getColSpanNum(itemEntity, colspanAc);
            int colspan = colspanAc.get();
            if(colspan<=0){
                colspan = 1;
            }
            columnHeader.setColspan(colspan);
            AtomicInteger itemNumAc = new AtomicInteger(0);
            getItemNum(itemEntity, itemNumAc);
            int rowspan = 1;
            if(itemNumAc.get()==0){
                rowspan = maxLevel - level;
            }
            columnHeader.setRowspan(rowspan);
            //columnHeader.setProperty(itemEntity.getProperty());
            row.add(columnHeader);
            if(itemEntity.getIsLeaf()){
                leafColumns.add(columnHeader);
            }
        }
        reportVo.setHeaderColumns(headerColumns);
        reportVo.setLeafColumns(leafColumns);

        return headerColumns;
    }

    private String getRowDimenKey(ReportDimenEntity rowDimen){

        return "rowDimen"+rowDimen.getId();
    }

    private String getRowDimenKey(String rowDimenId){

        return "rowDimen"+rowDimenId;
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
}
