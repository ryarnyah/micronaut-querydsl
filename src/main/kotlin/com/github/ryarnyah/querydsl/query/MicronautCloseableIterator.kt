package com.github.ryarnyah.querydsl.query

import com.mysema.commons.lang.CloseableIterator
import io.micronaut.transaction.TransactionStatus
import io.micronaut.transaction.jdbc.DataSourceTransactionManager
import java.sql.Connection

class MicronautCloseableIterator<T>(
    private val delegate: CloseableIterator<T>,
    private val status: TransactionStatus<Connection>,
    private val transactionManager: DataSourceTransactionManager
): CloseableIterator<T> by delegate {
    override fun close() {
        delegate.close()
        transactionManager.commit(status)
    }
}