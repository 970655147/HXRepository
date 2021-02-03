package com.hx.repository.postgres;

import com.hx.repository.base.postgres.AbstractPostgresMainEntityJdbcRepository;
import com.hx.repository.domain.Trade;
import org.springframework.stereotype.Repository;

/**
 * TradeRepository
 *
 * @author Jerry.X.He
 * @version 1.0
 * @date 2021-02-03 10:45
 */
@Repository
public class PostgresTradeRepository extends AbstractPostgresMainEntityJdbcRepository<Trade> {

    @Override
    public Class<Trade> getClazz() {
        return Trade.class;
    }

    public void doBiz() {

        System.out.println(" doBiz in repository ");

    }

}
