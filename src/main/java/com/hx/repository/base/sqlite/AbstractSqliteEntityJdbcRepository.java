package com.hx.repository.base.sqlite;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hx.repository.base.interf.AbstractEntityJdbcRepository;
import com.hx.repository.consts.FieldOperator;
import com.hx.repository.consts.SqlConstants;
import com.hx.repository.model.ClassInfo;
import com.hx.repository.model.FieldCondition;
import com.hx.repository.model.FieldInfo;
import com.hx.repository.utils.FieldInfoUtils;
import com.hx.repository.utils.FieldOperatorUtils;
import com.hx.repository.utils.QueryMapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * AbstractSqliteEntityJdbcRepository
 *
 * @author Jerry.X.He <970655147@qq.com>
 * @version 1.0
 * @date 2021-01-17 18:32
 */
public abstract class AbstractSqliteEntityJdbcRepository<T> extends AbstractEntityJdbcRepository<T> {

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
        for (Map<String, Object> json : list) {
            result.add(fromJson((JSONObject) JSON.toJSON(json)));
        }
        return result;
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

        String whereCond = String.format(" WHERE ID = '%s' LIMIT 1 ", id);
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

        String whereCondFragment = generateWhereCond(queryMap, andOr);
        String whereCond = SqlConstants.EMPTY_STR;
        if (StringUtils.isNotBlank(whereCondFragment)) {
            whereCond = String.format(" WHERE %s ", whereCondFragment);
        }
        return String.format(sqlTemplate, queryFieldList, tableName(), whereCond);
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

        String whereCondFragment = generateWhereCond(queryMap, andOr);
        String whereCond = SqlConstants.EMPTY_STR;
        if (StringUtils.isNotBlank(whereCondFragment)) {
            whereCond = String.format(" WHERE %s ", whereCondFragment);
        }

        int pageOffset = (pageNo - 1) * pageSize;
        return String.format(sqlTemplate, tableName(), whereCond, pageSize, pageOffset);
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
        String sqlTemplate = " UPDATE SET %s %s; ";

        String updateFragment = generateUpdateSqlFragment(entity, notNull);
        String id = getId(entity);
        String whereCond = String.format(" WHERE ID = '%s' ", id);
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
        String sqlTemplate = " UPDATE %s %s %s; ";

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

        String whereCond = String.format(" WHERE ID = '%s' ", id);
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
            String fieldValue = wrapFieldValueSql(cond.getValue(), cond.getFieldInfo());
            fieldConditionList.add(String.format(condTemplate, queryKey, queryOperatorSql, fieldValue));
        }

        String joinSep = andOr ? SqlConstants.OPERATOR_AND : SqlConstants.OPERATOR_OR;
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

            FieldOperator queryOperator = QueryMapUtils.parseQueryOperator(key);
            Object value = queryMap.get(queryField);
            result.add(new FieldCondition(queryField, queryOperator, value, fieldInfo));
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
            Object fieldValueObj = entityJson.get(field.getFieldName());
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

}
