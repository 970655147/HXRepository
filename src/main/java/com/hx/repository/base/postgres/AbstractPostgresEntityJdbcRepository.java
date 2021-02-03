package com.hx.repository.base.postgres;

import com.alibaba.fastjson.JSONObject;
import com.hx.repository.base.interf.AbstractEntityJdbcRepository;
import com.hx.repository.consts.FieldOperator;
import com.hx.repository.consts.SqlConstants;
import com.hx.repository.domain.BaseEntity;
import com.hx.repository.model.ClassInfo;
import com.hx.repository.model.FieldCondition;
import com.hx.repository.model.FieldInfo;
import com.hx.repository.utils.FieldInfoUtils;
import com.hx.repository.utils.FieldOperatorUtils;
import com.hx.repository.utils.QueryMapUtils;
import com.hx.repository.utils.TypeCastUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.*;

import static com.hx.repository.consts.SqlConstants.COLUMN_ID;
import static com.hx.repository.consts.SqlConstants.COLUMN_VERSION;

/**
 * AbstractPostgresEntityJdbcRepository
 *
 * @author Jerry.X.He
 * @version 1.0
 * @date 2021-02-02 19:10
 */
public abstract class AbstractPostgresEntityJdbcRepository<T> extends AbstractEntityJdbcRepository<T> {

    /**
     * 获取 当前实体对应的 tableName
     *
     * @return
     * @author Jerry.X.He
     * @date 2021-01-19 17:27
     */
    public abstract String tableName();

    // -------------------------------------- 通用工具方法 --------------------------------------

    /**
     * 执行给定的查询 返回给定的结果列表
     *
     * @param sql sql
     * @return java.util.List<T>
     * @author Jerry.X.He
     * @date 2021-01-19 17:56
     */
    protected List<T> allBy0(String sql) {
        List<Map<String, Object>> list = getJdbcTemplate().queryForList(sql);
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }

