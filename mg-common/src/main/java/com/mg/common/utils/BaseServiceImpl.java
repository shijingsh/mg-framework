package com.mg.common.utils;

import com.mysema.query.types.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * service基类
 * Created by kf on 2016/11/8.
 */

public abstract class BaseServiceImpl<T> {
    @Autowired
    private BaseDao<T> baseDao;

    public T save(T t){
       return baseDao.save(t);
    }

    public T saveAndFlush(T t){
        return baseDao.saveAndFlush(t);
    }

    public T get(String id){
        return baseDao.findOne(id);
    }

    public void delete(String id){
         baseDao.delete(id);
    }

    public void delete(T t){
        baseDao.delete(t);
    }

    public Page<T> findPageList(Predicate predicate, Pageable pageable){
        return baseDao.findAll(predicate, pageable);
    }
}
