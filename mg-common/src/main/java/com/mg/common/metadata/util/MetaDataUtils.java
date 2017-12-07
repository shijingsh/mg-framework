package com.mg.common.metadata.util;

import com.mg.framework.entity.metadata.*;
import com.mg.framework.utils.UserHolder;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.Map;

/**
 * Created by liukefu on 2015/9/14.
 */
public class MetaDataUtils {
    //元数据本身的名称
    public static final String DEFAULT_META_TABLE_NAME = "sys_meta_object";
    public static final String DEFAULT_META_PROPERTY_TABLE_NAME = "sys_meta_property";

    public static final String DEFAULT_MODULE_NAME = "hr";
    //元数据默认的字段
    public static final String META_FIELD_CREATED_ID = "created_by_id";
    public static final String META_FIELD_CREATED_NAME = "created_name";
    public static final String META_FIELD_CREATED_DATE = "created_date";
    public static final String META_FIELD_UPDATED_ID = "updated_by_id";
    public static final String META_FIELD_UPDATED_NAME = "updated_name";
    public static final String META_FIELD_UPDATED_DATE = "updated_date";
    public static final String META_FIELD_ID = "id";
    public static final String META_FIELD_NAME = "name";
    public static final String META_FIELD_STATUS = "status";
    public static final String META_FIELD_KEY = "key";
    //元数据脚本运行时的，当前对象数据
    public static final String META_CURR_OBJECT = "param";
    //历史记录里的开始时间和结束时间
    public static final String META_HISTORY_ID = "history_id";
    public static final String META_HISTORY_START_DATE = "start_date";
    public static final String META_HISTORY_END_DATE = "end_date";
    //表示是上一条记录的值
    public static final String META_HISTORY_PRE = "pre_";

    public static final String META_EMP_MODULE_NAME = "hr";


    public static final String SQL_COMMA = ",";
    public static final String SQL_COLON = ":";
    public static final String SQL_EQ = "=";
    public static final String SQL_UNDERLINE = "___";
    public static final String SQL_POINT = ".";
    public static final String SQL_OR = " or ";
    public static final String SQL_AND = " and ";
    public static final String SQL_ON = " on ";
    public static final String SQL_EMPTY = "  ";

    /**
     * 元数据创建表结构全局开关
     */
    public static Boolean META_GENGERATE_DDL = true;
    /**
     * 获取对象类型的元数据，显示名称
     * @param mPropertyEntity
     * @return
     */
    public static String getObjectFieldValue(MirrorPropertyEntity mPropertyEntity){

        StringBuilder sb = new StringBuilder();
        sb.append(mPropertyEntity.getPropertyPath()).append(SQL_UNDERLINE).append("name");
        return sb.toString();
    }
    /**
     * 字段名是否是系统预置的固定字段
     * @param fieldName
     * @return
     */
    public static boolean isSystemFields(String fieldName){

        if(StringUtils.equals(META_FIELD_ID,fieldName) ||
                StringUtils.equals(META_FIELD_CREATED_ID,fieldName) ||
                StringUtils.equals(META_FIELD_CREATED_NAME,fieldName) ||
                StringUtils.equals(META_FIELD_CREATED_DATE,fieldName) ||
                StringUtils.equals(META_FIELD_UPDATED_ID,fieldName) ||
                StringUtils.equals(META_FIELD_UPDATED_NAME,fieldName) ||
                StringUtils.equals(META_FIELD_UPDATED_DATE,fieldName)
                ){
            return true;
        }
        return false;
    }

    /**
     * 字段名是否是系统预置的固定字段
     * @param fieldName
     * @return
     */
    public static boolean isLogFields(String fieldName){

        if(     StringUtils.equals(META_FIELD_CREATED_ID,fieldName) ||
                StringUtils.equals(META_FIELD_CREATED_NAME,fieldName) ||
                StringUtils.equals(META_FIELD_CREATED_DATE,fieldName) ||
                StringUtils.equals(META_FIELD_UPDATED_ID,fieldName) ||
                StringUtils.equals(META_FIELD_UPDATED_NAME,fieldName) ||
                StringUtils.equals(META_FIELD_UPDATED_DATE,fieldName)
                ){
            return true;
        }
        return false;
    }
    /**
     * 字段名是否是系统预置的固定字段
     * @param fieldName
     * @return
     */
    public static boolean isSystemUpdateFields(String fieldName){

        if(
                StringUtils.equals(META_FIELD_UPDATED_ID,fieldName) ||
                StringUtils.equals(META_FIELD_UPDATED_NAME,fieldName) ||
                StringUtils.equals(META_FIELD_UPDATED_DATE,fieldName)
                ){
            return true;
        }
        return false;
    }
    /**
     * 元数据字段是否是对象类型
     * @param mPropertyEntity
     * @return
     */
    public static  boolean isObjectField(MPropertyEntity mPropertyEntity){
        if(mPropertyEntity.getTypeEnum() == MTypeEnum.mObject){
            return true;
        }
        return false;
    }

