package com.mg.common.metadata.util;

import org.hibernate.Session;
import org.hibernate.cfg.Environment;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.id.enhanced.Optimizer;
import org.hibernate.id.enhanced.OptimizerFactory;
import org.hibernate.internal.util.config.ConfigurationHelper;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.tuple.IdentifierProperty;
import org.hibernate.tuple.PropertyFactory;
import org.hibernate.type.IntegerType;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.Properties;

/**
 * Created by liukefu on 2015/11/11.
 */
@Service
public class MetaTableGenerator {

    @PersistenceContext
    private EntityManager entityManager;

    public static final String OPT_PARAM = "optimizer";

    public static final String INITIAL_PARAM = "initial_value";

    private int incrementSize;
    /**
     * 调用entity 的主键生成方式
     * @param entity
     * @return
     */
    public synchronized Serializable generate(Object entity) {
        Session factorySession = (org.hibernate.Session) entityManager.getDelegate();
        SessionImplementor sessionImplementor = entityManager.unwrap(SessionImplementor.class);
        SessionFactoryImplementor sessionFactory = (SessionFactoryImplementor) factorySession.getSessionFactory();
        //方式一
        String entityName  = "";
        EntityPersister persister = sessionImplementor.getEntityPersister(entityName, entity);
        Serializable generatedId = persister.getIdentifierGenerator().generate(sessionImplementor, entity);

        //方式二
        PersistentClass persistentClass = null;
        Property property = persistentClass.getIdentifierProperty();
        IdentifierProperty identifierProperty = PropertyFactory.buildIdentifierProperty(
                persistentClass,
                sessionFactory.getIdentifierGenerator(entityName)
        );
        identifierProperty.getIdentifierGenerator().generate(sessionImplementor,entity);
        sessionFactory.getDialect();
        sessionFactory.getEntityPersister("");
        //方式三
        IdentifierGenerator identifierGenerator = sessionFactory.getIdentifierGenerator(entityName);
        String id = "" + identifierGenerator.generate(sessionImplementor,entity);

        return id;
    }

    /**
     * jdbc 的主键生成方式
     * @param tableName
     * @return
     */
    public synchronized Serializable generate(String tableName) {
        // 如果表是一个hibernate 实体维护的
/*
        if(false){
            return generate(null);
        }*/
        Properties params = new Properties();
        String defaultPooledOptimizerStrategy = ConfigurationHelper.getBoolean( Environment.PREFER_POOLED_VALUES_LO, params, false )
                ? OptimizerFactory.StandardOptimizerDescriptor.POOLED_LO.getExternalName()
                : OptimizerFactory.StandardOptimizerDescriptor.POOLED.getExternalName();
        final String defaultOptimizerStrategy = incrementSize <= 1
                ? OptimizerFactory.StandardOptimizerDescriptor.NONE.getExternalName()
                : defaultPooledOptimizerStrategy;
        final String optimizationStrategy = ConfigurationHelper.getString( OPT_PARAM, params, defaultOptimizerStrategy );
        Optimizer optimizer = OptimizerFactory.buildOptimizer(
                optimizationStrategy,
                new IntegerType().getReturnedClass(),
                incrementSize,
                ConfigurationHelper.getInt(INITIAL_PARAM, params, -1));
        return null;
    }
}
