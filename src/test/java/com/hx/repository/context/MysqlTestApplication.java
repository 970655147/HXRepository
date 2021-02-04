package com.hx.repository.context;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

/**
 * MysqlTestApplication
 *
 * @author Jerry.X.He
 * @version 1.0
 * @date 2021-02-04 11:48
 */
@ComponentScan(basePackages = {"com.hx"})
@EnableAutoConfiguration
@PropertySource(value = "classpath:application_mysql.properties")
public class MysqlTestApplication {

}
