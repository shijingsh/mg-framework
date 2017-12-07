package com.mg.common.metadata.service;

import com.mg.common.metadata.groovy.MetaDataScriptEngineUtil;
import com.mg.common.metadata.util.MetaDataUtils;
import com.mg.common.metadata.vo.MTable;
import com.mg.common.metadata.vo.TableRelation;
import com.mg.framework.entity.metadata.*;
import com.mg.framework.utils.UserHolder;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.script.ScriptException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 元数据核心实现类
 * 创建表结构
 * 执行数据存储、及查询
 * Created by liukefu on 2015/8/28.
 */
@Service
public class MetaDataCoreServiceImpl implements MetaDataCoreService {

    Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    MTableGeneratorService mTableGeneratorService;
    @Autowired
    MetaDataQueryService metaDataQueryService;
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * 保存元数据对象
     *
     * @param mObjectEntity       元数据对象
     * @param mPropertyEntityList 元数据列表
     * @return
     */
    public String _save(MObjectEntity mObjectEntity, List<MirrorPropertyEntity> mPropertyEntityList) {
        //自动生成的字段
        whenCreateData(mObjectEntity, mPropertyEntityList);
        StringBuilder sb = new StringBuilder();
        sb.append(" INSERT INTO     ").append(mObjectEntity.getTableName());
        sb.append(" ( ");
        //值存放
        Map<String, Object> paramMap = new HashMap<>();
        String pk = null;
        for (int i = 0; i < mPropertyEntityList.size(); i++) {
            MirrorPropertyEntity mirrorPropertyEntity = mPropertyEntityList.get(i);

            if (i == mPropertyEntityList.size() - 1) {
                sb.append(MetaDataUtils.getFieldName(mirrorPropertyEntity));
            } else {
                sb.append(MetaDataUtils.getFieldName(mirrorPropertyEntity)).append(MetaDataUtils.SQL_COMMA);
            }
            if ( mirrorPropertyEntity.getMetaProperty().getIsPrimaryKey()) {
                if(mirrorPropertyEntity.getFieldValue() == null || StringUtils.isBlank(mirrorPropertyEntity.getFieldValue().toString())){
                    pk = "" + mTableGeneratorService.generate(mObjectEntity.getTableName());
                    mirrorPropertyEntity.setFieldValue(pk);
                }
            }
            if (mirrorPropertyEntity.getFieldValue() == null && StringUtils.isNotBlank(mirrorPropertyEntity.getFormula())) {
                //自动编号类型的字段
                String script = mirrorPropertyEntity.getFormula();
                try {
                    Object codeObject = MetaDataScriptEngineUtil.execGroovyScript(script, new HashMap<String, Object>());
                    mirrorPropertyEntity.setFieldValue(codeObject);
                } catch (ScriptException e) {
                    e.printStackTrace();
                }
            }
            paramMap.put(MetaDataUtils.getFieldName(mirrorPropertyEntity), mirrorPropertyEntity.getFieldValue());
        }
        sb.append(" ) VALUES ( ");
        for (int i = 0; i < mPropertyEntityList.size(); i++) {
            MirrorPropertyEntity mirrorPropertyEntity = mPropertyEntityList.get(i);

            if (i == mPropertyEntityList.size() - 1) {
                sb.append(MetaDataUtils.SQL_COLON).append(MetaDataUtils.getFieldName(mirrorPropertyEntity));
            } else {
                sb.append(MetaDataUtils.SQL_COLON).append(MetaDataUtils.getFieldName(mirrorPropertyEntity)).append(MetaDataUtils.SQL_COMMA);
            }
        }
        sb.append("  )  ");
        logger.debug("save sql ：{}", sb.toString());
        _update(sb.toString(), paramMap);
        return pk;
    }

    /**
     * 删除一条记录
     *
     * @param pkProperty
     * @return
     */
    public int _delete(MObjectEntity mObjectEntity, MirrorPropertyEntity pkProperty) {
        StringBuilder sb = new StringBuilder();
        sb.append(" delete  from ").append(mObjectEntity.getTableName());
        sb.append(" where ").append(MetaDataUtils.META_FIELD_ID).append("= :").append(MetaDataUtils.META_FIELD_ID);
        //值存放
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(MetaDataUtils.META_FIELD_ID, pkProperty.getFieldValue());

        return _update(sb.toString(), paramMap);
    }

