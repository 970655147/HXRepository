package com.hx.repository.domain;

import lombok.Data;

import java.util.List;

/**
 * BasePageSearchRequest
 *
 * @author Jerry.X.He <970655147@qq.com>
 * @version 1.0
 * @date 2021-01-24 18:48
 */
@Data
public class BasePageSearchRequest extends BaseSearchRequest {

    /** 当前页 */
    private int pageNo;
    /** 分页大小 */
    private int pageSize;

    /** 排序 */
    private List<String> orderBys;

    /**
     * BasePageSearchRequest
     *
     * @author Jerry.X.He
     * @date 2021-02-02 10:50
     */
    public BasePageSearchRequest() {
        this.pageNo = 1;
        this.pageSize = 10;
    }

}
