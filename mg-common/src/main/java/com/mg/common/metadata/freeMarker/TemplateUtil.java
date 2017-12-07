package com.mg.common.metadata.freeMarker;

import com.mg.common.metadata.freeMarker.directive.*;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;
import java.util.Properties;

/**
 * Created by liukefu on 2015/9/9.
 */
public class TemplateUtil {

    private static Configuration configuration = null;

    public static Configuration getDefaultConfiguration(){
        if(configuration == null){
            configuration = new Configuration();
            Properties settings = defaultConfiguration();
            try {
                configuration.setSettings(settings);
            } catch (TemplateException e) {
                e.printStackTrace();
            }
            configuration.setSharedVariable("caption", new CaptionDirective());
            configuration.setSharedVariable("element", new ElementDirective());
            configuration.setSharedVariable("headPortrait", new HeadPortraitDirective());
            configuration.setSharedVariable("images", new ImagesDirective());
            configuration.setSharedVariable("files", new FilesDirective());
            configuration.setSharedVariable("list", new ListElementDirective());
            configuration.setSharedVariable("listView", new ListViewElementDirective());
        }
        return configuration;
    }

    public static Template createTemplate(String templateStr) {
        String shiro = "<#assign shiro = JspTaglibs[\"/WEB-INF/tld/shiro.tld\"]>";
        templateStr = shiro + templateStr;
        Configuration cfg = getDefaultConfiguration();
        configuration.setTemplateLoader(new StringTemplateLoader(templateStr));
        try {

            Template template =  cfg.getTemplate(templateStr);

            return template;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String getTemplateHtml(String templateStr,Map<String, Object> rootMap) {
        Configuration cfg = getDefaultConfiguration();
        configuration.setTemplateLoader(new StringTemplateLoader(templateStr));
        try {
            Template template =  cfg.getTemplate(templateStr);

            StringWriter writer = new StringWriter();
            template.process(rootMap, writer);

            return writer.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static Properties defaultConfiguration() {
        Properties settings = new Properties();
        settings.put("classic_compatible", "true");
        settings.put("whitespace_stripping", "true");
        settings.put("template_update_delay", "300");
        settings.put("locale", "zh_CN");
        settings.put("default_encoding", "utf-8");
        settings.put("url_escaping_charset", "utf-8");
        settings.put("date_format", "yyyy-MM-dd");
        settings.put("time_format", "HH:mm:ss");
        settings.put("datetime_format", "yyyy-MM-dd HH:mm:ss");
        settings.put("number_format", "#.##");
        settings.put("boolean_format", "true,false");
        settings.put("output_encoding", "UTF-8");
        settings.put("tag_syntax", "auto_detect");
        return settings;
    }
}
