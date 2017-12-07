package com.mg.common.metadata.service;

import com.mg.common.metadata.vo.IdGenerator;
import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.cfg.Environment;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.internal.FormatStyle;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.engine.jdbc.spi.SqlStatementLogger;
import org.hibernate.engine.spi.SessionEventListenerManager;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.IdentifierGeneratorHelper;
import org.hibernate.id.IntegralDataTypeHolder;
import org.hibernate.id.enhanced.AccessCallback;
import org.hibernate.id.enhanced.Optimizer;
import org.hibernate.id.enhanced.OptimizerFactory;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.internal.util.config.ConfigurationHelper;
import org.hibernate.jdbc.AbstractReturningWork;
import org.hibernate.type.IntegerType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 元数据ID generator
 * 参考 org.hibernate.id.enhanced.TableGenerator
 * Created by liukefu on 2015/11/11.
 */
@Service
public class MTableGeneratorServiceImpl implements MTableGeneratorService {

    @PersistenceContext
    private EntityManager entityManager;

    public static final String OPT_PARAM = "optimizer";
    public static final String INITIAL_PARAM = "initial_value";

    private String tableName;

    private String valueColumnName;
    private int initialValue;
    private int incrementSize;

    private String segmentColumnName;

    private Properties params = new Properties();
    private Map<String, IdGenerator> generatorMap = new HashMap<>();

    private Dialect dialect = null;
    public void initParam() {
        params.put("format", "%1$d");
        params.put(org.hibernate.id.enhanced.TableGenerator.CONFIG_PREFER_SEGMENT_PER_ENTITY, true);
        params.put(org.hibernate.id.enhanced.TableGenerator.TABLE_PARAM, "mg_id_generator");
        params.put(org.hibernate.id.enhanced.TableGenerator.SEGMENT_COLUMN_PARAM, "gen_name");
        params.put(org.hibernate.id.enhanced.TableGenerator.VALUE_COLUMN_PARAM, "gen_value");
        params.put(org.hibernate.id.enhanced.TableGenerator.INITIAL_PARAM, "500000");
        params.put(org.hibernate.id.enhanced.TableGenerator.INCREMENT_PARAM, "50");
        params.put(org.hibernate.id.enhanced.TableGenerator.OPT_PARAM, "pooled");

        valueColumnName = "gen_value";
        initialValue = 500000;
        incrementSize = 50;

        tableName ="mg_id_generator";
        segmentColumnName = "gen_name";
    }

    public void initIdGenerator(String tableName) {
        //初始化
        if (params.isEmpty()) {
            initParam();
        }

        String defaultPooledOptimizerStrategy = ConfigurationHelper.getBoolean(Environment.PREFER_POOLED_VALUES_LO, params, false)
                ? OptimizerFactory.StandardOptimizerDescriptor.POOLED_LO.getExternalName()
                : OptimizerFactory.StandardOptimizerDescriptor.POOLED.getExternalName();
        final String defaultOptimizerStrategy = incrementSize <= 1
                ? OptimizerFactory.StandardOptimizerDescriptor.NONE.getExternalName()
                : defaultPooledOptimizerStrategy;
        final String optimizationStrategy = ConfigurationHelper.getString(OPT_PARAM, params, defaultOptimizerStrategy);
        Optimizer optimizer = OptimizerFactory.buildOptimizer(
                optimizationStrategy,
                new IntegerType().getReturnedClass(),
                incrementSize,
                ConfigurationHelper.getInt(INITIAL_PARAM, params, -1));

        String selectQuery = buildSelectQuery(dialect);
        String insertQuery = buildInsertQuery();
        String updateQuery = buildUpdateQuery();

        IdGenerator idGenerator = new IdGenerator(selectQuery,insertQuery,updateQuery,tableName,optimizer);
        generatorMap.put(tableName,idGenerator);
    }

    private SessionImplementor getSessionImplementor(){
        SessionImplementor sessionImplementor = entityManager.unwrap(SessionImplementor.class);

        return sessionImplementor;
    }

    protected String buildSelectQuery(Dialect dialect) {
        final String alias = "tbl";
        String query = "select " + StringHelper.qualify(alias, valueColumnName) +
                " from " + tableName + ' ' + alias +
                " where " + StringHelper.qualify(alias, segmentColumnName) + "=?";
        LockOptions lockOptions = new LockOptions(LockMode.PESSIMISTIC_WRITE);
        lockOptions.setAliasSpecificLockMode(alias, LockMode.PESSIMISTIC_WRITE);
        Map updateTargetColumnsMap = Collections.singletonMap(alias, new String[]{valueColumnName});
        return dialect.applyLocksToSql(query, lockOptions, updateTargetColumnsMap);
    }

