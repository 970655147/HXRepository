package com.hx.repository.mysql;

import com.hx.repository.base.msyql.AbstractMysqlMainEntityJdbcRepository;
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
public class MysqlTradeRepository extends AbstractMysqlMainEntityJdbcRepository<Trade> {

    @Override
    public Class<Trade> getClazz() {
        return Trade.class;
    }

}
