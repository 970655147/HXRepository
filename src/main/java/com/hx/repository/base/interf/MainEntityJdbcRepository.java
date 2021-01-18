package com.hx.repository.base.interf;

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

}
