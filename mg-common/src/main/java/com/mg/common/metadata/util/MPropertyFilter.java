package com.mg.common.metadata.util;

import com.mg.framework.entity.metadata.MirrorPropertyEntity;
import com.mg.framework.entity.metadata.MControllerTypeEnum;
import com.mg.framework.entity.metadata.MInVisibleTypeEnum;
import com.mg.framework.entity.metadata.MTemplateTypeEnum;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by liukefu on 2016/4/13.
 */
public class MPropertyFilter {
    /**
     * 用户能操作的属性
     * 即：隐藏了create_id,create_date,update_id,update_date 等属性
     * 和结构化字段
     * @param list
     * @return
     */
    public static List<MirrorPropertyEntity> showListProperties(List<MirrorPropertyEntity> list, boolean needSort) {

        List<MirrorPropertyEntity> rtList = new ArrayList<>();

        for (MirrorPropertyEntity mirrorPropertyEntity : list) {
            if (!MetaDataUtils.isLogFields(mirrorPropertyEntity.getFieldName())
                    && mirrorPropertyEntity.getInVisibleType() != MInVisibleTypeEnum.invisibleAll
                    && mirrorPropertyEntity.getControllerType()!= MControllerTypeEnum.subType
                   ) {
                rtList.add(mirrorPropertyEntity);
            }
        }
        if (needSort) {
            Collections.sort(rtList, new MirrorPropertyObjectComparator());
        }
        return rtList;
    }
    /**
     * 能显示在表单上的属性列表
     * 即：隐藏了id,create_id,create_date,update_id,update_date 等属性
     *
     * @param list
     * @return
     */
    public static List<MirrorPropertyEntity> showListProperties(List<MirrorPropertyEntity> list, int maxLength, boolean needSort) {

        List<MirrorPropertyEntity> rtList = new ArrayList<>();
        if (maxLength <= 0) {
            maxLength = 10000;
        }
        int length = 0;
        for (MirrorPropertyEntity mirrorPropertyEntity : list) {
            if (!MetaDataUtils.isSystemFields(mirrorPropertyEntity.getFieldName())
                    && mirrorPropertyEntity.getInVisibleType() != MInVisibleTypeEnum.invisibleAll
                    && length < maxLength) {
                length++;
                rtList.add(mirrorPropertyEntity);
            }
        }
        if (needSort) {
            Collections.sort(rtList, new MirrorPropertyComparator());
        }
        return rtList;
    }

    /**
     * 能显示在表单上的属性列表
     * 即：隐藏了id,create_id,create_date,update_id,update_date 等属性
     *
     * @param list
     * @return
     */
    public static List<MirrorPropertyEntity> showListProperties(List<MirrorPropertyEntity> list,
                                                                MTemplateTypeEnum templateTypeEnum,
                                                                int maxLength, boolean needSort) {

        List<MirrorPropertyEntity> rtList = new ArrayList<>();
        if (maxLength <= 0) {
            maxLength = 10000;
        }
        int length = 0;
        for (MirrorPropertyEntity mirrorPropertyEntity : list) {
            if (!MetaDataUtils.isSystemFields(mirrorPropertyEntity.getFieldName())
                    && mirrorPropertyEntity.getInVisibleType() != MInVisibleTypeEnum.invisibleAll
                    && length < maxLength) {
                if (templateTypeEnum == MTemplateTypeEnum.DataEntry) {
                    if (mirrorPropertyEntity.getInVisibleType() != MInVisibleTypeEnum.invisibleDataEntry
                            && mirrorPropertyEntity.getInVisibleType() != MInVisibleTypeEnum.invisibleDataEntryList) {
                        length++;
                        rtList.add(mirrorPropertyEntity);
                    }
                } else if (templateTypeEnum == MTemplateTypeEnum.DataList) {
                    if (mirrorPropertyEntity.getInVisibleType() != MInVisibleTypeEnum.invisibleDataEntryList
                            && mirrorPropertyEntity.getInVisibleType() != MInVisibleTypeEnum.invisibleDataList) {
                        length++;
                        rtList.add(mirrorPropertyEntity);
                    }
                } else {
                    if (mirrorPropertyEntity.getInVisibleType() != MInVisibleTypeEnum.invisibleDataView) {
                        length++;
                        rtList.add(mirrorPropertyEntity);
                    }
                }

            }
        }

        if (needSort) {
            Collections.sort(rtList, new MirrorPropertyComparator());
        }
        return rtList;
    }

    /**
     * 可见的属性列表
     * 即：隐藏了create_id,create_date,update_id,update_date 等属性
     *
     * @param list
     * @return
     */
    public static List<MirrorPropertyEntity> showVisibleProperties(List<MirrorPropertyEntity> list,
                                                                   MTemplateTypeEnum templateTypeEnum,
                                                                   int maxLength, boolean needSort) {

        List<MirrorPropertyEntity> rtList = new ArrayList<>();
        if (maxLength <= 0) {
            maxLength = 10000;
        }
        int length = 0;
        for (MirrorPropertyEntity mirrorPropertyEntity : list) {
            if (!MetaDataUtils.isLogFields(mirrorPropertyEntity.getFieldName())
                    && mirrorPropertyEntity.getInVisibleType() != MInVisibleTypeEnum.invisibleAll
                    && length < maxLength) {
                if (templateTypeEnum == MTemplateTypeEnum.DataEntry) {
                    if (mirrorPropertyEntity.getInVisibleType() != MInVisibleTypeEnum.invisibleDataEntry
                            && mirrorPropertyEntity.getInVisibleType() != MInVisibleTypeEnum.invisibleDataEntryList) {
                        length++;
                        rtList.add(mirrorPropertyEntity);
                    }
                } else if (templateTypeEnum == MTemplateTypeEnum.DataList) {
                    if (mirrorPropertyEntity.getInVisibleType() != MInVisibleTypeEnum.invisibleDataEntryList
                            && mirrorPropertyEntity.getInVisibleType() != MInVisibleTypeEnum.invisibleDataList) {
                        length++;
                        rtList.add(mirrorPropertyEntity);
                    }
                } else {
                    if (mirrorPropertyEntity.getInVisibleType() != MInVisibleTypeEnum.invisibleDataView) {
                        length++;
                        rtList.add(mirrorPropertyEntity);
                    }
                }
            }
        }

        if (needSort) {
            Collections.sort(rtList, new MirrorPropertyComparator());
        }
        return rtList;
    }
}
