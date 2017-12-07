package com.mg.common.metadata.service;

import com.mg.framework.entity.metadata.MSqlEntity;

import java.util.List;

/**
 * Created by liukefu on 2015/12/22.
 */
public interface MSqlService {

    /**
     * 获取分类下的sql
     * @param categoryName
     * @return
     */
    public List<MSqlEntity> getCategory(String categoryName);
    /**
     * 执行一个分类下面的全部sql
     * @param categoryName
     */
    public void executeCategory(String categoryName);

    /**
     * 执行名称为name的脚本
     * @param name
     */
    public void execute(String name);

    /**
     * 执行列表
     * @param list
     */
    public void execute(List<MSqlEntity> list);

    /**
     * 执行一个sql 语句，多条可以用分号分隔“;”
     * @param sql
     */
    public void executeUpdate(final String sql);
}