    /**
     * 修改多个元数据的值
     *
     * @param mPropertyEntityList
     * @return
     */
    public int _update(MObjectEntity mObjectEntity, List<MirrorPropertyEntity> mPropertyEntityList) {
        //自动生成的字段
        whenUpdateData(mObjectEntity, mPropertyEntityList);
        StringBuilder sb = new StringBuilder();
        sb.append(" update  ").append(mObjectEntity.getTableName());
        sb.append(" set  ");
        //值存放
        Map<String, Object> paramMap = new HashMap<>();
        //pk 属性
        MirrorPropertyEntity pkPropertyEntity = null;
        for (int i = 0; i < mPropertyEntityList.size(); i++) {
            MirrorPropertyEntity mirrorPropertyEntity = mPropertyEntityList.get(i);
            if (i == mPropertyEntityList.size() - 1) {
                sb.append(MetaDataUtils.getFieldName(mirrorPropertyEntity)).append(MetaDataUtils.SQL_EQ)
                        .append(MetaDataUtils.SQL_COLON).append(MetaDataUtils.getFieldName(mirrorPropertyEntity));
            } else {
                sb.append(MetaDataUtils.getFieldName(mirrorPropertyEntity)).append(MetaDataUtils.SQL_EQ)
                        .append(MetaDataUtils.SQL_COLON).append(MetaDataUtils.getFieldName(mirrorPropertyEntity)).append(MetaDataUtils.SQL_COMMA);
            }
            paramMap.put(MetaDataUtils.getFieldName(mirrorPropertyEntity), mirrorPropertyEntity.getFieldValue());

            if (mirrorPropertyEntity.getMetaProperty().getIsPrimaryKey()) {
                pkPropertyEntity = mirrorPropertyEntity;
            }
        }
        if (pkPropertyEntity == null) {
            //没有主键，不更新任何数据
            sb.append(" where 1=2");
        } else {
            sb.append(" where ").append(MetaDataUtils.getFieldName(pkPropertyEntity)).append(MetaDataUtils.SQL_EQ)
                    .append(MetaDataUtils.SQL_COLON).append(MetaDataUtils.getFieldName(pkPropertyEntity));
        }
        int count = _update(sb.toString(), paramMap);

        return count;
    }

    /**
     * 根据元数据、条件组，查询数据
     *
     * @param mObject            主元数据对象
     * @param showMProperties    查询返回的元数据
     * @param joinedObjs         需要连接的表
     * @param expressGroupEntity 查询条件
     * @return List<Map>
     */
    public List<Map<String, Object>> _query(MObjectEntity mObject, List<MirrorPropertyEntity> showMProperties,
                                            List<TableRelation> joinedObjs,
                                            Map<String, MTable> joinedMapping,
                                            MExpressGroupEntity expressGroupEntity) {

        Map<String, Object> paramMap = new HashMap<>();

        StringBuilder sb = new StringBuilder();
        sb.append("select ");
        sb.append(_select(showMProperties, joinedMapping, expressGroupEntity));
        sb.append(" from ");
        sb.append(_from(mObject, showMProperties, joinedObjs, joinedMapping));
        sb.append(" where ");
        sb.append(_where(mObject, expressGroupEntity, paramMap, joinedObjs, joinedMapping));
        sb.append(_orderBy(joinedMapping, expressGroupEntity));
        sb.append(_limit(expressGroupEntity));
        return _query(sb.toString(), paramMap);
    }


    /**
     * 根据元数据、条件组，查询数据总数
     *
     * @param mObject            主元数据对象
     * @param showMProperties    查询返回的元数据
     * @param joinedObjs         需要连接的表
     * @param expressGroupEntity 查询条件
     * @return List<Map>
     */
    public Integer _queryCount(MObjectEntity mObject, List<MirrorPropertyEntity> showMProperties,
                               List<TableRelation> joinedObjs,
                               Map<String, MTable> joinedMapping,
                               MExpressGroupEntity expressGroupEntity) {

        Map<String, Object> paramMap = new HashMap<>();

        StringBuilder sb = new StringBuilder();
        sb.append("select count(1) as count from (");
        sb.append("select ");
        sb.append(_select(showMProperties, joinedMapping, expressGroupEntity));
        sb.append(" from ");
        sb.append(_from(mObject, showMProperties, joinedObjs, joinedMapping));
        sb.append(" where ");
        sb.append(_where(mObject, expressGroupEntity, paramMap, joinedObjs, joinedMapping));
        sb.append(") t");
        Object obj = _queryObj(sb.toString(), paramMap);
        Map<String, Object> values = (Map<String, Object>) obj;
        BigInteger count = (BigInteger) values.get("count");
        return count.intValue();
    }

