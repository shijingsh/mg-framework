package com.mg.common.metadata.service;

import com.mg.framework.entity.metadata.MImportTemplateEntity;

import java.util.List;

/**
 * 通用元数据导入
 * Created by liukefu on 2015/10/21.
 */
public interface MetaDataImportService {

    /**
     * 数据导入
     * @param path
     */
    public void importData(String path);

    /**
     * 创建导入数据模板
     * @param template
     * @return
     */
    public  boolean createExcelTemplate(MImportTemplateEntity template, String objId);
    /**
     * 保存导入模板
     * @param templateEntity
     * @param objId
     * @return
     */
    public MImportTemplateEntity saveImportTemplate(MImportTemplateEntity templateEntity, String objId);
    /**
     * 查询对象下面的导入模板列表
     * @param objId
     * @return
     */
    public List<MImportTemplateEntity> queryImportTemplateList(String objId);
}
