package com.hx.repository.tests.utils;

import com.hx.common.util.AssertUtils;
import com.hx.repository.domain.Trade;
import com.hx.repository.tests.base.BaseTest;
import com.hx.repository.utils.TypeCastUtils;
import com.hx.repository.view.TradeView;
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
public class Test01TypeCastUtils extends BaseTest {

    /** BASE_PATH */
    private static String BASE_PATH = System.getProperty("user.dir");
    /** BASE_IGNORE_PATH */
    private static String BASE_IGNORE_PATH = BASE_PATH + "/src/test/java/com/hx/repository/tests/utils/ignore";

    @Test
    public void test01TradeToJsonCastWithFakePath() {
        Class<Trade> clazz = Trade.class;
        String filePath = "fakepath";
        List<String> lines = TypeCastUtils.generateJsonCaster(clazz, filePath);

        String toJsonMethodSignature = TypeCastUtils.generateToJsonSignature(clazz);
        boolean toJsonMethodExists = lines.stream().anyMatch(line -> line.contains(toJsonMethodSignature));
        AssertUtils.assert0(toJsonMethodExists, " !toJsonMethodExists ");

        String fromJsonMethodSignature = TypeCastUtils.generateToJsonSignature(clazz);
        boolean fromJsonMethodExists = lines.stream().anyMatch(line -> line.contains(fromJsonMethodSignature));
        AssertUtils.assert0(fromJsonMethodExists, " !fromJsonMethodExists ");
    }

    @Test
    public void test01TradeToJsonCastWithExistsMethod() {
        Class<Trade> clazz = Trade.class;
        String fileName = "Test01TradeToJsonCastWithExistsMethod";
        String filePath = BASE_IGNORE_PATH + String.format("/%s.java", fileName);
        saveLines(tradeConverterExistsMethodCode(fileName), filePath);

        List<String> lines = TypeCastUtils.generateJsonCaster(clazz, filePath);
        saveLines(lines, filePath);
        compileGeneratedJavaThenAssert(filePath);
    }

    @Test
    public void test01TradeToJsonCastWithNotExistsMethod() {
        Class<Trade> clazz = Trade.class;
        String fileName = "Test01TradeToJsonCastWithNotExistsMethod";
        String filePath = BASE_IGNORE_PATH + String.format("/%s.java", fileName);
        saveLines(tradeConverterNotExistsMethodCode(fileName), filePath);

        List<String> lines = TypeCastUtils.generateJsonCaster(clazz, filePath);
        saveLines(lines, filePath);
        compileGeneratedJavaThenAssert(filePath);
    }

    @Test
    public void test02TradeToTypeCastWithFakePath() {
        Class<Trade> sourceClazz = Trade.class;
        Class<TradeView> targetClazz = TradeView.class;
        String filePath = "fakepath";
        List<String> lines = TypeCastUtils.generateToTypeCaster(sourceClazz, targetClazz, filePath);

        String toTypeMethodSignature = TypeCastUtils.generateToTypeSignature(sourceClazz, targetClazz);
        boolean toTypeMethodExists = lines.stream().anyMatch(line -> line.contains(toTypeMethodSignature));
        AssertUtils.assert0(toTypeMethodExists, " !toTypeMethodExists ");

        String fromTypeMethodSignature = TypeCastUtils.generateToTypeSignature(targetClazz, sourceClazz);
        boolean fromTypeMethodExists = lines.stream().anyMatch(line -> line.contains(fromTypeMethodSignature));
        AssertUtils.assert0(fromTypeMethodExists, " !fromTypeMethodExists ");
    }

    @Test
    public void test02TradeToTypeCastWithExistsMethod() {
        String fileName = "Test02TradeToTypeCastWithExistsMethod";
        String filePath = BASE_IGNORE_PATH + String.format("/%s.java", fileName);
        saveLines(tradeConverterExistsMethodCode(fileName), filePath);

        Class<Trade> sourceClazz = Trade.class;
        Class<TradeView> targetClazz = TradeView.class;
        List<String> lines = TypeCastUtils.generateToTypeCaster(sourceClazz, targetClazz, filePath);
        saveLines(lines, filePath);
        compileGeneratedJavaThenAssert(filePath);
    }

