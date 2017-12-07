package com.mg.common.metadata.service;

import com.mg.framework.entity.metadata.MObjectEntity;

import java.util.List;
import java.util.Map;

/**
 * 模板类数据导出
 * Created by liukefu on 2016/4/21.
 */
public interface TemplateExportService {

    String createExcel(String templateFileName, Map<String,Object> beanParams, String resultFileName);

    String createExcel(MObjectEntity mObjectEntity, List<String> ids, String templatePath);
}
