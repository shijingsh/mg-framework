package com.mg.common.metadata.freeMarker.directive;

import com.mg.common.entity.vo.PermissionActionEnum;
import com.mg.common.metadata.service.MetaDataQueryService;
import com.mg.framework.entity.metadata.MirrorPropertyEntity;
import com.mg.framework.log.ContextLookup;
import com.mg.framework.entity.metadata.MTemplateTypeEnum;
import freemarker.core.Environment;
import freemarker.template.*;

import java.io.IOException;
import java.util.Map;

/**
 * 图片类型的元数据
 * Created by liukefu on 2015/9/28.
 */
public class HeadPortraitDirective extends BaseDirective {

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
        sb.append("<span class=\"template_content\" can_edit>");
        sb.append(createViewField(mirrorProperty));
        if(isPermitted(mirrorProperty, PermissionActionEnum.action_update,super.getObjectId(env))) {
            sb.append("     <div id=\"").append(mirrorProperty.getPropertyPath()).append("\" class='icon_edit'   title=\"编辑\"></div>");
        }
        sb.append("</span>");
        env.getOut().write(sb.toString());
    }

    public String createViewField(MirrorPropertyEntity mirrorProperty){
        StringBuilder sb = new StringBuilder();
        if(mirrorProperty!= null){
            sb.append("<head:portrait ");
            sb.append(" propertyId=\"").append(mirrorProperty.getId()).append("\"");
            sb.append(" uploadBtnId=\"").append(mirrorProperty.getPropertyPath()).append("\"");//{{object.").append(mPropertyEntity.getPropertyPath()).append("}}
            sb.append(" imageUrl=\"{{object.").append(mirrorProperty.getPropertyPath()).append("}}\"");
            sb.append(" callback=\"uploadImage\"");
            sb.append(" />");
        }

        return sb.toString();
    }

}
