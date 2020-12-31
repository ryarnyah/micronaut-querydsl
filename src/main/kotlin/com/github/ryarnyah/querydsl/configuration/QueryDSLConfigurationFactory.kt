package com.github.ryarnyah.querydsl.configuration

import com.querydsl.sql.Configuration
import com.querydsl.sql.SQLQueryFactory
import com.querydsl.sql.SQLTemplates
import com.querydsl.sql.SQLTemplatesRegistry
import io.micronaut.context.ApplicationContext
import io.micronaut.context.annotation.EachBean
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Parameter
import io.micronaut.context.annotation.Requires
import io.micronaut.inject.qualifiers.Qualifiers
import io.micronaut.transaction.jdbc.DataSourceTransactionManager
import org.slf4j.LoggerFactory
import javax.sql.DataSource

@Factory
@Requires(
    classes = [SQLQueryFactory::class, DataSource::class, DataSourceTransactionManager::class],
    beans = [DataSource::class, DataSourceTransactionManager::class]
)
class QueryDSLConfigurationFactory {

    companion object {
        private val logger = LoggerFactory.getLogger(QueryDSLConfigurationFactory::class.java)
    }

    @EachBean(DataSource::class)
    fun sqlQueryFactory(
        @Parameter name: String,
        applicationContext: ApplicationContext
    ): MicronautSQLQueryFactory {
        logger.debug("Register MicronautSQLQueryFactory for {}", name)
        val qualifier = if (name == "default") null else name
        val dataSource = applicationContext.getBean(
            DataSource::class.java, if (qualifier != null) Qualifiers.byName(qualifier) else null
        )

        val transactionManager = applicationContext.getBean(
            DataSourceTransactionManager::class.java, if (qualifier != null) Qualifiers.byName(qualifier) else null
        )

        val template = applicationContext.findBean(
            SQLTemplates::class.java, if (qualifier != null) Qualifiers.byName(qualifier) else null
        ).orElse(transactionManager.executeRead {
            dataSource.connection.use {
                SQLTemplatesRegistry().getTemplates(it.metaData)
            }
        })

        val configuration = Configuration(template)
        return MicronautSQLQueryFactory(SQLQueryFactory(configuration, dataSource), transactionManager)
    }
}