    /**
     * 元数据字段是否是对象类型
     * @param mPropertyEntity
     * @return
     */
    public static  boolean isObjectField(MirrorPropertyEntity mPropertyEntity){
        if(mPropertyEntity.getControllerType() == MControllerTypeEnum.object){
            return true;
        }
        return false;
    }

    /**
     * 是否为元数据自己的元数据
     * @param mObjectEntity
     * @return
     */
    public static boolean isSelf(MObjectEntity mObjectEntity){

        if( StringUtils.equals(DEFAULT_META_TABLE_NAME, mObjectEntity.getTableName()) ||
                StringUtils.equals(DEFAULT_META_PROPERTY_TABLE_NAME,mObjectEntity.getTableName())
                ){
            return true;
        }
        return false;
    }
    /**
     * 元数据字段是否是对象类型
     * @param mPropertyEntity
     * @return
     */
    public static  String getNamePropertyPath(MirrorPropertyEntity mPropertyEntity){
        String path = mPropertyEntity.getPropertyPath();
        if (mPropertyEntity.getControllerType() == MControllerTypeEnum.object) {
            path += MetaDataUtils.SQL_UNDERLINE + MetaDataUtils.META_FIELD_NAME;
        }
        return  path;
    }

    /**
     * 元数据字段是否是对象类型
     * @param mPropertyEntity
     * @return
     */
    public static  String getName(MirrorPropertyEntity mPropertyEntity, Map<String,Object> objectMap){
        if(objectMap == null)return null;
        String path = getNamePropertyPath(mPropertyEntity);

        return  (String)objectMap.get(path);
    }

    public static void fillSystemFields(MirrorPropertyEntity mPropertyEntity){

        switch (mPropertyEntity.getFieldName()){
            case META_FIELD_CREATED_ID:
                mPropertyEntity.setFieldValue(UserHolder.getLoginUserId());
                break;
            case META_FIELD_CREATED_NAME:
                mPropertyEntity.setFieldValue(UserHolder.getLoginUserName());
                break;
            case META_FIELD_UPDATED_ID:
                mPropertyEntity.setFieldValue(UserHolder.getLoginUserId());
                break;
            case META_FIELD_UPDATED_NAME:
                mPropertyEntity.setFieldValue(UserHolder.getLoginUserName());
                break;
            case META_FIELD_CREATED_DATE:
            case META_FIELD_UPDATED_DATE:
                mPropertyEntity.setFieldValue(new Date());
                break;
        }
    }

    public static void fillUpdateSystemFields(MirrorPropertyEntity mPropertyEntity){
        switch (mPropertyEntity.getFieldName()){
            case META_FIELD_UPDATED_ID:
                mPropertyEntity.setFieldValue(UserHolder.getLoginUserId());
                break;
            case META_FIELD_UPDATED_NAME:
                mPropertyEntity.setFieldValue(UserHolder.getLoginUserName());
                break;
            case META_FIELD_UPDATED_DATE:
                mPropertyEntity.setFieldValue(new Date());
                break;
        }
    }

    /**
     * 获取对象的唯一标识
     * @param mObjectEntity
     * @return
     */
    public static String getIdentifier(MObjectEntity mObjectEntity){
        if(StringUtils.isNotBlank(mObjectEntity.getIdentifier())){
            return mObjectEntity.getIdentifier();
        }
        return META_FIELD_NAME;
    }

    /**
     * 获取字段名称
     * @param propertyEntity
     * @return
     */
    public static String getFieldName(MirrorPropertyEntity propertyEntity){
        String name = propertyEntity.getMetaProperty().getFieldName();
        name = "`" + name + "`";
        return name;
    }
    /**
     * 获取字段名称
     * @param propertyEntity
     * @return
     */
    public static String getFieldName(MPropertyEntity propertyEntity) {
        String name = propertyEntity.getFieldName();
        name = "`" + name + "`";
        return name;
    }
}
