package com.hx.repository.base.sqlite;

import com.alibaba.fastjson.JSONObject;
import com.hx.repository.base.interf.AbstractEntityJdbcRepository;
import com.hx.repository.model.ClassInfo;
import com.hx.repository.model.FieldInfo;
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
                String fieldValue = (fieldValueObj == null)
                        ? "null" : String.format("'%s'", String.valueOf(fieldValueObj));
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

            String fieldValue = (fieldValueObj == null)
                    ? String.format(" %s = null", columnName)
                    : String.format("%s = '%s'", columnName, String.valueOf(fieldValueObj));
            setValueList.add(fieldValue);
        }
        String setValuesSql = StringUtils.join(setValueList, ", ");

        String sqlTemplate = " SET %s WHERE ID = '%s' ";
        String id = getId(entity);
        return String.format(sqlTemplate, setValuesSql, id);
    }


}
