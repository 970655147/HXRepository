package com.hx.repository.sqlite;

import com.hx.repository.base.sqlite.AbstractSqliteTaskEntityJdbcRepository;
import com.hx.repository.domain.Trade;
import org.springframework.stereotype.Repository;

/**
 * TaskTradeRepository
 *
 * @author Jerry.X.He
 * @version 1.0
 * @date 2021-01-19 22:08
 */
@Repository
public class TaskTradeRepository extends AbstractSqliteTaskEntityJdbcRepository<Trade> {

    @Override
    public Class<Trade> getClazz() {
        return Trade.class;
    }

    public void doBiz() {

        System.out.println(" doBiz in repository ");

    }

}
