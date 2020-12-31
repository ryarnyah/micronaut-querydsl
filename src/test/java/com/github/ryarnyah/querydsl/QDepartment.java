package com.github.ryarnyah.querydsl;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.sql.ColumnMetadata;

import javax.annotation.Generated;
import java.sql.Types;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;


/**
 * QDepartment is a Querydsl query type for Department
 */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class QDepartment extends com.querydsl.sql.RelationalPathBase<Department> {

    private static final long serialVersionUID = 1330446746;

    public static final QDepartment department = new QDepartment("DEPARTMENT");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final com.querydsl.sql.PrimaryKey<Department> constraint4 = createPrimaryKey(id);

    public final com.querydsl.sql.ForeignKey<Employee> _empDeptIdFk = createInvForeignKey(id, "DEPT_ID");

    public QDepartment(String variable) {
        super(Department.class, forVariable(variable), "PUBLIC", "DEPARTMENT");
        addMetadata();
    }

    public QDepartment(String variable, String schema, String table) {
        super(Department.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QDepartment(String variable, String schema) {
        super(Department.class, forVariable(variable), schema, "DEPARTMENT");
        addMetadata();
    }

    public QDepartment(Path<? extends Department> path) {
        super(path.getType(), path.getMetadata(), "PUBLIC", "DEPARTMENT");
        addMetadata();
    }

    public QDepartment(PathMetadata metadata) {
        super(Department.class, metadata, "PUBLIC", "DEPARTMENT");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(name, ColumnMetadata.named("NAME").withIndex(2).ofType(Types.VARCHAR).withSize(64).notNull());
    }

}

