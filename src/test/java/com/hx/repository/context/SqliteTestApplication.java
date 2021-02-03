package com.hx.repository.context;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

/**
 * SqliteTestApplication
 *
 * @author Jerry.X.He <970655147@qq.com>
 * @version 1.0
 * @date 2021-02-03 09:37
 */
@ComponentScan(basePackages = {"com.hx"})
@EnableAutoConfiguration
@PropertySource(value = "classpath:application_sqlite.properties")
public class SqliteTestApplication {

}
