package com.mg.common.metadata.freeMarker.directive;

import com.mg.common.metadata.service.MetaDataQueryService;
import com.mg.common.metadata.util.MPropertyFilter;
import com.mg.common.metadata.util.MetaDataUtils;
import com.mg.framework.entity.metadata.MirrorPropertyEntity;
import com.mg.framework.log.ContextLookup;
import com.mg.framework.entity.metadata.MObjectEntity;
import freemarker.core.Environment;
import freemarker.template.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 结构化字段
 * Created by liukefu on 2015/9/28.
 */
public class ListViewElementDirective extends BaseDirective {

    @Override
    public void execute(Environment env,
                        Map params, TemplateModel[] loopVars,
                        TemplateDirectiveBody body)
            throws TemplateException, IOException {
        // Check if no parameters were given:
        if (params.isEmpty()) {
            throw new TemplateModelException("This directive need 'property' parameters.");
        }
        SimpleNumber propertyScalar = (SimpleNumber)params.get("property");
        String property = propertyScalar.toString();

        //得到元数据
        MetaDataQueryService metaDataQueryService = ContextLookup.getBean(MetaDataQueryService.class);
        MirrorPropertyEntity mirrorProperty = metaDataQueryService.findMPropertyById(property);
        StringBuilder sb = new StringBuilder();
        //标题
        sb.append(createListLabel(mirrorProperty));

        sb.append(createTable(mirrorProperty));

        env.getOut().write(sb.toString());
    }

    private String createListLabel(MirrorPropertyEntity mirrorProperty){
        StringBuilder sb = new StringBuilder();
        sb.append("<h3 class=\"template_table_title\">");
        sb.append("<span class=\"left\">").append(mirrorProperty.getName()).append("</span>");
        sb.append("<a class=\"icon_edit\" title=\"编辑\" ng-hide=\"editStatus.")
                .append(mirrorProperty.getPropertyPath())
                .append("\" ng-click=\"editStructData('")
                .append(mirrorProperty.getPropertyPath()).append("')\"></a>");
        if(mirrorProperty.getMetaProperty().getMetaObject()!=null
                && mirrorProperty.getMetaProperty().getMetaObject().getIsManage()){
            sb.append("<a class=\"icon_select_all\" title=\"查看全部\" ")
                    .append("\" ng-click=\"listStructData('")
                    .append(mirrorProperty.getMetaProperty().getMetaObject().getId()).append("')\"></a>");
        }
        sb.append("<span class=\"icoGroup\" ng-show=\"editStatus.").append(mirrorProperty.getPropertyPath()).append("\">");
        sb.append("<a class=\"icon_save\" title=\"保存\" ng-click=\"saveStructData('").append(mirrorProperty.getPropertyPath()).append("','").append(mirrorProperty.getId()).append("')\"></a>");
        sb.append("<a class=\"icon_del\" title=\"删除\" ng-click=\"delStructData('").append(mirrorProperty.getPropertyPath()).append("','").append(mirrorProperty.getId()).append("')\"></a>");
        sb.append("<a class=\"icon_plus\" title=\"添加\" ng-click=\"addStructData('").append(mirrorProperty.getPropertyPath()).append("')\"></a>");
        sb.append("</span>");
        sb.append("</h3>");

        return sb.toString();
    }

    public String createField(MirrorPropertyEntity mPropertyEntity){
        StringBuilder sb = new StringBuilder();
        sb.append("<div>");
        switch (mPropertyEntity.getControllerType()){
            case text:
                sb.append("<input type=\"text\"").append("class=\"input_e \"")
                        .append(" ng-model=\"subOne.").append(mPropertyEntity.getPropertyPath()).append("\"");
                if(!mPropertyEntity.getIsNullable()){
                    sb.append(" cm-validator=\"required\"");
                }
                sb.append(" property=").append(mPropertyEntity.getId());
                sb.append(" />");
                break;
            case number:
                sb.append("<input type=\"number\"").append(" class=\"input_e \"")
                        .append(" ng-model=\"subOne.").append(mPropertyEntity.getPropertyPath()).append("\"");
                if(!mPropertyEntity.getIsNullable()){
                    sb.append(" cm-validator=\"required\"");
                }
                sb.append(" property=").append(mPropertyEntity.getId());
                sb.append(" />");
                break;
            case bool:
                sb.append("<input type=\"checkbox\"").append(" class=\"checkbox\"")
                        .append(" ng-model=\"subOne.").append(mPropertyEntity.getPropertyPath()).append("\"");
                sb.append(" property=").append(mPropertyEntity.getId());
                sb.append(" />");
                break;
            case date:
                sb.append("<input type=\"text\"").append(" class=\"input_e \"")
                        .append(" ng-model=\"subOne.").append(mPropertyEntity.getPropertyPath()).append("\"");
                if(!mPropertyEntity.getIsNullable()){
                    sb.append(" cm-validator=\"required\" ");
                }
                sb.append(" property=").append(mPropertyEntity.getId());
                sb.append(" cm-datepicker />");
                break;
            case mEnum:
                sb.append("<select ").append(" class=\"select\"")
                        .append(" ng-options=\"opt.key as opt.name for opt in selects.").append(mPropertyEntity.getFieldName()).append("\"")
                        .append(" ng-model=\"subOne.").append(mPropertyEntity.getPropertyPath()).append("\"");
                if(!mPropertyEntity.getIsNullable()){
                    sb.append(" cm-validator=\"selectRequired\"");
                }
                sb.append(" property=").append(mPropertyEntity.getId());
                sb.append(" />");
                break;
            case object:
                //隐藏域
                sb.append("<input type=\"hidden\"");
                sb.append(" ng-model=\"subOne.").append(mPropertyEntity.getPropertyPath()).append("\" />");
                //名称域
                sb.append("<input type=\"text\"").append(" class=\"input_e \"");
                sb.append(" ng-model=\"subOne.").append(MetaDataUtils.getObjectFieldValue(mPropertyEntity)).append("\"");
                sb.append(" select-object apisetter=\"objectSelect.apiSetter\" listeners=\"objectSelect.listeners\" options=\"objectSelect.options\" ");
                sb.append(" ng-click=\"objectSelect.api.popSelectWindow('"+mPropertyEntity.getMetaProperty().getMetaObject().getId()+"','"+mPropertyEntity.getPropertyPath()+"')\" ");
                if(!mPropertyEntity.getIsNullable()){
                    sb.append(" cm-validator=\"required\"");
                }
                sb.append(" property=").append(mPropertyEntity.getId());
                sb.append(" />");
                break;
        }
        sb.append("</div>");
        return sb.toString();
    }

