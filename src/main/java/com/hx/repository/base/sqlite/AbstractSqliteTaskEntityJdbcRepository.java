package com.hx.repository.base.sqlite;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hx.repository.base.interf.TaskEntityJdbcRepository;
import com.hx.repository.consts.SqlConstants;
import com.hx.repository.model.ClassInfo;
import com.hx.repository.model.Page;
import com.hx.repository.utils.QueryMapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.*;

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
    public List<T> allBy(String taskId, JSONObject queryMap, boolean andOr) {
        String sql = generateAllBySql(taskId, queryMap, andOr);
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

    @Override
    public Page<T> listBy(String taskId, JSONObject queryMap, boolean andOr) {
        int pageNo = QueryMapUtils.getPageNo(queryMap);
        int pageSize = QueryMapUtils.getPageSize(queryMap);
        int totalRecord = countBy(taskId, queryMap, andOr);

        String sql = generateListBySql(taskId, queryMap, andOr, pageNo, pageSize);
        List<Map<String, Object>> list = getJdbcTemplate().queryForList(sql);
        if (CollectionUtils.isEmpty(list)) {
            return Page.empty(pageNo, pageSize);
        }

        List<T> result = new ArrayList<>();
        for (Map<String, Object> json : list) {
            result.add(fromJson((JSONObject) JSON.toJSON(json)));
        }
        return Page.wrap(pageNo, pageSize, totalRecord, result);
    }

    @Override
    public int countBy(String taskId, JSONObject queryMap, boolean andOr) {
        String sql = generateCountBySql(taskId, queryMap, andOr);
        return getJdbcTemplate().queryForObject(sql, Integer.class);
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
    public int updateBy(String taskId, T entity, JSONObject queryMap, boolean andOr) {
        String sql = generateUpdateBySql(taskId, entity, queryMap, andOr, false);
        return getJdbcTemplate().update(sql);
    }

    @Override
    public int updateNotNullBy(String taskId, T entity, JSONObject queryMap, boolean andOr) {
        String sql = generateUpdateBySql(taskId, entity, queryMap, andOr, true);
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

    @Override
    public int deleteBy(String taskId, JSONObject queryMap, boolean andOr) {
        String sql = generateDeleteBySql(taskId, queryMap, andOr);
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
     * 生成 allBy 的查询 sql
     *
     * @param queryMap queryMap
     * @param andOr    andOr
     * @return java.lang.String
     * @author Jerry.X.He
     * @date 2021-01-19 15:51
     */
    protected String generateAllBySql(String taskId, JSONObject queryMap, boolean andOr) {
        return generateAllBySql0(taskId, queryMap, andOr, "*");
    }

    protected String generateCountBySql(String taskId, JSONObject queryMap, boolean andOr) {
        return generateAllBySql0(taskId, queryMap, andOr, "COUNT(*)");
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
    protected String generateAllBySql0(String taskId, JSONObject queryMap, boolean andOr, String queryFieldList) {
        String sqlTemplate = " SELECT %s FROM \"%s\".\"%s\" %s; ";

        ClassInfo classInfo = getClassInfo();
        String whereCondFragment = generateWhereCond(queryMap, andOr);
        String whereCond = SqlConstants.EMPTY_STR;
        if (StringUtils.isNotBlank(whereCondFragment)) {
            whereCond = String.format(" WHERE %s ", whereCondFragment);
        }
        return String.format(sqlTemplate, queryFieldList, taskId, classInfo.getTableName(), whereCond);
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
    protected String generateListBySql(String taskId, JSONObject queryMap, boolean andOr, int pageNo, int pageSize) {
        String sqlTemplate = " SELECT * FROM \"%s\".\"%s\" %s LIMIT %s OFFSET %s ; ";

        ClassInfo classInfo = getClassInfo();
        String whereCondFragment = generateWhereCond(queryMap, andOr);
        String whereCond = SqlConstants.EMPTY_STR;
        if (StringUtils.isNotBlank(whereCondFragment)) {
            whereCond = String.format(" WHERE %s ", whereCondFragment);
        }

        int pageOffset = (pageNo - 1) * pageSize;
        return String.format(sqlTemplate, taskId, classInfo.getTableName(), whereCond, pageSize, pageOffset);
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
        String sqlTemplate = " UPDATE \"%s\".\"%s\" %s %s; ";

        ClassInfo classInfo = getClassInfo();
        String updateFragment = generateUpdateSqlFragment(entity, notNull);
        String id = getId(entity);
        String whereCond = String.format(" WHERE ID = '%s' ", id);
        return String.format(sqlTemplate, taskId, classInfo.getTableName(), updateFragment, whereCond);
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
    protected String generateUpdateBySql(String taskId, T entity, JSONObject queryMap, boolean andOr, boolean notNull) {
        String sqlTemplate = " UPDATE \"%s\".\"%s\" %s %s; ";

        ClassInfo classInfo = getClassInfo();
        String updateFragment = generateUpdateSqlFragment(entity, notNull);
        String whereCondFragment = generateWhereCond(queryMap, andOr);
        String whereCond = SqlConstants.EMPTY_STR;
        if (StringUtils.isNotBlank(whereCondFragment)) {
            whereCond = String.format(" WHERE %s ", whereCondFragment);
        }

        return String.format(sqlTemplate, taskId, classInfo.getTableName(), updateFragment, whereCond);
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

    /**
     * 生成根据 queryMap 删除的 sql
     *
     * @param queryMap queryMap
     * @param andOr    andOr
     * @return java.lang.String
     * @author Jerry.X.He
     * @date 2021-01-19 17:00
     */
    protected String generateDeleteBySql(String taskId, JSONObject queryMap, boolean andOr) {
        String sqlTemplate = " DELETE FROM \"%s\".\"%s\" %s; ";

        ClassInfo classInfo = getClassInfo();
        String whereCond = SqlConstants.EMPTY_STR;
        String whereCondFragment = generateWhereCond(queryMap, andOr);
        if (StringUtils.isNotBlank(whereCondFragment)) {
            whereCond = String.format(" WHERE %s ", whereCondFragment);
        }
        return String.format(sqlTemplate, taskId, classInfo.getTableName(), whereCond);
    }

}
