package com.hx.repository.tests.utils;

import com.hx.repository.domain.BaseEntity;
import com.hx.repository.tests.base.BaseTest;
import com.hx.repository.utils.ClassUtils;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Test02ClassUtils
 *
 * @author Jerry.X.He <970655147@qq.com>
 * @version 1.0
 * @date 2021-02-01 15:59
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Test02ClassUtils extends BaseTest {

    /** BASE_PATH */
    private static String BASE_PATH = System.getProperty("user.dir");
    /** BASE_CLASS_PATH */
    private static String BASE_CLASS_PATH = BASE_PATH + "/target/test-classes";

    /** printFunc */
    private static Function<Class, String> printFunc = (clazz) -> clazz.getName();

    @Test
    public void test01GetAllEntityInFile() throws Exception {

        String path = BASE_CLASS_PATH;
        List<Class> classList = ClassUtils.getSubClasses(path, BaseEntity.class);

        // assertion
        printListExpect(classList, printFunc, "expectClassList");
        List<String> expectClassList = new ArrayList<>();
        expectClassList.add("com.hx.repository.domain.BaseEntity");
        expectClassList.add("com.hx.repository.domain.Trade");
        List<String> resultClassList = classList.stream().map(printFunc).collect(Collectors.toList());
        listEqualsThenAssert(expectClassList, resultClassList, " expectClassList & resultClassList ");

    }

    @Test
    public void test02GetAllEntityInJar() throws Exception {

        String path = "/Users/jerry/.m2/repository/com/hx/HXCodeGen/0.0.2/HXCodeGen-0.0.2.jar";
        List<Class> classList = ClassUtils.getSubClasses(path, BaseEntity.class);

        // assertion
        printListExpect(classList, printFunc, "expectClassList");
        List<String> expectClassList = new ArrayList<>();
        expectClassList.add("com.hx.codegen.yocoyt.domain.User");
        expectClassList.add("com.hx.codegen.yocoyt.domain.MainAccount");
        expectClassList.add("com.hx.codegen.yocoyt.domain.WarnCheckMapping");
        expectClassList.add("com.hx.codegen.yocoyt.domain.MainTrade");
        expectClassList.add("com.hx.repository.domain.BaseEntity");
        expectClassList.add("com.hx.repository.domain.Trade");
        List<String> resultClassList = classList.stream().map(printFunc).collect(Collectors.toList());
        listEqualsThenAssert(expectClassList, resultClassList, " expectClassList & resultClassList ");

    }


}
