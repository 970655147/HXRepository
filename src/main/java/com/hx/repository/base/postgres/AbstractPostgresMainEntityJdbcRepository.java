package com.hx.repository.base.postgres;

import com.alibaba.fastjson.JSONObject;
import com.hx.repository.base.interf.MainEntityJdbcRepository;
import com.hx.repository.model.ClassInfo;
import com.hx.repository.model.Page;
import com.hx.repository.utils.QueryMapUtils;
import com.hx.repository.utils.TypeCastUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * AbstractPostgresMainEntityJdbcRepository
 *
 * @author Jerry.X.He
 * @version 1.0
 * @date 2021-02-02 19:10
 */
public abstract class AbstractPostgresMainEntityJdbcRepository<T> extends AbstractPostgresEntityJdbcRepository<T>
        implements MainEntityJdbcRepository<T> {

    @Override
    public int add(T entity) {
        prePersist(entity);
        String sql = generateInsertSql(Collections.singletonList(entity));
        return getJdbcTemplate().update(sql);
    }

    @Override
    public int addAll(List<T> entityList) {
        prePersist(entityList);
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

        JSONObject json = TypeCastUtils.castMap2JsonWithCamel(list.get(0));
        return fromJson(json);
    }

    @Override
    public List<T> allBy(JSONObject queryMap, boolean andOr) {
        String sql = generateAllBySql(queryMap, andOr);
        return allBy0(sql);
    }

    @Override
    public Page<T> listBy(JSONObject queryMap, boolean andOr) {
        int pageNo = QueryMapUtils.parsePageNo(queryMap);
        int pageSize = QueryMapUtils.parsePageSize(queryMap);
        int totalRecord = countBy(queryMap, andOr);
        if (totalRecord == 0) {
            return Page.empty(pageNo, pageSize);
        }

        String sql = generateListBySql(queryMap, andOr, pageNo, pageSize);
        List<T> list = allBy0(sql);
        if (CollectionUtils.isEmpty(list)) {
            return Page.empty(pageNo, pageSize);
        }
        return Page.wrap(list, pageNo, pageSize, totalRecord);
    }

    @Override
    public int countBy(JSONObject queryMap, boolean andOr) {
        String sql = generateCountBySql(queryMap, andOr);
        String result = getJdbcTemplate().queryForObject(sql, String.class);
        return StringUtils.isNumeric(result) ? Integer.valueOf(result) : 0;
    }

    @Override
    public List<String> allDistinctBy(String fieldName, JSONObject queryMap, boolean andOr) {
        String columnName = getClassInfo().getField(fieldName).getColumnName();
        String sql = generateAllDistinctBySql(columnName, queryMap, andOr);
        return getJdbcTemplate().queryForList(sql, String.class);
    }

    @Override
    public int countDistinctBy(String fieldName, JSONObject queryMap, boolean andOr) {
        String columnName = getClassInfo().getField(fieldName).getColumnName();
        String sql = generateCountDistinctBySql(columnName, queryMap, andOr);
        String result = getJdbcTemplate().queryForObject(sql, String.class);
        return StringUtils.isNumeric(result) ? Integer.valueOf(result) : 0;
    }

    @Override
    public int update(T entity) {
        preUpdate(entity);
        String sql = generateUpdateSql(entity, false);
        return getJdbcTemplate().update(sql);
    }

    @Override
    public int updateNotNull(T entity) {
        preUpdate(entity);
        String sql = generateUpdateSql(entity, true);
        return getJdbcTemplate().update(sql);
    }

    @Override
    public int updateBy(T entity, JSONObject queryMap, boolean andOr) {
        preUpdate(entity);
        String sql = generateUpdateBySql(entity, queryMap, andOr, false);
        return getJdbcTemplate().update(sql);
    }

    @Override
    public int updateNotNullBy(T entity, JSONObject queryMap, boolean andOr) {
        preUpdate(entity);
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

    @Override
    public String tableName() {
        ClassInfo classInfo = getClassInfo();
        return String.format("%s", classInfo.getTableName());
    }

}
