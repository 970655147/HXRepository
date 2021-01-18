package com.hx.repository.sqlite;

import com.hx.repository.base.sqlite.AbstractSqliteMainEntityJdbcRepository;
import com.hx.repository.domain.Trade;
import org.springframework.stereotype.Repository;

/**
 * TradeRepositoryMainEntity
 *
 * @author Jerry.X.He <970655147@qq.com>
 * @version 1.0
 * @date 2021-01-15 11:54
 */
@Repository
public class TradeRepository extends AbstractSqliteMainEntityJdbcRepository<Trade> {

    @Override
    public Class<Trade> getClazz() {
        return Trade.class;
    }

    public void doBiz() {

        System.out.println(" doBiz in repository ");

    }

}
