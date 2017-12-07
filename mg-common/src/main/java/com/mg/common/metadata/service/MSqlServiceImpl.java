package com.mg.common.metadata.service;

import com.mg.common.metadata.dao.MSqlDao;
import com.mg.framework.entity.metadata.MSqlEntity;
import com.mg.groovy.define.keyword.GroovyConstants;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.jdbc.Work;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * 执行数据库脚本服务
 * Created by liukefu on 2015/12/22.
 */
@Service
public class MSqlServiceImpl implements MSqlService {
    @Autowired
    MetaDataCoreService metaDataCoreService;
    @PersistenceContext
    EntityManager entityManager;
    @Autowired
    MSqlDao mSqlDao;

    /**
     * 获取分类下的sql
     * @param categoryName
     * @return
     */
    public List<MSqlEntity> getCategory(String categoryName){
        List<MSqlEntity> list =  mSqlDao.findByCategoryNameOrderBySortAsc(categoryName);

        return list;
    }
    /**
     * 执行一个分类下面的全部sql
     * @param categoryName
     */
    public void executeCategory(String categoryName){
        List<MSqlEntity> list =  mSqlDao.findByCategoryNameOrderBySortAsc(categoryName);
        for(MSqlEntity sqlEntity:list){
            _update(sqlEntity.getSqlScript());
        }
    }

    /**
     * 执行名称为name的脚本
     * @param name
     */
    public void execute(String name){
        List<MSqlEntity> list =  mSqlDao.findByNameOrderBySortAsc(name);
        for(MSqlEntity sqlEntity:list){
            _update(sqlEntity.getSqlScript());
        }
    }

    /**
     * 执行列表
     * @param list
     */
    public void execute(List<MSqlEntity> list){

        for(MSqlEntity sqlEntity:list){
            _update(sqlEntity.getSqlScript());
        }
    }

    private int _update(String sql){
        String sqlArr[] = splitSql(sql, ";");
        int total = 0;
        /*for(String s:sqlArr){
            if(s!=null && StringUtils.isNotBlank(s.trim())){
                s = s+";";
                System.out.print(s);
                Query query = entityManager.createNativeQuery(s);
                total = total + query.executeUpdate();
            }
        }*/
        executeUpdate(sql);
        return total;
    }

    public String[] splitSql(String str,String separatorChars){
        int length = StringUtils.split(str, separatorChars).length;
        String params[] = new String[length];
        char [] cArr = str.toCharArray();
        char c = 0 ;
        int bracketsNum = 0;
        int paramIndex = 0;
        StringBuilder param = new StringBuilder();
        for(int i=0;i<cArr.length;i++){
            c = cArr[i];
            if(String.valueOf(c).equals(GroovyConstants.gc_brackets_small)){
                ++bracketsNum;
            }
            if(String.valueOf(c).equals(GroovyConstants.gc_brackets_small_end)){
                --bracketsNum;
            }

            if(String.valueOf(c).equals(GroovyConstants.gc_semicolon)){
                if(bracketsNum==0){
                    String p = param.toString();
                    params[paramIndex++] = p;
                    param.delete(0, p.length());
                }else{
                    param.append(c);
                }
            }else{
                param.append(c);
            }
        }
        //最后一个参数
        if(param.length()>0){
            params[paramIndex] = param.toString();
        }

        return params;
    }

    public void executeUpdate(final String sql){
        Session session = (org.hibernate.Session) entityManager.getDelegate();
        session.doWork(new Work() {
            public void execute(Connection connection) {
                try {
                    Statement stmt = connection.createStatement();
                    String sqlArr[] = splitSql(sql, ";");

                    for(String s:sqlArr){
                        if(s!=null && StringUtils.isNotBlank(s.trim())){
                            s = s+";";
                            System.out.print(s);
                            stmt.addBatch(s);
                        }
                    }
                    stmt.executeBatch();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } );
    }
}
