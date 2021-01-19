package com.hx.repository.utils;

import com.alibaba.fastjson.JSONObject;
import com.hx.log.util.Tools;
import com.hx.repository.consts.FieldOperator;
import com.hx.repository.consts.WebContextConstants;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    public static int getPageNo(JSONObject queryMap) {
        String valueInQueryMap = queryMap.getString(WebContextConstants.PAGE_NO);
        if (!StringUtils.isNumeric(valueInQueryMap)) {
            return 1;
        }
        return Integer.valueOf(valueInQueryMap);
    }

    public static int getPageSize(JSONObject queryMap) {
        String valueInQueryMap = queryMap.getString(WebContextConstants.PAGE_SIZE);
        if (!StringUtils.isNumeric(valueInQueryMap)) {
            return 1;
        }
        return Integer.valueOf(valueInQueryMap);
    }


}
