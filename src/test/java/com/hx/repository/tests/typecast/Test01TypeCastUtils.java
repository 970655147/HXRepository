package com.hx.repository.tests.typecast;

import com.hx.common.util.AssertUtils;
import com.hx.log.util.Tools;
import com.hx.repository.domain.Trade;
import com.hx.repository.tests.base.BaseServiceTest;
import com.hx.repository.utils.TypeCastUtils;
import com.hx.repository.view.TradeView;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.util.Arrays;
import java.util.List;

import static com.hx.log.log.LogPatternUtils.formatLogInfoWithIdx;

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
    /** BASE_IGNORE_PATH */
    private static String BASE_IGNORE_PATH = BASE_PATH + "/src/test/java/com/hx/repository/tests/typecast/ignore";

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
     * 编译已经生成的 .java 文件, 断言给定的 生成的 java 文件是否合法
     *
     * @param filePath filePath
     * @return void
     * @author Jerry.X.He
     * @date 2021-01-22 15:59
     */
    private void compileGeneratedJavaThenAssert(String filePath) {
        JavaCompiler jc = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager sjfm = jc.getStandardFileManager(null, null, null);

        File theJavaFile = new File(filePath);
        try {
            Iterable fileObjects = sjfm.getJavaFileObjects(theJavaFile);
            jc.getTask(null, sjfm, null, null, null, fileObjects).call();
            sjfm.close();
        } catch (Exception e) {
            AssertUtils.assert0(false, formatLogInfoWithIdx(" compile {0} failed ", filePath));
            return;
        }

        String fileName = theJavaFile.getName();
        String fileNameWithoutSuffix = fileName.substring(0, fileName.lastIndexOf("."));
        File parentFolder = theJavaFile.getParentFile();
        File theClassFile = new File(parentFolder, fileNameWithoutSuffix + ".class");
        AssertUtils.assert0(theClassFile.exists(), " the theClassFile does not exists ");
    }

    /**
     * tradeConverterExistsMethodCode
     *
     * @return java.util.List<java.lang.String>
     * @author Jerry.X.He
     * @date 2021-01-21 18:14
     */
    private List<String> tradeConverterExistsMethodCode(String className) {
        String code = "package com.hx.repository.tests.typecast.ignore;\n" +
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
                      "    public TradeView castTradeToTradeView(Trade entity) {\n" +
                      "        TradeView result = new TradeView();\n" +
                      "        return result;\n" +
                      "    }\n" +
                      "\n" +
                      "    public Trade castTradeViewToTrade(TradeView entity) {\n" +
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
        String code = "package com.hx.repository.tests.typecast.ignore;\n" +
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
                      "    public JSONObject castTrade1ToJson(Trade entity) {\n" +
                      "        JSONObject result = new JSONObject();\n" +
                      "        return result;\n" +
                      "    }\n" +
                      "\n" +
                      "    public Trade castJsonToTrade1(JSONObject json) {\n" +
                      "        Trade result = new Trade();\n" +
                      "        return result;\n" +
                      "    }\n" +
                      "\n" +
                      "    public TradeView castTrade1ToTradeView(Trade entity) {\n" +
                      "        TradeView result = new TradeView();\n" +
                      "        return result;\n" +
                      "    }\n" +
                      "\n" +
                      "    public Trade castTradeViewToTrade1(TradeView entity) {\n" +
                      "        Trade result = new Trade();\n" +
                      "        return result;\n" +
                      "    }\n" +
                      "\n" +
                      "}\n";
        return Arrays.asList(code.split("\n"));
    }

}