    /**
     * sql 拼接 select .... from 之间部分
     *
     * @param showMProperties
     * @param joinedMapping
     * @param expressGroupEntity
     * @return
     */
    public String _select(List<MirrorPropertyEntity> showMProperties,
                          Map<String, MTable> joinedMapping, MExpressGroupEntity expressGroupEntity) {
        StringBuilder sb = new StringBuilder();

        if (expressGroupEntity.getIsDistinct()) {
            sb.append(" distinct ");
        }
        for (MirrorPropertyEntity mirrorPropertyEntity : showMProperties) {

            String alias = _alias(joinedMapping, mirrorPropertyEntity);
            sb.append(alias).append(MetaDataUtils.SQL_POINT).append(MetaDataUtils.getFieldName(mirrorPropertyEntity))
                    .append(MetaDataUtils.SQL_EMPTY).append("as `").append(mirrorPropertyEntity.getPropertyPath()).append("`")
                    .append(MetaDataUtils.SQL_COMMA);
        }
        if (sb.length() - 1 > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    /**
     * sql 拼接 from .... where 之间部分
     *
     * @param mObject
     * @param showMProperties
     * @param joinedObjs
     * @param joinedMapping
     * @return
     */
    public String _from(MObjectEntity mObject, List<MirrorPropertyEntity> showMProperties,
                        List<TableRelation> joinedObjs, Map<String, MTable> joinedMapping) {
        StringBuilder sb = new StringBuilder();

        for (TableRelation tableRelation : joinedObjs) {
            MTable joinedTable = tableRelation.getJoinedTable();
            if (joinedTable == null) {
                MTable mainTable = tableRelation.getMainTable();
                if (mainTable != null) {
                    sb.append(mainTable.getName()).append(MetaDataUtils.SQL_EMPTY).append(mainTable.getAliasName());
                    continue;
                }
            }
            switch (tableRelation.getJoinedType()) {
                case LEFT_JOIN:
                    sb.append(" left join ");
                    break;
                case RIGHT_JOIN:
                    sb.append(" right join ");
                    break;
                case INNER_JOIN:
                    sb.append(" inner join ");
                    break;
                case OUTER_JOIN:
                    sb.append(" outer join ");
                    break;
            }
            sb.append(MetaDataUtils.SQL_EMPTY).append(joinedTable.getName()).append(MetaDataUtils.SQL_EMPTY).append(joinedTable.getAliasName());

            sb.append(_joinCondition(mObject, tableRelation, joinedMapping));
        }
        return sb.toString();
    }

    /**
     * sql 拼接 where 部分
     *
     * @param expressGroupEntity
     * @param paramMap
     * @param joinedMapping
     * @return
     */
    public String _where(MObjectEntity mObject, MExpressGroupEntity expressGroupEntity, Map<String, Object> paramMap,
                         List<TableRelation> joinedObjs, Map<String, MTable> joinedMapping) {
        StringBuilder sb = new StringBuilder();
        sb.append(" 1=1 ");
        //链接条件 joined tables
        //sb.append(_joinCondition(mObject,joinedObjs,joinedMapping));
        if (expressGroupEntity != null && expressGroupEntity.getMatched() != null) {
            //匹配条件
            /**
             * 条件组分为固定三个层次
             * 第1层、固定为匹配全部
             * 第2层、匹配全部 或 匹配任一
             * 第3层、具体条件 alias.a = alias.b
             */
            List<MExpressionEntity> machedList = expressGroupEntity.getMatched().getExpressions();
            if (machedList != null) {
                for (MExpressionEntity expressionEntity : machedList) {
                    int index = 0;
                    if (expressionEntity.getIsMatchAll()) {
                        //匹配全部，and 类直接追加条件
                        List<MExpressionEntity> list = expressionEntity.getExpressions();
                        for (MExpressionEntity express : list) {
                            _condition(sb, express, paramMap, expressionEntity.getIsMatchAll(), joinedMapping, index);
                            index++;
                        }
                    } else {
                        //匹配任一
                        List<MExpressionEntity> list = expressionEntity.getExpressions();
                        if (!_isAllEmpty(list)) {
                            sb.append(MetaDataUtils.SQL_AND).append("(").append("1=2");

                            for (MExpressionEntity express : list) {
                                _condition(sb, express, paramMap, expressionEntity.getIsMatchAll(), joinedMapping, index);
                                index++;
                            }
                            sb.append(")");
                        }
                    }
                }
            }
        }
        //添加原生的sql条件支持
        if (expressGroupEntity != null && expressGroupEntity.getConditions().size() > 0) {
            for (String where : expressGroupEntity.getConditions()) {
                sb.append(where);
            }
        }
        return sb.toString();
    }

    private boolean _isAllEmpty(List<MExpressionEntity> list) {
        for (MExpressionEntity express : list) {
            if (StringUtils.isNotBlank(express.getValue())) {
                return false;
            }
        }
        return true;
    }

    /**
     * 返回排序sql
     *
     * @param joinedMapping
     * @param expressGroupEntity
     * @return
     */
    private String _orderBy(Map<String, MTable> joinedMapping, MExpressGroupEntity expressGroupEntity) {
        StringBuilder sb = new StringBuilder();

        List<MOrderBy> list = expressGroupEntity.getOrderByList();
        if (list.size() > 0) {
            sb.append(" order by ");

            int index = 0;
            for (MOrderBy orderBy : list) {
                String alias = _alias(joinedMapping, orderBy.getProperty());
                if (index > 0) {
                    sb.append(MetaDataUtils.SQL_COMMA);
                } else {
                    index++;
                }
                sb.append(alias).append(MetaDataUtils.SQL_POINT).append(MetaDataUtils.getFieldName(orderBy.getProperty()))
                        .append(MetaDataUtils.SQL_EMPTY).append(orderBy.getOrderByEnum().name());

            }
        }

        return sb.toString();
    }

    /**
     * 返回分页 sql
     *
     * @param expressGroupEntity
     * @return
     */
    private String _limit(MExpressGroupEntity expressGroupEntity) {
        StringBuilder sb = new StringBuilder();

        Integer offset = expressGroupEntity.getPageNo();
        Integer limit = expressGroupEntity.getPageSize();
        if (limit != null && limit == -1) {
            return "";
        }
        if (limit == null || limit <= 0) {
            limit = 15;
        }
        if (offset == null || offset <= 0) {
            offset = 0;
        } else {
            offset = (offset - 1) * limit;
        }

        sb.append(" limit ").append(offset).append(MetaDataUtils.SQL_COMMA).append(limit);
        return sb.toString();
    }

    private String _joinCondition(MObjectEntity mObject, TableRelation tableRelation, Map<String, MTable> joinedMapping) {
        StringBuilder sb = new StringBuilder();
        sb.append(MetaDataUtils.SQL_EMPTY).append(MetaDataUtils.SQL_ON);
        MTable mMainTable = tableRelation.getMainTable();
        MTable mJoinedTable = tableRelation.getJoinedTable();
        String mappedByField = MetaDataUtils.META_FIELD_ID;
        MPropertyEntity mappedByProperty = tableRelation.getMappedByProperty();
        if (mappedByProperty != null) {
            mappedByField = MetaDataUtils.getFieldName(mappedByProperty);
        }
        sb.append(MetaDataUtils.SQL_EMPTY)
                .append(mMainTable.getAliasName()).append(MetaDataUtils.SQL_POINT).append(MetaDataUtils.getFieldName(tableRelation.getMainProperty()))
                .append(MetaDataUtils.SQL_EQ)
                .append(mJoinedTable.getAliasName()).append(MetaDataUtils.SQL_POINT).append(mappedByField);

        return sb.toString();
    }

    public void _condition(StringBuilder sb, MExpressionEntity express, Map<String, Object> paramMap,
                           boolean isMatchAll, Map<String, MTable> joinedMapping, int index) {

        if (express.getValue() != null && StringUtils.isNotBlank(express.getValue().trim())) {
            String alias = _alias(joinedMapping, express.getProperty());
            String varName = alias + MetaDataUtils.SQL_UNDERLINE + express.getProperty().getFieldName() + index;

            sb.append(leftExpress(express, isMatchAll, joinedMapping));
            sb.append(_relationExpress(express, varName, paramMap));
        }
    }

    private String leftExpress(MExpressionEntity express,
                               boolean isMatchAll, Map<String, MTable> joinedMapping) {
        StringBuilder sb = new StringBuilder();
        String alias = _alias(joinedMapping, express.getProperty());
        String relation = MetaDataUtils.SQL_AND;
        if (!isMatchAll) {
            relation = MetaDataUtils.SQL_OR;
        }
        sb.append(relation);
        StringBuilder field = new StringBuilder();
        field.append(alias)
                .append(MetaDataUtils.SQL_POINT)
                .append(MetaDataUtils.getFieldName(express.getProperty()));
        //字段启用函数
        if (express.getFunction() != null) {
            String functionCode = functionExpress(express.getFunction());

            if (StringUtils.isNotBlank(functionCode)) {
                //函数的实例：SUBSTRING(%s, 6, 10)
                sb.append(String.format(functionCode, field.toString(), field.toString()));
                return sb.toString();
            }
        }

        return sb.append(field).toString();
    }

    /**
     * 已经定义的函数映射关系
     *
     * @param mFunction
     * @return
     */
    private String functionExpress(MFunction mFunction) {

        switch (mFunction) {
            case date_year:
                return "SUBSTRING(%s, 1, 4)";
            case date_month:
                return "SUBSTRING(%s, 6, 2)";
            case date_year_month:
                return "CONCAT(SUBSTRING(%s, 1, 4) , SUBSTRING(%s, 6, 2))";
            case date_month_day:
                return "CONCAT(SUBSTRING(%s, 6, 2) , SUBSTRING(%s, 9, 2))";
        }
        return null;
    }

    /**
     * 获取别名
     *
     * @param joinedMapping
     * @param mirrorPropertyEntity
     * @return
     */
    private String _alias(Map<String, MTable> joinedMapping, MirrorPropertyEntity mirrorPropertyEntity) {
        MTable mTable = joinedMapping.get(mirrorPropertyEntity.getId());

        if (mTable != null) {
            return mTable.getAliasName();
        }
        return null;
    }

    /**
     * 生成关系表达式
     *
     * @param express
     * @param varName
     * @return
     */
    public String _relationExpress(MExpressionEntity express, String varName, Map<String, Object> paramMap) {
        StringBuilder sb = new StringBuilder();
        switch (express.getRelation()) {
            case 1:
                sb.append(">").append(MetaDataUtils.SQL_COLON).append(varName);
                break;
            case 2:
                sb.append(">=").append(MetaDataUtils.SQL_COLON).append(varName);
                break;
            case 3:
                sb.append("=").append(MetaDataUtils.SQL_COLON).append(varName);
                break;
            case 4:
                sb.append("<=").append(MetaDataUtils.SQL_COLON).append(varName);
                break;
            case 5:
                sb.append("<").append(MetaDataUtils.SQL_COLON).append(varName);
                break;
            case 6:
                sb.append("!=").append(MetaDataUtils.SQL_COLON).append(varName);
                break;
            case 10:
                sb.append(" like ").append(MetaDataUtils.SQL_COLON).append(varName);
                express.setValue("%" + express.getValue() + "%");
                break;
            case 11:
                sb.append(" not like ").append(MetaDataUtils.SQL_COLON).append(varName);
                express.setValue("%" + express.getValue() + "%");
                break;
            case 20:
                sb.append(" is null  ");
                break;
            case 21:
                sb.append(" is not null  ");
                break;
        }
        if (express.getProperty().getControllerType() == MControllerTypeEnum.bool) {
            //bool 型特殊处理
            if ("true".equalsIgnoreCase(express.getValue())) {
                paramMap.put(varName, "1");
            } else {
                paramMap.put(varName, "0");
            }
        } else {
            paramMap.put(varName, express.getValue());
        }

        return sb.toString();
    }

    /**
     * 创建数据的时候，增加系统字段：创建人、创建时间
     *
     * @param mObjectEntity
     * @param mPropertyEntityList
     * @return
     */
    public void whenCreateData(MObjectEntity mObjectEntity, List<MirrorPropertyEntity> mPropertyEntityList) {
        for (MirrorPropertyEntity propertyEntity : mPropertyEntityList) {
            if (MetaDataUtils.isSystemFields(propertyEntity.getFieldName())) {
                MetaDataUtils.fillSystemFields(propertyEntity);
            }
        }
    }

    /**
     * 修改数据的时候，增加系统字段：修改人、修改时间
     *
     * @param mObjectEntity
     * @param mPropertyEntityList
     * @return
     */
    public void whenUpdateData(MObjectEntity mObjectEntity, List<MirrorPropertyEntity> mPropertyEntityList) {
        List<MirrorPropertyEntity> list = metaDataQueryService.findMPropertyByRootMObject(mObjectEntity, 0);
        for (MirrorPropertyEntity propertyEntity : list) {
            if (MetaDataUtils.isSystemUpdateFields(propertyEntity.getFieldName())) {
                MetaDataUtils.fillUpdateSystemFields(propertyEntity);

                mPropertyEntityList.add(propertyEntity);
            }
        }
    }

    /**
     * 执行更新操作
     *
     * @param sql
     * @param paramMap
     * @return
     */
    private int _update(String sql, Map<String, Object> paramMap) {
        Query query = entityManager.createNativeQuery(sql);
        query.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);

        Iterator<String> it = paramMap.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            Object value = paramMap.get(key);
            query.setParameter(key, value);
        }
        return query.executeUpdate();
    }

