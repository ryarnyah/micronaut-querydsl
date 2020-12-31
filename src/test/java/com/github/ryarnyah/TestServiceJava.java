package com.github.ryarnyah;


import com.github.ryarnyah.querydsl.QTestData;
import com.github.ryarnyah.querydsl.TestData;
import com.github.ryarnyah.querydsl.configuration.MicronautSQLQueryFactory;

import javax.inject.Singleton;

@Singleton
public class TestServiceJava {

    private final MicronautSQLQueryFactory sqlQueryFactory;

    public TestServiceJava(MicronautSQLQueryFactory sqlQueryFactory) {
        this.sqlQueryFactory = sqlQueryFactory;
    }

    public TestData findById(String id) {
        return sqlQueryFactory.select(QTestData.testData)
                .from(QTestData.testData)
                .where(QTestData.testData.uid.eq(id))
                .fetchFirst();
    }
}