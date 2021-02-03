package com.hx.repository.tests.base;

import com.hx.repository.context.PostgresTestApplication;
import com.hx.repository.context.SpringContext;
import org.junit.BeforeClass;

/**
 * PostgresBaseRepositoryTest
 *
 * @author Jerry.X.He
 * @version 1.0
 * @date 2021-02-03 10:18
 */
public class PostgresBaseRepositoryTest extends BaseRepositoryTest {

    @BeforeClass
    public static void beforeClass01SpringContextInit() {
        SpringContext.init(PostgresTestApplication.class);
    }

}
