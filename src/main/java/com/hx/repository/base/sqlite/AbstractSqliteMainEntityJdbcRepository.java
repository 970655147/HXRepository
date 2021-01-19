package com.hx.repository.base.sqlite;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hx.repository.base.interf.MainEntityJdbcRepository;
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
public abstract class AbstractSqliteMainEntityJdbcRepository<T> extends AbstractSqliteEntityJdbcRepository<T>
        implements MainEntityJdbcRepository<T> {

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
    public List<T> allBy(JSONObject queryMap, boolean andOr) {
        String sql = generateAllBySql(queryMap, andOr);
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
    public Page<T> listBy(JSONObject queryMap, boolean andOr) {
        int pageNo = QueryMapUtils.getPageNo(queryMap);
        int pageSize = QueryMapUtils.getPageSize(queryMap);
        int totalRecord = countBy(queryMap, andOr);

        String sql = generateListBySql(queryMap, andOr, pageNo, pageSize);
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
    public int countBy(JSONObject queryMap, boolean andOr) {
        String sql = generateCountBySql(queryMap, andOr);
        return getJdbcTemplate().queryForObject(sql, Integer.class);
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
    public int updateBy(T entity, JSONObject queryMap, boolean andOr) {
        String sql = generateUpdateBySql(entity, queryMap, andOr, false);
        return getJdbcTemplate().update(sql);
    }

    @Override
    public int updateNotNullBy(T entity, JSONObject queryMap, boolean andOr) {
        String sql = generateUpdateBySql(entity, queryMap, andOr, true);
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

    @Override
    public int deleteBy(JSONObject queryMap, boolean andOr) {
        String sql = generateDeleteBySql(queryMap, andOr);
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
        String sqlTemplate = " SELECT %s FROM \"%s\" %s; ";

        ClassInfo classInfo = getClassInfo();
        String whereCondFragment = generateWhereCond(queryMap, andOr);
        String whereCond = SqlConstants.EMPTY_STR;
        if (StringUtils.isNotBlank(whereCondFragment)) {
            whereCond = String.format(" WHERE %s ", whereCondFragment);
        }
        return String.format(sqlTemplate, queryFieldList, classInfo.getTableName(), whereCond);
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
        String sqlTemplate = " SELECT * FROM \"%s\" %s LIMIT %s OFFSET %s ; ";

        ClassInfo classInfo = getClassInfo();
        String whereCondFragment = generateWhereCond(queryMap, andOr);
        String whereCond = SqlConstants.EMPTY_STR;
        if (StringUtils.isNotBlank(whereCondFragment)) {
            whereCond = String.format(" WHERE %s ", whereCondFragment);
        }

        int pageOffset = (pageNo - 1) * pageSize;
        return String.format(sqlTemplate, classInfo.getTableName(), whereCond, pageSize, pageOffset);
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
        String sqlTemplate = " UPDATE SET \"%s\" %s; ";

        ClassInfo classInfo = getClassInfo();
        String updateFragment = generateUpdateSqlFragment(entity, notNull);
        String id = getId(entity);
        String whereCond = String.format(" WHERE ID = '%s' ", id);
        return String.format(sqlTemplate, classInfo.getTableName(), updateFragment, whereCond);
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
        String sqlTemplate = " UPDATE \"%s\" %s %s; ";

        ClassInfo classInfo = getClassInfo();
        String updateFragment = generateUpdateSqlFragment(entity, notNull);
        String whereCondFragment = generateWhereCond(queryMap, andOr);
        String whereCond = SqlConstants.EMPTY_STR;
        if (StringUtils.isNotBlank(whereCondFragment)) {
            whereCond = String.format(" WHERE %s ", whereCondFragment);
        }

        return String.format(sqlTemplate, classInfo.getTableName(), updateFragment, whereCond);
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
        String sqlTemplate = " DELETE FROM \"%s\" %s; ";

        ClassInfo classInfo = getClassInfo();
        String whereCond = SqlConstants.EMPTY_STR;
        String whereCondFragment = generateWhereCond(queryMap, andOr);
        if (StringUtils.isNotBlank(whereCondFragment)) {
            whereCond = String.format(" WHERE %s ", whereCondFragment);
        }
        return String.format(sqlTemplate, classInfo.getTableName(), whereCond);
    }


}
