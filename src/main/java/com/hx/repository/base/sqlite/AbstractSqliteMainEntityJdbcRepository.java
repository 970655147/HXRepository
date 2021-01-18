package com.hx.repository.base.sqlite;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hx.repository.base.interf.MainEntityJdbcRepository;
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
public abstract class AbstractSqliteMainEntityJdbcRepository<T> extends AbstractSqliteEntityJdbcRepository<T> implements MainEntityJdbcRepository<T> {

    @Override
    public int add(T entity) {
        String sql = generateInsertSql(Arrays.asList(entity));
        return getJdbcTemplate().update(sql);
    }

    @Override
    public int addAll(List<T> entityList) {
        String sql = generateInsertSql(entityList);
        return getJdbcTemplate().update(sql);
    }

    @Override
    public T findById(String id) {
        String sql = generateFindByIdSql(id);
        List<Map<String, Object>> list = getJdbcTemplate().queryForList(sql);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }

        JSONObject json = (JSONObject) JSON.toJSON(list.get(0));
        return fromJson(json);
    }

    @Override
    public int update(T entity) {
        String sql = generateUpdateSql(entity, false);
        return getJdbcTemplate().update(sql);
    }

    @Override
    public int updateNotNull(T entity) {
        String sql = generateUpdateSql(entity, true);
        return getJdbcTemplate().update(sql);
    }

    @Override
    public int save(T entity) {
        String id = getId(entity);
        boolean exists = findById(id) != null;

        if (!exists) {
            return add(entity);
        }
        return update(entity);
    }

    @Override
    public int saveNotNull(T entity) {
        String id = getId(entity);
        boolean exists = findById(id) != null;

        if (!exists) {
            return add(entity);
        }
        return updateNotNull(entity);
    }

    @Override
    public int deleteById(String id) {
        String sql = generateDeleteByIdSql(id);
        return getJdbcTemplate().update(sql);
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
        String sqlTemplate = " INSERT INTO \"%s\" %s; ";

        ClassInfo classInfo = getClassInfo();
        String fieldAndValuesSql = generateInsertSqlFragment(entityList);
        return String.format(sqlTemplate, classInfo.getTableName(), fieldAndValuesSql);
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
        String sqlTemplate = " SELECT * FROM \"%s\" %s; ";

        ClassInfo classInfo = getClassInfo();
        String whereCond = String.format(" WHERE ID = '%s' LIMIT 1 ", id);
        return String.format(sqlTemplate, classInfo.getTableName(), whereCond);
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
        String sqlTemplate = " UPDATE \"%s\" %s; ";

        ClassInfo classInfo = getClassInfo();
        String updateAndCondSql = generateUpdateSqlFragment(entity, notNull);
        return String.format(sqlTemplate, classInfo.getTableName(), updateAndCondSql);
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
        String sqlTemplate = " DELETE FROM \"%s\" %s; ";

        ClassInfo classInfo = getClassInfo();
        String whereCond = String.format(" WHERE ID = '%s' ", id);
        return String.format(sqlTemplate, classInfo.getTableName(), whereCond);
    }


}
