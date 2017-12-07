package com.mg.common.metadata.service;

import com.mg.framework.entity.metadata.*;
import com.mg.framework.entity.vo.TableHeaderCellVO;

import java.util.List;

/**
 * Created by liukefu on 2015/8/25.
 */
public interface MetaDataExpressService {
    /**
     * 创建一个空的表达式
     * @return
     */
    public MExpressGroupEntity createBlankExpressGroup(MExpressGroupEntity express);
    /**
     * 创建一个默认的表达式
     * @param metaObject
     * @param maxLength
     * @return
     */
    public MExpressGroupEntity createDefaultExpressGroup(MObjectEntity metaObject, Integer maxLength);
    /**
     * 创建绝对相等表达式
     * 三层表达式
     * @param mProperty
     * @param value
     * @return
     */
    public MExpressionEntity createSimpleEqExpress(MirrorPropertyEntity mProperty, String value);

    /**
     * 创建绝对相等表达式
     * 二层表达式
     * @param mProperty
     * @param value
     * @return
     */
    public MExpressionEntity createqExpress(MirrorPropertyEntity mProperty, String value);

    /**
     * 创建绝对相等表达式
     * @param mProperty
     * @param mFunction
     * @param value
     * @return
     */
    public MExpressionEntity createqExpress(MirrorPropertyEntity mProperty, MFunction mFunction, String value);
    /**
     * 创建范围类的表达式
     * @param mProperty
     * @param start
     * @param end
     * @return
     */
    public MExpressionEntity createBetweenExpress(MirrorPropertyEntity mProperty, String start, String end);
    /**
     * 创建关系表达式
     * @param mPropertyList
     * @return
     */
    public MExpressionEntity createExpress(List<MirrorPropertyEntity> mPropertyList);


    /**
     * 匹配全部
     * @param list
     * @return
     */
    public MExpressionEntity createMatchAllExpress(List<MExpressionEntity> list);
    /**
     * 在保存之前，设置条件组
     * @param expressGroupEntity
     * @return
     */
    public MExpressGroupEntity initExpressBeforeSave(MExpressGroupEntity expressGroupEntity);

    /**
     * 根据条件组,和标题,设置表头发排序方式
     * @return
     */
    public void setTableHeaderSortType(MExpressGroupEntity express, TableHeaderCellVO col);
}
