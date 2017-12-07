package com.mg.common.metadata.freeMarker.directive;

import com.mg.common.metadata.service.MetaDataQueryService;
import com.mg.framework.log.ContextLookup;
import com.mg.framework.entity.metadata.MirrorPropertyEntity;
import com.mg.framework.entity.metadata.MTemplateTypeEnum;
import freemarker.core.Environment;
import freemarker.template.*;

import java.io.IOException;
import java.util.Map;

/**
 * 图片类型的元数据
 * Created by liukefu on 2015/9/28.
 */
public class ImagesDirective extends BaseDirective {

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
        SimpleScalar templateTypeScalar = (SimpleScalar)params.get("templateType");
        String property = propertyScalar.toString();
        String templateType = MTemplateTypeEnum.DataEntry.toString();
        if(templateTypeScalar!=null){
            templateType = templateTypeScalar.getAsString();
        }
        //得到元数据
        MetaDataQueryService metaDataQueryService = ContextLookup.getBean(MetaDataQueryService.class);
        MirrorPropertyEntity mirrorProperty = metaDataQueryService.findMPropertyById(property);
        StringBuilder sb = new StringBuilder();
        //标题
        sb.append("<span class=\"template_content\" >");
        sb.append(createLabel(mirrorProperty));
        sb.append(createViewField(mirrorProperty));
        //sb.append("     <div id=\"").append(mirrorProperty.getPropertyPath()).append("\" class='icon_edit'   title=\"编辑\"></div>");
        sb.append("</span>");
        env.getOut().write(sb.toString());
    }

    public String createViewField(MirrorPropertyEntity mirrorProperty){
        StringBuilder sb = new StringBuilder();
        sb.append("<a class=\"icon_attachment\" upload-pop=\"\"  ");
        sb.append(" propertyId=\"").append(mirrorProperty.getId()).append("\"");
        sb.append(" apisetter=\"uploadImagesPop.apiSetter\" callback=\"imageUploadCallback\" ");
        sb.append(" ng-click=\"uploadImagesPop.api.popSelectWindow('").append(mirrorProperty.getId()).append("','").append(mirrorProperty.getPropertyPath()).append("')\" ");

        sb.append(" />");

        sb.append("<span ng-bind-html=\"object.").append(mirrorProperty.getPropertyPath()).append(" | imagesFilter | trustHtml\"> </span>");
        return sb.toString();
    }

}
