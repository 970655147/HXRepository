package com.hx.repository.tests.base;

import com.hx.repository.context.SpringContext;
import com.hx.repository.context.SqliteTestApplication;
import com.hx.repository.domain.Trade;
import com.hx.repository.model.ClassInfo;
import com.hx.repository.utils.ClassInfoUtils;
import com.hx.repository.utils.PostgresUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

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

    @BeforeClass
    public static void beforeClass02CreateTradeSchema() {
        Class clazz = Trade.class;
        List<String> schemaList = PostgresUtils.generateTableSchema(clazz, "fakepath");
        JdbcTemplate jdbcTemplate = SpringContext.getBean(JdbcTemplate.class);

        ClassInfo classInfo = ClassInfoUtils.getClassInfo(clazz);
        String tableName = classInfo.getTableName();
        String dropTableSql = String.format("DROP TABLE IF EXISTS %s;", tableName);
        jdbcTemplate.update(dropTableSql);
        String schemaSql = StringUtils.join(schemaList.iterator(), "");
        jdbcTemplate.update(schemaSql);
    }

    @AfterClass
    public static void afterClass01SpringContextDestroy() {
        SpringContext.destroy();
    }

}
