package com.hx.repository.tests.base;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hx.common.util.AssertUtils;
import com.hx.log.util.Tools;
import com.hx.repository.context.SpringContext;
import com.hx.repository.context.task.TaskContext;
import com.hx.repository.context.task.TaskContextThreadLocal;
import com.hx.repository.context.user.UserContext;
import com.hx.repository.context.user.UserContextThreadLocal;
import com.hx.repository.model.ClassInfo;
import com.hx.repository.model.FieldInfo;
import com.hx.repository.utils.ClassInfoUtils;
import com.hx.repository.utils.FieldInfoUtils;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.util.List;

import static com.hx.log.log.LogPatternUtils.formatLogInfoWithIdx;

/**
 * AnalysisRelateTradeServiceTest
 *
 * @author Jerry.X.He <970655147@qq.com>
 * @version 1.0
 * @date 2020-04-16 11:44
 */
@RunWith(SpringJUnit4ClassRunner.class)
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@Transactional(rollbackFor = Exception.class)
public class BaseServiceTest {

    @BeforeClass
    public static void beforeClass01SpringContextInit() {
        SpringContext.init();
    }

    @BeforeClass
    public static void beforeClass02UserContextInit() {
        UserContext context = new UserContext("0x1111", "jerry");
        UserContextThreadLocal.set(context);
    }

    @BeforeClass
    public static void beforeClass03TaskContextInit() {
        TaskContext context = new TaskContext("0x0000", "main");
        TaskContextThreadLocal.set(context);
    }

//    @Test
//    public void test() {
//        System.out.println("Hello Junit");
//    }

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

            result.put(key, json.get(key));
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


}
