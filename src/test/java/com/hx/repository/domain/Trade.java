package com.hx.repository.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Trade
 *
 * @author Jerry.X.He
 * @version 1.0
 * @date 2021-01-18 10:19
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "TRADE")
public class Trade extends BaseEntity {

    private static final long serialVersionUID = 100L;

    /** 本端名称 */
    @Column(name = "SOURCE_ACCOUNT_NAME")
    private String sourceAccountName;

    /** 本端卡号 */
    @Column(name = "SOURCE_CARD_NUMBER", nullable = false)
    private String sourceCardNumber;

    /** 本端账号 */
    @Column(name = "SOURCE_ACCOUNT_NUMBER")
    private String sourceAccountNumber;

    /** 本端身份证号 */
    @Column(name = "SOURCE_ID_CARD")
    private String sourceIdCard;

    /** 本端开户行 */
    @Column(name = "SOURCE_DEPOSITED_BANK")
    private String sourceDepositedBank;

    /** 对手名称 */
    @Column(name = "TARGET_ACCOUNT_NAME")
    private String targetAccountName;

    /** 对手卡号 */
    @Column(name = "TARGET_CARD_NUMBER")
    private String targetCardNumber;

    /** 对手账号 */
    @Column(name = "TARGET_ACCOUNT_NUMBER")
    private String targetAccountNumber;

    /** 对手身份证号 */
    @Column(name = "TARGET_ID_CARD")
    private String targetIdCard;

    /** 对手开户行 */
    @Column(name = "TARGET_DEPOSITED_BANK")
    private String targetDepositedBank;

    /** 交易类型 */
    @Column(name = "TRADE_TYPE", nullable = false)
    @Enumerated(EnumType.STRING)
    private String tradeType;

    /** 交易时间 */
    @Column(name = "TRADE_AT", nullable = false)
    private Long tradeAt;

    /** 交易金额 */
    @Column(name = "TRADE_AMOUNT", nullable = false)
    private BigDecimal tradeAmount;

    /** 交易余额 */
    @Column(name = "TRADE_BALANCE")
    private BigDecimal tradeBalance;

    /** 交易网点 */
    @Column(name = "TRADE_NETWORK")
    private String tradeNetwork;

    /** 交易网点代码 */
    @Column(name = "TRADE_NETWORK_CODE")
    private String tradeNetworkCode;

    /** 摘要 */
    @Column(name = "MEMO")
    private String memo;

    /** 备注 */
    @Column(name = "REMARK")
    private String remark;

    /** 货币代码 */
    @Column(name = "CURRENCY_CODE")
    private String currencyCode;

    /** 借贷标识 */
    @Column(name = "LENDING_MARKS")
    private String lendingMarks;

    /** 现金标识 */
    @Column(name = "CASH_MARKS")
    private String cashMarks;

    /** IP */
    @Column(name = "IP")
    private String ip;

    /** MAC */
    @Column(name = "MAC")
    private String mac;

    /** 流水号 */
    @Column(name = "SERIAL_NUMBER")
    private String serialNumber;

    /** 导入记录ID */
    @Column(name = "IMPORT_RECORD_ID")
    private String importRecordId;

}
