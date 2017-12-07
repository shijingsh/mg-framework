package com.mg.common.metadata.service;

import com.mg.framework.entity.metadata.*;
import com.mg.framework.entity.vo.TableHeaderCellVO;
import com.mg.groovy.util.CloneFilter;
import com.mg.groovy.util.HRMSBeanClone;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * 条件表达式的服务类
 */
@Service
public class MetaDataExpressServiceImpl implements MetaDataExpressService {
    @Autowired
    MetaDataQueryService metaDataQueryService;

    /**
     * 创建一个空的表达式
     * @return
     */
    public MExpressGroupEntity createBlankExpressGroup(MExpressGroupEntity express){

        MExpressGroupEntity expressGroup = new MExpressGroupEntity();
        if(express!=null  && express.getPageSize()>0){
            expressGroup.setPageSize(express.getPageSize());
        }
        if(express!=null  && express.getPageNo()>0){
            expressGroup.setPageNo(express.getPageNo());
        }
        MExpressionEntity machedAll = new MExpressionEntity(true,true);
        expressGroup.setMatched(machedAll);
        return expressGroup;
    }
    /**
     * 创建一个默认的表达式
     * @param metaObject
     * @param maxLength
     * @return
     */
    public MExpressGroupEntity createDefaultExpressGroup(MObjectEntity metaObject, Integer maxLength){

        MExpressGroupEntity expressGroup = new MExpressGroupEntity();

        //根节点 匹配全部
        MExpressionEntity machedAll = new MExpressionEntity(true,true);

        List<MirrorPropertyEntity> list = metaDataQueryService.getSearchConditionListProperties(metaObject,maxLength);
        for(MirrorPropertyEntity mProperty:list){
            MExpressionEntity machedOne = new MExpressionEntity(mProperty,true,true);
            MExpressionEntity express = new MExpressionEntity(mProperty,MRelationEnum.EQ,"");

            machedOne.addExpressions(express);

            machedAll.addExpressions(machedOne);
        }
        expressGroup.setMatched(machedAll);
        return expressGroup;
    }
    /**
     * 创建绝对相等表达式
     * @param mProperty
     * @param value
     * @return
     */
    public MExpressionEntity createSimpleEqExpress(MirrorPropertyEntity mProperty, String value){
        if(mProperty!=null) {
            mProperty.setFieldValue(value);
        }
        //根节点 匹配全部
        MExpressionEntity machedAll = new MExpressionEntity(true,true);
        //二级节点 匹配任一
        MExpressionEntity machedOne = createqExpress(mProperty, value);

        machedAll.addExpressions(machedOne);

        return machedAll;
    }

    /**
     * 创建绝对相等表达式
     * @param mProperty
     * @param value
     * @return
     */
    public MExpressionEntity createqExpress(MirrorPropertyEntity mProperty, String value){
        if(mProperty!=null) {
            mProperty.setFieldValue(value);
        }
        //匹配任一
        MExpressionEntity machedOne = new MExpressionEntity(false,true);

        MExpressionEntity express = new MExpressionEntity(mProperty,MRelationEnum.EQ,value);
        machedOne.addExpressions(express);

        return machedOne;
    }

    /**
     * 创建绝对相等表达式
     * @param mProperty
     * @param mFunction
     * @param value
     * @return
     */
    public MExpressionEntity createqExpress(MirrorPropertyEntity mProperty, MFunction mFunction, String value){
        if(mProperty!=null) {
            mProperty.setFieldValue(value);
        }
        //匹配任一
        MExpressionEntity machedOne = new MExpressionEntity(false,true);

        MExpressionEntity express = new MExpressionEntity(mProperty,MRelationEnum.EQ,value);
        express.setFunction(mFunction);
        machedOne.addExpressions(express);

        return machedOne;
    }
    /**
     * 创建范围类的表达式
     * @param mProperty
     * @param start
     * @param end
     * @return
     */
    public MExpressionEntity createBetweenExpress(MirrorPropertyEntity mProperty, String start, String end){

        MExpressionEntity machedAll = new MExpressionEntity(true,true);

        //匹配
        if(StringUtils.isNotBlank(start)){
            MExpressionEntity expressStart = new MExpressionEntity(mProperty,MRelationEnum.GE,start);
            machedAll.addExpressions(expressStart);
        }
        if(StringUtils.isNotBlank(end)){
            MExpressionEntity expressEnd = new MExpressionEntity(mProperty,MRelationEnum.LE,end);

            machedAll.addExpressions(expressEnd);
        }

        return machedAll;
    }

    /**
     * 匹配全部
     * @param list
     * @return
     */
    public MExpressionEntity createMatchAllExpress(List<MExpressionEntity> list){

        MExpressionEntity machedAll = new MExpressionEntity(true,true);

        //匹配全部
        for(MExpressionEntity express:list){
            machedAll.addExpressions(express);
        }

        return machedAll;
    }
    /**
     * 创建关系表达式
     * @param mPropertyList
     * @return
     */
    public MExpressionEntity createExpress(List<MirrorPropertyEntity> mPropertyList){
        //根节点 匹配全部
        MExpressionEntity machedAll = new MExpressionEntity(true,true);

        for(MirrorPropertyEntity propertyEntity:mPropertyList){
            //二级节点 匹配任一
            MExpressionEntity machedOne = new MExpressionEntity(propertyEntity,true,true);
            //三级节点 匹配
            String value = String.valueOf(propertyEntity.getFieldValue());
            MExpressionEntity express = new MExpressionEntity(propertyEntity,MRelationEnum.EQ,value);
            machedOne.addExpressions(express);
            machedAll.addExpressions(machedOne);
        }

        return machedAll;
    }
    /**
     * 在保存之前，设置条件组
     * @param expressGroupEntity
     * @return
     */
    public MExpressGroupEntity initExpressBeforeSave(MExpressGroupEntity expressGroupEntity){

        if(expressGroupEntity!=null){
            List<CloneFilter> filterList = new ArrayList<>();
            filterList.add(new CloneFilter(MExpressGroupEntity.class,"id"));
            filterList.add(new CloneFilter(MExpressionEntity.class,"id"));
            filterList.add(new CloneFilter(MObjectEntity.class,"templates"));
            try {
                expressGroupEntity = (MExpressGroupEntity) HRMSBeanClone.deepClone(expressGroupEntity, filterList);

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            MExpressionEntity expressionEntity = expressGroupEntity.getMatched();
            setParentExpression(expressionEntity,expressionEntity.getExpressions());
        }
        return expressGroupEntity;
    }

    private void setParentExpression(MExpressionEntity expressionEntity, List<MExpressionEntity> list){

        if(list!= null && list.size()>0){
            for(MExpressionEntity child:list){
                child.setParentExpression(expressionEntity);
                setParentExpression(child,child.getExpressions());
            }
        }
    }


    /**
     * 根据条件组,和标题,设置表头发排序方式
     * @return
     */
    public void setTableHeaderSortType(MExpressGroupEntity express, TableHeaderCellVO col){

        if(StringUtils.isBlank(col.getField()))return;
        if(express.getOrderByList().size()==0) return;

        List<MOrderBy> orderByList = express.getOrderByList();
        for(MOrderBy orderBy: orderByList){
            if(orderBy.getProperty()!=null &&StringUtils.equals(orderBy.getProperty().getPropertyPath(),col.getField())){
                col.setSortType(orderBy.getOrderByEnum().name());
                return;
            }
        }
    }
}
