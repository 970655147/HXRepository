package com.hx.repository.base.sqlite;

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

import java.util.ArrayList;
import java.util.List;

/**
 * AbstractSqliteEntityJdbcRepository
 *
 * @author Jerry.X.He <970655147@qq.com>
 * @version 1.0
 * @date 2021-01-17 18:32
 */
public abstract class AbstractSqliteEntityJdbcRepository<T> extends AbstractEntityJdbcRepository<T> {

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
