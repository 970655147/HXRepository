package com.hx.repository.tests.sqlite;

import com.hx.repository.context.SpringContext;
import com.hx.repository.domain.Trade;
import com.hx.repository.sqlite.TradeRepository;
import com.hx.repository.tests.base.BaseServiceTest;
import org.apache.commons.lang3.RandomUtils;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Test01TradeRepository
 *
 * @author Jerry.X.He <970655147@qq.com>
 * @version 1.0
 * @date 2021-01-18 10:50
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Test01TradeRepository extends BaseServiceTest {

    @Test
    public void test01Save() {
        TradeRepository tradeRepository = SpringContext.getBean(TradeRepository.class);
        int i = 0;
        Trade trade = new Trade();
        String idx = String.format("%07d", i);
        String targetIdx = String.format("%07d", RandomUtils.nextInt(0, 100));
        trade.setId(UUID.randomUUID().toString());
        trade.setSourceAccountName(String.format("账户%s", idx));
        trade.setSourceAccountNumber(String.format("%s", idx));
        trade.setSourceCardNumber(String.format("%s", idx));
        trade.setSourceIdCard(String.format("%s", idx));
        trade.setSourceDepositedBank(String.format("银行%s", idx));
        trade.setTargetAccountName(String.format("账户%s", targetIdx));
        trade.setTargetAccountNumber(String.format("%s", targetIdx));
        trade.setTargetCardNumber(String.format("%s", targetIdx));
        trade.setTargetIdCard(String.format("%s", targetIdx));
        trade.setTargetDepositedBank(String.format("银行%s", targetIdx));

        trade.setTradeType("ROLL_OUT");
        trade.setTradeAt(System.currentTimeMillis());
        trade.setTradeAmount(new BigDecimal(RandomUtils.nextInt(0, 1000)));
        trade.setTradeBalance(new BigDecimal(RandomUtils.nextInt(0, 1000)));
        trade.setTradeNetwork(String.format("网点%s", idx));
        trade.setTradeNetworkCode(String.format("network-%s", idx));
        trade.setMemo(String.format("memo-%s", idx));
        trade.setRemark(String.format("remark-%s", idx));
        trade.setCurrencyCode("$");
        trade.setLendingMarks("lendingMarks");
        trade.setCashMarks("cashMarks");
        trade.setIp(String.format("ip-%s", idx));
        trade.setMac(String.format("mac-%s", idx));
        trade.setSerialNumber(idx);

        trade.setCreatedBy("a50c5403-c58b-4d9a-bb7b-3c6c520f5942");
        trade.setCreatedByUser("hexiong");
        trade.setCreatedOn(System.currentTimeMillis());
        trade.setLastUpdatedBy("a50c5403-c58b-4d9a-bb7b-3c6c520f5942");
        trade.setLastUpdatedByUser("hexiong");
        trade.setLastUpdatedOn(System.currentTimeMillis());

        tradeRepository.save(trade);

    }

}
