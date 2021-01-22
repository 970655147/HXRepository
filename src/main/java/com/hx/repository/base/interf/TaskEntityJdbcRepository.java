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
public interface TaskEntityJdbcRepository<T> {

    /**
     * 保存给定的实体
     *
     * @param taskId taskId
     * @param entity entity
     * @return int
     * @author Jerry.X.He
     * @date 2021-01-15 17:28
     */
    int add(String taskId, T entity);

    /**
     * 保存给定的实体
     *
     * @param taskId     taskId
     * @param entityList entityList
     * @return int
     * @author Jerry.X.He
     * @date 2021-01-15 17:28
     */
    int addAll(String taskId, List<T> entityList);

    /**
     * 根据 id 查询记录信息
     *
     * @param taskId taskId
     * @param id     id
     * @return T
     * @author Jerry.X.He
     * @date 2021-01-17 19:18
     */
    T findById(String taskId, String id);

    /**
     * 根据指定的 queryMap 查询所有的记录
     *
     * @param taskId   taskId
     * @param queryMap queryMap
     * @param andOr    andOr
     * @return List
     * @author Jerry.X.He
     * @date 2021-01-19 14:54
     */
    List<T> allBy(String taskId, JSONObject queryMap, boolean andOr);

    /**
     * 根据指定的 queryMap 分页查询
     *
     * @param taskId   taskId
     * @param queryMap queryMap
     * @param andOr    andOr
     * @return Page
     * @author Jerry.X.He
     * @date 2021-01-19 16:14
     */
    Page<T> listBy(String taskId, JSONObject queryMap, boolean andOr);

    /**
     * 查询符合 queryMap 查询所有的记录的数量
     *
     * @param taskId   taskId
     * @param queryMap queryMap
     * @param andOr    andOr
     * @return int
     * @author Jerry.X.He
     * @date 2021-01-19 14:54
     */
    int countBy(String taskId, JSONObject queryMap, boolean andOr);

    /**
     * 根据指定的 queryMap 查询所有的指定列并去重
     *
     * @param taskId    taskId
     * @param fieldName fieldName
     * @param queryMap  queryMap
     * @param andOr     andOr
     * @return List
     * @author Jerry.X.He
     * @date 2021-01-19 14:54
     */
    List<String> allDistinctBy(String taskId, String fieldName, JSONObject queryMap, boolean andOr);

    /**
     * 查询符合 queryMap 查询所有的指定列并去重的数量
     *
     * @param taskId    taskId
     * @param fieldName fieldName
     * @param queryMap  queryMap
     * @param andOr     andOr
     * @return int
     * @author Jerry.X.He
     * @date 2021-01-19 14:54
     */
    int countDistinctBy(String taskId, String fieldName, JSONObject queryMap, boolean andOr);

    /**
     * 根据实体的 id 进行更新
     *
     * @param taskId taskId
     * @param entity entity
     * @return int
     * @author Jerry.X.He
     * @date 2021-01-17 18:56
     */
    int update(String taskId, T entity);

    /**
     * 根据实体的 id 进行更新
     *
     * @param taskId taskId
     * @param entity entity
     * @return int
     * @author Jerry.X.He
     * @date 2021-01-17 18:56
     */
    int updateNotNull(String taskId, T entity);

    /**
     * 根据 queryMap 构造条件更新符合条件的实体
     *
     * @param taskId   taskId
     * @param entity   entity
     * @param queryMap queryMap
     * @param andOr    andOr
     * @return int
     * @author Jerry.X.He
     * @date 2021-01-19 16:45
     */
    int updateBy(String taskId, T entity, JSONObject queryMap, boolean andOr);

    /**
     * 根据 queryMap 构造条件更新符合条件的实体
     *
     * @param taskId   taskId
     * @param entity   entity
     * @param queryMap queryMap
     * @param andOr    andOr
     * @return int
     * @author Jerry.X.He
     * @date 2021-01-19 16:45
     */
    int updateNotNullBy(String taskId, T entity, JSONObject queryMap, boolean andOr);

    /**
     * 保存给定的实体, 不存在则新增, 存在则更新
     *
     * @param taskId taskId
     * @param entity entity
     * @return int
     * @author Jerry.X.He
     * @date 2021-01-17 19:26
     */
    int save(String taskId, T entity);

    /**
     * 保存给定的实体, 不存在则新增, 存在则更新
     *
     * @param taskId taskId
     * @param entity entity
     * @return int
     * @author Jerry.X.He
     * @date 2021-01-17 19:26
     */
    int saveNotNull(String taskId, T entity);

    /**
     * 根据 id 删除记录
     *
     * @param taskId taskId
     * @param id     id
     * @return int
     * @author Jerry.X.He
     * @date 2021-01-17 19:32
     */
    int deleteById(String taskId, String id);

    /**
     * 根据 queryMap 构造条件删除符合条件的实体
     *
     * @param taskId   taskId
     * @param queryMap queryMap
     * @param andOr    andOr
     * @return int
     * @author Jerry.X.He
     * @date 2021-01-19 16:45
     */
    int deleteBy(String taskId, JSONObject queryMap, boolean andOr);

}
