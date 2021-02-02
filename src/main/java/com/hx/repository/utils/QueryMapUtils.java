package com.hx.repository.utils;

import com.alibaba.fastjson.JSONObject;
import com.hx.log.util.Tools;
import com.hx.repository.consts.FieldOperator;
import com.hx.repository.consts.SqlConstants;
import com.hx.repository.consts.WebContextConstants;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.hx.repository.consts.SqlConstants.SORT_ASC;
import static com.hx.repository.consts.SqlConstants.SORT_DESC;

/**
 * QueryMapUtils
 *
 * @author Jerry.X.He <970655147@qq.com>
 * @version 1.0
 * @date 2021-01-19 15:07
 */
public final class QueryMapUtils {

    // disable constructor
    private QueryMapUtils() {
        Tools.assert0("can't instantiate !");
    }

    /** ORDER_BY_LENGTH_DESC */
    private static List<FieldOperator> ORDER_BY_LENGTH_DESC;

    // 根据 camelCode.length 倒序
    static {
        ORDER_BY_LENGTH_DESC = new ArrayList<>();
        ORDER_BY_LENGTH_DESC.addAll(Arrays.asList(FieldOperator.values()));
        ORDER_BY_LENGTH_DESC.sort(
                (v1, v2) -> -(v1.getCamelCode().length() - v2.getCamelCode().length()));
    }

    /**
     * 根据 queryKey 获取 查询列
     *
     * @param key key
     * @return com.hx.repository.consts.FieldOperator
     * @author Jerry.X.He
     * @date 2021-01-19 15:10
     */
    public static String parseQueryField(String key) {
        if (StringUtils.isBlank(key)) {
            return key;
        }

        for (FieldOperator operator : ORDER_BY_LENGTH_DESC) {
            if (key.endsWith(operator.getCamelCode())) {
                return key.substring(0, key.length() - operator.getCamelCode().length());
            }
        }
        return key;
    }

    /**
     * 根据 queryKey 获取 操作符
     *
     * @param key key
     * @return com.hx.repository.consts.FieldOperator
     * @author Jerry.X.He
     * @date 2021-01-19 15:08
     */
    public static FieldOperator parseQueryOperator(String key) {
        if (StringUtils.isBlank(key)) {
            return FieldOperator.EQ;
        }

        for (FieldOperator operator : ORDER_BY_LENGTH_DESC) {
            if (key.endsWith(operator.getCamelCode())) {
                return operator;
            }
        }
        return FieldOperator.EQ;
    }

    /**
     * 从 queryMap 中获取当前页
     *
     * @param queryMap queryMap
     * @return int
     * @author Jerry.X.He
     * @date 2021-01-19 16:16
     */
    public static int parsePageNo(JSONObject queryMap) {
        String valueInQueryMap = queryMap.getString(WebContextConstants.PAGE_NO);
        if (!StringUtils.isNumeric(valueInQueryMap)) {
            return 1;
        }
        return Integer.valueOf(valueInQueryMap);
    }

    public static int parsePageSize(JSONObject queryMap) {
        String valueInQueryMap = queryMap.getString(WebContextConstants.PAGE_SIZE);
        if (!StringUtils.isNumeric(valueInQueryMap)) {
            return 1;
        }
        return Integer.valueOf(valueInQueryMap);
    }

    /**
     * 根据给定的 orderBys 列表封装符合 queryMap 中的 orderBy
     *
     * @param queryMap queryMap
     * @param orderBys orderBys
     * @return java.util.Map<java.lang.String, java.lang.String>
     * @author Jerry.X.He
     * @date 2021-02-02 16:20
     */
    public static void generateOrderBy(JSONObject queryMap, List<String> orderBys) {
        for (int i = 0, len = orderBys.size(); i < len; i++) {
            String orderBy = orderBys.get(i);
            int directionIdx = (i + 1 >= len) ? -1 : i + 1;
            if (directionIdx < 0) {
                queryMap.put(wrapOrderBy(orderBy), true);
                continue;
            }
            String direction = orderBys.get(directionIdx);
            if (!isAscOrDesc(direction)) {
                queryMap.put(wrapOrderBy(orderBy), true);
                continue;
            }

            boolean directionValue = isAsc(direction);
            queryMap.put(wrapOrderBy(orderBy), directionValue);
            // skip direction value
            i++;
        }
    }

    /**
     * 根据给定的字段封装 orderByKey 在 queryMap 中的 key
     *
     * @param key key
     * @return java.lang.String
     * @author Jerry.X.He
     * @date 2021-02-02 16:21
     */
    public static String wrapOrderBy(String key) {
        return key + SqlConstants.ORDER_BY_SUFFIX;
    }

    /**
     * 判断给定的 key 是否是 xxOrderBy
     *
     * @param key key
     * @return boolean
     * @author Jerry.X.He
     * @date 2021-01-22 17:33
     */
    public static boolean isOrderBy(String key) {
        if (key == null) {
            return false;
        }
        return key.endsWith(SqlConstants.ORDER_BY_SUFFIX);
    }

    /**
     * 是 asc 或者是 desc ?
     *
     * @param direction direction
     * @return boolean
     * @author Jerry.X.He
     * @date 2021-02-02 16:25
     */
    public static boolean isAscOrDesc(String direction) {
        return SORT_ASC.equalsIgnoreCase(direction) || SORT_DESC.equalsIgnoreCase(direction);
    }

    public static boolean isAsc(String direction) {
        return SORT_ASC.equalsIgnoreCase(direction);
    }

    public static boolean isDesc(String direction) {
        return SORT_DESC.equalsIgnoreCase(direction);
    }

    /**
     * 判断给定的 key 是否是 xxOrderBy, 获取排序字段
     *
     * @param key key
     * @return java.lang.String
     * @author Jerry.X.He
     * @date 2021-01-22 17:35
     */
    public static String parseOrderByField(String key) {
        if (!isOrderBy(key)) {
            return null;
        }

        return key.substring(0, key.length() - SqlConstants.ORDER_BY_SUFFIX.length());
    }


}
