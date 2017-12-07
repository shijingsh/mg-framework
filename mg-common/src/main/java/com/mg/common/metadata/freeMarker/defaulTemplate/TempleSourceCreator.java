package com.mg.common.metadata.freeMarker.defaulTemplate;

import com.mg.common.metadata.service.MetaDataQueryService;
import com.mg.common.metadata.util.MPropertyFilter;
import com.mg.framework.entity.metadata.MObjectEntity;
import com.mg.framework.entity.metadata.MirrorPropertyEntity;
import com.mg.framework.entity.metadata.MTemplateTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 生成母模板
 */
@Service
public class TempleSourceCreator {

    @Autowired
    MetaDataQueryService metaDataQueryService;

    /**
     * 创建母模板
     * @param objectEntity
     * @return
     */
    public String create(MObjectEntity objectEntity, MTemplateTypeEnum templateTypeEnum){

        if(templateTypeEnum == MTemplateTypeEnum.DataList){
            return createListTemplate();
        }
        List<MirrorPropertyEntity> mPropertyEntityList = metaDataQueryService.findMPropertyByBelongMObject(objectEntity);
        List<MirrorPropertyEntity> showPropertyList =  MPropertyFilter.showListProperties(mPropertyEntityList, -1, true);
        StringBuilder sb = new StringBuilder();
        //模板的容器
        sb.append("<div id='template-container'>");
        //模板
        sb.append("<div class=\"layout-container layout-form\">");
        //表头
        sb.append("\n<p class=\"set-title\">" )
                .append(objectEntity.getName()+"信息")
                .append("</p>");
        //表格
        sb.append("\n<table class=\"template_table\">");
        sb.append("\n<tr>");
        int index = 0;
        for(MirrorPropertyEntity mPropertyEntity:showPropertyList){
            if(index!=0&& index % 2 ==0){
                sb.append("</tr>\n");
                sb.append("<tr>");
            }
            sb.append(createTD(mPropertyEntity));

            index ++;
        }
        sb.append("\n</tr>");
        sb.append("\n</table>");
        sb.append("\n</div>");
        //按钮部分
        sb.append("\n<div class=\"cm-btn-group-out\">");
        if(templateTypeEnum == MTemplateTypeEnum.DataEntry){
            sb.append("\n<button class=\"cm-btn\" ng-click=\"save()\">保存</button>");
        }else if(templateTypeEnum == MTemplateTypeEnum.DataView){
            sb.append("\n<button class=\"cm-btn\" ng-if=\"object.status\" ng-click=\"delete()\">删除</button>");
        }
        sb.append("\n<button class=\"cm-btn\" ng-click=\"cancel()\">返回</button>");
        sb.append("\n</div>");
        sb.append("\n</div>");
        return sb.toString();
    }

    public String createTD(MirrorPropertyEntity mPropertyEntity){
        StringBuilder sb = new StringBuilder();
        //mc = metadata container
        sb.append("<td><mc></mc></td>");
        return sb.toString();
    }

    public String createListTemplate(){
        String template = "<div>\n" +
                "    <cmmuti:table apiSetter=\"mutiTabs.apiSetter\" options=\"mutiTabs.options\"></cmmuti:table>\n" +
                "\n" +
                "    <div class=\"tab_next\">\n" +
                "\n" +
                "        <div class=\"tab_container tab_container_oper\">\n" +
                "            <div>\n" +
                "                <table class=\"search_table\" id=\"search_table\">\n" +
                "                    <thead>\n" +
                "                    <tr>\n" +
                "                        <th><label for=\"myFavoritesList\">我的收藏：</label></th>\n" +
                "                        <td>\n" +
                "                            <select  class=\"select\"  id=\"myFavoritesList\"\n" +
                "                                     ng-model=\"favoritesId\"\n" +
                "                                     ng-change=\"favoritesChange()\"\n" +
                "                                    ng-options=\"opt.id as opt.favoritesName for opt in myFavoritesList\">\n" +
                "                            </select>\n" +
                "                            <a class=\"a_search\" ng-click=\"showAdvancedSearch()\">高级搜索</a>\n" +
                "                            <a class=\"a_search\" ng-click=\"popAddFavorites()\" ng-if=\"!favoritesId\">收藏查询</a>\n" +
                "                            <a class=\"a_search\" ng-click=\"saveFavorites()\" ng-if=\"favoritesId\">更改收藏</a>\n" +
                "                            <button class=\"cm-btn R\" ng-click=\"popShowColumns()\">设置显示列</button>\n" +
                "                        </td>\n" +
                "                    </tr>\n" +
                "                    </thead>\n" +
                "                </table>\n" +
                "                <div  ng-show=\"showAdvanced\">\n" +
                "                    <express  autoInit=\"false\" lazycallback=\"init\" apiSetter=\"express.apiSetter\" options=\"express.options\" width=\"860px\">\n" +
                "                    </express>\n" +
                "                </div>\n" +
                "            </div>\n" +
                "            <div class=\"height10px\" ng-if=\"showAdvanced\"></div>\n" +
                "            <div ng-show=\"showAdvanced\" style=\"text-align: center\">\n" +
                "                <a ng-click=\"doQuerySubmit()\" class=\"cm-btn\">搜索</a>\n" +
                "                <a ng-click=\"addConditionWindow()\" class=\"cm-btn\">添加条件</a>\n" +
                "            </div>\n" +
                "            <div class=\"height10px\"></div>\n" +
                "            <cmpageable:table id=\"itemListTable\"\n" +
                "                              apiSetter=\"itemListTable.apiSetter\"\n" +
                "                              options=\"itemListTable.options\"\n" +
                "                              sortable = \"true\"\n" +
                "                              customCellBuilder=\"itemListTable.customCellBuilder\"\n" +
                "                              listeners=\"itemListTable.listeners\"\n" +
                "                              width=\"858px\">\n" +
                "            </cmpageable:table>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</div>";

        return template;
    }

}
