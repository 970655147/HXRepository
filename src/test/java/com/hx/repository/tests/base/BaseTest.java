package com.hx.repository.tests.base;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hx.common.util.AssertUtils;
import com.hx.log.util.Tools;
import com.hx.repository.context.task.TaskContext;
import com.hx.repository.context.task.TaskContextThreadLocal;
import com.hx.repository.context.user.UserContext;
import com.hx.repository.context.user.UserContextThreadLocal;
import com.hx.repository.model.ClassInfo;
import com.hx.repository.model.FieldInfo;
import com.hx.repository.utils.ClassInfoUtils;
import com.hx.repository.utils.FieldInfoUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.hx.log.log.LogPatternUtils.formatLogInfoWithIdx;

/**
 * BaseTest
 *
 * @author Jerry.X.He
 * @version 1.0
 * @date 2021-02-03 09:59
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class BaseTest {

    @BeforeClass
    public static void beforeClass01UserContextInit() {
        UserContext context = new UserContext("0x1111", "jerry");
        UserContextThreadLocal.set(context);
    }

    @BeforeClass
    public static void beforeClass02TaskContextInit() {
        TaskContext context = new TaskContext("0x0000", "main");
        TaskContextThreadLocal.set(context);
    }

    @Test
    public void test() {
        System.out.println("Hello Junit");
    }

    // ----------------------------------------- 辅助方法 -----------------------------------------

    /**
     * 对象转换为 json
     *
     * @param object object
     * @return com.alibaba.fastjson.JSONObject
     * @author Jerry.X.He
     * @date 2021-01-19 18:21
     */
    public JSONObject toDebugJSON(Object object) {
        if (object == null) {
            return new JSONObject();
        }

        Class clazz = object.getClass();
        ClassInfo classInfo = ClassInfoUtils.getClassInfo(clazz);
        List<FieldInfo> fieldInfoList = classInfo.getFields();

        JSONObject json = (JSONObject) JSON.toJSON(object);
        JSONObject result = new JSONObject();
        for (String key : json.keySet()) {
            FieldInfo fieldInfo = FieldInfoUtils.lookUpByFieldName(fieldInfoList, key);
            if (fieldInfo == null) {
                continue;
            }
            Object value = json.get(key);
            if (value != null && value instanceof BigDecimal) {
                value = (((BigDecimal) value).setScale(3)).toString();
            }

            result.put(key, String.valueOf(value));
        }
        return result;
    }

    public JSONObject toDebugFullJSON(Object object) {
        return (JSONObject) JSON.toJSON(object);
    }

    /**
     * 保存给定的 所有的行到 file
     *
     * @param lines    lines
     * @param filePath filePath
     * @return void
     * @author Jerry.X.He
     * @date 2021-01-21 18:04
     */
    public void saveLines(List<String> lines, String filePath) {
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
    public void compileGeneratedJavaThenAssert(String filePath) {
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
     * 判断 left & right 元素相同
     *
     * @param left    left
     * @param right   right
     * @param message message
     * @return void
     * @author Jerry.X.He
     * @date 2021-02-01 16:35
     */
    public void listEqualsThenAssert(List<String> left, List<String> right, String message) {
        if (left == null && right == null) {
            return;
        }
        if (left == null || right == null) {
            String leftString = JSON.toJSONString(left);
            String rightString = JSON.toJSONString(right);
            AssertUtils.assert0(formatLogInfoWithIdx(" msg : {0}, left : {1}, right : {2} ",
                                                     message, leftString, rightString));
        }

        List<String> onlyLeft = new ArrayList<>(left);
        onlyLeft.removeAll(right);
        List<String> onlyRight = new ArrayList<>(right);
        onlyRight.removeAll(left);

        if (onlyLeft.isEmpty() && onlyRight.isEmpty()) {
            return;
        }
        String onlyLeftString = JSON.toJSONString(onlyLeft);
        String onlyRightString = JSON.toJSONString(onlyRight);
        AssertUtils.assert0(formatLogInfoWithIdx(" msg : {0}, onlyLeft : {1}, onlyRight : {2} ",
                                                 message, onlyLeftString, onlyRightString));
    }

    /**
     * 打印出给定的 列表的期望输出
     *
     * @param list             list
     * @param printFunc        printFunc
     * @param containerVarName containerVarName
     * @return void
     * @author Jerry.X.He
     * @date 2021-02-01 16:58
     */
    public <T> void printListExpect(List<T> list, Function<T, String> printFunc, String containerVarName) {
        List<String> resultClassList = list.stream().map(printFunc).collect(Collectors.toList());
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("List<String> %s = new ArrayList<>();\n", containerVarName));
        for (String ele : resultClassList) {
            sb.append(String.format("%s.add(\"%s\");\n", containerVarName, ele));
        }
        System.out.println(sb.toString());
    }

    /**
     * 打印出给定的 集合的期望输出
     *
     * @param list             list
     * @param printFunc        printFunc
     * @param containerVarName containerVarName
     * @return void
     * @author Jerry.X.He
     * @date 2021-02-01 16:58
     */
    public <T> void printSetExpect(Set<T> list, Function<T, String> printFunc, String containerVarName) {
        List<String> resultClassList = list.stream().map(printFunc).collect(Collectors.toList());
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Set<String> %s = new LinkedHashSet<>();\n", containerVarName));
        for (String ele : resultClassList) {
            sb.append(String.format("%s.add(\"%s\");\n", containerVarName, ele));
        }
        System.out.println(sb.toString());
    }


}
