package com.hx.repository.base.interf;

import com.alibaba.fastjson.JSONObject;
import com.hx.repository.model.Page;

import java.util.List;

/**
 * MainEntityJdbcRepository
 *
 * @author Jerry.X.He <970655147@qq.com>
 * @version 1.0
 * @date 2021-01-15 17:16
 */
public interface MainEntityJdbcRepository<T> {

    /**
     * 保存给定的实体
     *
     * @param entity entity
     * @return int
     * @author Jerry.X.He
     * @date 2021-01-15 17:28
     */
    int add(T entity);

    int addAll(List<T> entityList);

    /**
     * 根据 id 查询记录信息
     *
     * @param id id
     * @return
     * @author Jerry.X.He
     * @date 2021-01-17 19:18
     */
    T findById(String id);

    /**
     * 根据指定的 queryMap 查询所有的记录
     *
     * @param queryMap queryMap
     * @param andOr    andOr
     * @return
     * @author Jerry.X.He
     * @date 2021-01-19 14:54
     */
    List<T> allBy(JSONObject queryMap, boolean andOr);

    /**
     * 根据指定的 queryMap 分页查询
     *
     * @param queryMap queryMap
     * @param andOr    andOr
     * @return
     * @author Jerry.X.He
     * @date 2021-01-19 16:14
     */
    Page<T> listBy(JSONObject queryMap, boolean andOr);

    /**
     * 查询符合 queryMap 查询所有的记录的数量
     *
     * @param queryMap queryMap
     * @param andOr    andOr
     * @return
     * @author Jerry.X.He
     * @date 2021-01-19 14:54
     */
    int countBy(JSONObject queryMap, boolean andOr);

    /**
     * 根据实体的 id 进行更新
     *
     * @param entity entity
     * @return
     * @author Jerry.X.He
     * @date 2021-01-17 18:56
     */
    int update(T entity);

    int updateNotNull(T entity);

    /**
     * 根据 queryMap 构造条件更新符合条件的实体
     *
     * @param entity   entity
     * @param queryMap queryMap
     * @param andOr    andOr
     * @return
     * @author Jerry.X.He
     * @date 2021-01-19 16:45
     */
    int updateBy(T entity, JSONObject queryMap, boolean andOr);

    int updateNotNullBy(T entity, JSONObject queryMap, boolean andOr);

    /**
     * 保存给定的实体, 不存在则新增, 存在则更新
     *
     * @param entity entity
     * @return
     * @author Jerry.X.He
     * @date 2021-01-17 19:26
     */
    int save(T entity);

    int saveNotNull(T entity);

    /**
     * 根据 id 删除记录
     *
     * @param id id
     * @return
     * @author Jerry.X.He
     * @date 2021-01-17 19:32
     */
    int deleteById(String id);

    /**
     * 根据 queryMap 构造条件删除符合条件的实体
     *
     * @param queryMap queryMap
     * @param andOr    andOr
     * @return
     * @author Jerry.X.He
     * @date 2021-01-19 16:45
     */
    int deleteBy(JSONObject queryMap, boolean andOr);

}
