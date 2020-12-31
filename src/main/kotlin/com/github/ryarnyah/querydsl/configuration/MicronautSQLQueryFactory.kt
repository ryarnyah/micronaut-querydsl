package com.github.ryarnyah.querydsl.configuration

import com.github.ryarnyah.querydsl.query.MicronautSQLQuery
import com.github.ryarnyah.querydsl.query.PopulateSQLMergeClause
import com.querydsl.core.Tuple
import com.querydsl.core.types.Expression
import com.querydsl.core.types.Path
import com.querydsl.core.types.SubQueryExpression
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.sql.Configuration
import com.querydsl.sql.RelationalPath
import com.querydsl.sql.SQLQuery
import com.querydsl.sql.SQLQueryFactory
import com.querydsl.sql.dml.SQLDeleteClause
import com.querydsl.sql.dml.SQLInsertClause
import com.querydsl.sql.dml.SQLUpdateClause
import io.micronaut.transaction.jdbc.DataSourceTransactionManager
import java.sql.Connection

class MicronautSQLQueryFactory(
    private val delegate: SQLQueryFactory,
    private val transactionManager: DataSourceTransactionManager
) {

    fun query(): SQLQuery<*> {
        return MicronautSQLQuery<Void>(connection, configuration, transactionManager)
    }

    fun <T> select(expr: Expression<T>?): SQLQuery<T> {
        return query().select(expr)
    }

    fun select(vararg exprs: Expression<*>?): SQLQuery<Tuple> {
        return query().select(*exprs)
    }

    fun <T> selectDistinct(expr: Expression<T>?): SQLQuery<T> {
        return query().select(expr).distinct()
    }

    fun selectDistinct(vararg exprs: Expression<*>?): SQLQuery<Tuple> {
        return query().select(*exprs).distinct()
    }

    fun selectZero(): SQLQuery<Int> {
        return select(Expressions.ZERO)
    }

    fun selectOne(): SQLQuery<Int> {
        return select(Expressions.ONE)
    }

    fun <T> selectFrom(expr: RelationalPath<T>?): SQLQuery<T> {
        return query().select(expr).from(expr)
    }

    fun delete(path: RelationalPath<*>?): SQLDeleteClause {
        return delegate.delete(path)
    }

    fun from(from: Expression<*>?): SQLQuery<*> {
        return query().from(from)
    }

    fun from(vararg args: Expression<*>?): SQLQuery<*> {
        return query().from(*args)
    }

    fun from(subQuery: SubQueryExpression<*>?, alias: Path<*>?): SQLQuery<*> {
        return query().from(subQuery, alias)
    }

    fun insert(path: RelationalPath<*>?): SQLInsertClause {
        return delegate.insert(path)
    }

    fun merge(path: RelationalPath<*>?): PopulateSQLMergeClause {
        return PopulateSQLMergeClause(this.connection, this.configuration, path)
    }

    fun update(path: RelationalPath<*>?): SQLUpdateClause {
        return delegate.update(path)
    }

    private val configuration: Configuration
        get() = delegate.configuration
    private val connection: Connection
        get() = delegate.connection
}