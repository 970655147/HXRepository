package com.hx.repository.tests.typecast;

import com.hx.common.util.AssertUtils;
import com.hx.log.util.Tools;
import com.hx.repository.domain.Trade;
import com.hx.repository.tests.base.BaseServiceTest;
import com.hx.repository.utils.TypeCastUtils;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.Arrays;
import java.util.List;

/**
 * Test01TypeCastUtils
 *
 * @author Jerry.X.He <970655147@qq.com>
 * @version 1.0
 * @date 2021-01-21 17:16
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Test01TypeCastUtils extends BaseServiceTest {

    /** BASE_PATH */
    private static String BASE_PATH = System.getProperty("user.dir");

    @Test
    public void test01GenerateTradeCastWithFakePath() {
        Class<Trade> clazz = Trade.class;
        String filePath = "fakepath";
        List<String> lines = TypeCastUtils.generateJsonTypeCaster(clazz, filePath);

        String toJsonMethodSignature = TypeCastUtils.generateToJsonSignature(clazz);
        boolean toJsonMethodExists = lines.stream().anyMatch(line -> line.contains(toJsonMethodSignature));
        AssertUtils.assert0(toJsonMethodExists, " !toJsonMethodExists ");

        String fromJsonMethodSignature = TypeCastUtils.generateToJsonSignature(clazz);
        boolean fromJsonMethodExists = lines.stream().anyMatch(line -> line.contains(fromJsonMethodSignature));
        AssertUtils.assert0(fromJsonMethodExists, " !fromJsonMethodExists ");
    }

    @Test
    public void test01GenerateTradeCastWithNone() {
        Class<Trade> clazz = Trade.class;
        String filePath = BASE_PATH + "/src/test/resources/ignore/TradeConverter01None.java";
        saveLines(Arrays.asList(), filePath);

        List<String> lines = TypeCastUtils.generateJsonTypeCaster(clazz, filePath);
        saveLines(lines, filePath);
    }

    @Test
    public void test01GenerateTradeCastWithExistsTradeMethod() {
        Class<Trade> clazz = Trade.class;
        String filePath = BASE_PATH + "/src/test/resources/ignore/TradeConverter01ExistsTradeMethod.java";
        saveLines(tradeConverter01ExistsTradeMethodCode(), filePath);

        List<String> lines = TypeCastUtils.generateJsonTypeCaster(clazz, filePath);
        saveLines(lines, filePath);
    }

    @Test
    public void test01GenerateTradeCastWithNotExistsTradeMethod() {
        Class<Trade> clazz = Trade.class;
        String filePath = BASE_PATH + "/src/test/resources/ignore/TradeConverter01NotExistsTradeMethod.java";
        saveLines(tradeConverter01NotExistsTradeMethodCode(), filePath);

        List<String> lines = TypeCastUtils.generateJsonTypeCaster(clazz, filePath);
        saveLines(lines, filePath);
    }

    // ----------------------------------------- 辅助方法 -----------------------------------------

    /**
     * 保存给定的 所有的行到 file
     *
     * @param lines    lines
     * @param filePath filePath
     * @return void
     * @author Jerry.X.He
     * @date 2021-01-21 18:04
     */
    private void saveLines(List<String> lines, String filePath) {
        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            sb.append(line).append(Tools.CRLF);
        }

        try {
            Tools.save(sb.toString(), filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * tradeConverter01ExistsTradeMethodCode
     *
     * @return java.util.List<java.lang.String>
     * @author Jerry.X.He
     * @date 2021-01-21 18:14
     */
    private List<String> tradeConverter01ExistsTradeMethodCode() {
        String code = "// TradeConverter01ExistsTradeMethod\n" +
                      "public static class TradeConverter01ExistsTradeMethod {\n" +
                      "    \n" +
                      "    public JSONObject castTradeToJson(Trade entity) {\n" +
                      "        JSONObject result = new JSONObject();\n" +
                      "        return result;\n" +
                      "    }\n" +
                      "\n" +
                      "    public Trade castJsonToTrade(JSONObject json) {\n" +
                      "        Trade result = new Trade();\n" +
                      "        return result;\n" +
                      "    }\n" +
                      "\n" +
                      "}\n";
        return Arrays.asList(code.split("\n"));
    }

    /**
     * tradeConverter01NotExistsTradeMethodCode
     *
     * @return java.util.List<java.lang.String>
     * @author Jerry.X.He
     * @date 2021-01-21 18:14
     */
    private List<String> tradeConverter01NotExistsTradeMethodCode() {
        String code = "// TradeConverter01NotExistsTradeMethod\n" +
                      "public static class TradeConverter01ExistsTradeMethod {\n" +
                      "    \n" +
                      "    public JSONObject castTradeEEEEEToJson(Trade entity) {\n" +
                      "        JSONObject result = new JSONObject();\n" +
                      "        return result;\n" +
                      "    }\n" +
                      "\n" +
                      "    public Trade castJsonToTradeEEEEE(JSONObject json) {\n" +
                      "        Trade result = new Trade();\n" +
                      "        return result;\n" +
                      "    }\n" +
                      "\n" +
                      "}\n";
        return Arrays.asList(code.split("\n"));
    }

}
