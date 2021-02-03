package com.hx.repository.tests.base;

import com.hx.repository.context.SpringContext;
import com.hx.repository.context.SqliteTestApplication;
import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 * SqliteBaseRepositoryTest
 *
 * @author Jerry.X.He
 * @version 1.0
 * @date 2021-02-03 10:00
 */
public class SqliteBaseRepositoryTest extends BaseRepositoryTest {

    @BeforeClass
    public static void beforeClass01SpringContextInit() {
        SpringContext.init(SqliteTestApplication.class);
    }

    @AfterClass
    public static void afterClass01SpringContextDestory() {
        SpringContext.destroy();
    }

}
