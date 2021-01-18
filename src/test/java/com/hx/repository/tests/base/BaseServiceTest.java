package com.hx.repository.tests.base;

import com.hx.repository.context.SpringContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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

    @Before
    public void springContextInit() {
        SpringContext.init();
    }

    @Test
    public void test() {
        System.out.println("Hello Junit");
    }

}
