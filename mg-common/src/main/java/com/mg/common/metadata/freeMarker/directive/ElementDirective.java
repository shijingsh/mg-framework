package com.mg.common.metadata.freeMarker.directive;

import com.mg.common.metadata.service.MetaDataQueryService;
import com.mg.framework.entity.metadata.MirrorPropertyEntity;
import com.mg.framework.log.ContextLookup;
import com.mg.framework.entity.metadata.MTemplateTypeEnum;
import freemarker.core.Environment;
import freemarker.template.*;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Map;

/**
 * Created by liukefu on 2015/9/28.
 */
public class ElementDirective extends BaseDirective {

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
        sb.append("<span class=\"template_content\">");
        sb.append(createLabel(mirrorProperty));

        if(StringUtils.equals(templateType, MTemplateTypeEnum.DataEntry.toString())){
            //数据录入模板
            sb.append(createField(mirrorProperty));
        }else{
            sb.append(createViewField(env,mirrorProperty));
        }
        sb.append("</span>");
        env.getOut().write(sb.toString());
    }

}
