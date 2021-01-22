package com.hx.repository.base.sqlite;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hx.repository.base.interf.TaskEntityJdbcRepository;
import com.hx.repository.context.task.TaskContext;
import com.hx.repository.context.task.TaskContextThreadLocal;
import com.hx.repository.model.ClassInfo;
import com.hx.repository.model.Page;
import com.hx.repository.utils.QueryMapUtils;
import com.hx.repository.utils.TaskContextUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * MainEntityJdbcRepository
 *
 * @author Jerry.X.He <970655147@qq.com>
 * @version 1.0
 * @date 2021-01-15 17:16
 */
public abstract class AbstractSqliteTaskEntityJdbcRepository<T> extends AbstractSqliteEntityJdbcRepository<T>
        implements TaskEntityJdbcRepository<T> {

    @Override
    public int add(String taskId, T entity) {
        String sql = doGenerateSqlWithTaskId(taskId, (param) -> generateInsertSql(Collections.singletonList(entity)));
        return getJdbcTemplate().update(sql);
    }

    @Override
    public int addAll(String taskId, List<T> entityList) {
        String sql = doGenerateSqlWithTaskId(taskId, (param) -> generateInsertSql(entityList));
        return getJdbcTemplate().update(sql);
    }

    @Override
    public T findById(String taskId, String id) {
        String sql = doGenerateSqlWithTaskId(taskId, (param) -> generateFindByIdSql(id));
        List<Map<String, Object>> list = getJdbcTemplate().queryForList(sql);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }

        JSONObject json = (JSONObject) JSON.toJSON(list.get(0));
        return fromJson(json);
    }

    @Override
    public List<T> allBy(String taskId, JSONObject queryMap, boolean andOr) {
        String sql = doGenerateSqlWithTaskId(taskId, (param) -> generateAllBySql(queryMap, andOr));
        return allBy0(sql);
    }

    @Override
    public Page<T> listBy(String taskId, JSONObject queryMap, boolean andOr) {
        int pageNo = QueryMapUtils.parsePageNo(queryMap);
        int pageSize = QueryMapUtils.parsePageSize(queryMap);
        int totalRecord = countBy(taskId, queryMap, andOr);

        String sql = doGenerateSqlWithTaskId(taskId, (param) -> generateListBySql(queryMap, andOr, pageNo, pageSize));
        List<T> list = allBy0(sql);
        if (CollectionUtils.isEmpty(list)) {
            return Page.empty(pageNo, pageSize);
        }
        return Page.wrap(pageNo, pageSize, totalRecord, list);
    }

    @Override
    public int countBy(String taskId, JSONObject queryMap, boolean andOr) {
        String sql = doGenerateSqlWithTaskId(taskId, (param) -> generateCountBySql(queryMap, andOr));
        String result = getJdbcTemplate().queryForObject(sql, String.class);
        return StringUtils.isNumeric(result) ? Integer.valueOf(result) : 0;
    }

    @Override
    public List<String> allDistinctBy(String taskId, String fieldName, JSONObject queryMap, boolean andOr) {
        String columnName = getClassInfo().getField(fieldName).getColumnName();
        String sql = doGenerateSqlWithTaskId(
                taskId, (param) -> generateAllDistinctBySql(columnName, queryMap, andOr));
        return getJdbcTemplate().queryForList(sql, String.class);
    }

    @Override
    public int countDistinctBy(String taskId, String fieldName, JSONObject queryMap, boolean andOr) {
        String columnName = getClassInfo().getField(fieldName).getColumnName();
        String sql = doGenerateSqlWithTaskId(
                taskId, (param) -> generateCountDistinctBySql(columnName, queryMap, andOr));
        String result = getJdbcTemplate().queryForObject(sql, String.class);
        return StringUtils.isNumeric(result) ? Integer.valueOf(result) : 0;
    }

    @Override
    public int update(String taskId, T entity) {
        String sql = doGenerateSqlWithTaskId(taskId, (param) -> generateUpdateSql(entity, false));
        return getJdbcTemplate().update(sql);
    }

    @Override
    public int updateNotNull(String taskId, T entity) {
        String sql = doGenerateSqlWithTaskId(taskId, (param) -> generateUpdateSql(entity, true));
        return getJdbcTemplate().update(sql);
    }

    @Override
    public int updateBy(String taskId, T entity, JSONObject queryMap, boolean andOr) {
        String sql = doGenerateSqlWithTaskId(taskId, (param) -> generateUpdateBySql(entity, queryMap, andOr, false));
        return getJdbcTemplate().update(sql);
    }

    @Override
    public int updateNotNullBy(String taskId, T entity, JSONObject queryMap, boolean andOr) {
        String sql = doGenerateSqlWithTaskId(taskId, (param) -> generateUpdateBySql(entity, queryMap, andOr, true));
        return getJdbcTemplate().update(sql);
    }

    @Override
    public int save(String taskId, T entity) {
        String id = getId(entity);
        boolean exists = findById(taskId, id) != null;

        if (!exists) {
            return add(taskId, entity);
        }
        return update(taskId, entity);
    }

    @Override
    public int saveNotNull(String taskId, T entity) {
        String id = getId(entity);
        boolean exists = findById(taskId, id) != null;

        if (!exists) {
            return add(taskId, entity);
        }
        return updateNotNull(taskId, entity);
    }

    @Override
    public int deleteById(String taskId, String id) {
        String sql = doGenerateSqlWithTaskId(taskId, (param) -> generateDeleteByIdSql(id));
        return getJdbcTemplate().update(sql);
    }

    @Override
    public int deleteBy(String taskId, JSONObject queryMap, boolean andOr) {
        String sql = doGenerateSqlWithTaskId(taskId, (param) -> generateDeleteBySql(queryMap, andOr));
        return getJdbcTemplate().update(sql);
    }

    @Override
    public String tableName() {
        ClassInfo classInfo = getClassInfo();
        TaskContext context = TaskContextThreadLocal.get();
        // assert context != null
        String taskId = context == null ? null : context.getTaskId();
        return String.format("\"%s\".\"%s\"", taskId, classInfo.getTableName());
    }

    /**
     * 在给定的 taskId 的上下文生成 sql
     *
     * @param taskId  taskId
     * @param sqlFunc sqlFunc
     * @return java.lang.String
     * @author Jerry.X.He
     * @date 2021-01-19 17:47
     */
    private String doGenerateSqlWithTaskId(String taskId, Function<Void, String> sqlFunc) {
        TaskContext oldContext = TaskContextThreadLocal.get();
        try {
            TaskContext newContext = TaskContextUtils.lookUp(taskId);
            TaskContextThreadLocal.set(newContext);
            return sqlFunc.apply(null);
        } finally {
            TaskContextThreadLocal.set(oldContext);
        }
    }

}
