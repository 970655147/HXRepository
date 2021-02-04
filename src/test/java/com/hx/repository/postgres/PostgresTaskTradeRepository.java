package com.hx.repository.postgres;

import com.hx.repository.base.postgres.AbstractPostgresTaskEntityJdbcRepository;
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
public class PostgresTaskTradeRepository extends AbstractPostgresTaskEntityJdbcRepository<Trade> {

    @Override
    public Class<Trade> getClazz() {
        return Trade.class;
    }

}