    protected String buildUpdateQuery() {
        return "update " + tableName +
                " set " + valueColumnName + "=? " +
                " where " + valueColumnName + "=? and " + segmentColumnName + "=?";
    }

    protected String buildInsertQuery() {
        return "insert into " + tableName + " (" + segmentColumnName + ", " + valueColumnName + ") " + " values (?,?)";
    }

    private PreparedStatement prepareStatement(
            Connection connection,
            String sql,
            SqlStatementLogger statementLogger,
            SessionEventListenerManager statsCollector) throws SQLException {
        statementLogger.logStatement(sql, FormatStyle.BASIC.getFormatter());
        try {
            statsCollector.jdbcPrepareStatementStart();
            return connection.prepareStatement(sql);
        } finally {
            statsCollector.jdbcPrepareStatementEnd();
        }
    }

    private int executeUpdate(PreparedStatement ps, SessionEventListenerManager statsCollector) throws SQLException {
        try {
            statsCollector.jdbcExecuteStatementStart();
            return ps.executeUpdate();
        } finally {
            statsCollector.jdbcExecuteStatementEnd();
        }

    }

    private IntegralDataTypeHolder makeValue() {
        return IdentifierGeneratorHelper.getIntegralDataTypeHolder(new IntegerType().getReturnedClass());
    }

    private ResultSet executeQuery(PreparedStatement ps, SessionEventListenerManager statsCollector) throws SQLException {
        try {
            statsCollector.jdbcExecuteStatementStart();
            return ps.executeQuery();
        } finally {
            statsCollector.jdbcExecuteStatementEnd();
        }
    }

    private AccessCallback getAccessCallback(final SessionImplementor session, final AbstractReturningWork<IntegralDataTypeHolder> abstractReturningWork) {
        AccessCallback callback = new AccessCallback() {
            @Override
            public IntegralDataTypeHolder getNextValue() {
                return session.getTransactionCoordinator().getTransaction().createIsolationDelegate().delegateWork(abstractReturningWork, true);
            }
        };

        return callback;
    }

    private AbstractReturningWork<IntegralDataTypeHolder> getAbstractReturningWork(final SessionEventListenerManager statsCollector, final SqlStatementLogger statementLogger,final IdGenerator idGenerator) {
        AbstractReturningWork<IntegralDataTypeHolder> abstractReturningWork = new AbstractReturningWork<IntegralDataTypeHolder>() {
            @Override
            public IntegralDataTypeHolder execute(Connection connection) throws SQLException {
                {
                    final IntegralDataTypeHolder value = makeValue();
                    int rows;
                    do {
                        final PreparedStatement selectPS = prepareStatement(connection, idGenerator.getSelectQuery(), statementLogger, statsCollector);

                        try {
                            selectPS.setString(1, idGenerator.getSegmentValue());
                            final ResultSet selectRS = executeQuery(selectPS, statsCollector);
                            if (!selectRS.next()) {
                                value.initialize(initialValue);

                                final PreparedStatement insertPS = prepareStatement(connection, idGenerator.getInsertQuery(), statementLogger, statsCollector);
                                try {
                                    insertPS.setString(1, idGenerator.getSegmentValue());
                                    value.bind(insertPS, 2);
                                    executeUpdate(insertPS, statsCollector);
                                } finally {
                                    insertPS.close();
                                }
                            } else {
                                value.initialize(selectRS, 1);
                            }
                            selectRS.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                            throw e;
                        } finally {
                            selectPS.close();
                        }

                        final PreparedStatement updatePS = prepareStatement(connection, idGenerator.getUpdateQuery(), statementLogger, statsCollector);
                        try {
                            final IntegralDataTypeHolder updateValue = value.copy();
                            if (idGenerator.getOptimizer().applyIncrementSizeToSourceValues()) {
                                updateValue.add(incrementSize);
                            } else {
                                updateValue.increment();
                            }
                            updateValue.bind(updatePS, 1);
                            value.bind(updatePS, 2);
                            updatePS.setString(3, idGenerator.getSegmentValue());
                            rows = executeUpdate(updatePS, statsCollector);
                        } catch (SQLException e) {
                            throw e;
                        } finally {
                            updatePS.close();
                        }
                    }
                    while (rows == 0);


                    return value;
                }
            }
        };

        return abstractReturningWork;
    }

