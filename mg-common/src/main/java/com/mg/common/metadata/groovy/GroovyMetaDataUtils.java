package com.mg.common.metadata.groovy;

import com.mg.common.entity.UserEntity;
import com.mg.common.metadata.service.MHistoryService;
import com.mg.common.metadata.service.MTableGeneratorService;
import com.mg.common.metadata.service.MetaDataService;
import com.mg.common.user.service.UserService;
import com.mg.framework.log.ContextLookup;
import com.mg.groovy.lib.domain.interfaces.GBaseFunction;
import com.mg.groovy.lib.domain.interfaces.GFunction;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.HashMap;

/**
 * GroovyMetaDataUtils
 * 元数据任务相关函数
 *
 * @author: liukefu
 * @date: 2015年4月28日 上午11:25:27
 */
public class GroovyMetaDataUtils extends GBaseFunction {
    public String funTypeName = "元数据函数";

    /**
     * 在值域param中获取指定名称的元数据
     *
     * @param mObjectName
     * @param propertyName
     * @param param
     * @return
     */
    @GFunction
    public Object 获取元数据(String mObjectName, String propertyName, HashMap<String, Object> param) {
        /**
         * param 中 传当前对象的一条记录
         */
        MetaDataService metaDataService = ContextLookup.getBean(MetaDataService.class);

        return metaDataService.queryMetaData(mObjectName, propertyName, param);
    }

    @GFunction
    public int 更新元数据(String mObjectName, String propertyName, Object value, HashMap<String, Object> param) {
        /**
         * param 中 传当前对象的一条记录
         */
        MetaDataService metaDataService = ContextLookup.getBean(MetaDataService.class);
        return metaDataService.updateMetaData(mObjectName, propertyName, value, param);
    }

    @GFunction
    public int 生成历史记录(String mObjectName, String propertyName, String historyPropertyName, HashMap<String, Object> param) {
        /**
         * param 中 传当前对象的一条记录
         */
        MHistoryService mHistoryService = ContextLookup.getBean(MHistoryService.class);
        mHistoryService.createHistory(mObjectName, propertyName, historyPropertyName, param);
        return 0;
    }

    @GFunction
    public int 生成历史记录(String mObjectName, String propertyName, String historyPropertyName, String startDatePropertyName, HashMap<String, Object> param) {
        /**
         * param 中 传当前对象的一条记录
         */
        MHistoryService mHistoryService = ContextLookup.getBean(MHistoryService.class);
        mHistoryService.createHistory(mObjectName, propertyName, historyPropertyName,startDatePropertyName, param);
        return 0;
    }
    @GFunction
    public int 生成头像(String userName, Object value) {
        /**
         * param 中 传当前对象的一条记录
         */
        UserService userService = ContextLookup.getBean(UserService.class);
        UserEntity userEntity = userService.getUser(userName);
        if (userEntity != null && value != null) {
            userEntity.setHeadPortrait(String.valueOf(value));
            userService.updateUser(userEntity);
        }

        return 0;
    }

    @GFunction
    public String 自动编号(String prefix,String tableName) {
        MTableGeneratorService mTableGeneratorService = ContextLookup.getBean(MTableGeneratorService.class);
        String number = ""+ mTableGeneratorService.generate(tableName);
        if(StringUtils.isNotBlank(prefix)){
            number = prefix + number;
        }

        return number;
    }

    @GFunction
    public String 自动编号(String prefix,String tableName,BigDecimal initialValue) {
        MTableGeneratorService mTableGeneratorService = ContextLookup.getBean(MTableGeneratorService.class);
        String number = ""+ mTableGeneratorService.generate(tableName,initialValue.intValue());
        if(StringUtils.isNotBlank(prefix)){
            number = prefix + number;
        }

        return number;
    }
    public String getFunTypeName() {
        return funTypeName;
    }

    public void setFunTypeName(String funTypeName) {
        this.funTypeName = funTypeName;
    }
}
