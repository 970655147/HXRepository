package com.hx.repository.tests.mysql;

import com.hx.common.util.AssertUtils;
import com.hx.repository.domain.Trade;
import com.hx.repository.tests.base.MysqlBaseRepositoryTest;
import com.hx.repository.utils.MysqlUtils;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.Arrays;
import java.util.List;

/**
 * Test03MysqlUtils
 *
 * @author Jerry.X.He
 * @version 1.0
 * @date 2021-02-04 11:51
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Test03MysqlUtils extends MysqlBaseRepositoryTest {

    /** BASE_PATH */
    private static String BASE_PATH = System.getProperty("user.dir");
    /** BASE_IGNORE_PATH */
    private static String BASE_IGNORE_PATH = BASE_PATH + "/src/test/java/com/hx/repository/tests/mysql/ignore";

    @Test
    public void test01TradeSchemaWithFakePath() {
        Class<Trade> clazz = Trade.class;
        String filePath = "fakepath";
        List<String> lines = MysqlUtils.generateTableSchema(clazz, filePath);

        String createTableSignature = MysqlUtils.generateCreateTableSignature(clazz);
        boolean createTableExists = lines.stream().anyMatch(line -> line.contains(createTableSignature));
        AssertUtils.assert0(createTableExists, " !createTableExists ");
    }

    @Test
    public void test01TradeSchemaWithExistsSchema() {
        Class<Trade> clazz = Trade.class;
        String fileName = "Test01TradeSchemaWithExistsSchema";
        String filePath = BASE_IGNORE_PATH + String.format("/%s.sql", fileName);
        saveLines(tradeSchemaWithExistsSchemaCode(fileName), filePath);

        List<String> lines = MysqlUtils.generateTableSchema(clazz, filePath);
        saveLines(lines, filePath);
    }

    @Test
    public void test01TradeSchemaWithNotExistsSchema() {
        Class<Trade> clazz = Trade.class;
        String fileName = "Test01TradeSchemaWithNotExistsSchema";
        String filePath = BASE_IGNORE_PATH + String.format("/%s.sql", fileName);
        saveLines(tradeSchemaWithNotExistsSchemaCode(fileName), filePath);

        List<String> lines = MysqlUtils.generateTableSchema(clazz, filePath);
        saveLines(lines, filePath);
    }

    // ----------------------------------------- 辅助方法 -----------------------------------------

    /**
     * tradeSchemaWithExistsSchemaCode
     *
     * @return java.util.List<java.lang.String>
     * @author Jerry.X.He
     * @date 2021-01-21 18:14
     */
    private List<String> tradeSchemaWithExistsSchemaCode(String className) {
        String code = "CREATE TABLE TRADE (\n" +
                      "    SOURCE_ACCOUNT_NAME   VARCHAR(111)            DEFAULT NULL,\n" +
                      "    SOURCE_CARD_NUMBER    VARCHAR(111)   NOT NULL DEFAULT NULL,\n" +
                      "    SOURCE_ACCOUNT_NUMBER VARCHAR(111)            DEFAULT NULL,\n" +
                      "    SOURCE_ID_CARD        VARCHAR(111)            DEFAULT NULL,\n" +
                      "    SOURCE_DEPOSITED_BANK VARCHAR(111)            DEFAULT NULL,\n" +
                      "    TARGET_ACCOUNT_NAME   VARCHAR(111)            DEFAULT NULL,\n" +
                      "    TARGET_CARD_NUMBER    VARCHAR(111)            DEFAULT NULL,\n" +
                      "    TARGET_ACCOUNT_NUMBER VARCHAR(111)            DEFAULT NULL,\n" +
                      "    TARGET_ID_CARD        VARCHAR(111)            DEFAULT NULL,\n" +
                      "    TARGET_DEPOSITED_BANK VARCHAR(111)            DEFAULT NULL,\n" +
                      "    TRADE_TYPE            VARCHAR(111)   NOT NULL DEFAULT NULL,\n" +
                      "    TRADE_AT              BIGINT         NOT NULL DEFAULT NULL,\n" +
                      "    TRADE_AMOUNT          NUMERIC(30,3) NOT NULL DEFAULT NULL,\n" +
                      "    TRADE_BALANCE         NUMERIC(30,3)          DEFAULT NULL,\n" +
                      "    TRADE_NETWORK         VARCHAR(111)            DEFAULT NULL,\n" +
                      "    TRADE_NETWORK_CODE    VARCHAR(111)            DEFAULT NULL,\n" +
                      "    MEMO                  VARCHAR(111)            DEFAULT NULL,\n" +
                      "    REMARK                VARCHAR(111)            DEFAULT NULL,\n" +
                      "    CURRENCY_CODE         VARCHAR(111)            DEFAULT NULL,\n" +
                      "    LENDING_MARKS         VARCHAR(111)            DEFAULT NULL,\n" +
                      "    CASH_MARKS            VARCHAR(111)            DEFAULT NULL,\n" +
                      "    IP                    VARCHAR(111)            DEFAULT NULL,\n" +
                      "    MAC                   VARCHAR(111)            DEFAULT NULL,\n" +
                      "    SERIAL_NUMBER         VARCHAR(111)            DEFAULT NULL,\n" +
                      "    IMPORT_RECORD_ID      VARCHAR(111)            DEFAULT NULL,\n" +
                      "    ID                    VARCHAR(111)            DEFAULT NULL,\n" +
                      "    SOURCE                VARCHAR(111)   NOT NULL DEFAULT 'SYSTEM',\n" +
                      "    ENABLED               BOOLEAN        NOT NULL DEFAULT TRUE,\n" +
                      "    LOCKED                BOOLEAN        NOT NULL DEFAULT FALSE,\n" +
                      "    DELETED               BOOLEAN        NOT NULL DEFAULT FALSE,\n" +
                      "    CREATED_BY            VARCHAR(111)            DEFAULT NULL,\n" +
                      "    CREATED_BY_USER       VARCHAR(111)            DEFAULT NULL,\n" +
                      "    CREATED_ON            BIGINT                  DEFAULT NULL,\n" +
                      "    LAST_UPDATED_BY       VARCHAR(111)            DEFAULT NULL,\n" +
                      "    LAST_UPDATED_BY_USER  VARCHAR(111)            DEFAULT NULL,\n" +
                      "    LAST_UPDATED_ON       BIGINT                  DEFAULT NULL,\n" +
                      "    VERSION_NUMBER        BIGINT                  DEFAULT 1,\n" +
                      "    PRIMARY KEY (ID)\n" +
                      ");\n";
        return Arrays.asList(code.split("\n"));
    }

    /**
     * tradeSchemaWithNotExistsSchemaCode
     *
     * @return java.util.List<java.lang.String>
     * @author Jerry.X.He
     * @date 2021-01-21 18:14
     */
    private List<String> tradeSchemaWithNotExistsSchemaCode(String className) {
        String code = "CREATE TABLE TRADE1 (\n" +
                      "    SOURCE_ACCOUNT_NAME   VARCHAR(255)            DEFAULT NULL,\n" +
                      "    SOURCE_CARD_NUMBER    VARCHAR(255)   NOT NULL DEFAULT NULL,\n" +
                      "    SOURCE_ACCOUNT_NUMBER VARCHAR(255)            DEFAULT NULL,\n" +
                      "    SOURCE_ID_CARD        VARCHAR(255)            DEFAULT NULL,\n" +
                      "    SOURCE_DEPOSITED_BANK VARCHAR(255)            DEFAULT NULL,\n" +
                      "    TARGET_ACCOUNT_NAME   VARCHAR(255)            DEFAULT NULL,\n" +
                      "    TARGET_CARD_NUMBER    VARCHAR(255)            DEFAULT NULL,\n" +
                      "    TARGET_ACCOUNT_NUMBER VARCHAR(255)            DEFAULT NULL,\n" +
                      "    TARGET_ID_CARD        VARCHAR(255)            DEFAULT NULL,\n" +
                      "    TARGET_DEPOSITED_BANK VARCHAR(255)            DEFAULT NULL,\n" +
                      "    TRADE_TYPE            VARCHAR(255)   NOT NULL DEFAULT NULL,\n" +
                      "    TRADE_AT              BIGINT         NOT NULL DEFAULT NULL,\n" +
                      "    TRADE_AMOUNT          NUMERIC(20,2) NOT NULL DEFAULT NULL,\n" +
                      "    TRADE_BALANCE         NUMERIC(20,2)          DEFAULT NULL,\n" +
                      "    TRADE_NETWORK         VARCHAR(255)            DEFAULT NULL,\n" +
                      "    TRADE_NETWORK_CODE    VARCHAR(255)            DEFAULT NULL,\n" +
                      "    MEMO                  VARCHAR(255)            DEFAULT NULL,\n" +
                      "    REMARK                VARCHAR(255)            DEFAULT NULL,\n" +
                      "    CURRENCY_CODE         VARCHAR(255)            DEFAULT NULL,\n" +
                      "    LENDING_MARKS         VARCHAR(255)            DEFAULT NULL,\n" +
                      "    CASH_MARKS            VARCHAR(255)            DEFAULT NULL,\n" +
                      "    IP                    VARCHAR(255)            DEFAULT NULL,\n" +
                      "    MAC                   VARCHAR(255)            DEFAULT NULL,\n" +
                      "    SERIAL_NUMBER         VARCHAR(255)            DEFAULT NULL,\n" +
                      "    IMPORT_RECORD_ID      VARCHAR(255)            DEFAULT NULL,\n" +
                      "    ID                    VARCHAR(255)            DEFAULT NULL,\n" +
                      "    SOURCE                VARCHAR(255)   NOT NULL DEFAULT 'SYSTEM',\n" +
                      "    ENABLED               BOOLEAN        NOT NULL DEFAULT TRUE,\n" +
                      "    LOCKED                BOOLEAN        NOT NULL DEFAULT FALSE,\n" +
                      "    DELETED               BOOLEAN        NOT NULL DEFAULT FALSE,\n" +
                      "    CREATED_BY            VARCHAR(255)            DEFAULT NULL,\n" +
                      "    CREATED_BY_USER       VARCHAR(255)            DEFAULT NULL,\n" +
                      "    CREATED_ON            BIGINT                  DEFAULT NULL,\n" +
                      "    LAST_UPDATED_BY       VARCHAR(255)            DEFAULT NULL,\n" +
                      "    LAST_UPDATED_BY_USER  VARCHAR(255)            DEFAULT NULL,\n" +
                      "    LAST_UPDATED_ON       BIGINT                  DEFAULT NULL,\n" +
                      "    VERSION_NUMBER        BIGINT                  DEFAULT 1,\n" +
                      "    PRIMARY KEY (ID)\n" +
                      ");\n";
        return Arrays.asList(code.split("\n"));
    }

}