    private AbstractReturningWork<IntegralDataTypeHolder> getAbstractReturningWork(final SessionEventListenerManager statsCollector, final SqlStatementLogger statementLogger,final IdGenerator idGenerator,final int initialValue) {
        AbstractReturningWork<IntegralDataTypeHolder> abstractReturningWork = new AbstractReturningWork<IntegralDataTypeHolder>() {
            @Override
            public IntegralDataTypeHolder execute(Connection connection) throws SQLException {
                {
                    final IntegralDataTypeHolder value = makeValue();
                    int rows;
                    do {
                        final PreparedStatement selectPS = prepareStatement(connection, idGenerator.getSelectQuery(), statementLogger, statsCollector);

                        try {
                            selectPS.setString(1, idGenerator.getSegmentValue());
                            final ResultSet selectRS = executeQuery(selectPS, statsCollector);
                            if (!selectRS.next()) {
                                value.initialize(initialValue);

                                final PreparedStatement insertPS = prepareStatement(connection, idGenerator.getInsertQuery(), statementLogger, statsCollector);
                                try {
                                    insertPS.setString(1, idGenerator.getSegmentValue());
                                    value.bind(insertPS, 2);
                                    executeUpdate(insertPS, statsCollector);
                                } finally {
                                    insertPS.close();
                                }
                            } else {
                                value.initialize(selectRS, 1);
                            }
                            selectRS.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                            throw e;
                        } finally {
                            selectPS.close();
                        }

                        final PreparedStatement updatePS = prepareStatement(connection, idGenerator.getUpdateQuery(), statementLogger, statsCollector);
                        try {
                            final IntegralDataTypeHolder updateValue = value.copy();
                            if (idGenerator.getOptimizer().applyIncrementSizeToSourceValues()) {
                                updateValue.add(incrementSize);
                            } else {
                                updateValue.increment();
                            }
                            updateValue.bind(updatePS, 1);
                            value.bind(updatePS, 2);
                            updatePS.setString(3, idGenerator.getSegmentValue());
                            rows = executeUpdate(updatePS, statsCollector);
                        } catch (SQLException e) {
                            throw e;
                        } finally {
                            updatePS.close();
                        }
                    }
                    while (rows == 0);


                    return value;
                }
            }
        };

        return abstractReturningWork;
    }
    /**
     * 产生对应表“tableName”的ID
     * @param tableName
     * @return
     */
    @Transactional
    public Serializable generate(final String tableName) {
        SessionImplementor session = getSessionImplementor();
        if (generatorMap.get(tableName) == null) {
            dialect = session.getFactory().getDialect();
            initIdGenerator(tableName);
        }

        IdGenerator idGenerator = generatorMap.get(tableName);
        Optimizer optimizer = idGenerator.getOptimizer();

        final SqlStatementLogger statementLogger = session.getFactory().getServiceRegistry()
                .getService(JdbcServices.class)
                .getSqlStatementLogger();
        final SessionEventListenerManager statsCollector = session.getEventListenerManager();
        AbstractReturningWork<IntegralDataTypeHolder> abstractReturningWork = getAbstractReturningWork(statsCollector, statementLogger,idGenerator);
        AccessCallback callback = getAccessCallback(session, abstractReturningWork);


        return optimizer.generate(callback);

    }

    /**
     * 产生对应表“tableName”的ID
     * 指定序号的初始值
     * @param tableName
     * @param initialValue
     * @return
     */
    @Transactional
    public Serializable generate(final String tableName,final int initialValue) {
        SessionImplementor session = getSessionImplementor();
        if (generatorMap.get(tableName) == null) {
            dialect = session.getFactory().getDialect();
            initIdGenerator(tableName);
        }

        IdGenerator idGenerator = generatorMap.get(tableName);
        Optimizer optimizer = idGenerator.getOptimizer();

        final SqlStatementLogger statementLogger = session.getFactory().getServiceRegistry()
                .getService(JdbcServices.class)
                .getSqlStatementLogger();
        final SessionEventListenerManager statsCollector = session.getEventListenerManager();
        AbstractReturningWork<IntegralDataTypeHolder> abstractReturningWork = getAbstractReturningWork(statsCollector, statementLogger,idGenerator,initialValue);
        AccessCallback callback = getAccessCallback(session, abstractReturningWork);


        return optimizer.generate(callback);

    }
}