    public String createTable(MirrorPropertyEntity mPropertyEntity){
        StringBuilder sb = new StringBuilder();
        sb.append("<div class=\"struct_box\">");
        sb.append("<table class=\"struct_table\" ng-class=\"{'table th_blue':!editStatus.").append(mPropertyEntity.getPropertyPath()).append("}\">");
        sb.append("<thead class=\"tableShow\">");
        sb.append("<tr>");
        sb.append("<th ng-if=\"editStatus.").append(mPropertyEntity.getPropertyPath()).append("\">");
        sb.append("<input class=\"checkbox\" type=\"checkbox\" ng-click=\"checkAll('").append(mPropertyEntity.getPropertyPath()).append("')\">");
        sb.append("</th>");

        MetaDataQueryService metaDataQueryService = ContextLookup.getBean(MetaDataQueryService.class);
        MObjectEntity objectEntity = mPropertyEntity.getMetaProperty().getMetaObject();
        List<MirrorPropertyEntity> mPropertyEntityList = metaDataQueryService.findMPropertyByBelongMObject(objectEntity);
        List<MirrorPropertyEntity> showPropertyList =  MPropertyFilter.showListProperties(mPropertyEntityList, 10, true);

        for(MirrorPropertyEntity propertyEntity:showPropertyList){
            sb.append("<th>").append(propertyEntity.getName()).append("</th>");
        }
        sb.append("</tr>");
        sb.append("</thead>");
        sb.append("<tbody ng-hide=\"editStatus.").append(mPropertyEntity.getPropertyPath()).append("\">");
        sb.append("<tr class=\"date_lim\" ng-repeat=\"subOne in object.").append(mPropertyEntity.getPropertyPath()).append("\">");
        for(MirrorPropertyEntity propertyEntity:showPropertyList){
            sb.append("<td>").append(viewFieldValue(propertyEntity)).append("</td>");
        }
       sb.append("</tr>");
       sb.append(" </tbody>");
        sb.append("<tbody ng-if=\"editStatus.").append(mPropertyEntity.getPropertyPath()).append("\">");
        sb.append("<tr class=\"date_lim\" ng-repeat=\"subOne in object.").append(mPropertyEntity.getPropertyPath()).append("\">");
        sb.append("<td><input class=\"checkbox\" type=\"checkbox\" ng-model=\"subOne.checkedStatus\"></td>");
        for(MirrorPropertyEntity propertyEntity:showPropertyList){
            sb.append("<td>").append(createField(propertyEntity)).append("</td>");
        }
        sb.append("</tr>");
        sb.append(" </tbody>");
       sb.append("</table>");
       sb.append("</div>");

       return sb.toString();
    }

    public String viewFieldValue(MirrorPropertyEntity mPropertyEntity){
        StringBuilder sb = new StringBuilder();
        switch (mPropertyEntity.getControllerType()){
            case text:
            case number:
            case date:
                sb.append("{{subOne.").append(mPropertyEntity.getPropertyPath()).append("}}");
                break;
            case bool:
                sb.append("{{subOne.").append(mPropertyEntity.getPropertyPath()).append(" | booleanFilter}}");
                break;
            case mEnum:
                sb.append("{{subOne.").append(mPropertyEntity.getPropertyPath()).append(" | selectFilter:selects.").append(mPropertyEntity.getPropertyPath()).append("}}");
                break;
            case object:
                sb.append("{{subOne.").append(MetaDataUtils.getObjectFieldValue(mPropertyEntity)).append("}}");
                break;
        }
        return sb.toString();
    }
}
