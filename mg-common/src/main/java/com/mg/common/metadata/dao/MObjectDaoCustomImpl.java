package com.mg.common.metadata.dao;

import com.mg.framework.entity.metadata.MObjectEntity;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mg.framework.entity.metadata.QMObjectEntity;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Map;

@Component
public class MObjectDaoCustomImpl implements MObjectDaoCustom {
	@PersistenceContext
	private EntityManager entityManager;

	/**
	 * 获取员工的元数据对象
	 * @return
	 */
	public MObjectEntity findEmployeeMObject(List<String> names){

		QMObjectEntity mObjectEntity = QMObjectEntity.mObjectEntity;

		JPAQuery query = new JPAQuery(entityManager);
		List<MObjectEntity> users = query.from(mObjectEntity)
				.where(mObjectEntity.isEnable.eq(true)
								.and(mObjectEntity.name.in(names))
				)
				.list(mObjectEntity);

		if(users.size()>0){
			return users.get(0);
		}
		return null;
	}
	@Override
	public List<MObjectEntity> findPageList(Map<String, Object> map) {
		QMObjectEntity mObjectEntity = QMObjectEntity.mObjectEntity;
		Integer offset = (Integer)map.get("pageNo");
		Integer limit = (Integer)map.get("pageSize");

		if(limit==null || limit <=0){
			limit = 15;
		}
		if(offset==null || offset <=0){
			offset = 0;
		}else{
			offset = (offset-1) * limit;
		}
		
		JPAQuery query = new JPAQuery(entityManager);
		List<MObjectEntity> users = query.from(mObjectEntity)
				.where(mObjectEntity.isEnable.eq(true)).offset(offset).limit(limit)
				.list(mObjectEntity);

		return users;
	}

	public Long findCount(Map<String, Object> map) {
		QMObjectEntity mObjectEntity = QMObjectEntity.mObjectEntity;
		Integer offset = (Integer)map.get("pageNo");
		Integer limit = (Integer)map.get("pageSize");

		if(limit==null || limit <=0){
			limit = 15;
		}
		if(offset==null || offset <=0){
			offset = 0;
		}else{
			offset = (offset-1) * limit;
		}
		
		JPAQuery query = new JPAQuery(entityManager);
		Long totalNum = query.from(mObjectEntity).where(mObjectEntity.isEnable.eq(true)).offset(offset).limit(limit).count();
		return totalNum;
	}

	/**
	 * 根据名称查询元数据对象
	 * @return
	 */
	public MObjectEntity findMObjectByName(String name){

		QMObjectEntity mObjectEntity = QMObjectEntity.mObjectEntity;

		JPAQuery query = new JPAQuery(entityManager);
		List<MObjectEntity> users = query.from(mObjectEntity)
				.where(mObjectEntity.isEnable.eq(true)
								.and(mObjectEntity.name.eq(name).or(mObjectEntity.secondName.eq(name)))
				)
				.list(mObjectEntity);

		if(users.size()>0){
			return users.get(0);
		}
		return null;
	}
}

