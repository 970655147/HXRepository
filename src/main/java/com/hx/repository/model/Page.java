package com.hx.repository.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * Page
 *
 * @author Jerry.X.He <970655147@qq.com>
 * @version 1.0
 * @date 2021-01-19 16:11
 */
@Data
public class Page<T> {

    /** 当前页 */
    private int pageNo;
    /** 分页大小 */
    private int pageSize;

    /** 分页数量 */
    private int totalPage;
    /** 总共数量 */
    private int totalRecord;

    /** 当前页的数据 */
    private List<T> list;

    /**
     * 获取一个 空的 Page
     *
     * @param pageNo   pageNo
     * @param pageSize pageSize
     * @return com.hx.repository.model.Page<T>
     * @author Jerry.X.He
     * @date 2021-01-19 16:27
     */
    public static <T> Page<T> empty(int pageNo, int pageSize) {
        Page<T> result = new Page<>();
        result.setPageNo(pageNo);
        result.setPageSize(pageSize);
        result.setTotalPage(0);
        result.setTotalRecord(0);
        result.setList(Collections.emptyList());
        return result;
    }

    /**
     * 按照给定的 list 封装一个 Page
     *
     * @param pageNo      pageNo
     * @param pageSize    pageSize
     * @param totalRecord totalRecord
     * @param list        list
     * @return com.hx.repository.model.Page<T>
     * @author Jerry.X.He
     * @date 2021-02-02 16:08
     */
    public static <T> Page<T> wrap(List<T> list, int pageNo, int pageSize, int totalRecord) {
        int totalPage = calcTotalPage(totalRecord, pageSize);
        Page<T> result = new Page<>();
        result.setPageNo(pageNo);
        result.setPageSize(pageSize);
        result.setTotalPage(totalPage);
        result.setTotalRecord(totalRecord);
        result.setList(list);
        return result;
    }

    /**
     * 根据总共的数量和分页数量计算总共的页数
     *
     * @param totalRecord totalRecord
     * @param pageSize    pageSize
     * @return int
     * @author Jerry.X.He
     * @date 2021-02-02 16:09
     */
    public static int calcTotalPage(int totalRecord, int pageSize) {
        if (totalRecord == 0) {
            return 0;
        }
        return ((totalRecord - 1) / pageSize) + 1;
    }

    /**
     * 当前 page 的内容 apply 给定的 mapper
     *
     * @param mapper mapper
     * @return com.hx.repository.model.Page<R>
     * @author Jerry.X.He
     * @date 2021-01-19 16:27
     */
    public <R> Page<R> map(Function<? super T, ? extends R> mapper) {
        Page<R> result = new Page<>();
        result.setPageNo(pageNo);
        result.setPageSize(pageSize);
        result.setTotalPage(totalPage);
        result.setTotalRecord(totalRecord);

        List<R> newList = new ArrayList<>();
        for (T entity : list) {
            newList.add(mapper.apply(entity));
        }
        result.setList(newList);

        return result;
    }


}
