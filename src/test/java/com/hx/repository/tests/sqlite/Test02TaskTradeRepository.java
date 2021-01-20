package com.hx.repository.tests.sqlite;

import com.alibaba.fastjson.JSONObject;
import com.hx.common.util.AssertUtils;
import com.hx.log.util.Log;
import com.hx.repository.consts.WebContextConstants;
import com.hx.repository.context.SpringContext;
import com.hx.repository.domain.Trade;
import com.hx.repository.model.Page;
import com.hx.repository.sqlite.TaskTradeRepository;
import com.hx.repository.tests.base.BaseServiceTest;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.hx.log.log.LogPatternUtils.formatLogInfoWithIdx;
import static com.hx.repository.tests.sqlite.Test01TradeRepository.lookUp;
import static com.hx.repository.tests.sqlite.Test01TradeRepository.newRandomTrade;

/**
 * Test02TaskTradeRepository
 *
 * @author Jerry.X.He
 * @version 1.0
 * @date 2021-01-19 22:12
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Test02TaskTradeRepository extends BaseServiceTest {

    private static final String TEST_TASK_ID = "main";

    @Before
    public void test01ClearTrades() {
        TaskTradeRepository taskTradeRepository = SpringContext.getBean(TaskTradeRepository.class);
        int updated = taskTradeRepository.deleteBy(TEST_TASK_ID, new JSONObject(), true);
        Log.info(formatLogInfoWithIdx(" 移除了 {0} 条交易信息 ", updated));
    }

    @Test
    public void test01Add() {
        TaskTradeRepository taskTradeRepository = SpringContext.getBean(TaskTradeRepository.class);

        Trade trade = newRandomTrade(0);
        int updated = taskTradeRepository.add(TEST_TASK_ID, trade);
        Log.info(formatLogInfoWithIdx(" 新增了 {0} 条交易信息 ", updated));

        String savedId = trade.getId();
        Trade tradeInDb = taskTradeRepository.findById(TEST_TASK_ID, savedId);

        AssertUtils.assert0(toDebugJSON(trade), toDebugJSON(tradeInDb), " trade <> tradeInDb ");
        Log.info(formatLogInfoWithIdx(" test01Save succeed "));
    }

    @Test
    public void test02AddAll() {
        TaskTradeRepository taskTradeRepository = SpringContext.getBean(TaskTradeRepository.class);

        int loopCount = 10;
        List<Trade> tradeList = new ArrayList<>();
        for (int i = 0; i < loopCount; i++) {
            tradeList.add(newRandomTrade(i));
        }
        int updated = taskTradeRepository.addAll(TEST_TASK_ID, tradeList);
        Log.info(formatLogInfoWithIdx(" 新增了 {0} 条交易信息 ", updated));

        List<Trade> tradeListInDb = taskTradeRepository.allBy(TEST_TASK_ID, new JSONObject(), true);
        for (Trade trade : tradeList) {
            Trade tradeInDb = lookUp(tradeListInDb, trade.getId());
            AssertUtils.assert0(toDebugJSON(trade), toDebugJSON(tradeInDb), " trade <> tradeInDb ");
        }
        Log.info(formatLogInfoWithIdx(" test02SaveAll succeed "));
    }

    @Test
    public void test03SaveAdd() {
        TaskTradeRepository taskTradeRepository = SpringContext.getBean(TaskTradeRepository.class);

        Trade trade = newRandomTrade(0);
        int updated = taskTradeRepository.save(TEST_TASK_ID, trade);
        Log.info(formatLogInfoWithIdx(" 保存了 {0} 条交易信息 ", updated));

        String savedId = trade.getId();
        Trade tradeInDb = taskTradeRepository.findById(TEST_TASK_ID, savedId);

        AssertUtils.assert0(toDebugJSON(trade), toDebugJSON(tradeInDb), " trade <> tradeInDb ");
        Log.info(formatLogInfoWithIdx(" test03Save succeed "));
    }

    @Test
    public void test03SaveUpdate() {
        TaskTradeRepository taskTradeRepository = SpringContext.getBean(TaskTradeRepository.class);

        Trade trade = newRandomTrade(0);
        int updated = taskTradeRepository.add(TEST_TASK_ID, trade);
        Log.info(formatLogInfoWithIdx(" 新增了 {0} 条交易信息 ", updated));

        trade.setSourceCardNumber("updated");
        trade.setTargetCardNumber("updated");
        trade.setCashMarks("updated");
        trade.setIp("updated");
        trade.setMac("updated");
        updated = taskTradeRepository.save(TEST_TASK_ID, trade);
        Log.info(formatLogInfoWithIdx(" 更新了 {0} 条交易信息 ", updated));

        String savedId = trade.getId();
        Trade tradeInDb = taskTradeRepository.findById(TEST_TASK_ID, savedId);

        AssertUtils.assert0(toDebugJSON(trade), toDebugJSON(tradeInDb), " trade <> tradeInDb ");
        Log.info(formatLogInfoWithIdx(" test03SaveUpdate succeed "));
    }

    @Test
    public void test04SaveNotNullAdd() {
        TaskTradeRepository taskTradeRepository = SpringContext.getBean(TaskTradeRepository.class);

        Trade trade = newRandomTrade(0);
        trade.setMemo(null);
        trade.setRemark(null);
        trade.setCurrencyCode(null);
        trade.setCashMarks(null);
        int updated = taskTradeRepository.saveNotNull(TEST_TASK_ID, trade);
        Log.info(formatLogInfoWithIdx(" 保存了 {0} 条交易信息 ", updated));

        String savedId = trade.getId();
        Trade tradeInDb = taskTradeRepository.findById(TEST_TASK_ID, savedId);

        AssertUtils.assert0(toDebugJSON(trade), toDebugJSON(tradeInDb), " trade <> tradeInDb ");
        Log.info(formatLogInfoWithIdx(" test04SaveNotNullAdd succeed "));
    }

    @Test
    public void test04SaveNotNullUpdate() {
        TaskTradeRepository taskTradeRepository = SpringContext.getBean(TaskTradeRepository.class);

        Trade trade = newRandomTrade(0);
        int updated = taskTradeRepository.add(TEST_TASK_ID, trade);
        Log.info(formatLogInfoWithIdx(" 新增了 {0} 条交易信息 ", updated));

        trade.setMemo(null);
        trade.setRemark(null);
        trade.setCurrencyCode(null);
        trade.setCashMarks(null);
        trade.setSourceCardNumber("updated");
        trade.setTargetCardNumber("updated");
        trade.setCashMarks("updated");
        trade.setIp("updated");
        trade.setMac("updated");
        updated = taskTradeRepository.saveNotNull(TEST_TASK_ID, trade);
        Log.info(formatLogInfoWithIdx(" 更新了 {0} 条交易信息 ", updated));

        String savedId = trade.getId();
        Trade tradeInDb = taskTradeRepository.findById(TEST_TASK_ID, savedId);

        AssertUtils.assert0(toDebugJSON(trade), toDebugJSON(tradeInDb), false, " trade == tradeInDb ");
        AssertUtils.assert0(tradeInDb.getMemo(), null, false, " memo == null ");
        AssertUtils.assert0(tradeInDb.getRemark(), null, false, " remark == null ");
        AssertUtils.assert0(tradeInDb.getCurrencyCode(), null, false, " currencyCode == null ");
        AssertUtils.assert0(tradeInDb.getCashMarks(), null, false, " cashMarks == null ");
        AssertUtils.assert0(tradeInDb.getSourceCardNumber(), "updated", true, " sourceCardNumber == updated ");
        AssertUtils.assert0(tradeInDb.getTargetCardNumber(), "updated", true, " sourceCardNumber == updated ");
        Log.info(formatLogInfoWithIdx(" test04SaveNotNullUpdate succeed "));
    }

    @Test
    public void test05ListByAll() {
        TaskTradeRepository taskTradeRepository = SpringContext.getBean(TaskTradeRepository.class);

        int loopCount = 15;
        int pageNo = 1, pageSize = 12;

        List<Trade> tradeList = new ArrayList<>();
        for (int i = 0; i < loopCount; i++) {
            tradeList.add(newRandomTrade(i));
        }
        int updated = taskTradeRepository.addAll(TEST_TASK_ID, tradeList);
        Log.info(formatLogInfoWithIdx(" 新增了 {0} 条交易信息 ", updated));
        int totalRecord = tradeList.size();
        int totalPage = (((totalRecord - 1) / pageSize) + 1);
        int expectedListSize = pageSize < totalRecord ? pageSize : totalRecord;

        JSONObject queryMap = new JSONObject();
        queryMap.put(WebContextConstants.PAGE_NO, pageNo);
        queryMap.put(WebContextConstants.PAGE_SIZE, pageSize);
        Page<Trade> tradeListInDb = taskTradeRepository.listBy(TEST_TASK_ID, queryMap, true);
        for (Trade trade : tradeListInDb.getList()) {
            Trade tradeInDb = lookUp(tradeList, trade.getId());
            AssertUtils.assert0(toDebugJSON(trade), toDebugJSON(tradeInDb), " trade <> tradeInDb ");
        }

        AssertUtils.assert0(tradeListInDb.getPageNo(), pageNo, " pageNo <> page.pageNo ");
        AssertUtils.assert0(tradeListInDb.getPageSize(), pageSize, " pageSize <> page.pageSize ");
        AssertUtils.assert0(tradeListInDb.getTotalRecord(), totalRecord, " totalRecord <> page.totalRecord ");
        AssertUtils.assert0(tradeListInDb.getTotalPage(), totalPage, " totalPage <> page.totalPage ");
        AssertUtils.assert0(tradeListInDb.getList().size(), expectedListSize, " listSize <> page.listSize ");
        Log.info(formatLogInfoWithIdx(" test05ListByAll succeed "));
    }

    @Test
    public void test05ListByTradeAmount() {
        TaskTradeRepository taskTradeRepository = SpringContext.getBean(TaskTradeRepository.class);

        int loopCount = 15;
        int pageNo = 1, pageSize = 12;

        List<Trade> tradeList = new ArrayList<>();
        for (int i = 0; i < loopCount; i++) {
            tradeList.add(newRandomTrade(i));
        }
        int updated = taskTradeRepository.addAll(TEST_TASK_ID, tradeList);
        Log.info(formatLogInfoWithIdx(" 新增了 {0} 条交易信息 ", updated));

        Trade targetTrade = tradeList.get(0);
        int totalRecord = (int) tradeList
                .stream()
                .filter(row -> row.getTradeAmount().compareTo(targetTrade.getTradeAmount()) == 0)
                .count();
        int totalPage = (((totalRecord - 1) / pageSize) + 1);
        int expectedListSize = pageSize < totalRecord ? pageSize : totalRecord;

        JSONObject queryMap = new JSONObject();
        queryMap.put(WebContextConstants.PAGE_NO, pageNo);
        queryMap.put(WebContextConstants.PAGE_SIZE, pageSize);
        queryMap.put("tradeAmount", targetTrade.getTradeAmount());
        Page<Trade> tradeListInDb = taskTradeRepository.listBy(TEST_TASK_ID, queryMap, true);
        for (Trade trade : tradeListInDb.getList()) {
            Trade tradeInDb = lookUp(tradeList, trade.getId());
            AssertUtils.assert0(toDebugJSON(trade), toDebugJSON(tradeInDb), " trade <> tradeInDb ");
        }

        AssertUtils.assert0(tradeListInDb.getPageNo(), pageNo, " pageNo <> page.pageNo ");
        AssertUtils.assert0(tradeListInDb.getPageSize(), pageSize, " pageSize <> page.pageSize ");
        AssertUtils.assert0(tradeListInDb.getTotalRecord(), totalRecord, " totalRecord <> page.totalRecord ");
        AssertUtils.assert0(tradeListInDb.getTotalPage(), totalPage, " totalPage <> page.totalPage ");
        AssertUtils.assert0(tradeListInDb.getList().size(), expectedListSize, " listSize <> page.listSize ");
        Log.info(formatLogInfoWithIdx(" test05ListByTradeAmount succeed "));
    }

    @Test
    public void test05ListByTradeAmountEq() {
        TaskTradeRepository taskTradeRepository = SpringContext.getBean(TaskTradeRepository.class);

        int loopCount = 15;
        int pageNo = 1, pageSize = 12;

        List<Trade> tradeList = new ArrayList<>();
        for (int i = 0; i < loopCount; i++) {
            tradeList.add(newRandomTrade(i));
        }
        int updated = taskTradeRepository.addAll(TEST_TASK_ID, tradeList);
        Log.info(formatLogInfoWithIdx(" 新增了 {0} 条交易信息 ", updated));

        Trade targetTrade = tradeList.get(0);
        int totalRecord = (int) tradeList
                .stream()
                .filter(row -> row.getTradeAmount().compareTo(targetTrade.getTradeAmount()) == 0)
                .count();
        int totalPage = (((totalRecord - 1) / pageSize) + 1);
        int expectedListSize = pageSize < totalRecord ? pageSize : totalRecord;

        JSONObject queryMap = new JSONObject();
        queryMap.put(WebContextConstants.PAGE_NO, pageNo);
        queryMap.put(WebContextConstants.PAGE_SIZE, pageSize);
        queryMap.put("tradeAmountEq", targetTrade.getTradeAmount());
        Page<Trade> tradeListInDb = taskTradeRepository.listBy(TEST_TASK_ID, queryMap, true);
        for (Trade trade : tradeListInDb.getList()) {
            Trade tradeInDb = lookUp(tradeList, trade.getId());
            AssertUtils.assert0(toDebugJSON(trade), toDebugJSON(tradeInDb), " trade <> tradeInDb ");
        }

        AssertUtils.assert0(tradeListInDb.getPageNo(), pageNo, " pageNo <> page.pageNo ");
        AssertUtils.assert0(tradeListInDb.getPageSize(), pageSize, " pageSize <> page.pageSize ");
        AssertUtils.assert0(tradeListInDb.getTotalRecord(), totalRecord, " totalRecord <> page.totalRecord ");
        AssertUtils.assert0(tradeListInDb.getTotalPage(), totalPage, " totalPage <> page.totalPage ");
        AssertUtils.assert0(tradeListInDb.getList().size(), expectedListSize, " listSize <> page.listSize ");
        Log.info(formatLogInfoWithIdx(" test05ListByTradeAmountEq succeed "));
    }

    @Test
    public void test05ListByTradeAmountNe() {
        TaskTradeRepository taskTradeRepository = SpringContext.getBean(TaskTradeRepository.class);

        int loopCount = 15;
        int pageNo = 1, pageSize = 12;

        List<Trade> tradeList = new ArrayList<>();
        for (int i = 0; i < loopCount; i++) {
            tradeList.add(newRandomTrade(i));
        }
        int updated = taskTradeRepository.addAll(TEST_TASK_ID, tradeList);
        Log.info(formatLogInfoWithIdx(" 新增了 {0} 条交易信息 ", updated));

        Trade targetTrade = tradeList.get(0);
        int totalRecord = (int) tradeList
                .stream()
                .filter(row -> (row.getTradeAmount().compareTo(targetTrade.getTradeAmount())) != 0)
                .count();
        int totalPage = (((totalRecord - 1) / pageSize) + 1);
        int expectedListSize = pageSize < totalRecord ? pageSize : totalRecord;

        JSONObject queryMap = new JSONObject();
        queryMap.put(WebContextConstants.PAGE_NO, pageNo);
        queryMap.put(WebContextConstants.PAGE_SIZE, pageSize);
        queryMap.put("tradeAmountNe", targetTrade.getTradeAmount());
        Page<Trade> tradeListInDb = taskTradeRepository.listBy(TEST_TASK_ID, queryMap, true);
        for (Trade trade : tradeListInDb.getList()) {
            Trade tradeInDb = lookUp(tradeList, trade.getId());
            AssertUtils.assert0(toDebugJSON(trade), toDebugJSON(tradeInDb), " trade <> tradeInDb ");
        }

        AssertUtils.assert0(tradeListInDb.getPageNo(), pageNo, " pageNo <> page.pageNo ");
        AssertUtils.assert0(tradeListInDb.getPageSize(), pageSize, " pageSize <> page.pageSize ");
        AssertUtils.assert0(tradeListInDb.getTotalRecord(), totalRecord, " totalRecord <> page.totalRecord ");
        AssertUtils.assert0(tradeListInDb.getTotalPage(), totalPage, " totalPage <> page.totalPage ");
        AssertUtils.assert0(tradeListInDb.getList().size(), expectedListSize, " listSize <> page.listSize ");
        Log.info(formatLogInfoWithIdx(" test05ListByTradeAmountNe succeed "));
    }

    @Test
    public void test05ListByTradeAmountGt() {
        TaskTradeRepository taskTradeRepository = SpringContext.getBean(TaskTradeRepository.class);

        int loopCount = 15;
        int pageNo = 1, pageSize = 12;

        List<Trade> tradeList = new ArrayList<>();
        for (int i = 0; i < loopCount; i++) {
            tradeList.add(newRandomTrade(i));
        }
        int updated = taskTradeRepository.addAll(TEST_TASK_ID, tradeList);
        Log.info(formatLogInfoWithIdx(" 新增了 {0} 条交易信息 ", updated));

        Trade targetTrade = tradeList.get(0);
        int totalRecord = (int) tradeList
                .stream()
                .filter(row -> row.getTradeAmount().compareTo(targetTrade.getTradeAmount()) > 0)
                .count();
        int totalPage = (((totalRecord - 1) / pageSize) + 1);
        int expectedListSize = pageSize < totalRecord ? pageSize : totalRecord;

        JSONObject queryMap = new JSONObject();
        queryMap.put(WebContextConstants.PAGE_NO, pageNo);
        queryMap.put(WebContextConstants.PAGE_SIZE, pageSize);
        queryMap.put("tradeAmountGt", targetTrade.getTradeAmount());
        Page<Trade> tradeListInDb = taskTradeRepository.listBy(TEST_TASK_ID, queryMap, true);
        for (Trade trade : tradeListInDb.getList()) {
            Trade tradeInDb = lookUp(tradeList, trade.getId());
            AssertUtils.assert0(toDebugJSON(trade), toDebugJSON(tradeInDb), " trade <> tradeInDb ");
        }

        AssertUtils.assert0(tradeListInDb.getPageNo(), pageNo, " pageNo <> page.pageNo ");
        AssertUtils.assert0(tradeListInDb.getPageSize(), pageSize, " pageSize <> page.pageSize ");
        AssertUtils.assert0(tradeListInDb.getTotalRecord(), totalRecord, " totalRecord <> page.totalRecord ");
        AssertUtils.assert0(tradeListInDb.getTotalPage(), totalPage, " totalPage <> page.totalPage ");
        AssertUtils.assert0(tradeListInDb.getList().size(), expectedListSize, " listSize <> page.listSize ");
        Log.info(formatLogInfoWithIdx(" test05ListByTradeAmountGt succeed "));
    }

    @Test
    public void test05ListByTradeAmountGte() {
        TaskTradeRepository taskTradeRepository = SpringContext.getBean(TaskTradeRepository.class);

        int loopCount = 15;
        int pageNo = 1, pageSize = 12;

        List<Trade> tradeList = new ArrayList<>();
        for (int i = 0; i < loopCount; i++) {
            tradeList.add(newRandomTrade(i));
        }
        int updated = taskTradeRepository.addAll(TEST_TASK_ID, tradeList);
        Log.info(formatLogInfoWithIdx(" 新增了 {0} 条交易信息 ", updated));

        Trade targetTrade = tradeList.get(0);
        int totalRecord = (int) tradeList
                .stream()
                .filter(row -> row.getTradeAmount().compareTo(targetTrade.getTradeAmount()) >= 0)
                .count();
        int totalPage = (((totalRecord - 1) / pageSize) + 1);
        int expectedListSize = pageSize < totalRecord ? pageSize : totalRecord;

        JSONObject queryMap = new JSONObject();
        queryMap.put(WebContextConstants.PAGE_NO, pageNo);
        queryMap.put(WebContextConstants.PAGE_SIZE, pageSize);
        queryMap.put("tradeAmountGte", targetTrade.getTradeAmount());
        Page<Trade> tradeListInDb = taskTradeRepository.listBy(TEST_TASK_ID, queryMap, true);
        for (Trade trade : tradeListInDb.getList()) {
            Trade tradeInDb = lookUp(tradeList, trade.getId());
            AssertUtils.assert0(toDebugJSON(trade), toDebugJSON(tradeInDb), " trade <> tradeInDb ");
        }

        AssertUtils.assert0(tradeListInDb.getPageNo(), pageNo, " pageNo <> page.pageNo ");
        AssertUtils.assert0(tradeListInDb.getPageSize(), pageSize, " pageSize <> page.pageSize ");
        AssertUtils.assert0(tradeListInDb.getTotalRecord(), totalRecord, " totalRecord <> page.totalRecord ");
        AssertUtils.assert0(tradeListInDb.getTotalPage(), totalPage, " totalPage <> page.totalPage ");
        AssertUtils.assert0(tradeListInDb.getList().size(), expectedListSize, " listSize <> page.listSize ");
        Log.info(formatLogInfoWithIdx(" test05ListByTradeAmountGte succeed "));
    }

    @Test
    public void test05ListByTradeAmountLt() {
        TaskTradeRepository taskTradeRepository = SpringContext.getBean(TaskTradeRepository.class);

        int loopCount = 15;
        int pageNo = 1, pageSize = 12;

        List<Trade> tradeList = new ArrayList<>();
        for (int i = 0; i < loopCount; i++) {
            tradeList.add(newRandomTrade(i));
        }
        int updated = taskTradeRepository.addAll(TEST_TASK_ID, tradeList);
        Log.info(formatLogInfoWithIdx(" 新增了 {0} 条交易信息 ", updated));

        Trade targetTrade = tradeList.get(0);
        int totalRecord = (int) tradeList
                .stream()
                .filter(row -> row.getTradeAmount().compareTo(targetTrade.getTradeAmount()) < 0)
                .count();
        int totalPage = (((totalRecord - 1) / pageSize) + 1);
        int expectedListSize = pageSize < totalRecord ? pageSize : totalRecord;

        JSONObject queryMap = new JSONObject();
        queryMap.put(WebContextConstants.PAGE_NO, pageNo);
        queryMap.put(WebContextConstants.PAGE_SIZE, pageSize);
        queryMap.put("tradeAmountLt", targetTrade.getTradeAmount());
        Page<Trade> tradeListInDb = taskTradeRepository.listBy(TEST_TASK_ID, queryMap, true);
        for (Trade trade : tradeListInDb.getList()) {
            Trade tradeInDb = lookUp(tradeList, trade.getId());
            AssertUtils.assert0(toDebugJSON(trade), toDebugJSON(tradeInDb), " trade <> tradeInDb ");
        }

        AssertUtils.assert0(tradeListInDb.getPageNo(), pageNo, " pageNo <> page.pageNo ");
        AssertUtils.assert0(tradeListInDb.getPageSize(), pageSize, " pageSize <> page.pageSize ");
        AssertUtils.assert0(tradeListInDb.getTotalRecord(), totalRecord, " totalRecord <> page.totalRecord ");
        AssertUtils.assert0(tradeListInDb.getTotalPage(), totalPage, " totalPage <> page.totalPage ");
        AssertUtils.assert0(tradeListInDb.getList().size(), expectedListSize, " listSize <> page.listSize ");
        Log.info(formatLogInfoWithIdx(" test05ListByTradeAmountLt succeed "));
    }

    @Test
    public void test05ListByTradeAmountLte() {
        TaskTradeRepository taskTradeRepository = SpringContext.getBean(TaskTradeRepository.class);

        int loopCount = 15;
        int pageNo = 1, pageSize = 12;

        List<Trade> tradeList = new ArrayList<>();
        for (int i = 0; i < loopCount; i++) {
            tradeList.add(newRandomTrade(i));
        }
        int updated = taskTradeRepository.addAll(TEST_TASK_ID, tradeList);
        Log.info(formatLogInfoWithIdx(" 新增了 {0} 条交易信息 ", updated));

        Trade targetTrade = tradeList.get(0);
        int totalRecord = (int) tradeList
                .stream()
                .filter(row -> row.getTradeAmount().compareTo(targetTrade.getTradeAmount()) <= 0)
                .count();
        int totalPage = (((totalRecord - 1) / pageSize) + 1);
        int expectedListSize = pageSize < totalRecord ? pageSize : totalRecord;

        JSONObject queryMap = new JSONObject();
        queryMap.put(WebContextConstants.PAGE_NO, pageNo);
        queryMap.put(WebContextConstants.PAGE_SIZE, pageSize);
        queryMap.put("tradeAmountLte", targetTrade.getTradeAmount());
        Page<Trade> tradeListInDb = taskTradeRepository.listBy(TEST_TASK_ID, queryMap, true);
        for (Trade trade : tradeListInDb.getList()) {
            Trade tradeInDb = lookUp(tradeList, trade.getId());
            AssertUtils.assert0(toDebugJSON(trade), toDebugJSON(tradeInDb), " trade <> tradeInDb ");
        }

        AssertUtils.assert0(tradeListInDb.getPageNo(), pageNo, " pageNo <> page.pageNo ");
        AssertUtils.assert0(tradeListInDb.getPageSize(), pageSize, " pageSize <> page.pageSize ");
        AssertUtils.assert0(tradeListInDb.getTotalRecord(), totalRecord, " totalRecord <> page.totalRecord ");
        AssertUtils.assert0(tradeListInDb.getTotalPage(), totalPage, " totalPage <> page.totalPage ");
        AssertUtils.assert0(tradeListInDb.getList().size(), expectedListSize, " listSize <> page.listSize ");
        Log.info(formatLogInfoWithIdx(" test05ListByTradeAmountLte succeed "));
    }

    @Test
    public void test05ListBySourceCardNumberLike() {
        TaskTradeRepository taskTradeRepository = SpringContext.getBean(TaskTradeRepository.class);

        int loopCount = 15;
        int pageNo = 1, pageSize = 12;

        List<Trade> tradeList = new ArrayList<>();
        for (int i = 0; i < loopCount; i++) {
            tradeList.add(newRandomTrade(i));
        }
        int updated = taskTradeRepository.addAll(TEST_TASK_ID, tradeList);
        Log.info(formatLogInfoWithIdx(" 新增了 {0} 条交易信息 ", updated));

        Trade targetTrade = tradeList.get(0);
        int totalRecord = (int) tradeList
                .stream()
                .filter(row -> row.getSourceCardNumber().contains(targetTrade.getSourceCardNumber()))
                .count();
        int totalPage = (((totalRecord - 1) / pageSize) + 1);
        int expectedListSize = pageSize < totalRecord ? pageSize : totalRecord;

        JSONObject queryMap = new JSONObject();
        queryMap.put(WebContextConstants.PAGE_NO, pageNo);
        queryMap.put(WebContextConstants.PAGE_SIZE, pageSize);
        queryMap.put("sourceCardNumberLike", String.format("%%%s%%", targetTrade.getSourceCardNumber()));
        Page<Trade> tradeListInDb = taskTradeRepository.listBy(TEST_TASK_ID, queryMap, true);
        for (Trade trade : tradeListInDb.getList()) {
            Trade tradeInDb = lookUp(tradeList, trade.getId());
            AssertUtils.assert0(toDebugJSON(trade), toDebugJSON(tradeInDb), " trade <> tradeInDb ");
        }

        AssertUtils.assert0(tradeListInDb.getPageNo(), pageNo, " pageNo <> page.pageNo ");
        AssertUtils.assert0(tradeListInDb.getPageSize(), pageSize, " pageSize <> page.pageSize ");
        AssertUtils.assert0(tradeListInDb.getTotalRecord(), totalRecord, " totalRecord <> page.totalRecord ");
        AssertUtils.assert0(tradeListInDb.getTotalPage(), totalPage, " totalPage <> page.totalPage ");
        AssertUtils.assert0(tradeListInDb.getList().size(), expectedListSize, " listSize <> page.listSize ");
        Log.info(formatLogInfoWithIdx(" test05ListBySourceCardNumberLike succeed "));
    }

    @Test
    public void test05ListBySourceCardNumberNotLike() {
        TaskTradeRepository taskTradeRepository = SpringContext.getBean(TaskTradeRepository.class);

        int loopCount = 15;
        int pageNo = 1, pageSize = 12;

        List<Trade> tradeList = new ArrayList<>();
        for (int i = 0; i < loopCount; i++) {
            tradeList.add(newRandomTrade(i));
        }
        int updated = taskTradeRepository.addAll(TEST_TASK_ID, tradeList);
        Log.info(formatLogInfoWithIdx(" 新增了 {0} 条交易信息 ", updated));

        Trade targetTrade = tradeList.get(0);
        int totalRecord = (int) tradeList
                .stream()
                .filter(row -> (!row.getSourceCardNumber().contains(targetTrade.getSourceCardNumber())))
                .count();
        int totalPage = (((totalRecord - 1) / pageSize) + 1);
        int expectedListSize = pageSize < totalRecord ? pageSize : totalRecord;

        JSONObject queryMap = new JSONObject();
        queryMap.put(WebContextConstants.PAGE_NO, pageNo);
        queryMap.put(WebContextConstants.PAGE_SIZE, pageSize);
        queryMap.put("sourceCardNumberNotLike", String.format("%%%s%%", targetTrade.getSourceCardNumber()));
        Page<Trade> tradeListInDb = taskTradeRepository.listBy(TEST_TASK_ID, queryMap, true);
        for (Trade trade : tradeListInDb.getList()) {
            Trade tradeInDb = lookUp(tradeList, trade.getId());
            AssertUtils.assert0(toDebugJSON(trade), toDebugJSON(tradeInDb), " trade <> tradeInDb ");
        }

        AssertUtils.assert0(tradeListInDb.getPageNo(), pageNo, " pageNo <> page.pageNo ");
        AssertUtils.assert0(tradeListInDb.getPageSize(), pageSize, " pageSize <> page.pageSize ");
        AssertUtils.assert0(tradeListInDb.getTotalRecord(), totalRecord, " totalRecord <> page.totalRecord ");
        AssertUtils.assert0(tradeListInDb.getTotalPage(), totalPage, " totalPage <> page.totalPage ");
        AssertUtils.assert0(tradeListInDb.getList().size(), expectedListSize, " listSize <> page.listSize ");
        Log.info(formatLogInfoWithIdx(" test05ListBySourceCardNumberNotLike succeed "));
    }

    @Test
    public void test05ListBySourceCardNumberIn() {
        TaskTradeRepository taskTradeRepository = SpringContext.getBean(TaskTradeRepository.class);

        int loopCount = 15;
        int pageNo = 1, pageSize = 12;

        List<Trade> tradeList = new ArrayList<>();
        for (int i = 0; i < loopCount; i++) {
            tradeList.add(newRandomTrade(i));
        }
        int updated = taskTradeRepository.addAll(TEST_TASK_ID, tradeList);
        Log.info(formatLogInfoWithIdx(" 新增了 {0} 条交易信息 ", updated));

        Trade targetTrade = tradeList.get(0);
        int totalRecord = (int) tradeList
                .stream()
                .filter(row -> (row.getSourceCardNumber().equals(targetTrade.getSourceCardNumber())))
                .count();
        int totalPage = (((totalRecord - 1) / pageSize) + 1);
        int expectedListSize = pageSize < totalRecord ? pageSize : totalRecord;

        JSONObject queryMap = new JSONObject();
        queryMap.put(WebContextConstants.PAGE_NO, pageNo);
        queryMap.put(WebContextConstants.PAGE_SIZE, pageSize);
        queryMap.put("sourceCardNumberIn", Collections.singletonList(targetTrade.getSourceCardNumber()));
        Page<Trade> tradeListInDb = taskTradeRepository.listBy(TEST_TASK_ID, queryMap, true);
        for (Trade trade : tradeListInDb.getList()) {
            Trade tradeInDb = lookUp(tradeList, trade.getId());
            AssertUtils.assert0(toDebugJSON(trade), toDebugJSON(tradeInDb), " trade <> tradeInDb ");
        }

        AssertUtils.assert0(tradeListInDb.getPageNo(), pageNo, " pageNo <> page.pageNo ");
        AssertUtils.assert0(tradeListInDb.getPageSize(), pageSize, " pageSize <> page.pageSize ");
        AssertUtils.assert0(tradeListInDb.getTotalRecord(), totalRecord, " totalRecord <> page.totalRecord ");
        AssertUtils.assert0(tradeListInDb.getTotalPage(), totalPage, " totalPage <> page.totalPage ");
        AssertUtils.assert0(tradeListInDb.getList().size(), expectedListSize, " listSize <> page.listSize ");
        Log.info(formatLogInfoWithIdx(" test05ListBySourceCardNumberIn succeed "));
    }

    @Test
    public void test05ListBySourceCardNumberNotIn() {
        TaskTradeRepository taskTradeRepository = SpringContext.getBean(TaskTradeRepository.class);

        int loopCount = 15;
        int pageNo = 1, pageSize = 12;

        List<Trade> tradeList = new ArrayList<>();
        for (int i = 0; i < loopCount; i++) {
            tradeList.add(newRandomTrade(i));
        }
        int updated = taskTradeRepository.addAll(TEST_TASK_ID, tradeList);
        Log.info(formatLogInfoWithIdx(" 新增了 {0} 条交易信息 ", updated));

        Trade targetTrade = tradeList.get(0);
        int totalRecord = (int) tradeList
                .stream()
                .filter(row -> (!row.getSourceCardNumber().equals(targetTrade.getSourceCardNumber())))
                .count();
        int totalPage = (((totalRecord - 1) / pageSize) + 1);
        int expectedListSize = pageSize < totalRecord ? pageSize : totalRecord;

        JSONObject queryMap = new JSONObject();
        queryMap.put(WebContextConstants.PAGE_NO, pageNo);
        queryMap.put(WebContextConstants.PAGE_SIZE, pageSize);
        queryMap.put("sourceCardNumberNotIn", Collections.singletonList(targetTrade.getSourceCardNumber()));
        Page<Trade> tradeListInDb = taskTradeRepository.listBy(TEST_TASK_ID, queryMap, true);
        for (Trade trade : tradeListInDb.getList()) {
            Trade tradeInDb = lookUp(tradeList, trade.getId());
            AssertUtils.assert0(toDebugJSON(trade), toDebugJSON(tradeInDb), " trade <> tradeInDb ");
        }

        AssertUtils.assert0(tradeListInDb.getPageNo(), pageNo, " pageNo <> page.pageNo ");
        AssertUtils.assert0(tradeListInDb.getPageSize(), pageSize, " pageSize <> page.pageSize ");
        AssertUtils.assert0(tradeListInDb.getTotalRecord(), totalRecord, " totalRecord <> page.totalRecord ");
        AssertUtils.assert0(tradeListInDb.getTotalPage(), totalPage, " totalPage <> page.totalPage ");
        AssertUtils.assert0(tradeListInDb.getList().size(), expectedListSize, " listSize <> page.listSize ");
        Log.info(formatLogInfoWithIdx(" test05ListBySourceCardNumberNotIn succeed "));
    }

    @Test
    public void test06Update() {
        TaskTradeRepository taskTradeRepository = SpringContext.getBean(TaskTradeRepository.class);

        Trade trade = newRandomTrade(0);
        int updated = taskTradeRepository.save(TEST_TASK_ID, trade);
        Log.info(formatLogInfoWithIdx(" 新增了 {0} 条交易信息 ", updated));

        trade.setMemo(null);
        trade.setRemark(null);
        trade.setCurrencyCode(null);
        trade.setCashMarks(null);
        trade.setSourceCardNumber("updated");
        trade.setTargetCardNumber("updated");
        trade.setCashMarks("updated");
        trade.setIp("updated");
        trade.setMac("updated");
        updated = taskTradeRepository.update(TEST_TASK_ID, trade);
        Log.info(formatLogInfoWithIdx(" 更新了 {0} 条交易信息 ", updated));

        String savedId = trade.getId();
        Trade tradeInDb = taskTradeRepository.findById(TEST_TASK_ID, savedId);

        AssertUtils.assert0(toDebugJSON(trade), toDebugJSON(tradeInDb), " trade <> tradeInDb ");
        Log.info(formatLogInfoWithIdx(" test06Update succeed "));
    }

    @Test
    public void test07UpdateNotNull() {
        TaskTradeRepository taskTradeRepository = SpringContext.getBean(TaskTradeRepository.class);

        Trade trade = newRandomTrade(0);
        int updated = taskTradeRepository.save(TEST_TASK_ID, trade);
        Log.info(formatLogInfoWithIdx(" 新增了 {0} 条交易信息 ", updated));

        trade.setMemo(null);
        trade.setRemark(null);
        trade.setCurrencyCode(null);
        trade.setCashMarks(null);
        trade.setSourceCardNumber("updated");
        trade.setTargetCardNumber("updated");
        trade.setCashMarks("updated");
        trade.setIp("updated");
        trade.setMac("updated");
        updated = taskTradeRepository.updateNotNull(TEST_TASK_ID, trade);
        Log.info(formatLogInfoWithIdx(" 更新了 {0} 条交易信息 ", updated));

        String savedId = trade.getId();
        Trade tradeInDb = taskTradeRepository.findById(TEST_TASK_ID, savedId);

        AssertUtils.assert0(toDebugJSON(trade), toDebugJSON(tradeInDb), false, " trade == tradeInDb ");
        AssertUtils.assert0(tradeInDb.getMemo(), null, false, " memo == null ");
        AssertUtils.assert0(tradeInDb.getRemark(), null, false, " remark == null ");
        AssertUtils.assert0(tradeInDb.getCurrencyCode(), null, false, " currencyCode == null ");
        AssertUtils.assert0(tradeInDb.getCashMarks(), null, false, " cashMarks == null ");
        AssertUtils.assert0(tradeInDb.getSourceCardNumber(), "updated", true, " sourceCardNumber == updated ");
        AssertUtils.assert0(tradeInDb.getTargetCardNumber(), "updated", true, " sourceCardNumber == updated ");
        Log.info(formatLogInfoWithIdx(" test06Update succeed "));
    }

    @Test
    public void test08UpdateBy() {
        TaskTradeRepository taskTradeRepository = SpringContext.getBean(TaskTradeRepository.class);

        int loopCount = 15;
        JSONObject queryMap = new JSONObject();

        List<Trade> tradeList = new ArrayList<>();
        for (int i = 0; i < loopCount; i++) {
            tradeList.add(newRandomTrade(i));
        }
        int updated = taskTradeRepository.addAll(TEST_TASK_ID, tradeList);
        Log.info(formatLogInfoWithIdx(" 新增了 {0} 条交易信息 ", updated));

        Trade targetTrade = tradeList.get(0);
        queryMap.put("id", targetTrade.getId());
        targetTrade.setMemo(null);
        targetTrade.setRemark(null);
        targetTrade.setCurrencyCode(null);
        targetTrade.setCashMarks(null);
        targetTrade.setSourceCardNumber("updated");
        targetTrade.setTargetCardNumber("updated");
        targetTrade.setCashMarks("updated");
        targetTrade.setIp("updated");
        targetTrade.setMac("updated");
        updated = taskTradeRepository.updateBy(TEST_TASK_ID, targetTrade, queryMap, true);
        Log.info(formatLogInfoWithIdx(" 更新了 {0} 条交易信息 ", updated));

        Page<Trade> tradeListInDb = taskTradeRepository.listBy(TEST_TASK_ID, queryMap, true);
        for (Trade tradeInDb : tradeListInDb.getList()) {
            Trade trade = lookUp(tradeList, targetTrade.getId());
            AssertUtils.assert0(toDebugJSON(trade), toDebugJSON(tradeInDb), " trade <> tradeInDb ");
        }
        Log.info(formatLogInfoWithIdx(" test08UpdateBy succeed "));
    }

    @Test
    public void test09UpdateNotNullBy() {
        TaskTradeRepository taskTradeRepository = SpringContext.getBean(TaskTradeRepository.class);

        int loopCount = 15;
        JSONObject queryMap = new JSONObject();

        List<Trade> tradeList = new ArrayList<>();
        for (int i = 0; i < loopCount; i++) {
            tradeList.add(newRandomTrade(i));
        }
        int updated = taskTradeRepository.addAll(TEST_TASK_ID, tradeList);
        Log.info(formatLogInfoWithIdx(" 新增了 {0} 条交易信息 ", updated));

        Trade targetTrade = tradeList.get(0);
        queryMap.put("id", targetTrade.getId());
        targetTrade.setMemo(null);
        targetTrade.setRemark(null);
        targetTrade.setCurrencyCode(null);
        targetTrade.setCashMarks(null);
        targetTrade.setSourceCardNumber("updated");
        targetTrade.setTargetCardNumber("updated");
        targetTrade.setCashMarks("updated");
        targetTrade.setIp("updated");
        targetTrade.setMac("updated");
        updated = taskTradeRepository.updateNotNullBy(TEST_TASK_ID, targetTrade, queryMap, true);
        Log.info(formatLogInfoWithIdx(" 更新了 {0} 条交易信息 ", updated));

        Page<Trade> tradeListInDb = taskTradeRepository.listBy(TEST_TASK_ID, queryMap, true);
        for (Trade tradeInDb : tradeListInDb.getList()) {
            Trade trade = lookUp(tradeList, targetTrade.getId());
            AssertUtils.assert0(toDebugJSON(trade), toDebugJSON(tradeInDb), false, " trade == tradeInDb ");
            AssertUtils.assert0(tradeInDb.getMemo(), null, false, " memo == null ");
            AssertUtils.assert0(tradeInDb.getRemark(), null, false, " remark == null ");
            AssertUtils.assert0(tradeInDb.getCurrencyCode(), null, false, " currencyCode == null ");
            AssertUtils.assert0(tradeInDb.getCashMarks(), null, false, " cashMarks == null ");
            AssertUtils.assert0(tradeInDb.getSourceCardNumber(), "updated", true, " sourceCardNumber == updated ");
            AssertUtils.assert0(tradeInDb.getTargetCardNumber(), "updated", true, " sourceCardNumber == updated ");
        }
        Log.info(formatLogInfoWithIdx(" test09UpdateNotNullBy succeed "));
    }

    @Test
    public void test10DeleteById() {
        TaskTradeRepository taskTradeRepository = SpringContext.getBean(TaskTradeRepository.class);

        Trade trade = newRandomTrade(0);
        int updated = taskTradeRepository.add(TEST_TASK_ID, trade);
        Log.info(formatLogInfoWithIdx(" 新增了 {0} 条交易信息 ", updated));

        String savedId = trade.getId();
        updated = taskTradeRepository.deleteById(TEST_TASK_ID, savedId);
        Log.info(formatLogInfoWithIdx(" 移除了 {0} 条交易信息 ", updated));

        Trade tradeInDb = taskTradeRepository.findById(TEST_TASK_ID, savedId);

        AssertUtils.assert0(tradeInDb, null, " tradeInDb <> null ");
        Log.info(formatLogInfoWithIdx(" test10DeleteById succeed "));
    }

    @Test
    public void test11DeleteBy() {
        TaskTradeRepository taskTradeRepository = SpringContext.getBean(TaskTradeRepository.class);

        int loopCount = 15;
        JSONObject queryMap = new JSONObject();

        List<Trade> tradeList = new ArrayList<>();
        for (int i = 0; i < loopCount; i++) {
            tradeList.add(newRandomTrade(i));
        }
        int updated = taskTradeRepository.addAll(TEST_TASK_ID, tradeList);
        Log.info(formatLogInfoWithIdx(" 新增了 {0} 条交易信息 ", updated));


        updated = taskTradeRepository.deleteBy(TEST_TASK_ID, queryMap, true);
        Log.info(formatLogInfoWithIdx(" 移除了 {0} 条交易信息 ", updated));

        List<Trade> tradeListInDb = taskTradeRepository.allBy(TEST_TASK_ID, queryMap, true);

        AssertUtils.assert0(tradeListInDb.size(), 0, " tradeListInDb.length <> 0 ");
        Log.info(formatLogInfoWithIdx(" test11DeleteBy succeed "));
    }

}