    @Test
    public void test02TradeToTypeCastWithNotExistsMethod() {
        String fileName = "Test02TradeToTypeCastWithNotExistsMethod";
        String filePath = BASE_IGNORE_PATH + String.format("/%s.java", fileName);
        saveLines(tradeConverterNotExistsMethodCode(fileName), filePath);

        Class<Trade> sourceClazz = Trade.class;
        Class<TradeView> targetClazz = TradeView.class;
        List<String> lines = TypeCastUtils.generateToTypeCaster(sourceClazz, targetClazz, filePath);
        saveLines(lines, filePath);
        compileGeneratedJavaThenAssert(filePath);
    }

    // ----------------------------------------- 辅助方法 -----------------------------------------

    /**
     * tradeConverterExistsMethodCode
     *
     * @return java.util.List<java.lang.String>
     * @author Jerry.X.He
     * @date 2021-01-21 18:14
     */
    private List<String> tradeConverterExistsMethodCode(String className) {
        String code = "package com.hx.repository.tests.utils.ignore;\n" +
                      "\n" +
                      "import com.alibaba.fastjson.JSONObject;\n" +
                      "import com.hx.repository.domain.Trade;\n" +
                      "import com.hx.repository.view.TradeView;\n" +
                      "\n" +
                      "import java.math.BigDecimal;\n" +
                      "\n" +
                      "// " + className + "\n" +
                      "public class " + className + " {\n" +
                      "\n" +
                      "    public static JSONObject castTradeToJson(Trade entity) {\n" +
                      "        JSONObject result = new JSONObject();\n" +
                      "        return result;\n" +
                      "    }\n" +
                      "\n" +
                      "    public static Trade castJsonToTrade(JSONObject json) {\n" +
                      "        Trade result = new Trade();\n" +
                      "        return result;\n" +
                      "    }\n" +
                      "\n" +
                      "    public static TradeView castTradeToTradeView(Trade entity) {\n" +
                      "        TradeView result = new TradeView();\n" +
                      "        return result;\n" +
                      "    }\n" +
                      "\n" +
                      "    public static Trade castTradeViewToTrade(TradeView entity) {\n" +
                      "        Trade result = new Trade();\n" +
                      "        return result;\n" +
                      "    }\n" +
                      "\n" +
                      "}\n";
        return Arrays.asList(code.split("\n"));
    }

    /**
     * tradeConverterNotExistsMethodCode
     *
     * @return java.util.List<java.lang.String>
     * @author Jerry.X.He
     * @date 2021-01-21 18:14
     */
    private List<String> tradeConverterNotExistsMethodCode(String className) {
        String code = "package com.hx.repository.tests.utils.ignore;\n" +
                      "\n" +
                      "import com.alibaba.fastjson.JSONObject;\n" +
                      "import com.hx.repository.domain.Trade;\n" +
                      "import com.hx.repository.view.TradeView;\n" +
                      "\n" +
                      "import java.math.BigDecimal;\n" +
                      "\n" +
                      "// " + className + "\n" +
                      "public class " + className + " {\n" +
                      "\n" +
                      "    public static JSONObject castTrade1ToJson(Trade entity) {\n" +
                      "        JSONObject result = new JSONObject();\n" +
                      "        return result;\n" +
                      "    }\n" +
                      "\n" +
                      "    public static Trade castJsonToTrade1(JSONObject json) {\n" +
                      "        Trade result = new Trade();\n" +
                      "        return result;\n" +
                      "    }\n" +
                      "\n" +
                      "    public static TradeView castTrade1ToTradeView(Trade entity) {\n" +
                      "        TradeView result = new TradeView();\n" +
                      "        return result;\n" +
                      "    }\n" +
                      "\n" +
                      "    public static Trade castTradeViewToTrade1(TradeView entity) {\n" +
                      "        Trade result = new Trade();\n" +
                      "        return result;\n" +
                      "    }\n" +
                      "\n" +
                      "}\n";
        return Arrays.asList(code.split("\n"));
    }

}
