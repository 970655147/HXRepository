package com.hx.repository.base.interf;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hx.repository.domain.BaseEntity;
import com.hx.repository.model.ClassInfo;
import com.hx.repository.utils.ClassInfoUtils;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;

/**
 * MainEntityJdbcRepository
 *
 * @author Jerry.X.He <970655147@qq.com>
 * @version 1.0
 * @date 2021-01-15 17:16
 */
public abstract class AbstractEntityJdbcRepository<T> {

    @Resource
    private JdbcTemplate jdbcTemplate;

    /** 暂存的 classInfo */
    private ClassInfo<T> classInfo;

    /** 是否是继承自 BaseEntity */
    private Boolean isBaseEntity;

    /**
     * 获取 jdbcTemplate
     *
     * @return org.springframework.jdbc.core.JdbcTemplate
     * @author Jerry.X.He
     * @date 2021-01-15 17:18
     */
    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    // -------------------------------------- 相关抽象 --------------------------------------

    /**
     * 获取封装的实体的类型
     *
     * @return
     * @author Jerry.X.He
     * @date 2021-01-15 17:17
     */
    public abstract Class<T> getClazz();

    /**
     * 获取当前实体的 id
     *
     * @param entity entity
     * @return java.lang.String
     * @author Jerry.X.He
     * @date 2021-01-17 19:29
     */
    public String getId(T entity) {
        if (isBaseEntity()) {
            return ((BaseEntity) entity).getId();
        }
        return toJson(entity).getString("id");
    }

    /**
     * 获取当前实体的 classInfo 元数据信息
     *
     * @return dcamsclient.repository.base.ClassInfo<T>
     * @author Jerry.X.He
     * @date 2021-01-17 11:00
     */
    public ClassInfo<T> getClassInfo() {
        if (classInfo != null) {
            return classInfo;
        }

        classInfo = ClassInfoUtils.getClassInfo(getClazz());
        return classInfo;
    }

    /**
     * 是否继承自 BaseEntity
     *
     * @return boolean
     * @author Jerry.X.He
     * @date 2021-01-24 15:11
     */
    public boolean isBaseEntity() {
        if (isBaseEntity != null) {
            return isBaseEntity;
        }

        isBaseEntity = BaseEntity.class.isAssignableFrom(getClazz());
        return isBaseEntity;
    }

    /**
     * 将 entity 转换为 BaseEntity
     *
     * @param entity entity
     * @return com.hx.repository.domain.BaseEntity
     * @author Jerry.X.He
     * @date 2021-01-24 15:12
     */
    public BaseEntity castBaseEntity(T entity) {
        if (!isBaseEntity()) {
            return null;
        }
        return (BaseEntity) entity;
    }

    // -------------------------------------- 部分子类选择重写 --------------------------------------

    /**
     * 将对象转换为 json, 作为一种中间数据结构
     * 之后如果 XXRepository, 做成模板, 可以重写 toJson, 以提高效率
     *
     * @param entity entity
     * @return com.alibaba.fastjson.JSONObject
     * @author Jerry.X.He
     * @date 2021-01-15 17:31
     */
    protected JSONObject toJson(T entity) {
        if (entity == null) {
            return new JSONObject();
        }
        return (JSONObject) JSON.toJSON(entity);
    }

    protected JSONObject toNotNullJson(T entity) {
        JSONObject result = toJson(entity);
        for (String key : new ArrayList<>(result.keySet())) {
            if (result.get(key) == null) {
                result.remove(key);
            }
        }
        return result;
    }

    /**
     * 将 json 转换为对象
     *
     * @param json json
     * @return T
     * @author Jerry.X.He
     * @date 2021-01-17 19:20
     */
    protected T fromJson(JSONObject json) {
        return JSON.toJavaObject(json, getClazz());
    }


}
