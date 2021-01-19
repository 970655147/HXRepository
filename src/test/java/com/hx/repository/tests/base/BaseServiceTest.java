package com.hx.repository.tests.base;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hx.repository.context.SpringContext;
import com.hx.repository.model.ClassInfo;
import com.hx.repository.model.FieldInfo;
import com.hx.repository.utils.ClassInfoUtils;
import com.hx.repository.utils.FieldInfoUtils;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

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
    public static void springContextInit() {
        SpringContext.init();
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


}
