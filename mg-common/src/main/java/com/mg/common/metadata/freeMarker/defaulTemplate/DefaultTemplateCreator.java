package com.mg.common.metadata.freeMarker.defaulTemplate;

import com.mg.common.metadata.service.MTemplateService;
import com.mg.common.metadata.service.MetaDataQueryService;
import com.mg.common.metadata.util.MPropertyFilter;
import com.mg.common.metadata.util.MetaDataUtils;
import com.mg.framework.entity.metadata.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by liukefu on 2015/9/10.
 */
@Service
public class DefaultTemplateCreator {

    public static final String TEMPLATE_CONTAINER = "td";
    @Autowired
    MetaDataQueryService metaDataQueryService;
    @Autowired
    MTemplateService mTemplateService;

    /**
     * 将元数据装入模板
     *
     * @param showPropertyList
     * @param templateTypeEnum
     * @param templateSource
     * @return
     */
    public String fillTemplate(List<MirrorPropertyEntity> showPropertyList,
                               MTemplateTypeEnum templateTypeEnum, String templateSource, AtomicInteger index) {
        if (StringUtils.isBlank(templateSource)) {
            return "";
        }

        StringBuffer sb = new StringBuffer();
        //mc = metadata container
        String regxp = "<mc( .*?)?>(.*?)</mc>";
        Pattern pattern = Pattern.compile(regxp);
        Matcher matcher = pattern.matcher(templateSource);
        boolean result1 = matcher.find();
        while (result1) {
            String marchContent = matcher.group(2);
            if (StringUtils.isBlank(marchContent)) {

            }
            StringBuffer temp = new StringBuffer();
            String nextProperty = nextMProperty(showPropertyList, templateTypeEnum, index.intValue());
            temp.append(nextProperty);

            matcher.appendReplacement(sb, temp.toString());
            result1 = matcher.find();
            index.incrementAndGet();
        }
        matcher.appendTail(sb);

        return sb.toString();
    }

    private List<MirrorPropertyEntity> getTemplateProperties(MObjectEntity objectEntity, MTemplateTypeEnum templateTypeEnum){
        //所有元数据
        List<MirrorPropertyEntity> mPropertyEntityList = metaDataQueryService.findMPropertyByBelongMObject(objectEntity);
        List<MirrorPropertyEntity> showPropertyList = MPropertyFilter.showListProperties(mPropertyEntityList, templateTypeEnum, -1, true);
        List<MirrorPropertyEntity> rtList = new ArrayList<>();
        for(MirrorPropertyEntity propertyEntity:showPropertyList){
            if(!MetaDataUtils.isSystemFields(propertyEntity.getFieldName())){
                rtList.add(propertyEntity);
            }
        }
        return rtList;
    }

    /**
     * 根据元数据对象和母模板，生成模板
     *
     * @param objectEntity
     * @param templateSource
     * @return
     */
    public String createTemple(MObjectEntity objectEntity, String templateSource, MTemplateTypeEnum templateTypeEnum) {
        if(templateTypeEnum == MTemplateTypeEnum.DataList){
            //列表页面直接返回
            return templateSource;
        }
        List<MirrorPropertyEntity> showPropertyList = getTemplateProperties(objectEntity,templateTypeEnum);
        return fillTemplate(showPropertyList, templateTypeEnum, templateSource, new AtomicInteger(0));
    }

    /**
     * 根据元数据对象和母模板，生成模板
     *
     * @param objectEntity
     * @return
     */
    public List<MTemplateEntity> createTemple(MObjectEntity objectEntity, MTemplateTypeEnum templateTypeEnum) {
        List<MirrorPropertyEntity> showPropertyList = getTemplateProperties(objectEntity, templateTypeEnum);
        List<MTemplateEntity> templates = mTemplateService.findByBelongMObjectAndTemplateType(objectEntity, templateTypeEnum);
        AtomicInteger index = new AtomicInteger(0);
        for (MTemplateEntity templateEntity : templates) {
            String templateSource = templateEntity.getTemplateSource();

            String templateStr = fillTemplate(showPropertyList, templateTypeEnum, templateSource, index);

            templateEntity.setTemplate(templateStr);
        }

        return templates;
    }

    /**
     * 获得一个元数据
     *
     * @param showPropertyList
     * @param index
     * @return
     */
    private String nextMProperty(List<MirrorPropertyEntity> showPropertyList, MTemplateTypeEnum templateTypeEnum, int index) {
        StringBuilder sb = new StringBuilder();
        MirrorPropertyEntity propertyEntity = null;
        if (showPropertyList.size() > index) {
            propertyEntity = showPropertyList.get(index);
        }
        if (propertyEntity != null && propertyEntity.getControllerType() == MControllerTypeEnum.subType) {
            if (templateTypeEnum == MTemplateTypeEnum.DataEntry) {
                sb.append("<@list property=").append(propertyEntity.getId()).append(" />");
            } else {
                sb.append("<@listView property=").append(propertyEntity.getId()).append(" />");
            }
        } else if (propertyEntity != null && propertyEntity.getControllerType() == MControllerTypeEnum.headPortrait) {
            sb.append("<@headPortrait property=").append(propertyEntity.getId()).append(" templateType='").append(templateTypeEnum.toString()).append("' />");
        } else if (propertyEntity != null && propertyEntity.getControllerType() == MControllerTypeEnum.image) {
            sb.append("<@images property=").append(propertyEntity.getId()).append(" templateType='").append(templateTypeEnum.toString()).append("' />");
        } else if (propertyEntity != null && propertyEntity.getControllerType() == MControllerTypeEnum.file) {
            sb.append("<@files property=").append(propertyEntity.getId()).append(" templateType='").append(templateTypeEnum.toString()).append("' />");
        } else if (propertyEntity != null ) {
            sb.append("<@element property=").append(propertyEntity.getId()).append(" templateType='").append(templateTypeEnum.toString()).append("' />");
        }
        return sb.toString();
    }

}
