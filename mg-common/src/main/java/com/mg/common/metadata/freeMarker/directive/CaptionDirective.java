package com.mg.common.metadata.freeMarker.directive;

import freemarker.core.Environment;
import freemarker.template.*;

import java.io.IOException;
import java.util.Map;

/**
 * Created by liukefu on 2015/9/28.
 */
public class CaptionDirective implements TemplateDirectiveModel {
    @Override
    public void execute(Environment env,
                        Map params, TemplateModel[] loopVars,
                        TemplateDirectiveBody body)
            throws TemplateException, IOException {
        // Check if no parameters were given:
        if (params.isEmpty()) {
            throw new TemplateModelException("This directive need 'content' parameters.");
        }

        SimpleScalar contentScalar = (SimpleScalar)params.get("content");
        String content = contentScalar.getAsString();
        StringBuilder sb = new StringBuilder();
        sb.append("<h3 class=\"template_table_title\">\n" +
                "    <span class=\"left\">"+content+"</span>\n" +
                "</h3>");
        env.getOut().write(sb.toString());
    }

}
