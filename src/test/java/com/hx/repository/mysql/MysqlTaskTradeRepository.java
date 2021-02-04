package com.hx.repository.mysql;

import com.hx.repository.base.msyql.AbstractMysqlTaskEntityJdbcRepository;
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
public class MysqlTaskTradeRepository extends AbstractMysqlTaskEntityJdbcRepository<Trade> {

    @Override
    public Class<Trade> getClazz() {
        return Trade.class;
    }

}
