package com.github.ryarnyah.querydsl.query

import com.mysema.commons.lang.CloseableIterator
import com.querydsl.core.QueryMetadata
import com.querydsl.sql.SQLQuery
import io.micronaut.transaction.jdbc.DataSourceTransactionManager
import com.querydsl.sql.SQLTemplates
import com.querydsl.core.QueryResults
import com.querydsl.sql.Configuration
import io.micronaut.transaction.TransactionDefinition
import java.sql.Connection
import java.sql.ResultSet
import java.util.function.Supplier

class MicronautSQLQuery<T> : SQLQuery<T> {
    private val transactionManager: DataSourceTransactionManager

    constructor(transactionManager: DataSourceTransactionManager) {
        this.transactionManager = transactionManager
    }

    constructor(templates: SQLTemplates?, transactionManager: DataSourceTransactionManager) : super(templates) {
        this.transactionManager = transactionManager
    }

    constructor(conn: Connection?, templates: SQLTemplates?, transactionManager: DataSourceTransactionManager) : super(
        conn,
        templates
    ) {
        this.transactionManager = transactionManager
    }

    constructor(
        conn: Connection?,
        templates: SQLTemplates?,
        metadata: QueryMetadata?,
        transactionManager: DataSourceTransactionManager
    ) : super(conn, templates, metadata) {
        this.transactionManager = transactionManager
    }

    constructor(
        configuration: Configuration?,
        transactionManager: DataSourceTransactionManager
    ) : super(configuration) {
        this.transactionManager = transactionManager
    }

    constructor(
        conn: Connection?,
        configuration: Configuration?,
        transactionManager: DataSourceTransactionManager
    ) : super(conn, configuration) {
        this.transactionManager = transactionManager
    }

    constructor(
        conn: Connection?,
        configuration: Configuration?,
        metadata: QueryMetadata?,
        transactionManager: DataSourceTransactionManager
    ) : super(conn, configuration, metadata) {
        this.transactionManager = transactionManager
    }

    constructor(
        connProvider: Supplier<Connection?>?,
        configuration: Configuration?,
        transactionManager: DataSourceTransactionManager
    ) : super(connProvider, configuration) {
        this.transactionManager = transactionManager
    }

    constructor(
        connProvider: Supplier<Connection?>?,
        configuration: Configuration?,
        metadata: QueryMetadata?,
        transactionManager: DataSourceTransactionManager
    ) : super(connProvider, configuration, metadata) {
        this.transactionManager = transactionManager
    }

    override fun iterate(): CloseableIterator<T> {
        val status = transactionManager.getTransaction(TransactionDefinition.READ_ONLY)
        return MicronautCloseableIterator(
            super.iterate(),
            status,
            transactionManager
        )
    }

    override fun fetchResults(): QueryResults<T> {
        return transactionManager.executeRead { super.fetchResults() }
    }

    override fun fetchOne(): T? {
        return transactionManager.executeRead { super.fetchOne() }
    }

    override fun fetchCount(): Long {
        return transactionManager.executeRead { super.fetchCount() }
    }

    override fun fetch(): List<T> {
        return transactionManager.executeRead { super.fetch() }
    }

    override fun getResults(): ResultSet {
        return transactionManager.executeRead { super.getResults() }
    }

    override fun clone(conn: Connection?): SQLQuery<T> {
        val q = MicronautSQLQuery<T>(conn, getConfiguration(), metadata.clone(), transactionManager)
        q.clone(this)
        return q
    }
}