package com.github.ryarnyah.querydsl;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.sql.ColumnMetadata;

import javax.annotation.Generated;
import java.sql.Types;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;


/**
 * QMyTest is a Querydsl query type for MyTest
 */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class QTestData extends com.querydsl.sql.RelationalPathBase<TestData> {

    private static final long serialVersionUID = 922366630;

    public static final QTestData testData = new QTestData("TEST_DATA");

    public final StringPath uid = createString("uid");

    public final com.querydsl.sql.PrimaryKey<TestData> constraint7 = createPrimaryKey(uid);

    public QTestData(String variable) {
        super(TestData.class, forVariable(variable), "PUBLIC", "TEST_DATA");
        addMetadata();
    }

    public QTestData(String variable, String schema, String table) {
        super(TestData.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QTestData(String variable, String schema) {
        super(TestData.class, forVariable(variable), schema, "TEST_DATA");
        addMetadata();
    }

    public QTestData(Path<? extends TestData> path) {
        super(path.getType(), path.getMetadata(), "PUBLIC", "TEST_DATA");
        addMetadata();
    }

    public QTestData(PathMetadata metadata) {
        super(TestData.class, metadata, "PUBLIC", "TEST_DATA");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(uid, ColumnMetadata.named("UID").withIndex(1).ofType(Types.VARCHAR).withSize(256).notNull());
    }

}

