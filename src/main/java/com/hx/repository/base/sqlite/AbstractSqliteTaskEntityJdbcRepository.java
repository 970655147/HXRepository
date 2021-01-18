package com.hx.repository.base.sqlite;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hx.repository.base.interf.TaskEntityJdbcRepository;
import com.hx.repository.model.ClassInfo;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * MainEntityJdbcRepository
 *
 * @author Jerry.X.He <970655147@qq.com>
 * @version 1.0
 * @date 2021-01-15 17:16
 */
public abstract class AbstractSqliteTaskEntityJdbcRepository<T> extends AbstractSqliteEntityJdbcRepository<T> implements TaskEntityJdbcRepository<T> {

    @Override
    public int add(String taskId, T entity) {
        String sql = generateInsertSql(taskId, Arrays.asList(entity));
        return getJdbcTemplate().update(sql);
    }

    @Override
    public int addAll(String taskId, List<T> entityList) {
        String sql = generateInsertSql(taskId, entityList);
        return getJdbcTemplate().update(sql);
    }

    @Override
    public T findById(String taskId, String id) {
        String sql = generateFindByIdSql(taskId, id);
        List<Map<String, Object>> list = getJdbcTemplate().queryForList(sql);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }

        JSONObject json = (JSONObject) JSON.toJSON(list.get(0));
        return fromJson(json);
    }

    @Override
    public int update(String taskId, T entity) {
        String sql = generateUpdateSql(taskId, entity, false);
        return getJdbcTemplate().update(sql);
    }

    @Override
    public int updateNotNull(String taskId, T entity) {
        String sql = generateUpdateSql(taskId, entity, true);
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
        String sql = generateDeleteByIdSql(taskId, id);
        return getJdbcTemplate().update(sql);
    }

    // -------------------------------------- 间接工具方法 --------------------------------------

    /**
     * 获取插入给定的实体的 插入语句
     *
     * @param taskId     taskId
     * @param entityList entityList
     * @return java.lang.String
     * @author Jerry.X.He
     * @date 2021-01-17 11:11
     */
    protected String generateInsertSql(String taskId, List<T> entityList) {
        String sqlTemplate = " INSERT INTO \"%s\".\"%s\" %s; ";

        ClassInfo classInfo = getClassInfo();
        String fieldAndValuesSql = generateInsertSqlFragment(entityList);
        return String.format(sqlTemplate, taskId, classInfo.getTableName(), fieldAndValuesSql);
    }

    /**
     * 生成根据 id 查询的 sql
     *
     * @param id id
     * @return java.lang.String
     * @author Jerry.X.He
     * @date 2021-01-17 19:21
     */
    protected String generateFindByIdSql(String taskId, String id) {
        String sqlTemplate = " SELECT * FROM \"%s\".\"%s\" %s; ";

        ClassInfo classInfo = getClassInfo();
        String whereCond = String.format(" ID = '%s' LIMIT 1 ", id);
        return String.format(sqlTemplate, taskId, classInfo.getTableName(), whereCond);
    }

    /**
     * 获取更新给定的实体的 更新语句
     *
     * @param taskId taskId
     * @param entity entity
     * @return java.lang.String
     * @author Jerry.X.He
     * @date 2021-01-17 18:59
     */
    protected String generateUpdateSql(String taskId, T entity, boolean notNull) {
        String sqlTemplate = " UPDATE \"%s\".\"%s\" %s; ";

        ClassInfo classInfo = getClassInfo();
        String updateAndCondSql = generateUpdateSqlFragment(entity, notNull);
        return String.format(sqlTemplate, taskId, classInfo.getTableName(), updateAndCondSql);
    }

    /**
     * 生成根据 id 删除的 sql
     *
     * @param id id
     * @return java.lang.String
     * @author Jerry.X.He
     * @date 2021-01-17 19:21
     */
    protected String generateDeleteByIdSql(String taskId, String id) {
        String sqlTemplate = " DELETE FROM \"%s\".\"%s\" %s; ";

        ClassInfo classInfo = getClassInfo();
        String whereCond = String.format(" ID = '%s' ", id);
        return String.format(sqlTemplate, taskId, classInfo.getTableName(), whereCond);
    }


}
