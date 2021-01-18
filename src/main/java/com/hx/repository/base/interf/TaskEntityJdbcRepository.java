package com.hx.repository.base.interf;

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
     * @param entity entity
     * @return int
     * @author Jerry.X.He
     * @date 2021-01-15 17:28
     */
    int add(String taskId, T entity);

    int addAll(String taskId, List<T> entityList);

    /**
     * 根据 id 查询记录信息
     *
     * @param taskId taskId
     * @param id     id
     * @return
     * @author Jerry.X.He
     * @date 2021-01-17 19:18
     */
    T findById(String taskId, String id);

    /**
     * 根据实体的 id 进行更新
     *
     * @param taskId taskId
     * @param entity entity
     * @return
     * @author Jerry.X.He
     * @date 2021-01-17 18:56
     */
    int update(String taskId, T entity);

    int updateNotNull(String taskId, T entity);

    /**
     * 保存给定的实体, 不存在则新增, 存在则更新
     *
     * @param taskId taskId
     * @param entity entity
     * @return
     * @author Jerry.X.He
     * @date 2021-01-17 19:26
     */
    int save(String taskId, T entity);

    int saveNotNull(String taskId, T entity);

    /**
     * 根据 id 删除记录
     *
     * @param taskId taskId
     * @param id     id
     * @return
     * @author Jerry.X.He
     * @date 2021-01-17 19:32
     */
    int deleteById(String taskId, String id);

}