    /**
     * 执行查询操作
     *
     * @param sql
     * @param paramMap
     * @return
     */
    public List<Map<String, Object>> _query(String sql, Map<String, Object> paramMap) {
        Query query = entityManager.createNativeQuery(sql);
        query.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);

        Iterator<String> it = paramMap.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            Object value = paramMap.get(key);
            query.setParameter(key, value);
        }

        List<Map<String, Object>> maps = query.getResultList();
        return maps;
    }

    public Object _queryObj(String sql, Map<String, Object> paramMap) {
        Query query = entityManager.createNativeQuery(sql);
        query.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);

        Iterator<String> it = paramMap.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            Object value = paramMap.get(key);
            query.setParameter(key, value);
        }

        return query.getSingleResult();
    }
    //--------------------------------------------------------根据元数据生成表结构---------------------------------------------

    /**
     * 执行更新表结构操作
     *
     * @param sql
     * @param paramMap
     * @return
     */
    private int _updateTable(String sql, Map<String, Object> paramMap) {
        if (!MetaDataUtils.META_GENGERATE_DDL) {
            return 0;
        }
        try {
            Query query = entityManager.createNativeQuery(sql);

            return query.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 根据元数据对象，创建表结构
     * 默认创建ID主键
     *
     * @param mObject
     * @return
     */
    public boolean _createTabel(MObjectEntity mObject) {

        StringBuilder sb = new StringBuilder();
        sb.append(" DROP TABLE IF EXISTS  ").append(mObject.getTableName()).append(";");
        _updateTable(sb.toString(), new HashMap<String, Object>());
        sb = new StringBuilder();
        sb.append(" CREATE TABLE ").append(mObject.getTableName())
                .append("(")
                .append("`id` varchar(30) NOT NULL,")
                .append("PRIMARY KEY (`id`)")
                .append(") ENGINE=InnoDB DEFAULT CHARSET=utf8;");

        _updateTable(sb.toString(), new HashMap<String, Object>());
        return true;
    }

    /**
     * 根据元数据生成字段
     *
     * @param mObject
     * @param mPropertyList
     * @return
     */
    public boolean _createFields(MObjectEntity mObject, List<MPropertyEntity> mPropertyList) {
        StringBuilder sb = new StringBuilder();
        for (MPropertyEntity mPropertyEntity : mPropertyList) {
            sb.append(_createFieldsStr(mPropertyEntity));
        }

        _updateTable(sb.toString(), new HashMap<String, Object>());
        return true;
    }

    /**
     * 根据元数据生成字段SQL
     *
     * @param mProperty
     * @return
     */
    private String _createFieldsStr(MPropertyEntity mProperty) {
        MObjectEntity mObject = mProperty.getBelongMObject();
        //ALTER TABLE comp_assess_category_item_answer ADD COLUMN real_score VARCHAR(255);
        StringBuilder sb = new StringBuilder();
        sb.append(" ALTER TABLE ").append(mObject.getTableName())
                .append(" ADD ")
                .append(" COLUMN ")
                .append(mProperty.getFieldName())
                .append(MetaDataUtils.SQL_EMPTY).append(getFieldTypeStr(mProperty))
                .append(" ;");

        return sb.toString();
    }

    /**
     * 根据元数据生成字段SQL
     *
     * @param mProperty
     * @return
     */
    public boolean _createFields(MPropertyEntity mProperty) {
        MObjectEntity mObject = mProperty.getBelongMObject();
        String token = UserHolder.getLoginUserTenantId();
        String sql = "SELECT column_name FROM information_schema.columns WHERE table_schema='mg_inst_" + token + "'" +
                " AND table_name = '" + mObject.getTableName() + "' AND column_name = '" + mProperty.getFieldName() + "'";
        List<Map<String, Object>> maps = _query(sql, new HashMap<String, Object>());
        if (maps.size() == 0) {
            StringBuilder sb = new StringBuilder();
            sb.append(" ALTER TABLE ").append(mObject.getTableName())
                    .append(" ADD ")
                    .append(" COLUMN ")
                    .append(mProperty.getFieldName())
                    .append(MetaDataUtils.SQL_EMPTY).append(getFieldTypeStr(mProperty))
                    .append(" ;");
            _updateTable(sb.toString(), new HashMap<String, Object>());
            if (MetaDataUtils.isObjectField(mProperty)
                    || mProperty.getFieldName().equals(MetaDataUtils.META_FIELD_NAME)) {
                sb = new StringBuilder();
                sb.append(" ALTER TABLE  ").append(mObject.getTableName())
                        .append(" ADD INDEX ")
                        .append(mObject.getTableName()).append("_").append(mProperty.getFieldName())
                        .append(" (").append(mProperty.getFieldName()).append(");");//.append(" (").append(mProperty.getFieldLength())

                _updateTable(sb.toString(), new HashMap<String, Object>());
            }
        }

        return true;
    }

    /**
     * 根据元数据更新字段
     *
     * @param mProperty
     * @return
     */
    public boolean _updateFields(MPropertyEntity mProperty) {
        //ALTER TABLE sys_meta_express  CHANGE COLUMN `updated_name` `updated_name` VARCHAR(500) ;
        MObjectEntity mObject = mProperty.getBelongMObject();
        _createFields(mProperty);
        StringBuilder sb = new StringBuilder();
        sb.append(" ALTER TABLE ").append(mObject.getTableName())
                .append(" CHANGE ")
                .append(" COLUMN ")
                .append(mProperty.getFieldName()).append(MetaDataUtils.SQL_EMPTY).append(mProperty.getFieldName())
                .append(MetaDataUtils.SQL_EMPTY).append(getFieldTypeStr(mProperty))
                .append(" ;");

        _updateTable(sb.toString(), new HashMap<String, Object>());
        return true;
    }

    /**
     * 根据元数据删除字段
     *
     * @param mProperty
     * @return
     */
    public boolean _dropFields(MPropertyEntity mProperty) {
        //ALTER TABLE `comp_star_req` DROP COLUMN `updated_name`;
        MObjectEntity mObject = mProperty.getBelongMObject();
        StringBuilder sb = new StringBuilder();
        sb.append(" ALTER TABLE ").append(mObject.getTableName())
                .append(" DROP ")
                .append(" COLUMN ")
                .append(mProperty.getFieldName())
                .append(" ;");

        _updateTable(sb.toString(), new HashMap<String, Object>());
        return true;
    }

    /**
     * 字段类型
     *
     * @param mProperty
     * @return
     */
    private String getFieldTypeStr(MPropertyEntity mProperty) {
        switch (mProperty.getFieldType()) {
            case TEXT:
                return "text";
            case VARCHAR:
                return "varchar(" + mProperty.getFieldLength() + ")";
            case DATE:
                return "date";
            case INTEGER:
                int defaultIntLength = 11;
                if (mProperty.getFieldLength() != null && mProperty.getFieldLength() >= 2) {
                    defaultIntLength = mProperty.getFieldLength();
                }
                return "int(" + defaultIntLength + ")";
            case LONG:
                return "long(" + mProperty.getFieldLength() + ")";
            case DOUBLE:
                return "double(" + mProperty.getFieldLength() + "," + mProperty.getFieldPrecision() + ")";
            case DECIMAL:
                return "decimal(" + mProperty.getFieldLength() + "," + mProperty.getFieldPrecision() + ")";
            case DATETIME:
                return "datetime";
            case BOOL:
                return "bool";
        }
        return "varchar(255)";
    }
}
