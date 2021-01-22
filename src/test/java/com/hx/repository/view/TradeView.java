package com.hx.repository.view;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * TradeView
 *
 * @author Jerry.X.He
 * @version 1.0
 * @date 2021-01-22 14:54
 */
@Data
public class TradeView implements Serializable {

    private String sourceAccountName;

    private String sourceCardNumber;

    private String sourceIdCard;

    private String sourceDepositedBank;

    private String targetAccountName;

    private String targetCardNumber;

    private String targetIdCard;

    private String targetDepositedBank;

    private String tradeType;

    private Long tradeAt;

    private BigDecimal tradeAmount;

    private BigDecimal tradeBalance;

    private BigDecimal rollInAmount;

    private BigDecimal rollOutAmount;

    private BigDecimal cashInAmount;

    private BigDecimal cashOutAmount;

    private BigDecimal costAmount;

    private BigDecimal incomeAmount;

    private BigDecimal otherAmount;

    private BigDecimal flowInAmount;

    private BigDecimal flowOutAmount;

    private BigDecimal flowDifferenceAmount;

    private BigDecimal flowTotalAmount;

    private Long tradeNumber;

    private String tradeNetwork;

    private String tradeNetworkCode;

    private String memo;

    private String currencyCode;

    private String remark;

    private String lendingMarks;

    private String cashMarks;

    private Long firstTradeAt;

    private Long lastTradeAt;

    private String timeline;

}
