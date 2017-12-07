package com.mg.common.metadata.dao;

import com.mg.framework.entity.metadata.MObjectEntity;

import java.util.List;
import java.util.Map;

public interface MObjectDaoCustom {

	/**
	 * 获取员工的元数据对象
	 * @return
	 */
	public MObjectEntity findEmployeeMObject(List<String> names);

	public List<MObjectEntity> findPageList(Map<String, Object> map);
		
	public Long findCount(Map<String, Object> map) ;

	/**
	 * 根据名称查询元数据对象
	 * @return
	 */
	public MObjectEntity findMObjectByName(String name);
}

