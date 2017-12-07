package com.mg.framework.entity.metadata;

import com.mg.framework.model.BaseInnerEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 元数据 关系类型枚举
 * @author liukefu
 */
public class MRelationEnum extends BaseInnerEnum {

    private static Logger logger = LoggerFactory.getLogger(MRelationEnum.class);
    public static final MRelationEnum instance = new MRelationEnum();

    public static final int GT = 1; //:'大于',
    public static final int GE = 2; //2:'大于等于',
    public static final int EQ = 3; //3:'等于',
    public static final int LE = 4; //:'小于等于',
    public static final int LT = 5; //:'小于',
    public static final int NE = 6; //:'不等于',

    public static final int LIKE = 10;       //:'包含',
    public static final int NOT_LIKE = 11 ;  //:'不包含',

    public static final int NULL = 20;      //为空
    public static final int NOT_NULL = 21;  //不为空

    private MRelationEnum() {
        ENUM(GT, "大于");
        ENUM(GE, "大于等于");
        ENUM(EQ, "等于");
        ENUM(LE, "小于等于");
        ENUM(LT, "小于");
        ENUM(NE, "不等于");

        ENUM(LIKE, "包含");
        ENUM(NOT_LIKE, "不包含");

        ENUM(NULL, "为空");
        ENUM(NOT_NULL, "不为空");
    }
}
