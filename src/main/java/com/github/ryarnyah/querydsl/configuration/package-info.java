@Configuration
@Requires(
        classes = {SQLQueryFactory.class, DataSource.class, DataSourceTransactionManager.class},
        beans = {DataSource.class, DataSourceTransactionManager.class}
)
package com.github.ryarnyah.querydsl.configuration;

import com.querydsl.sql.SQLQueryFactory;
import io.micronaut.context.annotation.Configuration;
import io.micronaut.context.annotation.Requires;
import io.micronaut.transaction.jdbc.DataSourceTransactionManager;

import javax.sql.DataSource;