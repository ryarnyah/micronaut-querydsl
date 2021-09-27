package com.github.ryarnyah.querydsl.query

import com.querydsl.core.types.Path
import com.querydsl.sql.Configuration
import com.querydsl.sql.RelationalPath
import com.querydsl.sql.SQLTemplates
import com.querydsl.sql.dml.DefaultMapper
import com.querydsl.sql.dml.Mapper
import com.querydsl.sql.dml.SQLMergeClause
import java.sql.Connection
import java.util.function.Supplier

class PopulateSQLMergeClause : SQLMergeClause {
    constructor(connection: Connection?, templates: SQLTemplates?, entity: RelationalPath<*>?) : super(
        connection,
        templates,
        entity
    )

    constructor(connection: Connection?, configuration: Configuration?, entity: RelationalPath<*>?) : super(
        connection,
        configuration,
        entity
    )

    constructor(connection: Supplier<Connection?>?, configuration: Configuration?, entity: RelationalPath<*>?) : super(
        connection,
        configuration,
        entity
    )

    fun populate(bean: Any): SQLMergeClause {
        return populate(bean, DefaultMapper.DEFAULT)
    }

    /**
     * Populate the MERGE clause with the properties of the given bean using the given Mapper.
     *
     * @param obj object to use for population
     * @param mapper mapper to use
     * @return the current object
     */
    fun <T> populate(obj: T, mapper: Mapper<T>): SQLMergeClause {
        val values = mapper.createMap(entity, obj)
        for ((key, value) in values) {
            set(key as Path<Any>, value)
        }
        return this
    }
}