        List<T> result = new ArrayList<>();
        for (Map<String, Object> map : list) {
            result.add(fromJson(TypeCastUtils.castMap2JsonWithCamel(map)));
        }
        return result;
    }

    /**
     * 持久化之前 需要处理的业务
     *
     * @param entityList entityList
     * @return java.util.List<T>
     * @author Jerry.X.He
     * @date 2021-01-24 15:15
     */
    protected void prePersist(List<T> entityList) {
        for (T entity : entityList) {
            if (isBaseEntity()) {
                ((BaseEntity) entity).prePersist();
            }
        }
    }

    /**
     * 持久化之前 需要处理的业务
     *
     * @param entity entity
     * @return java.util.List<T>
     * @author Jerry.X.He
     * @date 2021-01-24 15:15
     */
    protected void prePersist(T entity) {
        if (isBaseEntity()) {
            ((BaseEntity) entity).prePersist();
        }
    }

    /**
     * 更新实体之前 需要处理的业务
     *
     * @param entity entity
     * @return java.util.List<T>
     * @author Jerry.X.He
     * @date 2021-01-24 15:15
     */
    protected void preUpdate(T entity) {
        if (isBaseEntity()) {
            ((BaseEntity) entity).preUpdate();
        }
    }

    // -------------------------------------- 间接工具方法 --------------------------------------

    /**
     * 获取插入给定的实体的 插入语句
     *
     * @param entityList entityList
     * @return java.lang.String
     * @author Jerry.X.He
     * @date 2021-01-17 11:11
     */
    protected String generateInsertSql(List<T> entityList) {
        String sqlTemplate = " INSERT INTO %s %s; ";
        String fieldAndValuesSql = generateInsertSqlFragment(entityList);
        return String.format(sqlTemplate, tableName(), fieldAndValuesSql);
    }

    /**
     * 生成根据 id 查询的 sql
     *
     * @param id id
     * @return java.lang.String
     * @author Jerry.X.He
     * @date 2021-01-17 19:21
     */
    protected String generateFindByIdSql(String id) {
        String sqlTemplate = " SELECT * FROM %s %s; ";
        String whereCond = String.format(" WHERE %s = '%s' LIMIT 1 ", COLUMN_ID, id);
        return String.format(sqlTemplate, tableName(), whereCond);
    }

    /**
     * 生成 allBy 的查询 sql
     *
     * @param queryMap queryMap
     * @param andOr    andOr
     * @return java.lang.String
     * @author Jerry.X.He
     * @date 2021-01-19 15:51
     */
    protected String generateAllBySql(JSONObject queryMap, boolean andOr) {
        return generateAllBySql0(queryMap, andOr, "*");
    }

    protected String generateCountBySql(JSONObject queryMap, boolean andOr) {
        return generateAllBySql0(queryMap, andOr, "COUNT(*)");
    }

    /**
     * 生成 allDistinctBy 的查询 sql
     *
     * @param queryMap queryMap
     * @param andOr    andOr
     * @return java.lang.String
     * @author Jerry.X.He
     * @date 2021-01-22 16:16
     */
    protected String generateAllDistinctBySql(String columnName, JSONObject queryMap, boolean andOr) {
        String queryFieldList = String.format("DISTINCT(%s)", columnName);
        return generateAllBySql0(queryMap, andOr, queryFieldList);
    }

    protected String generateCountDistinctBySql(String columnName, JSONObject queryMap, boolean andOr) {
        String queryFieldList = String.format("COUNT(DISTINCT(%s))", columnName);
        return generateAllBySql0(queryMap, andOr, queryFieldList);
    }

    /**
     * 生成查询给定的字段列表, 根据给定的 queryMap 构造查询条件的 sql
     *
     * @param queryMap       queryMap
     * @param andOr          andOr
     * @param queryFieldList queryFieldList
     * @return java.lang.String
     * @author Jerry.X.He
     * @date 2021-01-19 15:53
     */
    protected String generateAllBySql0(JSONObject queryMap, boolean andOr, String queryFieldList) {
        String sqlTemplate = " SELECT %s FROM %s %s; ";
        String whereCondAndOrderBy = generateWhereCondAndOrderBy(queryMap, andOr);
        return String.format(sqlTemplate, queryFieldList, tableName(), whereCondAndOrderBy);
    }

    /**
     * 生成查询给定的字段列表, 根据给定的 queryMap 构造查询条件的分页查询 sql
     *
     * @param queryMap queryMap
     * @param andOr    andOr
     * @param pageNo   pageNo
     * @param pageSize pageSize
     * @return java.lang.String
     * @author Jerry.X.He
     * @date 2021-01-19 16:24
     */
    protected String generateListBySql(JSONObject queryMap, boolean andOr, int pageNo, int pageSize) {
        String sqlTemplate = " SELECT * FROM %s %s LIMIT %s OFFSET %s ; ";
        String whereCondAndOrderBy = generateWhereCondAndOrderBy(queryMap, andOr);
        int pageOffset = (pageNo - 1) * pageSize;
        return String.format(sqlTemplate, tableName(), whereCondAndOrderBy, pageSize, pageOffset);
    }

    /**
     * 获取更新给定的实体的 更新语句
     *
     * @param entity entity
     * @return java.lang.String
     * @author Jerry.X.He
     * @date 2021-01-17 18:59
     */
    protected String generateUpdateSql(T entity, boolean notNull) {
        String sqlTemplate = " UPDATE %s SET %s %s; ";
        String updateFragment = generateUpdateSqlFragment(entity, notNull);

        String id = getId(entity);
        String whereCond = String.format(" WHERE %s = '%s' ", COLUMN_ID, id);
        // 如果是 BaseEntity, 带上 version
        if (isBaseEntity()) {
            long version = ((BaseEntity) entity).getVersion();
            whereCond += String.format(" AND %s = %s ", COLUMN_VERSION, version);
        }
        return String.format(sqlTemplate, tableName(), updateFragment, whereCond);
    }

    /**
     * 获取更新给定的实体的 根据 queryMap 更新语句
     *
     * @param entity  entity
     * @param notNull notNull
     * @return java.lang.String
     * @author Jerry.X.He
     * @date 2021-01-19 16:50
     */
    protected String generateUpdateBySql(T entity, JSONObject queryMap, boolean andOr, boolean notNull) {
        String sqlTemplate = " UPDATE %s SET %s %s; ";
        String updateFragment = generateUpdateSqlFragment(entity, notNull);
        String whereCondFragment = generateWhereCond(queryMap, andOr);
        String whereCond = SqlConstants.EMPTY_STR;
        if (StringUtils.isNotBlank(whereCondFragment)) {
            whereCond = String.format(" WHERE %s ", whereCondFragment);
        }

        return String.format(sqlTemplate, tableName(), updateFragment, whereCond);
    }

    /**
     * 生成根据 id 删除的 sql
     *
     * @param id id
     * @return java.lang.String
     * @author Jerry.X.He
     * @date 2021-01-17 19:21
     */
    protected String generateDeleteByIdSql(String id) {
        String sqlTemplate = " DELETE FROM %s %s; ";
        String whereCond = String.format(" WHERE %s = '%s' ", COLUMN_ID, id);
        return String.format(sqlTemplate, tableName(), whereCond);
    }

    /**
     * 生成根据 queryMap 删除的 sql
     *
     * @param queryMap queryMap
     * @param andOr    andOr
     * @return java.lang.String
     * @author Jerry.X.He
     * @date 2021-01-19 17:00
     */
    protected String generateDeleteBySql(JSONObject queryMap, boolean andOr) {
        String sqlTemplate = " DELETE FROM %s %s; ";
        String whereCond = SqlConstants.EMPTY_STR;
        String whereCondFragment = generateWhereCond(queryMap, andOr);
        if (StringUtils.isNotBlank(whereCondFragment)) {
            whereCond = String.format(" WHERE %s ", whereCondFragment);
        }
        return String.format(sqlTemplate, tableName(), whereCond);
    }

    // -------------------------------------- 更细节封装 sql 相关 --------------------------------------

    /**
     * 生成插入语句的 sql 片段
     *
     * @param entityList entityList
     * @return java.lang.String
     * @author Jerry.X.He
     * @date 2021-01-17 10:42
     */
    protected String generateInsertSqlFragment(List<T> entityList) {
        ClassInfo classInfo = getClassInfo();
        List<FieldInfo> allFieldList = classInfo.allFieldInfo();

        List<String> fieldList = new ArrayList<>();
        for (FieldInfo field : allFieldList) {
            String fieldNameInDb = field.getColumnName();
            fieldList.add(fieldNameInDb);
        }
        String fieldColumnsSql = StringUtils.join(fieldList, ", ");

        List<String> fieldValueList = new ArrayList<>();
        for (T entity : entityList) {
            JSONObject entityJson = toJson(entity);
            List<String> valueList = new ArrayList<>();
            for (FieldInfo field : allFieldList) {
                // 如果不可在 INSERT, 过滤掉
                if (!field.isInsertable()) {
                    continue;
                }

                Object fieldValueObj = entityJson.get(field.getFieldName());
                String fieldValue = wrapFieldValueSql(fieldValueObj, field);
                valueList.add(fieldValue);
            }
            String oneEntityValues = StringUtils.join(valueList, ", ");
            fieldValueList.add("(" + oneEntityValues + ")");
        }
        String fieldValuesSql = StringUtils.join(fieldValueList, ", ");

        String sqlTemplate = " (%s) VALUES %s";
        return String.format(sqlTemplate, fieldColumnsSql, fieldValuesSql);
    }

    /**
     * 生成 where & orderBy
     *
     * @param queryMap queryMap
     * @param andOr    andOr
     * @return java.lang.String
     * @author Jerry.X.He
     * @date 2021-01-22 17:48
     */
    protected String generateWhereCondAndOrderBy(JSONObject queryMap, boolean andOr) {
        String whereCondFragment = generateWhereCond(queryMap, andOr);
        String orderByFragment = generateOrderBy(queryMap);

        String whereCond = SqlConstants.EMPTY_STR;
        if (StringUtils.isNotBlank(whereCondFragment)) {
            whereCond = (" WHERE " + whereCondFragment);
        }

        String orderBy = SqlConstants.EMPTY_STR;
        if (StringUtils.isNotBlank(orderByFragment)) {
            orderBy = (" ORDER BY " + orderByFragment);
        }

        String sqlTemplate = "%s %s";
        return String.format(sqlTemplate, whereCond, orderBy);
    }

    /**
     * 根据给定的 queryMap 封装查询语句
     *
     * @param queryMap queryMap
     * @param andOr    andOr
     * @return java.lang.String
     * @author Jerry.X.He
     * @date 2021-01-19 14:57
     */
    protected String generateWhereCond(JSONObject queryMap, boolean andOr) {
        List<FieldCondition> fieldConditions = parseFieldConditions(queryMap);
        List<String> fieldConditionList = new ArrayList<>();
        String condTemplate = " %s %s %s ";
        for (FieldCondition cond : fieldConditions) {
            FieldOperator queryOperator = cond.getOperator();
            String queryOperatorSql = FieldOperatorUtils.getSqliteOperatorSql(queryOperator);
            String queryKey = cond.getColumnName();
            String fieldValue = wrapWhereFieldValueSql(cond.getValue(), queryOperator, cond.getFieldInfo());
            fieldConditionList.add(String.format(condTemplate, queryKey, queryOperatorSql, fieldValue));
        }

        String joinSep = andOr ? SqlConstants.OPERATOR_AND : SqlConstants.OPERATOR_OR;
        return StringUtils.join(fieldConditionList, joinSep);
    }

    /**
     * 根据给定的 queryMap 封装 orderBy 语句
     *
     * @param queryMap queryMap
     * @return java.lang.String
     * @author Jerry.X.He
     * @date 2021-01-22 17:20
     */
    protected String generateOrderBy(JSONObject queryMap) {
        List<FieldCondition> fieldOrderBys = parseOrderBys(queryMap);
        List<String> fieldConditionList = new ArrayList<>();
        String condTemplate = " %s %s ";
        for (FieldCondition cond : fieldOrderBys) {
            String orderBy = cond.getColumnName();
            String ascOrDesc = (Boolean) cond.getValue() ? SqlConstants.SORT_ASC : SqlConstants.SORT_DESC;
            fieldConditionList.add(String.format(condTemplate, orderBy, ascOrDesc));
        }

        String joinSep = SqlConstants.COMMA;
        return StringUtils.join(fieldConditionList, joinSep);
    }

    /**
     * 查询符合 queryMap 的条件列表
     *
     * @param queryMap queryMap
     * @return java.util.List<com.hx.repository.model.FieldCondition>
     * @author Jerry.X.He
     * @date 2021-01-19 15:19
     */
    protected List<FieldCondition> parseFieldConditions(JSONObject queryMap) {
        ClassInfo classInfo = getClassInfo();
        List<FieldInfo> allFieldList = classInfo.allFieldInfo();

        List<FieldCondition> result = new ArrayList<>();
        for (String key : queryMap.keySet()) {
            String queryField = QueryMapUtils.parseQueryField(key);
            FieldInfo fieldInfo = FieldInfoUtils.lookUpByFieldName(allFieldList, queryField);
            // unknown field, skip
            if (fieldInfo == null) {
                continue;
            }
            Object value = queryMap.get(key);
            // null value, skip
            if (value == null) {
                continue;
            }

            FieldOperator queryOperator = QueryMapUtils.parseQueryOperator(key);
            result.add(new FieldCondition(fieldInfo.getColumnName(), queryOperator, value, fieldInfo));
        }
        return result;
    }

    /**
     * 解析给定的 queryMap 里面的 orderBy
     *
     * @param queryMap queryMap
     * @return java.util.List<com.hx.repository.model.FieldCondition>
     * @author Jerry.X.He
     * @date 2021-01-22 17:24
     */
    protected List<FieldCondition> parseOrderBys(JSONObject queryMap) {
        ClassInfo classInfo = getClassInfo();
        List<FieldInfo> allFieldList = classInfo.allFieldInfo();

        List<FieldCondition> result = new ArrayList<>();
        for (String key : queryMap.keySet()) {
            // 如果不是 OrderBy 配置
            String orderByField = QueryMapUtils.parseOrderByField(key);
            if (StringUtils.isBlank(orderByField)) {
                continue;
            }

            FieldInfo fieldInfo = FieldInfoUtils.lookUpByFieldName(allFieldList, orderByField);
            // unknown field, skip
            if (fieldInfo == null) {
                continue;
            }

            String valueStr = String.valueOf(queryMap.get(key));
            boolean ascOrDesc = Boolean.valueOf(valueStr);
            result.add(new FieldCondition(fieldInfo.getColumnName(), FieldOperator.EQ, ascOrDesc, fieldInfo));
        }
        return result;
    }

    /**
     * 生成个更新语句的 sql
     *
     * @param entity entity
     * @return java.lang.String
     * @author Jerry.X.He
     * @date 2021-01-17 18:59
     */
    protected String generateUpdateSqlFragment(T entity, boolean notNull) {
        ClassInfo classInfo = getClassInfo();
        List<FieldInfo> allFieldList = classInfo.allFieldInfo();

        JSONObject entityJson = notNull ? toNotNullJson(entity) : toJson(entity);
        List<String> setValueList = new ArrayList<>();
        for (FieldInfo field : allFieldList) {
            String columnName = field.getColumnName();
            // 如果不可在 UPDATE, 过滤掉
            if (!field.isUpdatable()) {
                continue;
            }
            // 如果是数值 增量更新 的字段, 直接处理
            if (field.isUpdateIncr()) {
                String fieldUpdated = String.format(" (%s + %s) ", columnName, field.getUpdateIncrOffset());
                String fieldValueUpdate = String.format(" %s = %s ", columnName, fieldUpdated);
                setValueList.add(fieldValueUpdate);
                continue;
            }

            Object fieldValueObj = entityJson.get(field.getFieldName());
            // 如果是 updateNotNull, 并且 value 为 null, 直接跳过
            if (notNull && fieldValueObj == null) {
                continue;
            }

            String fieldValue = wrapFieldValueSql(fieldValueObj, field);
            String fieldValueUpdate = String.format(" %s = %s ", columnName, fieldValue);
            setValueList.add(fieldValueUpdate);
        }

        return StringUtils.join(setValueList, ", ");
    }

    /**
     * 封装给定的 fieldValue
     *
     * @param fieldValueObj fieldValueObj
     * @return java.lang.String
     * @author Jerry.X.He
     * @date 2021-01-19 15:21
     */
    protected String wrapFieldValueSql(Object fieldValueObj, FieldInfo fieldInfo) {
        return (fieldValueObj == null) ? "null"
                : String.format("'%s'", String.valueOf(fieldValueObj));
    }

    /**
     * 封装给定的 where 条件下面的 fieldValue
     *
     * @param fieldValueObj fieldValueObj
     * @param fieldInfo     fieldInfo
     * @return java.lang.String
     * @author Jerry.X.He
     * @date 2021-02-02 15:56
     */
    protected String wrapWhereFieldValueSql(Object fieldValueObj, FieldOperator queryOperator, FieldInfo fieldInfo) {
        // 如果是 in/notIn
        if (queryOperator == FieldOperator.IN || queryOperator == FieldOperator.NOT_IN) {
            if (fieldValueObj instanceof Collection) {
                Iterator<String> ite = ((Collection) fieldValueObj).iterator();
                List<String> itemList = new ArrayList<>();
                while (ite.hasNext()) {
                    String item = wrapFieldValueSql(ite.next(), fieldInfo);
                    itemList.add(item);
                }
                String itemListSql = StringUtils.join(itemList, ", ");
                return String.format("(%s)", itemListSql);
            }
        }
        if (queryOperator == FieldOperator.LIKE || queryOperator == FieldOperator.NOT_LIKE) {
            return (fieldValueObj == null) ? "null"
                    : String.format("'%%%s%%'", String.valueOf(fieldValueObj));
        }

        return wrapFieldValueSql(fieldValueObj, fieldInfo);
    }

}
