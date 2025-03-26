package com.github.ryarnyah.querydsl;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.sql.ColumnMetadata;

import jakarta.annotation.Generated;
import java.sql.Types;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;


/**
 * QEmployee is a Querydsl query type for Employee
 */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class QEmployee extends com.querydsl.sql.RelationalPathBase<Employee> {

    private static final long serialVersionUID = -678652170;

    public static final QEmployee employee = new QEmployee("EMPLOYEE");

    public final NumberPath<Long> deptId = createNumber("deptId", Long.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final com.querydsl.sql.PrimaryKey<Employee> constraint7 = createPrimaryKey(id);

    public final com.querydsl.sql.ForeignKey<Department> empDeptIdFk = createForeignKey(deptId, "ID");

    public QEmployee(String variable) {
        super(Employee.class, forVariable(variable), "PUBLIC", "EMPLOYEE");
        addMetadata();
    }

    public QEmployee(String variable, String schema, String table) {
        super(Employee.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QEmployee(String variable, String schema) {
        super(Employee.class, forVariable(variable), schema, "EMPLOYEE");
        addMetadata();
    }

    public QEmployee(Path<? extends Employee> path) {
        super(path.getType(), path.getMetadata(), "PUBLIC", "EMPLOYEE");
        addMetadata();
    }

    public QEmployee(PathMetadata metadata) {
        super(Employee.class, metadata, "PUBLIC", "EMPLOYEE");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(deptId, ColumnMetadata.named("DEPT_ID").withIndex(3).ofType(Types.BIGINT).withSize(19));
        addMetadata(id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(name, ColumnMetadata.named("NAME").withIndex(2).ofType(Types.VARCHAR).withSize(64).notNull());
    }

}

